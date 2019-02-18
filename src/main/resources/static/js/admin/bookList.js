// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var getBookUrl = '/admin/getBook'
    var saveBookUrl = '/admin/saveBook'
    var bookListLikeUrl = '/admin/bookListLike'
    var delBookUrl = '/admin/delBook'
    var delBookId = null

    $("#managerSearch input").attr("placeholder", "输入书名进行搜索")

    // 搜索
    $("#managerSearch").off("click", "a").on("click", "a", function () {
        bookListLike(1, _pageSize);
    });
    //绑定回车键
    $("#managerSearch").off("keydown", "input").on("keydown", "input", function (event) {
        if (event.keyCode == 13) {
            bookListLike(1, _pageSize)
        }
    });

    //升降序排列
    $("#rightContainer").off("change", "#desc").on("change", "#desc", function () {
        if($(this).val() == '1'){
            _desc = true
        }else{
            _desc = false
        }
        bookListLike(_pageNum, _pageSize);
    })

    //修改图书
    $("#bookList").off("click", ".modifyButton").on("click", ".modifyButton", function () {
        var id = $(this).attr("bookId")
        getBook(id)
    })
    $("#modifyModel").off("click", ".submit").on("click", ".submit", function () {
        saveBook()
    })

    //删除图书
    $("#bookList").off("click", ".delButton").on("click", ".delButton", function () {
        delBookId = $(this).attr("bookId")
    })
    $("#delModel").off("click", ".submit").on("click", ".submit", function () {
        delBook()
    })

    // 分页
    $.tbpage("#bookList", function (pageNum, pageSize) {
        bookListLike(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function bookListLike(pageNum, pageSize) {
        $.ajax({
            url: bookListLikeUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "pageNum": pageNum,
                "pageSize": pageSize,
                "desc": _desc,
                "query": $("#managerSearch input").val()
            },
            success: function (data) {
                var prefix = data.toString().substring(0, 4);
                if(prefix == '<div'){
                    $("#bookList").html(data);
                }
            }
        });
    }

    function getBook(id) {
        $.ajax({
            url: getBookUrl,
            type: "GET",
            data:{
                id: id
            },
            success: function (responseBookDto) {
                if(responseBookDto.success){
                    var bookDto = responseBookDto.bookDto
                    $("#bookForm .id").val(bookDto.id)
                    $("#bookForm .name").val(bookDto.name)
                    $("#bookForm .introduction").val(bookDto.introduction)
                    $("#bookForm .catalog").val(bookDto.catalog)
                    $("#bookForm .bookTypeId").val(bookDto.bookTypeId)
                    $("#bookForm .fileUrl").val(bookDto.fileUrl)
                    $("#bookForm .coverUrl").val(bookDto.coverUrl)
                } else {
                    alert(responseBookDto.errorMsg)
                }
            }
        });
    }

    function saveBook() {
        var form = document.querySelector("#bookForm");
        var formData = new FormData(form)
        var name = formData.get("name")
        if(name == null || name == ''){
            $("#bookName").addClass("has-danger")
            return
        }
        $("#bookName").removeClass("has-danger")

        var introduction = formData.get("introduction")
        if(introduction == null || introduction == ''){
            $("#bookIntroduction").addClass("has-danger")
            return
        }
        $("#bookIntroduction").removeClass("has-danger")

        var fileUrl = formData.get("fileUrl")
        if(fileUrl == null || fileUrl == ''){
            $("#bookUrl").addClass("has-danger")
            return
        }
        $("#bookUrl").removeClass("has-danger")

        var coverUrl = formData.get("coverUrl")
        if(coverUrl == null || coverUrl == ''){
            $("#bookCoverUrl").addClass("has-danger")
            return
        }
        $("#bookCoverUrl").removeClass("has-danger")

        $.ajax({
            url: saveBookUrl,
            type: "POST",
            contentType: false,// 不设置内容类型
            processData: false,//用于对data参数进行序列化处理 这里必须false
            data: formData,
            success: function (responseBookDto) {
                if(responseBookDto.success){
                    $("#modifyModel").modal("hide")
                    bookListLike(_pageNum, _pageSize)
                } else {
                    alert(responseBookDto.errorMsg)
                }
            }
        });
    }

    function delBook() {
        $.ajax({
            url: delBookUrl + '/' + delBookId,
            type: "delete",
            success: function (response) {
                if(response.success){
                    bookListLike(_pageNum, _pageSize)
                } else {
                    alert(response.errorMsg)
                }
            }
        });
    }

});