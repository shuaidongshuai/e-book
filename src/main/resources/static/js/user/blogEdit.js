$(function () {
    var blogImgUploadUrl = "/user/blogImgUpload"
    var blogSaveUrl = "/user/blogSave"
    var _value = ''
    var _render = ''
    var _title = ''
    var content = $("#content").attr("content")

    Vue.use(window['mavon-editor'])
    var vue = new Vue({
        'el': '#content',
        data:{
            value: content
        },
        template:'<mavon-editor v-model="value" ref=md @imgAdd="$imgAdd" @save="$save" style="min-height: 600px;z-index: 1000"></mavon-editor>',
        methods: {
            $imgAdd: function (pos, file) {
                uploadFile(pos, file, this.$refs.md)
            },
            $save: function (value, render) {
                submitCheck()
            }
        }
    })

    $("#blogSave").off("click").on("click", function () {
        submitCheck()
    });

    $("#submit").off("click").on("click", function () {
        $('.saveModal').modal('hide')
        blogSave()
    });

    function submitCheck() {
        _title = $("#title").val()
        if(_title == ''){
            //标题没写
            modalTitleFail()
            return
        }
        _value = vue.$refs.md.d_value
        _render = vue.$refs.md.d_render
        if(_value == '' || _render == ''){
            //内容没写
            modalContentFail()
            return
        }
        //请用户确认
        modalSaveCheck()
    }

    function uploadFile(pos, file, md) {
        $.ajax({
            url: blogImgUploadUrl,
            type: 'get',
            success: function (responseUploadDto) {
                if (responseUploadDto.success) {
                    var fileUrl = responseUploadDto.fileUrl
                    var blogImgHost = responseUploadDto.host
                    var formData = new FormData();   //这里连带form里的其他参数也一起提交了,如果不需要提交其他参数可以直接FormData无参数的构造函数
                    formData.append('OSSAccessKeyId', responseUploadDto.accessKeyId);
                    formData.append('policy', responseUploadDto.policy);
                    formData.append('signature', responseUploadDto.signature);
                    formData.append('key', responseUploadDto.filename);
                    formData.append('success_action_status', '200');
                    formData.append('file', file);  //append函数的第一个参数是后台获取数据的参数名,和html标签的input的name属性功能相同
                    $.ajax({
                        url: blogImgHost,
                        contentType: false,// 不设置内容类型
                        processData: false,//用于对data参数进行序列化处理 这里必须false
                        type: 'POST',
                        data: formData,
                        success: function () {
                            md.$img2Url(pos, fileUrl)
                        },
                        error: function () {
                            hintModalFail()
                        }
                    });
                } else {
                    hintModalFail()
                }
            },
            error: function () {
                hintModalFail()
            }
        })
    }
    
    function blogSave() {
        $.ajax({
            url: blogSaveUrl,
            type: 'post',
            data:{
                id: $("#blogId").val(),
                title: _title,
                content: _value,
                contentHtml: _render
            },
            success: function (responseUploadDto) {
                if (responseUploadDto.success) {
                    hintModalSuccess();
                    // 成功后，重定向
                    window.location = '/blog/' + responseUploadDto.blogId;
                } else {
                    hintModalFail()
                }
            },
            error: function () {
                hintModalFail()
            }
        })
    }

    function hintModalOpen() {
        $('.hintModal').modal('show');
        setTimeout("$('.hintModal').modal('hide')", 1000);
    }

    function hintModalSuccess() {
        $('.hintModal p').text('上传成功')
        hintModalOpen()
    }

    function hintModalFail() {
        $('.hintModal strong').text('Upload failure')
        $('.hintModal p').text('上传失败')
        hintModalOpen()
    }

    function modalTitleFail() {
        $('.hintModal strong').text('Prompt')
        $('.hintModal p').text('标题不能为空')
        hintModalOpen()
    }

    function modalContentFail() {
        $('.hintModal strong').text('Prompt')
        $('.hintModal p').text('内容不能为空')
        hintModalOpen()
    }
    
    function modalSaveCheck() {
        $('.saveModal strong').text('Prompt')
        $('.saveModal p').text('确定是否提交此次编辑')
        $('.saveModal').modal('show');
    }

})
