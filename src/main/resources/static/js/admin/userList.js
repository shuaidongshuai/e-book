$(function () {
    var _pageNum = 1;
    var _pageSize = 10;
    var findNameUrl = "/admin/findUsername";
    var lockUrl = "/admin/changeStatus";
    var roleUrl = "/admin/changeRole";

    // 根据用户名、页面索引、页面大小获取用户列表
    function getUserList(pageNum, pageSize) {
        $.ajax({
            url: findNameUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "pageNum": pageNum,
                "pageSize": pageSize,
                "username": $(".searchUsername").val()
            },
            success: function (data) {
                var prefix = data.toString().substring(0, 4);
                if(prefix == '<div'){
                    $("#userList").html(data);
                }
            }
        });
    }

    // 搜索
    $(".searchUsernameBtn").click(function () {
        getUserList(1, _pageSize);
    });
    //绑定回车键
    $('.searchUsername').bind('keydown', function (event) {
        if (event.keyCode == 13) {
            getUserList(1, _pageSize)
        }
    });

    // 分页
    $.tbpage("#userList", function (pageNum, pageSize) {
        getUserList(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    var userId;
    var userStatus;
    var userRole;
    //监听userList事件子事件，并绑定userStatus事件
    $("#userList").off("click", ".userStatus").on("click", ".userStatus", function () {
        userId = $(this).attr('userId')
        userStatus = $(this).attr('userStatus')
    });
    $("#userList").off("click", ".role").on("click", ".role", function () {
        userId = $(this).attr('userId')
        userRole = $(this).attr('role')
    });
    //不能监听userStatus事件，因为重新加载用户列表之后，新列表没有监听事件
    // $(".userStatus").click(function () {});


    //lockModel
    $('#lockModel').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var content = button.data('whatever') // Extract info from data-* attributes
        content = '是否确定' + content + '该用户'
        // modal.find('.modal-body input').val('是否确定' + content + '该用户')
        $(this).find('.modal-body p').text(content)
    });
    //roleModel
    $('#roleModel').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var content = button.data('whatever') // Extract info from data-* attributes
        content = '是否确定' + content
        $(this).find('.modal-body p').text(content)
    });


    $('.lockModelSubmit').click(function () {
        $.ajax({
            url: lockUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "userId": userId,
                "userStatus": userStatus
            },
            success: function (data) {
                if(data.success){
                    getUserList(_pageNum, _pageSize)
                } else {
                    alert(data.errorMsg)
                }
            }
        });
    });

    $('.roleModelSubmit').click(function () {
        $.ajax({
            url: roleUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "userId": userId,
                "userRole": userRole
            },
            success: function (data) {
                if(data.success){
                    getUserList(_pageNum, _pageSize)
                } else {
                    alert(data.errorMsg)
                }
            }
        });
    });
});