// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var concernListLikeUrl = '/user/concernListLike'

    $("#managerSearch input").attr("placeholder", "输入昵称进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        concernListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            concernListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        concernListLike(_pageNum, _pageSize);
    })

    // 分页
    $.tbpage("#concernList", function (pageNum, pageSize) {
        concernListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function concernListLike(pageNum, pageSize) {
        $.ajax({
            url: concernListLikeUrl,
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
                    $("#concernList").html(data);
                }
            }
        });
    }
});