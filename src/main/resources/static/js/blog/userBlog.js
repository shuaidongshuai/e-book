$(function() {
    var concernUrl = '/user/concern/' + $("#blog").attr("userId")
    var cancelConcernUrl = '/user/cancelConcern/' + $("#blog").attr("userId")
    var voteUrl = '/user/vote/' + $("#blog").attr("blogId")
    var cancelVoteUrl = '/user/cancelVote/' + $("#blog").attr("blogId")
    var contentHtml = $("#blogContent").attr('data')
    $("#blogContent").html(contentHtml)

    //关注
    var exist = ("#selfBlog")
    if(exist){
        var concern = $("#selfBlog").attr("concern")
        if(concern == "true"){
            //关注过了 打开“已关注”
            $("#concerned").toggle()
        } else {
            $("#concern").toggle()
        }
    }

    //点赞
    var vote = $("#vote").attr("vote")
    if(vote == "true"){
        $("#voteed").toggle()
    } else {
        $("#vote").toggle()
    }

    $("#concern").off("click").on("click", function () {
        $.ajax({
            url: concernUrl,
            type: 'put',
            success: function (response) {
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    modalOpen()
                } else {
                    if(response.success){
                        $("#concern").toggle()
                        $("#concerned").toggle()
                    } else {
                        alert(response.errorMsg)
                    }
                }
            }
        })
    });

    $("#concerned").off("click").on("click", function () {
        $.ajax({
            url: cancelConcernUrl,
            type: 'delete',
            success: function (response) {
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    modalOpen()
                } else {
                    if(response.success){
                        $("#concern").toggle()
                        $("#concerned").toggle()
                    } else{
                        alert(response.errorMsg)
                    }
                }
            }
        })
    });

    $("#vote").off("click").on("click", function () {
        $.ajax({
            url: voteUrl,
            type: 'put',
            success: function (response) {
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    modalOpen()
                } else {
                    if(response.success){
                        $("#vote").toggle()
                        $("#voteed").toggle()
                    } else{
                        alert(response.errorMsg)
                    }
                }
            }
        })
    });

    $("#voteed").off("click").on("click", function () {
        $.ajax({
            url: cancelVoteUrl,
            type: 'delete',
            success: function (response) {
                var prefix = response.toString().substring(0, 1);
                if(prefix == '<'){
                    //说明没有登录
                    modalOpen()
                } else {
                    if(response.success){
                        $("#vote").toggle()
                        $("#voteed").toggle()
                    } else{
                        alert(response.errorMsg)
                    }
                }
            }
        })
    });

    function modalOpen() {
        $('.hintModal').modal('show');
        setTimeout("$('.hintModal').modal('hide')", 1000);
    }
})