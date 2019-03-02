// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var getPictureUrl = '/admin/getPicture'
    var savePictureUrl = '/admin/savePicture'
    var pictureListLikeUrl = '/admin/pictureListLike'
    var delPictureUrl = '/admin/delPicture'
    var delPictureId = null

    $("#managerSearch input").attr("placeholder", "输入标题进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        pictureListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            pictureListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        pictureListLike(_pageNum, _pageSize);
    })

    //修改
    $("#pictureList").off("click", ".modifyButton").on("click", ".modifyButton", function () {
        var id = $(this).attr("pictureId")
        getPicture(id)
    })
    $("#modifyModel").off("click", ".submit").on("click", ".submit", function () {
        savePicture()
    })

    //删除
    $("#pictureList").off("click", ".delButton").on("click", ".delButton", function () {
        delPictureId = $(this).attr("pictureId")
    })
    $("#delModel").off("click", ".submit").on("click", ".submit", function () {
        delPicture()
    })

    // 分页
    $.tbpage("#pictureList", function (pageNum, pageSize) {
        pictureListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function pictureListLike(pageNum, pageSize) {
        $.ajax({
            url: pictureListLikeUrl,
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
                    $("#pictureList").html(data);
                }
            }
        });
    }

    function getPicture(id) {
        $.ajax({
            url: getPictureUrl,
            type: "GET",
            data:{
                id: id
            },
            success: function (responsePictureDto) {
                if(responsePictureDto.success){
                    var pictureDto = responsePictureDto.pictureDto
                    $("#pictureForm .id").val(pictureDto.id)
                    $("#pictureForm .title").val(pictureDto.title)
                    $("#pictureForm .pictureTypeId").val(pictureDto.pictureTypeId)
                    $("#pictureForm .fileUrl").val(pictureDto.fileUrl)
                } else {
                    alert(responsePictureDto.errorMsg)
                }
            }
        });
    }

    function savePicture() {
        var form = document.querySelector("#pictureForm");
        var formData = new FormData(form)
        var title = formData.get("title")
        if(title == null || title == ''){
            $("#pictureTitle").addClass("has-danger")
            return
        }
        $("#pictureTitle").removeClass("has-danger")

        var fileUrl = formData.get("urlJson")
        if(fileUrl == null || fileUrl == ''){
            $("#pictureUrl").addClass("has-danger")
            return
        }
        $("#pictureUrl").removeClass("has-danger")

        $.ajax({
            url: savePictureUrl,
            type: "POST",
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (responsePictureDto) {
                if(responsePictureDto.success){
                    $("#modifyModel").modal("hide")
                    pictureListLike(_pageNum, _pageSize)
                } else {
                    alert(responsePictureDto.errorMsg)
                }
            }
        });
    }

    function delPicture() {
        $.ajax({
            url: delPictureUrl + '/' + delPictureId,
            type: "delete",
            success: function (response) {
                if(response.success){
                    pictureListLike(_pageNum, _pageSize)
                } else {
                    alert(response.errorMsg)
                }
            }
        });
    }

});