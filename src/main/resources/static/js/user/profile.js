$(function () {

    function openModal() {
        $('.saveSuccess').modal('show');
        setTimeout("$('.saveSuccess').modal('hide')", 1500);
    }

    var afterSave = $('.saveSuccess').attr('afterSave')
    if(afterSave){
        openModal()
    }



})