$(function() {
    var blogListUrl = '/blog/userBlogList'
    var delBlogUrl = ''
    var userId = $("#blog").attr("userId")
    var _pageNum = 1;
    var _pageSize = 5;

    //删除 确认框
    $("#blogList").off("click", ".delBlog").on("click", ".delBlog", function () {
        delBlogUrl = '/user/deleteBlog/' + $(this).attr("blogId")
        openConfirmModal()
    })

    //删除博客
    $("#confirmModalSubmit").off("click").on("click", function () {
        delBlog()
    })

    $.tbpage("#blogList", function (pageNum, pageSize) {
        getBlogList(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function getBlogList(pageNum, pageSize) {
        window.location.href = blogListUrl + '?userId=' + userId + '&pageNum=' + pageNum + '&pageSize=' + pageSize;
    }

    function delBlog() {
        $.ajax({
            url: delBlogUrl,
            type: 'delete',
            success: function (response) {
                delBlogUrl = ''
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    hideModal()
                } else {
                    if(response.success){
                        window.location = blogListUrl + '?userId=' + userId + '&pageNum=' + _pageNum + '&pageSize=' + _pageSize;
                    } else {
                        alert(response.errorMsg)
                    }
                }
            }
        })
    }

    function hideModal() {
        $(".hintModal").modal('show');
        setTimeout("$('.hintModal').modal('hide')", 1000);
    }

    function openConfirmModal() {
        $('.confirmModal').modal('show');
    }
})