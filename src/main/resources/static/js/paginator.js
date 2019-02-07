(function($) {
    $.tbpage = function (selector, handler) {
        $(selector).off("click", ".tbpage-item").on("click", ".tbpage-item", function () {
            var pageNum = $(this).attr("pageNum");
            var pageSize = $('.tbpage-size option:selected').val();
            // if ($(this).parent().attr("class").indexOf("active") > 0) {
            //     alert("为当前页面");
            // } else {
            //     handler(pageIndex, pageSize);
            // }
            handler(pageNum, pageSize);
        });
        $(selector).off("change", ".tbpage-size").on("change", ".tbpage-size", function () {
            var pageNum = $(this).attr("pageNum");
            var pageSize = $('.tbpage-size option:selected').val();
            handler(pageNum, pageSize);
        });
    };
})(jQuery);