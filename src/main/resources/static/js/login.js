$(function () {

    $("#loginUsername").focus()

    $('#password2').off("blur").on("blur", function() {
        checkPassword()
    })

    function checkPassword() {
        var pwd = $.trim($("#password1").val())
        //获取this，即ipwd的val()值，trim函数的作用是去除空格
        var rpwd = $.trim($("#password2").val())
        if(rpwd != ""){
            if(pwd == rpwd) {
                $("#passwordError").text("")
                $("#submit").attr("disabled", false)
            } else {
                $("#passwordError").text(" Oh snap ! 两次密码输入不一致")
                $("#submit").attr("disabled", true)
            }
        }
    }
})