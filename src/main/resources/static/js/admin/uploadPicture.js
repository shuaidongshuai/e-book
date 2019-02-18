// DOM 加载完再执行
$(function () {
    var curUrl = '/admin/uploadPicture'
    var getPictureServerUrl = '/admin/getPictureServer'
    var savePictureUrl = '/admin/savePicture'
    var fileUrls = new Array()

    // https://www.22vd.com/34844.html
    $('#ssi-upload').ssi_uploader({
        url: getPictureServerUrl,
        maxFileSize: 6,
        allowed: ['jpg', 'gif', 'jpeg', 'png', 'bmp'],
        // beforeUpload: function () {
        //     console.log('文件上传准备就绪！');
        // },
        onEachUpload:function(fileInfo){
            fileUrls.push(fileInfo.fileUrl)
        },
        onUpload:function(){
            // 所有文件上传完毕，保存picture到服务器
            // savePicture()
        }
    });

    $("#pictureSubmit").off("click").on("click", function () {
        savePicture()
    })

    function savePicture() {
        var form = document.querySelector("#pictureForm");
        var formData = new FormData(form)
        var title = formData.get("title")
        console.log(title)
        if(title == null || title == ''){
            $("#pictureTitle").addClass("has-danger")
            return
        }
        $("#pictureTitle").removeClass("has-danger")
        if(fileUrls == null || fileUrls.length == 0){
            alert("未上传文件")
            return
        }
        formData.append("urls", fileUrls)
        $.ajax({
            url: savePictureUrl,
            type: "POST",
            traditional: true,//不让jquery深度序列化参数对象
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (responseCommonDto) {
                if(responseCommonDto.success){
                    saveSuccess()
                }else{
                    alert(responseCommonDto.errorMsg)
                }
            },
            error: function () {
                alert("应用服务器错误")
            }
        })
    }
    
    //保存成功
    function saveSuccess() {
        //刷新页面
        $.refresh(curUrl)
    }
});