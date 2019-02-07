// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var blogListUrl = '/admin/BlogListLike'
    var delBlogUrl = '/user/deleteBlog'
    var delBlogId

    // 搜索
    $(".searchBtn").click(function () {
        getBlogList(1, _pageSize);
    });
    //绑定回车键
    $('.search').bind('keydown', function (event) {
        if (event.keyCode == 13) {
            getBlogList(1, _pageSize)
        }
    });

    $("#blogList").off("click", ".delBlog").on("click", ".delBlog", function () {
        delBlogId = $(this).attr("blogId")
    })

    $(".modelSubmit").off("click").on("click", function () {
        delBlog()
    })

    // 分页
    $.tbpage("#blogList", function (pageNum, pageSize) {
        getBlogList(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function getBlogList(pageNum, pageSize) {
        $.ajax({
            url: blogListUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "pageNum": pageNum,
                "pageSize": pageSize,
                "query": $(".search").val()
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
                    getBlogList(_pageNum, _pageSize)
                } else {
                    alert(response.errorMsg)
                }
            }
        });
    }

});