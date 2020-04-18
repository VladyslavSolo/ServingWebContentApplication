$(document).ready(function () {
    $(".updatebutton").click(function (evt) {
        evt.preventDefault();

        if($(".id").val() === ""){
            $('.result').html("Product isn't updated. Please select a product name");
            fail;
        }

        var categories = [];
        $(".categories option:selected").each(function (i, category) {
            categories.push({id: category.value});
        });
        if (typeof categories == "undefined"
            || categories.length == null
            || categories.length === 0) {
            $('.result').html("Product isn't updated. Please select a categories");
            fail;
        }

        var dollValue = $(".DOLLAR").val();
        var bynValue = $(".BYN").val();
        var euroValue = $(".EURO").val();

        var prices = [];
        if(dollValue === "" && bynValue === "" && euroValue === ""){
            $('.result').html("Product isn't updated. Please select a prices");
            fail;
        }

        if((dollValue) !== ""){
            prices.push({value: dollValue, currency: "DOLLAR"});
        }if((bynValue) !== ""){
            prices.push({value: bynValue, currency: "BYN"});
        }if((euroValue) !== ""){
            prices.push({value: euroValue, currency: "EURO"});
        }

        var product = {
            id: $(".id").val(),
            name: $(".name").val(),
            categories: categories,
            prices: prices
        };

        $.ajax({
            url: "/product",
            type: 'PUT',
            contentType: "application/json",
            data: JSON.stringify(product),
            success: function (data) {
                $('.result').html("success");
            },
            error: function (data, errorThrown) {
                $('.result').html("error");
                alert(errorThrown);
            }
        });
    });
});
