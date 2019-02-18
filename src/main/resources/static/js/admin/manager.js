// DOM 加载完再执行
$(function() {
	var pageNum = 1
	var pageSize = 10

    // 菜单事件
    $("#sidebarnav").off("click", "a").on("click", "a", function () {
        var url = $(this).attr("url");
        if(url == null){
            return
        }
        $.ajax({
            url: url,
            type: "GET",
            contentType: "application/json",
            data:{
                "pageNum": pageNum,
                "pageSize": pageSize,
                "desc": true
            },
            success: function(data){
                var prefix = data.toString().substring(0, 15);
                if(prefix != '<!doctype html>'){
                    $("#rightContainer").html(data);
                }
            }
        });
	});
});