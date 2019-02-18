$(function () {
    var addMusicTagUrl = '/user/addMusicTag/'
    var delMusicTagUrl = '/user/delMusicTag/'
    $.preferenceInit()

    $(".checkbox").off("click").on("click", function () {
        var idx = $(this).attr("idx")
        var className = $(this).attr("className")
        var url;
        if($(this)[0].checked){
            url = addMusicTagUrl + $(this).attr("typeId")
            $.addTag(url, idx, className)
        }else{
            url = delMusicTagUrl + $(this).attr("typeId")
            $.delTag(url, idx)
        }
    })

})