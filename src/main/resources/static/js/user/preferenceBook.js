$(function () {
    var addBookTagUrl = '/user/addBookTag/'
    var delBookTagUrl = '/user/delBookTag/'
    $.preferenceInit()

    $(".checkbox").off("click").on("click", function () {
        var idx = $(this).attr("idx")
        var className = $(this).attr("className")
        var url;
        if($(this)[0].checked){
            url = addBookTagUrl + $(this).attr("typeId")
            $.addTag(url, idx, className)
        }else{
            url = delBookTagUrl + $(this).attr("typeId")
            $.delTag(url, idx)
        }
    })

})