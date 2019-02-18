$(function () {
    var profileSaveUrl = '/user/profileSave'

    $('#newPassword2').off("blur").on("blur", function() {
        checkPassword()
    })

    function checkPassword() {
        var pwd = $.trim($("#newPassword1").val())
        //获取this，即ipwd的val()值，trim函数的作用是去除空格
        var rpwd = $.trim($("#newPassword2").val())
        if(rpwd != ""){
            if(pwd == rpwd) {
                $("#passwordError").text("")
                $("#submit").attr("disabled", false)
            } else {
                $("#passwordError").text(" Oh snap ! 两次新密码输入不一致")
                $("#submit").attr("disabled", true)
            }
        }
    }

    $("#submit").off("click").on("click", function () {
        $.ajax({
            url: profileSaveUrl,
            type: "POST",
            data: $("#profileForm").serialize(),
            success: function(responseCommonDto) {
                if(responseCommonDto.success){
                    openModal()
                } else {
                    alert(responseCommonDto.errorMsg)
                }
            },
            error: function() {
                alert("Connection error");
            }
        })
    })

    function openModal() {
        $('.saveSuccess').modal('show');
        setTimeout("$('.saveSuccess').modal('hide')", 1500);
    }

})