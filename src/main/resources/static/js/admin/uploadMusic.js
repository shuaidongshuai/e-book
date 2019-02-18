// DOM 加载完再执行
$(function() {
    var curUrl = '/admin/uploadMusic'
    var getMusicServerUrl = '/admin/getMusicServer'
    var getMusicCoverServerUrl = '/admin/getMusicCoverServer'
    var saveMusicUrl = '/admin/saveMusic'
    var musicUrl = null
    var musicCoverUrl = null
    var progressMusicTimeout = 100
    var progressMusicCoverTimeout = 100
    var progressMusicValue = 0
    var progressMusicCoverValue = 0

    // http://www.htmleaf.com/jQuery/Form/201510142663.html
    // $('.dropify').dropify();
    var messages = {
        'default': '点击或拖拽文件到这里',
        'replace': '点击或拖拽文件到这里来替换文件',
        'remove':  'remove',
        'error':   '对不起，你上传的文件太大了'
    }
    var musicEvent = $("#music").dropify( {
        messages: messages
    });
    var musicCoverEvent = $("#musicCover").dropify({
        messages: messages
    });
    //remove事件
    musicEvent.on('dropify.afterClear', function(event, element){
        musicUrl = null
        progressMusicReset()
    });
    musicCoverEvent.on('dropify.afterClear', function(event, element){
        musicCoverUrl = null
        progressMusicCoverReset()
    });


    //文件上传
    $("#musicFile input").change(function () {
        if($("#musicFile input")[0].files.length > 0){
            var musicFile = $("#musicFile input")[0].files[0]
            var musicFileName = $("#musicFile input")[0].files[0].name
            uploadFile(getMusicServerUrl, musicFileName, musicFile, false)
        }
    })
    //封面上传
    $("#musicCoverFile input").change(function () {
        if($("#musicCoverFile input")[0].files.length > 0){
            var musicCoverFile = $("#musicCoverFile input")[0].files[0]
            var musicCoverFileName = $("#musicCoverFile input")[0].files[0].name
            uploadFile(getMusicCoverServerUrl, musicCoverFileName, musicCoverFile, true)
        }
    })

    //提交
    $("#musicSubmit").off("click").on("click", function () {
        var form = document.querySelector("#musicForm");
        var formData = new FormData(form)
        var name = formData.get("name")
        if(name == null || name == ''){
            $("#musicName").addClass("has-danger")
            return
        }
        $("#musicName").removeClass("has-danger")

        var author = formData.get("author")
        if(author == null || author == ''){
            $("#musicAuthor").addClass("has-danger")
            return
        }
        $("#musicAuthor").removeClass("has-danger")

        var composer = formData.get("composer")
        if(composer == null || composer == ''){
            $("#musicComposer").addClass("has-danger")
            return
        }
        $("#musicComposer").removeClass("has-danger")

        var singer = formData.get("singer")
        if(singer == null || singer == ''){
            $("#musicSinger").addClass("has-danger")
            return
        }
        $("#musicSinger").removeClass("has-danger")


        if(musicUrl == null){
            $("#musicFile label").addClass("text-danger")
            return
        }
        $("#musicFile label").removeClass("text-danger")
        // if(musicCoverUrl == null){
        //     $("#musicCoverFile label").addClass("text-danger")
        //     return
        // }
        // $("#musicCoverFile label").removeClass("text-danger")

        formData.append("fileUrl", musicUrl)
        formData.append("coverUrl", musicCoverUrl)
        saveMusic(saveMusicUrl, formData)
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
                                musicCoverUrl = fileUrl
                                progressMusicCoverSuccess()
                            } else {
                                musicUrl = fileUrl
                                progressMusicSuccess()
                            }
                        },
                        error: function () {
                            fileUrl = null
                            if(isCover){
                                progressMusicCoverFail()
                            } else {
                                progressMusicFail()
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
    function saveMusic(url, formData) {
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
                alert("保存音乐失败")
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
            progressMusicCoverTimeout = progressTimeout
            progressMusicCoverStart()
        } else{
            progressMusicTimeout = progressTimeout
            progressMusicStart()
        }
    }
    function progressMusicStart() {
        progressMusicRestart()
    }
    function progressMusicCoverStart() {
        progressMusicCoverRestart()
    }

    //每次+2 最多48次
    function progressMusic() {
        progressMusicValue += 2
        if(progressMusicValue < 98){
            $("#musicProgress").css("width", progressMusicValue + "%").text(progressMusicValue + "%");
            setTimeout(progressMusic, progressMusicTimeout);
        }
    }
    function progressMusicCover() {
        progressMusicCoverValue += 2
        if(progressMusicCoverValue < 98){
            $("#musicCoverProgress").css("width", progressMusicCoverValue + "%").text(progressMusicCoverValue + "%");
            setTimeout(progressMusicCover, progressMusicCoverTimeout);
        }
    }

    function progressMusicSuccess() {
        progressMusicValue = 100
        $("#musicProgress").css("width", "100%").text("100%");
    }
    function progressMusicCoverSuccess() {
        progressMusicCoverValue = 100
        $("#musicCoverProgress").css("width", "100%").text("100%");
    }

    function progressMusicFail() {
        progressMusicValue = 0
        $("#musicProgress").addClass("bg-danger");
    }
    function progressMusicCoverFail() {
        progressMusicCoverValue = 0
        $("#musicCoverProgress").addClass("bg-danger");
    }

    function progressMusicRestart() {
        progressMusicReset()
        progressMusic()
    }
    function progressMusicCoverRestart() {
        progressMusicCoverReset()
        progressMusicCover()
    }

    function progressMusicReset() {
        progressMusicValue = 0
        $("#musicProgress").css("width", progressMusicValue + "%").text(progressMusicValue + "%");
        $("#musicProgress").removeClass("bg-danger");
    }
    function progressMusicCoverReset() {
        progressMusicCoverValue = 0
        $("#musicCoverProgress").css("width", progressMusicValue + "%").text(progressMusicValue + "%");
        $("#musicCoverProgress").removeClass("bg-danger");
    }

    //保存成功
    function saveMusicModalOpen() {
        $('#rightContainer #saveModel').modal('show');
        setTimeout("$('#rightContainer #saveModel').modal('hide')", 1000);
    }

    //保存成功
    function saveSuccess() {
        //刷新页面
        $.ajax({
            url: curUrl,
            type: "GET",
            async: false,
            contentType: "application/json",
            success: function(data){
                $("#rightContainer").html(data);
            }
        });
        saveMusicModalOpen()
    }
});