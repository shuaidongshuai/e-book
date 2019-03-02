$(function() {
    var _pageNum = 1
    var _pageSize = 5
    var _url = '/search/blog'

    //hotBlog
    $(".hotNum").each(function(index, item) {
        if(index == 0){
            $(this).addClass('badge-danger')
        }else if(index == 1){
            $(this).addClass('badge-warning')
        }else if(index == 2){
            $(this).addClass('badge-primary')
        }else if(index == 3){
            $(this).addClass('badge-info')
        }else if(index == 4){
            $(this).addClass('badge-default')
        }else if(index == 5){
            $(this).addClass('badge-default')
        }
    });

    //分页
    $.tbpage("#blogList", function (pageNum, pageSize) {
        getBlogList(_url, pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    //搜索
    $("#search").off("click", ".btn").on("click", ".btn", function () {
        getBlogList(_url, _pageNum, _pageSize)
    })

    $("#search input").off("keypress").on("keypress", function (e) {
        if (e.which == 13) {
            getBlogList(_url, _pageNum, _pageSize)
        }
    })

    //选项卡
    $("#searchType a").off("click").on("click", function () {
        var url = $(this).attr("url")
        getBlogList(url, 1, _pageSize)
    })

    function getBlogList(url, pageNum, pageSize) {
        var query = $("#search input").val()
        //url传参特殊字符(+、#、%、&)处理
        query = query.replace(/\+/g, '%2B')
        query = query.replace(/\#/g,"%23")
        query = query.replace(/\%/g,"%25")
        query = query.replace(/\&/g, '%26')
        var url = url + '?query=' + query + '&pageNum=' + pageNum + '&pageSize=' + pageSize
        window.location = url
    }
});