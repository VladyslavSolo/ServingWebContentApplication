$(document).ready(function () {
    $(".deletebutton").click(function (evt) {
        evt.preventDefault();

        var product = {
            id: evt.currentTarget.id
        };

        $.ajax({
            url: "/product",
            type: 'DELETE',
            contentType: "application/json",
            data: JSON.stringify(product),
            success: function (data) {
                window.location.href = "/";
            },
            error: function (data, errorThrown) {
                alert(errorThrown);
            }
        });
    });
});
