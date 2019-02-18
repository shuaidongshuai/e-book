$(function () {
    var addPictureTagUrl = '/user/addPictureTag/'
    var delPictureTagUrl = '/user/delPictureTag/'
    $.preferenceInit()

    $(".checkbox").off("click").on("click", function () {
        var idx = $(this).attr("idx")
        var className = $(this).attr("className")
        var url;
        if($(this)[0].checked){
            url = addPictureTagUrl + $(this).attr("typeId")
            $.addTag(url, idx, className)
        }else{
            url = delPictureTagUrl + $(this).attr("typeId")
            $.delTag(url, idx)
        }
    })

})