// DOM 加载完再执行
$(function() {
	var pageNum = 1
	var pageSize = 10

    // 菜单事件
	$(".manager-menu .btn").click(function(){
        // 先移除其他的点击样式，再添加当前的点击样式
		$(".manager-menu .btn").removeClass("active");
		$(this).addClass("active");
        var url = $(this).attr("url");
        $.ajax({
            url: url,
            type: "GET",
            contentType: "application/json",
            data:{
                "pageNum": pageNum,
                "pageSize": pageSize
            },
            success: function(data){
                var prefix = data.toString().substring(0, 4);
                if(prefix == '<div'){
                    $("#rightContainer").html(data);
                }
            }
        });
	});

	//自动触发一次点击
    $(".manager-menu .btn:first").trigger("click");
});