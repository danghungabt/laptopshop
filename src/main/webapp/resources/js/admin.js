$(document).ready(function () {
    $(document).on('click', '#thong-ke-link', function (event) {
        $('#thong-ke-submenu').toggle();
        console.log("click");
    });
});