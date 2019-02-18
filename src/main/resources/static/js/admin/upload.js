// DOM 加载完再执行
$(function() {
    var bookServerUrl = '/admin/uploadBook'
    var videoServerUrl = '/admin/uploadVideo'
    var musicServerUrl = '/admin/uploadMusic'
    var pictureServerUrl = '/admin/uploadPicture'
    var uploadAdaptUrl = '/admin/uploadAdapt'
    var saveBookUrl = '/admin/saveBook'
    var saveVideoUrl = '/admin/saveVideo'
    var saveMusicUrl = '/admin/saveMusic'
    var savePictureUrl = '/admin/savePicture'

    var getHostUrl = null
    var saveFileUrl = null

    var bookUrl = null
    var videoUrl = null
    var musicUrl = null
    var pictureUrls = new Array()
    var bookCoverUrl = null
    var videoCoverUrl = null
    var musicCoverUrl = null

    var curUploadFile = null
    var curUploadFilename = null

    var curFileType = null
    var _el = null

    /**
     * http://www.htmleaf.com/jQuery/Form/201511052749.html
     */
    var filer_default_opts = {
        changeInput2: '<div class="jFiler-input-dragDrop"><div class="jFiler-input-inner"><div class="jFiler-input-icon"><i class="icon-jfi-cloud-up-o"></i></div><div class="jFiler-input-text"><h3>Drag&Drop files here</h3> <span style="display:inline-block; margin: 15px 0">or</span></div><a class="jFiler-input-choose-btn blue-light">Browse Files</a></div></div>',
        limit: null,
        maxSize: 500,
        templates: {
            box: '<ul class="jFiler-items-list jFiler-items-grid"></ul>',
            item: '<li class="jFiler-item" style="width: 49%">\
                            <div class="jFiler-item-container">\
                                <div class="jFiler-item-inner">\
                                    <div class="jFiler-item-thumb">\
                                        <div class="jFiler-item-status"></div>\
                                        <div class="jFiler-item-info">\
                                            <span class="jFiler-item-title"><b title="{{fi-name}}">{{fi-name | limitTo: 25}}</b></span>\
                                            <span class="jFiler-item-others">{{fi-size2}}</span>\
                                        </div>\
                                        {{fi-image}}\
                                    </div>\
                                    <div class="jFiler-item-assets jFiler-row">\
                                        <ul class="list-inline pull-left">\
                                            <li>{{fi-progressBar}}</li>\
                                        </ul>\
                                        <ul class="list-inline pull-right">\
                                            <li><a class="icon-jfi-trash jFiler-item-trash-action"></a></li>\
                                        </ul>\
                                    </div>\
                                </div>\
                            </div>\
                        </li>',
            itemAppend: '<li class="jFiler-item" style="width: 49%">\
                                <div class="jFiler-item-container">\
                                    <div class="jFiler-item-inner">\
                                        <div class="jFiler-item-thumb">\
                                            <div class="jFiler-item-status"></div>\
                                            <div class="jFiler-item-info">\
                                                <span class="jFiler-item-title"><b title="{{fi-name}}">{{fi-name | limitTo: 25}}</b></span>\
                                                <span class="jFiler-item-others">{{fi-size2}}</span>\
                                            </div>\
                                            {{fi-image}}\
                                        </div>\
                                        <div class="jFiler-item-assets jFiler-row">\
                                            <ul class="list-inline pull-left">\
                                                <li><span class="jFiler-item-others">{{fi-icon}}</span></li>\
                                            </ul>\
                                            <ul class="list-inline pull-right">\
                                                <li><a class="icon-jfi-trash jFiler-item-trash-action"></a></li>\
                                            </ul>\
                                        </div>\
                                    </div>\
                                </div>\
                            </li>',
            progressBar: '<div class="bar"></div>',
            itemAppendToEnd: false,
            removeConfirmation: true,
            _selectors: {
                list: '.jFiler-items-list',
                item: '.jFiler-item',
                progressBar: '.bar',
                remove: '.jFiler-item-trash-action'
            }
        },
        dragDrop: {},
        uploadFile: {
            url: "",
            type: 'put',
            data: {},
            beforeSend: function(){},
            success: function(data, el){
            },
            error: function(el){
                uploadFail(el)
            },
            statusCode: null,
            onProgress: null,
            onComplete: null
        },
        onRemove: function(itemEl, file, id, listEl, boxEl, newInputEl, inputEl){
            // var file = file.name;
        },
    };

    var uploadType = {
        uploadBook: {
            url: uploadAdaptUrl,
            type: 'put',
            data: {},
            beforeSend: function(){},
            success: function(data, el){
                _el = el
                getHostUrl = bookServerUrl
                saveFileUrl = saveBookUrl
                curFileType = "book"
                //拖拽的图片特殊
                if($("#bookFile")[0].files.length == 0){
                    curUploadFile = $("#bookFile")[0].jFiler.files[0]
                    curUploadFilename = $("#bookFile")[0].jFiler.files[0].name
                } else {
                    curUploadFile = $("#bookFile")[0].files[0]
                    curUploadFilename = $("#bookFile")[0].files[0].name
                }
                uploadFile()
            },
            error: function(el){
                uploadFail(el)
            }
        },
        uploadVideo: {
            url: uploadAdaptUrl,
            type: 'put',
            data: {},
            beforeSend: function(){},
            success: function(data, el){
                _el = el
                getHostUrl = videoServerUrl
                saveFileUrl = saveVideoUrl
                curFileType = "video"
                if($("#videoFile")[0].files.length == 0){
                    curUploadFile = $("#videoFile")[0].jFiler.files[0]
                    curUploadFilename = $("#videoFile")[0].jFiler.files[0].name
                } else {
                    curUploadFile = $("#videoFile")[0].files[0]
                    curUploadFilename = $("#videoFile")[0].files[0].name
                }
                uploadFile()
            },
            error: function(el){
                uploadFail(el)
            },
        },
        uploadMusic: {
            url: uploadAdaptUrl,
            type: 'put',
            data: {},
            beforeSend: function(){},
            success: function(data, el){
                _el = el
                getHostUrl = musicServerUrl
                saveFileUrl = saveMusicUrl
                curFileType = "music"
                if($("#musicFile")[0].files.length == 0){
                    curUploadFile = $("#musicFile")[0].jFiler.files[0]
                    curUploadFilename = $("#musicFile")[0].jFiler.files[0].name
                } else {
                    curUploadFile = $("#musicFile")[0].files[0]
                    curUploadFilename = $("#musicFile")[0].files[0].name
                }
                uploadFile()
            },
            error: function(el){
                uploadFail(el)
            },
        },
        uploadPicture: {
            url: uploadAdaptUrl,
            type: 'put',
            data: {},
            beforeSend: function(){},
            success: function(data, el){
                _el = el
                getHostUrl = pictureServerUrl
                saveFileUrl = savePictureUrl
                curFileType = "picture"
                if($("#pictureFile")[0].files.length == 0){
                    curUploadFile = $("#pictureFile")[0].jFiler.files[0]
                    curUploadFilename = $("#pictureFile")[0].jFiler.files[0].name
                } else {
                    curUploadFile = $("#pictureFile")[0].files[0]
                    curUploadFilename = $("#pictureFile")[0].files[0].name
                }
                uploadFile()
            },
            error: function(el){
                uploadFail(el)
            },
        }
    }

    $('#bookFile').filer({
        changeInput: filer_default_opts.changeInput2,
        limit: 1,
        showThumbs: true,
        theme: "dragdropbox",
        templates: filer_default_opts.templates,
        dragDrop: filer_default_opts.dragDrop,
        uploadFile: uploadType.uploadBook,
        onRemove: filer_default_opts.onRemove,
        clipBoardPaste: true
    });

    $('#uploadBookCover').filer({
        changeInput: filer_default_opts.changeInput2,
        limit: 1,
        showThumbs: true,
        theme: "dragdropbox",
        templates: filer_default_opts.templates,
        dragDrop: filer_default_opts.dragDrop,
        uploadFile: uploadType.uploadBook,
        onRemove: filer_default_opts.onRemove
    });

    //获取服务器参数
    function uploadFile() {
        $.ajax({
            url: getHostUrl,
            type: 'get',
            data: {
                filename: curUploadFilename
            },
            // async: false, //不让异步执行
            success: function (responseUploadDto) {
                if (responseUploadDto.success) {
                    var fileUrl = responseUploadDto.fileUrl
                    var fileServerHost = responseUploadDto.host
                    var formData = new FormData();
                    formData.append('OSSAccessKeyId', responseUploadDto.accessKeyId);
                    formData.append('policy', responseUploadDto.policy);
                    formData.append('signature', responseUploadDto.signature);
                    formData.append('key', responseUploadDto.filename);
                    formData.append('success_action_status', '200');
                    formData.append('file', curUploadFile);
                    //上传到文件服务器
                    $.ajax({
                        url: fileServerHost,
                        contentType: false,// 不设置内容类型
                        processData: false,//用于对data参数进行序列化处理 这里必须false
                        type: 'POST',
                        data: formData,
                        success: function () {
                            //修改fileUrl
                            if(curFileType == "book"){
                                bookUrl = fileUrl
                            } else if(curFileType == "video"){
                                videoUrl = fileUrl
                            } else if(curFileType == "music"){
                                musicUrl = fileUrl
                            } else if(curFileType == "picture"){
                                pictureUrls.add(fileUrl)
                            } else if(curFileType == "bookCover"){
                                bookCoverUrl = fileUrl
                            } else if(curFileType == "videoCover"){
                                videoCoverUrl = fileUrl
                            } else if(curFileType == "musicCover"){
                                musicCoverUrl = fileUrl
                            }
                            uploadSuccess(_el)
                        },
                        error: function () {
                            alert("上传到文件服务器失败")
                            uploadFail(_el)
                        }
                    });
                } else {
                    alert(responseUploadDto.errorMsg)
                    uploadFail(_el)
                }
            },
            error: function () {
                alert("应用服务器适配错误")
                uploadFail(_el)
            }
        })
    }

    function saveBook() {
        $.ajax({
            url: saveFileUrl,
            type: 'POST',
            data: {

            },
            success: function (responseCommonDto) {
                if(responseCommonDto.success){

                }else{
                    alert(responseCommonDto.errorMsg)
                    uploadFail(_el)
                }
            },
            error: function () {
                alert("上传到应用服务器失败")
                uploadFail(_el)
            }
        });
    }

    function uploadSuccess(el) {
        var parent = el.find(".jFiler-jProgressBar").parent();
        el.find(".jFiler-jProgressBar").fadeOut("slow", function(){
            $("<div class=\"jFiler-item-others text-success\"><i class=\"icon-jfi-check-circle\"></i> 上传成功</div>").hide().appendTo(parent).fadeIn("slow");
        });
    }

    function uploadFail(el) {
        var parent = el.find(".jFiler-jProgressBar").parent();
        el.find(".jFiler-jProgressBar").fadeOut("slow", function(){
            $("<div class=\"jFiler-item-others text-error\"><i class=\"icon-jfi-minus-circle\"></i> 上传失败</div>").hide().appendTo(parent).fadeIn("slow");
        });
    }
});