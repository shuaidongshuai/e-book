(function($) {
    //保存成功
    function saveModalOpen() {
        $('#rightContainer #saveModel').modal('show');
        setTimeout("$('#rightContainer #saveModel').modal('hide')", 1000);
    }

    $.refresh = function (url) {
        //刷新页面
        $.ajax({
            url: url,
            type: "GET",
            async: false,
            contentType: "application/json",
            success: function(data){
                $("#rightContainer").html(data);
            }
        });
        saveModalOpen()
    }
})(jQuery);