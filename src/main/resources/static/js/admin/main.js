// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var pageViewUrl = '/admin/getPageView'
    var hotWordsUrl = '/admin/hotWords'

	//访问量-图表
    $.ajax({
        url: pageViewUrl,
        type: "GET",
        success: function(response){
            if(response.success){
                var data = []
                var pageViewDtoList = response.pageViewDtoList
                for(let pageView of pageViewDtoList){
                    var date = new Date(pageView.createTime);
                    data.push([date.getHours(), pageView.number]);
                }
                initChart(data)
            }else{
                alert(response.errorMsg)
            }
        }
    })

    // 分页
    $.tbpage("#hotWordsList", function (pageNum, pageSize) {
        hotWordsList(pageNum, pageSize)
        _pageNum = pageNum
        _pageSize = pageSize
    });

    function initChart(data) {
        var options = {
            series: {
                lines: {
                    show: true
                },
                points: {
                    show: true
                }
            },
            grid: {
                hoverable: true //IMPORTANT! this is needed for tooltip to work
            },
            colors: ["#009efb", "#55ce63"],
            grid: {
                color: "#AFAFAF",
                hoverable: true,
                borderWidth: 0,
                backgroundColor: '#FFF'
            },
            tooltip: true,
            tooltipOpts: {
                content: "%x.0点 ❤ %y.0次",
                defaultTheme: false
            }
        };

        var plot = $.plot($("#rightContainer #pageView"), [{
            data: data,
            label: "访问量",
        }], options);

        // plot.draw();
    }

    function hotWordsList(pageNum, pageSize) {
        $.ajax({
            url: hotWordsUrl,
            type: "GET",
            contentType: "application/json",
            data: {
                "pageNum": pageNum,
                "pageSize": pageSize,
                "desc": _desc,
            },
            success: function (data) {
                var prefix = data.toString().substring(0, 4);
                if(prefix == '<div'){
                    $("#hotWordsList").html(data);
                }
            }
        });
    }

});