$(function () {
    var addBlogTagUrl = '/user/addBlogTag/'
    var delBlogTagUrl = '/user/delBlogTag/'
    $.preferenceInit()

    $(".checkbox").off("click").on("click", function () {
        var idx = $(this).attr("idx")
        var className = $(this).attr("className")
        var url;
        if($(this)[0].checked){
            url = addBlogTagUrl + $(this).attr("typeId")
            $.addTag(url, idx, className)
        }else{
            url = delBlogTagUrl + $(this).attr("typeId")
            $.delTag(url, idx)
        }
    })

})