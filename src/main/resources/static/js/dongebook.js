$(function() {
    $(".dropdown").mouseenter(function () {
        $(this).addClass('show')
        // $(".userMenu button").dropdown('toggle')
        // $(".userMenu button").css({"style":"display: block;"})
    });

    $("#headerNav").mouseleave(function () {
        $(".dropdown").removeClass("show")
    })

    // $(".userMenu button").off("click").on("click", function () {
    //     window.location.href = "/user/profile"
    // })
});