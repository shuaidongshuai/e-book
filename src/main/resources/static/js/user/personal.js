$(function () {
    $("#sidebarnav a").off("click").on("click", function () {
        var url = $(this).attr("url")
        if(url == null){
            return
        }
        $.ajax({
            url: url,
            type: "GET",
            success: function (data) {
                var prefix = data.toString().substring(0, 15);
                if(prefix != '<!doctype html>'){
                    $("#rightContainer").html(data);
                }
            }
        });
    })


})