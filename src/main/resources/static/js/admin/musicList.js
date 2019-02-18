// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var getMusicUrl = '/admin/getMusic'
    var saveMusicUrl = '/admin/saveMusic'
    var musicListLikeUrl = '/admin/musicListLike'
    var delMusicUrl = '/admin/delMusic'
    var delMusicId = null

    $("#managerSearch input").attr("placeholder", "输入歌名进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        musicListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            musicListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        musicListLike(_pageNum, _pageSize);
    })

    //修改
    $("#musicList").off("click", ".modifyButton").on("click", ".modifyButton", function () {
        var id = $(this).attr("musicId")
        getMusic(id)
    })
    $("#modifyModel").off("click", ".submit").on("click", ".submit", function () {
        saveMusic()
    })

    //删除
    $("#musicList").off("click", ".delButton").on("click", ".delButton", function () {
        delMusicId = $(this).attr("musicId")
    })
    $("#delModel").off("click", ".submit").on("click", ".submit", function () {
        delMusic()
    })

    // 分页
    $.tbpage("#musicList", function (pageNum, pageSize) {
        musicListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function musicListLike(pageNum, pageSize) {
        $.ajax({
            url: musicListLikeUrl,
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
                    $("#musicList").html(data);
                }
            }
        });
    }

    function getMusic(id) {
        $.ajax({
            url: getMusicUrl,
            type: "GET",
            data:{
                id: id
            },
            success: function (responseMusicDto) {
                if(responseMusicDto.success){
                    var musicDto = responseMusicDto.musicDto
                    $("#musicForm .id").val(musicDto.id)
                    $("#musicForm .name").val(musicDto.name)
                    $("#musicForm .musicTypeId").val(musicDto.musicTypeId)
                    $("#musicForm .fileUrl").val(musicDto.fileUrl)
                    $("#musicForm .coverUrl").val(musicDto.coverUrl)
                } else {
                    alert(responseMusicDto.errorMsg)
                }
            }
        });
    }

    function saveMusic() {
        var form = document.querySelector("#musicForm");
        var formData = new FormData(form)
        var name = formData.get("name")
        if(name == null || name == ''){
            $("#musicName").addClass("has-danger")
            return
        }
        $("#musicName").removeClass("has-danger")

        var fileUrl = formData.get("fileUrl")
        if(fileUrl == null || fileUrl == ''){
            $("#musicUrl").addClass("has-danger")
            return
        }
        $("#musicUrl").removeClass("has-danger")

        var coverUrl = formData.get("coverUrl")
        if(coverUrl == null || coverUrl == ''){
            $("#musicCoverUrl").addClass("has-danger")
            return
        }
        $("#musicCoverUrl").removeClass("has-danger")

        $.ajax({
            url: saveMusicUrl,
            type: "POST",
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (responseMusicDto) {
                if(responseMusicDto.success){
                    $("#modifyModel").modal("hide")
                    musicListLike(_pageNum, _pageSize)
                } else {
                    alert(responseMusicDto.errorMsg)
                }
            }
        });
    }

    function delMusic() {
        $.ajax({
            url: delMusicUrl + '/' + delMusicId,
            type: "delete",
            success: function (response) {
                if(response.success){
                    musicListLike(_pageNum, _pageSize)
                } else {
                    alert(response.errorMsg)
                }
            }
        });
    }

});