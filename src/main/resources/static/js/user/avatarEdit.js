$(function () {
    var uploadAvatar = '/user/uploadAvatar'
    var saveAvatar = '/user/saveAvatar'

    //裁剪框开关
    function switchTailor() {
        $(".tailoring-container").toggle();
    }

    $(".blog-content-container").off("click", ".avatar-edit").on("click", ".avatar-edit", function () {
        switchTailor();
    });

    $(".close-tailoring").off("click").on("click", "", function () {
        switchTailor();
    });

    //弹出框水平垂直居中
    (window.onresize = function () {
        var win_height = $(window).height();
        var win_width = $(window).width();
        if (win_width <= 768) {
            $(".tailoring-content").css({
                "top": (win_height - $(".tailoring-content").outerHeight()) / 2,
                "left": 0
            });
        } else {
            $(".tailoring-content").css({
                "top": (win_height - $(".tailoring-content").outerHeight()) / 2,
                "left": (win_width - $(".tailoring-content").outerWidth()) / 2
            });
        }
    })();

    //弹出图片裁剪框
    $("#replaceImg").off("click").on("click", function () {
        $(".tailoring-container").toggle();
    });

    //图像上传
    function selectImg(file) {
        if (!file.files || !file.files[0]) {
            return;
        }
        var reader = new FileReader();
        reader.onload = function (evt) {
            var replaceSrc = evt.target.result;
            //更换cropper的图片
            $('#tailoringImg').cropper('replace', replaceSrc, false);//默认false，适应高度，不失真
        }
        reader.readAsDataURL(file.files[0]);
    }

    $(".selectImg").off("click").on("change", "", function () {
        selectImg(this);
    });

    //cropper图片裁剪
    $('#tailoringImg').cropper({
        aspectRatio: 1 / 1,//默认比例
        preview: '.previewImg',//预览视图
        guides: false,  //裁剪框的虚线(九宫格)
        autoCropArea: 0.5,  //0-1之间的数值，定义自动剪裁区域的大小，默认0.8
        movable: false, //是否允许移动图片
        dragCrop: true,  //是否允许移除当前的剪裁框，并通过拖动来新建一个剪裁框区域
        movable: true,  //是否允许移动剪裁框
        resizable: true,  //是否允许改变裁剪框的大小
        zoomable: false,  //是否允许缩放图片大小
        mouseWheelZoom: false,  //是否允许通过鼠标滚轮来缩放图片
        touchDragZoom: true,  //是否允许通过触摸移动来缩放图片
        rotatable: true,  //是否允许旋转图片
        crop: function (e) {
            // console.log(e)
        }
    });
    //旋转
    $(".cropper-rotate-btn").off("click").on("click", function () {
        $('#tailoringImg').cropper("rotate", 45);
    });
    //复位
    $(".cropper-reset-btn").off("click").on("click", function () {
        $('#tailoringImg').cropper("reset");
    });
    //换向
    var flagX = true;
    $(".cropper-scaleX-btn").off("click").on("click", function () {
        if (flagX) {
            $('#tailoringImg').cropper("scaleX", -1);
            flagX = false;
        } else {
            $('#tailoringImg').cropper("scaleX", 1);
            flagX = true;
        }
        flagX != flagX;
    });

    //裁剪后的处理
    $("#sureCut").off("click").on("click", function () {
        if ($("#tailoringImg").attr("src") == null) {
            return false;
        } else {
            var cas = $('#tailoringImg').cropper('getCroppedCanvas');//获取被裁剪后的canvas
            // var base64url = cas.toDataURL('image/*'); //转换为base64地址形式
            // $("#finalImg").prop("src", base64url);//显示为图片的形式
            //上传到服务器
            // uploadFile(encodeURIComponent(base64url));
            uploadFile(cas);
            //关闭裁剪框
            switchTailor();
        }
    });

    //ajax请求上传
    function uploadFile(cas) {
        //获取服务器参数
        $.ajax({
            url: uploadAvatar,
            type: 'get',
            success: function (responseUploadDto) {
                if (responseUploadDto.success) {
                    var fileUrl = responseUploadDto.fileUrl
                    var avatarHost = responseUploadDto.host
                    cas.toBlob(function (blob) {
                        var formData = new FormData();   //这里连带form里的其他参数也一起提交了,如果不需要提交其他参数可以直接FormData无参数的构造函数
                        formData.append('OSSAccessKeyId', responseUploadDto.accessKeyId);
                        formData.append('policy', responseUploadDto.policy);
                        formData.append('signature', responseUploadDto.signature);
                        formData.append('key', responseUploadDto.filename);
                        formData.append('success_action_status', '200');
                        formData.append('file', blob);  //append函数的第一个参数是后台获取数据的参数名,和html标签的input的name属性功能相同
                        $.ajax({
                            url: avatarHost,
                            contentType: false,// 不设置内容类型
                            processData: false,//用于对data参数进行序列化处理 这里必须false
                            type: 'POST',
                            data: formData,
                            success: function (data) {
                                //把文件url同步给后台
                                $.ajax({
                                    url: saveAvatar,
                                    type: 'get',
                                    data: {
                                        'fileUrl': fileUrl
                                    },
                                    success: function () {
                                        modalSuccess()
                                        //由于在img的src中增加了随机数参数，多次访问图片时，浏览器认为是访问了不同的图片路径(或者说是访问了不同的图片），
                                        //浏览器会每次重新访问服务器读取图片， 而不再读取缓存中的图片。
                                        $(".userAvatarImg").attr('src', fileUrl + '?t=' + Math.random());
                                    },
                                    error: function () {
                                        modalFail()
                                    }
                                })
                            },
                            error: function (XMLHttpRequest, textStatus) {
                                // console.log(XMLHttpRequest.status)
                                // console.log(XMLHttpRequest.readyState)
                                modalFail()
                            }
                        });
                    });
                } else {
                    modalFail()
                }
            },
            error: function () {
                modalFail()
            }
        })
    }

    function modalOpen() {
        $('.saveSuccess').modal('show');
        setTimeout("$('.saveSuccess').modal('hide')", 1500);
    }

    function modalSuccess() {
        $('.saveSuccess p').text('上传成功')
        modalOpen()
    }

    function modalFail() {
        $('.saveSuccess strong').text('Upload failure')
        $('.saveSuccess p').text('上传失败')
        modalOpen()
    }
});