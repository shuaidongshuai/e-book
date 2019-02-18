// DOM 加载完再执行
$(function() {
    var curUrl = '/admin/uploadVideo'
    var getVideoServerUrl = '/admin/getVideoServer'
    var getVideoCoverServerUrl = '/admin/getVideoCoverServer'
    var saveVideoUrl = '/admin/saveVideo'
    var videoUrl = null
    var videoCoverUrl = null
    var progressVideoTimeout = 100
    var progressVideoCoverTimeout = 100
    var progressVideoValue = 0
    var progressVideoCoverValue = 0

    // http://www.htmleaf.com/jQuery/Form/201510142663.html
    // $('.dropify').dropify();
    var messages = {
        'default': '点击或拖拽文件到这里',
        'replace': '点击或拖拽文件到这里来替换文件',
        'remove':  'remove',
        'error':   '对不起，你上传的文件太大了'
    }
    var videoEvent = $("#video").dropify( {
        messages: messages
    });
    var videoCoverEvent = $("#videoCover").dropify({
        messages: messages
    });
    //remove事件
    videoEvent.on('dropify.afterClear', function(event, element){
        videoUrl = null
        progressVideoReset()
    });
    videoCoverEvent.on('dropify.afterClear', function(event, element){
        videoCoverUrl = null
        progressVideoCoverReset()
    });

    //文件上传
    $("#videoFile input").change(function () {
        if($("#videoFile input")[0].files.length > 0){
            var videoFile = $("#videoFile input")[0].files[0]
            var videoFileName = $("#videoFile input")[0].files[0].name
            uploadFile(getVideoServerUrl, videoFileName, videoFile, false)
        }
    })
    //封面上传
    $("#videoCoverFile input").change(function () {
        if($("#videoCoverFile input")[0].files.length > 0){
            var videoCoverFile = $("#videoCoverFile input")[0].files[0]
            var videoCoverFileName = $("#videoCoverFile input")[0].files[0].name
            uploadFile(getVideoCoverServerUrl, videoCoverFileName, videoCoverFile, true)
        }
    })

    //提交
    $("#videoSubmit").off("click").on("click", function () {
        var form = document.querySelector("#videoForm");
        var formData = new FormData(form)
        var title = formData.get("title")
        if(title == null || title == ''){
            $("#videoTitle").addClass("has-danger")
            return
        }
        $("#videoTitle").removeClass("has-danger")

        if(videoCoverUrl == null){
            $("#videoCoverFile label").addClass("text-danger")
            return
        }
        $("#videoCoverFile label").removeClass("text-danger")
        //没有上传视频需要填写file地址
        if(videoUrl == null){
            var vUrl = formData.get("videoUrl")
            if(vUrl == null || vUrl == ""){
                $("#videoUrl").addClass("text-danger")
                $("#videoFile label").addClass("text-danger")
                return
            }
            videoUrl = vUrl
        }
        $("#videoUrl").removeClass("text-danger")
        $("#videoFile label").removeClass("text-danger")

        formData.append("fileUrl", videoUrl)
        formData.append("coverUrl", videoCoverUrl)
        saveVideo(saveVideoUrl, formData)
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
                                videoCoverUrl = fileUrl
                                progressVideoCoverSuccess()
                            } else {
                                videoUrl = fileUrl
                                progressVideoSuccess()
                            }
                        },
                        error: function () {
                            fileUrl = null
                            if(isCover){
                                progressVideoCoverFail()
                            } else {
                                progressVideoFail()
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
    function saveVideo(url, formData) {
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
                alert("保存视频失败")
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
            progressVideoCoverTimeout = progressTimeout
            progressVideoCoverStart()
        } else{
            progressVideoTimeout = progressTimeout
            progressVideoStart()
        }
    }
    function progressVideoStart() {
        progressVideoRestart()
    }
    function progressVideoCoverStart() {
        progressVideoCoverRestart()
    }

    //每次+2 最多48次
    function progressVideo() {
        progressVideoValue += 2
        if(progressVideoValue < 98){
            $("#videoProgress").css("width", progressVideoValue + "%").text(progressVideoValue + "%");
            setTimeout(progressVideo, progressVideoTimeout);
        }
    }
    function progressVideoCover() {
        progressVideoCoverValue += 2
        if(progressVideoCoverValue < 98){
            $("#videoCoverProgress").css("width", progressVideoCoverValue + "%").text(progressVideoCoverValue + "%");
            setTimeout(progressVideoCover, progressVideoCoverTimeout);
        }
    }

    function progressVideoSuccess() {
        progressVideoValue = 100
        $("#videoProgress").css("width", "100%").text("100%");
    }
    function progressVideoCoverSuccess() {
        progressVideoCoverValue = 100
        $("#videoCoverProgress").css("width", "100%").text("100%");
    }

    function progressVideoFail() {
        progressVideoValue = 0
        $("#videoProgress").addClass("bg-danger");
    }
    function progressVideoCoverFail() {
        progressVideoCoverValue = 0
        $("#videoCoverProgress").addClass("bg-danger");
    }

    function progressVideoRestart() {
        progressVideoReset()
        progressVideo()
    }
    function progressVideoCoverRestart() {
        progressVideoCoverReset()
        progressVideoCover()
    }

    function progressVideoReset() {
        progressVideoValue = 0
        $("#videoProgress").css("width", progressVideoValue + "%").text(progressVideoValue + "%");
        $("#videoProgress").removeClass("bg-danger");
    }
    function progressVideoCoverReset() {
        progressVideoCoverValue = 0
        $("#videoCoverProgress").css("width", progressVideoValue + "%").text(progressVideoValue + "%");
        $("#videoCoverProgress").removeClass("bg-danger");
    }

    //保存成功
    function saveVideoModalOpen() {
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
        saveVideoModalOpen()
    }
});