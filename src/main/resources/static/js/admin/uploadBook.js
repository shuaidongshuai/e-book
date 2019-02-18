// DOM 加载完再执行
$(function() {
    var curUrl = '/admin/uploadBook'
    var getBookServerUrl = '/admin/getBookServer'
    var getBookCoverServerUrl = '/admin/getBookCoverServer'
    var saveBookUrl = '/admin/saveBook'
    var bookUrl = null
    var bookCoverUrl = null
    var progressBookTimeout = 100
    var progressBookCoverTimeout = 100
    var progressBookValue = 0
    var progressBookCoverValue = 0

    // http://www.htmleaf.com/jQuery/Form/201510142663.html
    // $('.dropify').dropify();
    var messages = {
        'default': '点击或拖拽文件到这里',
        'replace': '点击或拖拽文件到这里来替换文件',
        'remove':  'remove',
        'error':   '对不起，你上传的文件太大了'
    }
    var bookEvent = $("#book").dropify( {
        messages: messages
    });
    var bookCoverEvent = $("#bookCover").dropify({
        messages: messages
    });
    //remove事件
    bookEvent.on('dropify.afterClear', function(event, element){
        bookUrl = null
        progressBookReset()
    });
    bookCoverEvent.on('dropify.afterClear', function(event, element){
        bookCoverUrl = null
        progressBookCoverReset()
    });


    //文件上传
    $("#bookFile input").change(function () {
        if($("#bookFile input")[0].files.length > 0){
            var bookFile = $("#bookFile input")[0].files[0]
            var bookFileName = $("#bookFile input")[0].files[0].name
            uploadFile(getBookServerUrl, bookFileName, bookFile, false)
        }
    })
    //封面上传
    $("#bookCoverFile input").change(function () {
        if($("#bookCoverFile input")[0].files.length > 0){
            var bookCoverFile = $("#bookCoverFile input")[0].files[0]
            var bookCoverFileName = $("#bookCoverFile input")[0].files[0].name
            uploadFile(getBookCoverServerUrl, bookCoverFileName, bookCoverFile, true)
        }
    })

    //提交
    $("#bookSubmit").off("click").on("click", function () {
        var form = document.querySelector("#bookForm");
        var formData = new FormData(form)
        var name = formData.get("name")
        if(name == null || name == ''){
            $("#bookName").addClass("has-danger")
            return
        }
        $("#bookName").removeClass("has-danger")

        var introduction = formData.get("introduction")
        if(introduction == null || introduction == ''){
            $("#bookIntroduction").addClass("has-danger")
            return
        }
        $("#bookIntroduction").removeClass("has-danger")

        if(bookUrl == null){
            $("#bookFile label").addClass("text-danger")
            return
        }
        $("#bookFile label").removeClass("text-danger")
        if(bookCoverUrl == null){
            $("#bookCoverFile label").addClass("text-danger")
            return
        }
        $("#bookCoverFile label").removeClass("text-danger")

        formData.append("fileUrl", bookUrl)
        formData.append("coverUrl", bookCoverUrl)
        saveBook(saveBookUrl, formData)
    })

    //上传文件和封面
    function uploadFile(url, filename, file, isCover) {
        $.ajax({
            url: url,
            type: 'get',
            data: {
                filename: filename
            },
            success: function (responseUploadDto) {
                if (responseUploadDto.success) {
                    //启动进度条
                    progressFileStart(file.size, isCover)

                    var fileUrl = responseUploadDto.fileUrl
                    var fileServerHost = responseUploadDto.host
                    var formData = new FormData();
                    formData.append('OSSAccessKeyId', responseUploadDto.accessKeyId);
                    formData.append('policy', responseUploadDto.policy);
                    formData.append('signature', responseUploadDto.signature);
                    formData.append('key', responseUploadDto.filename);
                    formData.append('success_action_status', '200');
                    formData.append('file', file);
                    //上传到文件服务器
                    $.ajax({
                        url: fileServerHost,
                        contentType: false,// 不设置内容类型
                        processData: false,//用于对data参数进行序列化处理 这里必须false
                        type: 'POST',
                        data: formData,
                        success: function () {
                            if(isCover){
                                bookCoverUrl = fileUrl
                                progressBookCoverSuccess()
                            } else {
                                bookUrl = fileUrl
                                progressBookSuccess()
                            }
                        },
                        error: function () {
                            fileUrl = null
                            if(isCover){
                                progressBookCoverFail()
                            } else {
                                progressBookFail()
                            }
                            alert("文件服务器错误")
                        }
                    });
                } else {
                    alert(responseUploadDto.errorMsg)
                }
            },
            error: function () {
                alert("应用服务器错误")
            }
        })
    }

    //保存
    function saveBook(url, formData) {
        $.ajax({
            url: url,
            type: "POST",
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (responseCommonDto) {
                if (responseCommonDto.success) {
                    saveSuccess()
                } else {
                    alert(responseCommonDto.errorMsg)
                }
            },
            error: function () {
                alert("保存图书失败")
            }
        })
    }

    //进度条
    function progressFileStart(fileSize, isCover){
        //速率 1k/ms 计算总时间ms
        var totalTime = fileSize / 1024
        //平均展示50次
        var progressTimeout = totalTime / 50
        if(progressTimeout < 100){
            progressTimeout = 100
        }
        if(isCover){
            progressBookCoverTimeout = progressTimeout
            progressBookCoverStart()
        } else{
            progressBookTimeout = progressTimeout
            progressBookStart()
        }
    }
    function progressBookStart() {
        progressBookRestart()
    }
    function progressBookCoverStart() {
        progressBookCoverRestart()
    }

    //每次+2 最多48次
    function progressBook() {
        progressBookValue += 2
        if(progressBookValue < 98){
            $("#bookProgress").css("width", progressBookValue + "%").text(progressBookValue + "%");
            setTimeout(progressBook, progressBookTimeout);
        }
    }
    function progressBookCover() {
        progressBookCoverValue += 2
        if(progressBookCoverValue < 98){
            $("#bookCoverProgress").css("width", progressBookCoverValue + "%").text(progressBookCoverValue + "%");
            setTimeout(progressBookCover, progressBookCoverTimeout);
        }
    }

    function progressBookSuccess() {
        progressBookValue = 100
        $("#bookProgress").css("width", "100%").text("100%");
    }
    function progressBookCoverSuccess() {
        progressBookCoverValue = 100
        $("#bookCoverProgress").css("width", "100%").text("100%");
    }

    function progressBookFail() {
        progressBookValue = 0
        $("#bookProgress").addClass("bg-danger");
    }
    function progressBookCoverFail() {
        progressBookCoverValue = 0
        $("#bookCoverProgress").addClass("bg-danger");
    }

    function progressBookRestart() {
        progressBookReset()
        progressBook()
    }
    function progressBookCoverRestart() {
        progressBookCoverReset()
        progressBookCover()
    }

    function progressBookReset() {
        progressBookValue = 0
        $("#bookProgress").css("width", progressBookValue + "%").text(progressBookValue + "%");
        $("#bookProgress").removeClass("bg-danger");
    }
    function progressBookCoverReset() {
        progressBookCoverValue = 0
        $("#bookCoverProgress").css("width", progressBookValue + "%").text(progressBookValue + "%");
        $("#bookCoverProgress").removeClass("bg-danger");
    }

    //保存成功
    function saveSuccess() {
        //刷新页面
        $.refresh(curUrl)
    }
});