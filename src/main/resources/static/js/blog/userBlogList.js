$(function() {
    var concernUrl = '/user/concern/' + $("#blog").attr("userId")
    var cancelConcernUrl = '/user/cancelConcern/' + $("#blog").attr("userId")
    var blogListUrl = '/blog/userBlogList'
    var delBlogUrl = ''
    var userId = $("#blog").attr("userId")
    var _pageNum = 1;
    var _pageSize = 5;

    //关注
    var exist = ("#selfBlog")
    if(exist){
        var concern = $("#selfBlog").attr("concern")
        if(concern == "true"){
            //关注过了 打开“已关注”
            $("#concerned").toggle()
        } else {
            $("#concern").toggle()
        }
    }

    $("#concern").off("click").on("click", function () {
        $.ajax({
            url: concernUrl,
            type: 'put',
            success: function (response) {
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    hideModal()
                } else {
                    if(response.success){
                        $("#concern").toggle()
                        $("#concerned").toggle()
                    } else {
                        alert(response.errorMsg)
                    }
                }
            }
        })
    });

    $("#concerned").off("click").on("click", function () {
        $.ajax({
            url: cancelConcernUrl,
            type: 'delete',
            success: function (response) {
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    hideModal()
                } else {
                    if(response.success){
                        $("#concern").toggle()
                        $("#concerned").toggle()
                    } else{
                        alert(response.errorMsg)
                    }
                }
            }
        })
    });

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