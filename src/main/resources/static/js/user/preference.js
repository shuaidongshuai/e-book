(function ($) {
    $.preferenceInit = function () {
        // http://www.htmleaf.com/jQuery/Form/201808095268.html
        var inputs = $(".input")
        for (var idx = inputs.length - 1; idx >= 0; idx--) {
            var input = inputs[idx];
            var num = idx % 4
            if(num == 0){
                input.classList.add("input--primary")
            } else if(num == 1){
                input.classList.add("input--secondary")
            } else if(num == 2){
                input.classList.add("input--success")
            } else if(num == 3){
                input.classList.add("input--danger")
            }
        }

        /* TODO: prevent this timeout */
        setTimeout(removePreload, 500);
        function removePreload() {
            var preloadElements = document.getElementsByClassName('preload');
            for (var preloadIndex = preloadElements.length - 1; preloadIndex >= 0; preloadIndex--) {
                var preload = preloadElements[preloadIndex];
                preload.classList.remove('preload');
            }
        }

        // http://www.htmleaf.com/html5/SVG/201602253145.html
        var entries = [];
        var classNames = $('.className')
        var checkboxs = $('.checkbox')
        for (var idx = classNames.length - 1; idx >= 0; idx--) {
            if(checkboxs[idx].checked){
                entries[idx] = { label: classNames[idx].textContent, url: '#', target: '_top' }
            }else{
                entries[idx] = { label: '', url: '#', target: '_top' }
            }
        }

        var settings = {
            entries: entries,
            width: 500,
            height: 400,
            radius: '65%',
            radiusMin: 75,
            bgDraw: false,//是否使用背景色
            bgColor: '',//背景颜色
            opacityOver: 1.00,
            opacityOut: 0.05,
            opacitySpeed: 6,
            fov: 800,
            speed: 0.5,
            fontFamily: 'Oswald, Arial, sans-serif',
            fontSize: '15',
            fontColor: '#fff',
            fontWeight: 'normal',//bold
            fontStyle: 'normal',//italic
            fontStretch: 'normal',//wider, narrower, ultra-condensed, extra-condensed, condensed, semi-condensed, semi-expanded, expanded, extra-expanded, ultra-expanded
            fontToUpperCase: true

        };

        //var svg3DTagCloud = new SVG3DTagCloud( document.getElementById( 'holder'  ), settings );
        $( '#tag-cloud' ).svg3DTagCloud( settings );
    }

    $.addTag = function (url, idx, className) {
        var _idx = idx
        var _className = className
        $.ajax({
            url: url,
            type: 'PUT',
            success: function (response) {
                if(response.success){
                    $('#tag-cloud a text')[_idx].textContent = _className
                }else{
                    alert(response.errorMsg)
                }
            },
            error: function () {
                alert("标签添加失败，服务器错误")
            }
        })
    }

    $.delTag = function (url, idx) {
        var _idx = idx
        $.ajax({
            url: url,
            type: 'DELETE',
            success: function (response) {
                if(response.success){
                    $('#tag-cloud a text')[_idx].textContent = ''
                }else{
                    alert(response.errorMsg)
                }
            },
            error: function () {
                alert("标签删除失败，服务器错误")
            }
        })
    }
})(jQuery)