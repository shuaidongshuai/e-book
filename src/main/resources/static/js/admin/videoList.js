// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var getVideoUrl = '/admin/getVideo'
    var saveVideoUrl = '/admin/saveVideo'
    var videoListLikeUrl = '/admin/videoListLike'
    var delVideoUrl = '/admin/delVideo'
    var delVideoId = null

    $("#managerSearch input").attr("placeholder", "输入标题进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        videoListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            videoListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        videoListLike(_pageNum, _pageSize);
    })

    //修改
    $("#videoList").off("click", ".modifyButton").on("click", ".modifyButton", function () {
        var id = $(this).attr("videoId")
        getVideo(id)
    })
    $("#modifyModel").off("click", ".submit").on("click", ".submit", function () {
        saveVideo()
    })

    //删除
    $("#videoList").off("click", ".delButton").on("click", ".delButton", function () {
        delVideoId = $(this).attr("videoId")
    })
    $("#delModel").off("click", ".submit").on("click", ".submit", function () {
        delVideo()
    })

    // 分页
    $.tbpage("#videoList", function (pageNum, pageSize) {
        videoListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function videoListLike(pageNum, pageSize) {
        $.ajax({
            url: videoListLikeUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "pageNum": pageNum,
                "pageSize": pageSize,
                "desc": _desc,
                "query": $("#managerSearch input").val()
            },
            success: function (data) {
                var prefix = data.toString().substring(0, 4);
                if(prefix == '<div'){
                    $("#videoList").html(data);
                }
            }
        });
    }

    function getVideo(id) {
        $.ajax({
            url: getVideoUrl,
            type: "GET",
            data:{
                id: id
            },
            success: function (responseVideoDto) {
                if(responseVideoDto.success){
                    var videoDto = responseVideoDto.videoDto
                    $("#videoForm .id").val(videoDto.id)
                    $("#videoForm .title").val(videoDto.title)
                    $("#videoForm .videoTypeId").val(videoDto.videoTypeId)
                    $("#videoForm .fileUrl").val(videoDto.fileUrl)
                    $("#videoForm .coverUrl").val(videoDto.coverUrl)
                } else {
                    alert(responseVideoDto.errorMsg)
                }
            }
        });
    }

    function saveVideo() {
        var form = document.querySelector("#videoForm");
        var formData = new FormData(form)
        var title = formData.get("title")
        if(title == null || title == ''){
            $("#videoTitle").addClass("has-danger")
            return
        }
        $("#videoTitle").removeClass("has-danger")

        var fileUrl = formData.get("fileUrl")
        if(fileUrl == null || fileUrl == ''){
            $("#videoUrl").addClass("has-danger")
            return
        }
        $("#videoUrl").removeClass("has-danger")

        var coverUrl = formData.get("coverUrl")
        if(coverUrl == null || coverUrl == ''){
            $("#videoCoverUrl").addClass("has-danger")
            return
        }
        $("#videoCoverUrl").removeClass("has-danger")

        $.ajax({
            url: saveVideoUrl,
            type: "POST",
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (responseVideoDto) {
                if(responseVideoDto.success){
                    $("#modifyModel").modal("hide")
                    videoListLike(_pageNum, _pageSize)
                } else {
                    alert(responseVideoDto.errorMsg)
                }
            }
        });
    }

    function delVideo() {
        $.ajax({
            url: delVideoUrl + '/' + delVideoId,
            type: "delete",
            success: function (response) {
                if(response.success){
                    videoListLike(_pageNum, _pageSize)
                } else {
                    alert(response.errorMsg)
                }
            }
        });
    }

});