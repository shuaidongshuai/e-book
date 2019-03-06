// DOM 加载完再执行
$(function() {
    var _pageNum = 1
    var _pageSize = 10
    var _desc = true
    var pageViewUrl = '/admin/getPageView'
    var hotWordsUrl = '/admin/hotWords'
    var _dateStr = null

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
                    if(_dateStr == null){
                        var curr_date = date.getDate();
                        var curr_month = date.getMonth() + 1;
                        var curr_year = date.getFullYear();
                        String(curr_month).length < 2 ? (curr_month = "0" + curr_month): curr_month;
                        String(curr_date).length < 2 ? (curr_date = "0" + curr_date): curr_date;
                        _dateStr= curr_year + "年" + curr_month +"月"+ curr_date + "日";
                    }
                    data.push([date.getHours(), pageView.number]);
                }
                initChart(data)
            }else{
                alert(response.errorMsg)
            }
        }
    })

    // $("#pageView").bind("plothover", function (event, pos, item) {
    //     if (item) {
    //         console.log(item)
    //         console.log(pos)
    //         $("#tooltip").remove();
    //         var x = item.datapoint[0], y = item.datapoint[1];
    //         showTooltip(item.pageX, item.pageY, item.series.label + "[" + x + "] : " + y);
    //     }
    // });

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

        var plot = $.plot($("#pageView"), [{
            data: data,
            label: "访问量-" + _dateStr,
        }], options);

        // plot.draw();
    }

    // function showTooltip(x, y, contents) {
    //     $('<div id="tooltip">' + contents + '</div>').css( {
    //         position: 'absolute',
    //         display: 'none',
    //         top: y + 10,
    //         left: x + 10,
    //         border: '1px solid #fdd',
    //         padding: '2px',
    //         'background-color': '#dfeffc',
    //         opacity: 0.80
    //     }).appendTo("body").fadeIn(200);
    // }

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