// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var fansListLikeUrl = '/user/fansListLike'

    $("#managerSearch input").attr("placeholder", "输入昵称进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        fansListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            fansListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        fansListLike(_pageNum, _pageSize);
    })

    // 分页
    $.tbpage("#fansList", function (pageNum, pageSize) {
        fansListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function fansListLike(pageNum, pageSize) {
        $.ajax({
            url: fansListLikeUrl,
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
                    $("#fansList").html(data);
                }
            }
        });
    }
});