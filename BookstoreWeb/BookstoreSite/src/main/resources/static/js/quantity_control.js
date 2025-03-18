$(document).ready(function() {
    $(".linkMinus").on("click", function(evt) {
        evt.preventDefault();
        var productId = $(this).attr("pid");
        var quantityInput = $("#quantity" + productId);
        var maxQuantity = parseInt($(this).attr("maxQuantity")); // Lấy số lượng tối đa từ thuộc tính
        var newQuantity = parseInt(quantityInput.val()) - 1;

        if (newQuantity >= 1) { // Đảm bảo số lượng không thấp hơn 1
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Số lượng tối thiểu là 1');
        }
    });

    $(".linkPlus").on("click", function(evt) {
        evt.preventDefault();
        var productId = $(this).attr("pid");
        var quantityInput = $("#quantity" + productId);
        var maxQuantity = parseInt($(this).attr("maxQuantity")); // Lấy số lượng tối đa từ thuộc tính
        var newQuantity = parseInt(quantityInput.val()) + 1;

        if (newQuantity <= maxQuantity) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Số lượng tối đa là ' + maxQuantity);
        }
    });
});
