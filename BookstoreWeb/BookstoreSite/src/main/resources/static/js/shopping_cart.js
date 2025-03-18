$(document).ready(function () {
    $(".linkMinus").on("click", function (evt) {
        evt.preventDefault();
        decreaseQuantity($(this));
    });

    $(".linkPlus").on("click", function (evt) {
        evt.preventDefault();
        increaseQuantity($(this));
    });

    $(".linkRemove").on("click", function (evt) {
        evt.preventDefault();
        removeProduct($(this));
    });
});

function decreaseQuantity(link) {
    var productId = link.attr("pid");
    var quantityInput = $("#quantity" + productId);
    var maxQuantity = parseInt(link.attr("maxQuantity")); // Lấy số lượng tối đa từ thuộc tính
    var newQuantity = parseInt(quantityInput.val()) - 1;

    if (newQuantity >= 1) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Số lượng tối thiểu là 1');
    }
}

function increaseQuantity(link) {
    var productId = link.attr("pid");
    var quantityInput = $("#quantity" + productId);
    var maxQuantity = parseInt(link.attr("maxQuantity")); // Lấy số lượng tối đa từ thuộc tính
    var newQuantity = parseInt(quantityInput.val()) + 1;

    if (newQuantity <= maxQuantity) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Số lượng tối đa là ' + maxQuantity);
    }
}

function updateQuantity(productId, quantity) {
    var url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (updatedSubtotal) {
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function () {
        showErrorModal("Lỗi khi cập nhật số lượng sản phẩm.");
    });
}

function updateSubtotal(updatedSubtotal, productId) {
    var formattedNumber = $.number(updatedSubtotal, 0, '.', ',');
    $("#subtotal" + productId).text(formattedNumber);
}

function updateTotal() {
    var total = 0.0;
    var productCount = 0;
    $(".subtotal").each(function (index, element) {
        productCount++;
        total += parseFloat(element.innerHTML.replaceAll(",", ""));
    });

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        var formattedTotal = $.number(total, 0, '.', ',');
        $("#total").text(formattedTotal);
    }
}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
    var url = link.attr("href");

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        var rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();
        updateCountNumbers();

        showModalDialog("Giỏ hàng", response);
    }).fail(function () {
        showErrorModal("Lỗi khi xóa sản phẩm.");
    });
}

function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}
