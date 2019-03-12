$(function () {
    var registerUrl = "/register"
    var loginUrl = "/login"

    $("#loginUsername").focus()

    $('#username').off("blur").on("blur", function() {
        checkInput($(this), $("#usernameError"), " Oh snap ! 用户名长度必须在6-30以内", 6, 30)
    })

    $('#password1').off("blur").on("blur", function() {
        checkInput($(this), $("#password1Error"), " Oh snap ! 密码长度必须在6-20以内", 6, 20)
    })

    $('#password2').off("blur").on("blur", function() {
        checkPassword()
    })

    $('#email').off("blur").on("blur", function() {
        checkInput($(this), $("#emailError"), " Oh snap ! 邮箱长度必须在6-20以内", 6, 20)
    })

    $('#nickname').off("blur").on("blur", function() {
        checkInput($(this), $("#nicknameError"), " Oh snap ! 昵称长度必须在1-10以内", 1, 10)
    })

    $('#phoneNumber').off("blur").on("blur", function() {
        checkInput($(this), $("#phoneNumberError"), " Oh snap ! 电话号长度必须3-30以内", 3, 30)
    })

    $('#registerForm').off("click", '#submit').on("click", '#submit', function() {
        var form = document.querySelector("#registerForm");
        var formData = new FormData(form)
        var username = formData.get("username")
        if(username == ''){
            return
        }
        var password = formData.get("password")
        if(password == ''){
            return
        }
        var email = formData.get("email")
        if(email == ''){
            return
        }
        var nickname = formData.get("nickname")
        if(nickname == ''){
            return
        }
        var phoneNumber = formData.get("phoneNumber")
        if(phoneNumber == ''){
            return
        }
        var birthday = formData.get("birthday")
        if(birthday == ''){
            return
        }
        register(formData)
        return
    })

    function checkInput(self, error, errorText, minLen, maxLen) {
        var input = $.trim(self.val())
        if(input == '' || input.length < minLen || input.length > maxLen){
            $("#submit").attr("disabled", true)
            error.text(errorText)
            return
        }
        $("#submit").attr("disabled", false)
        error.text("")
    }

    function checkPassword() {
        var pwd = $.trim($("#password1").val())
        //获取this，即ipwd的val()值，trim函数的作用是去除空格
        var rpwd = $.trim($("#password2").val())
        if(rpwd != ""){
            if(pwd == rpwd) {
                $("#password2Error").text("")
                $("#submit").attr("disabled", false)
            } else {
                $("#password2Error").text(" Oh snap ! 两次密码输入不一致")
                $("#submit").attr("disabled", true)
            }
        }
    }

    function register(formData) {
        var username = formData.get('username')
        var password = formData.get('password')
        $.ajax({
            url: registerUrl,
            type: "POST",
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (response) {
                if(response.success){
                    var loginForm = new FormData()
                    loginForm.append('username', username);
                    loginForm.append('password', password);
                    login(loginForm)
                }else{
                    alert(response.errorMsg)
                }
            }
        })
    }

    function login(formData) {
        $.ajax({
            url: loginUrl,
            type: "POST",
            contentType: false,
            processData: false,
            data: formData,
            success: function () {
                window.location.href = "/"
            },
            error :function (XMLHttpRequest, textStatus, errorThrown) {
                console.log(XMLHttpRequest)
                console.log(textStatus)
                console.log(errorThrown)
            }
        })
    }
})