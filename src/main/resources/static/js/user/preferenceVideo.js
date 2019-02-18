$(function () {
    var addVideoTagUrl = '/user/addVideoTag/'
    var delVideoTagUrl = '/user/delVideoTag/'
    $.preferenceInit()

    $(".checkbox").off("click").on("click", function () {
        var idx = $(this).attr("idx")
        var className = $(this).attr("className")
        var url;
        if($(this)[0].checked){
            url = addVideoTagUrl + $(this).attr("typeId")
            $.addTag(url, idx, className)
        }else{
            url = delVideoTagUrl + $(this).attr("typeId")
            $.delTag(url, idx)
        }
    })

})