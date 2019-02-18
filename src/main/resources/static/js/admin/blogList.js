// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var blogListLikeUrl = '/admin/BlogListLike'
    var delBlogUrl = '/user/deleteBlog'
    var delBlogId

    $("#managerSearch input").attr("placeholder", "输入ID或标题进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        blogListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            blogListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        blogListLike(_pageNum, _pageSize);
    })

    $("#blogList").off("click", ".delBlog").on("click", ".delBlog", function () {
        delBlogId = $(this).attr("blogId")
    })

    $(".modelSubmit").off("click").on("click", function () {
        delBlog()
    })

    // 分页
    $.tbpage("#blogList", function (pageNum, pageSize) {
        blogListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function blogListLike(pageNum, pageSize) {
        $.ajax({
            url: blogListLikeUrl,
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
                    $("#blogList").html(data);
                }
            }
        });
    }

    function delBlog() {
        $.ajax({
            url: delBlogUrl + '/' + delBlogId,
            type: "delete",
            success: function (response) {
                if(response.success){
                    blogListLike(_pageNum, _pageSize)
                } else {
                    alert(response.errorMsg)
                }
            }
        });
    }

});