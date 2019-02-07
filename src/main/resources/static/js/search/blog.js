$(function() {
    var _pageNum = 1
    var _pageSize = 5

    //search输入框
    $("#search input").val()

    $.tbpage("#blogList", function (pageNum, pageSize) {
        getBlogList(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    $("#search").off("click", ".btn").on("click", ".btn", function () {
        getBlogList(_pageNum, _pageSize)
    })

    $("#search input").off("keypress").on("keypress", function (e) {
        if (e.which == 13) {
            getBlogList(_pageNum, _pageSize)
        }
    })

    function getBlogList(pageNum, pageSize) {
        var query = $("#search input").val()
        //url传参特殊字符(+、#、%、&)处理
        query = query.replace(/\+/g, '%2B')
        query = query.replace(/\#/g,"%23")
        query = query.replace(/\%/g,"%25")
        query = query.replace(/\&/g, '%26')
        var url = '/search/blog?query=' + query + '&pageNum=' + pageNum + '&pageSize=' + pageSize
        window.location = url
    }
});