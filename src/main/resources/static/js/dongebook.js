$(function() {
    $(".dropdown").mouseenter(function () {
        $(this).addClass('show')
        // $(".userMenu button").dropdown('toggle')
        // $(".userMenu button").css({"style":"display: block;"})
    });

    $("#headerNav").mouseleave(function () {
        $(".dropdown").removeClass("show")
    })

    var _pageNum = 1
    var _pageSize = 5

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