$(document).ready(function() {
    $("#buttonAddToCart").on("click", function(evt) {
        addToCart() 
    })
})

function addToCart() {
    quantity = $("#quantity" + productId).val()
    url = contextPath + "cart/add/" + productId + "/" + quantity

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        }
    }).done(function(respone) {
        showModalDialog("Giỏ hàng", respone)
    }).fail(function() {
        showErrorModal("Không thể thêm sản phẩm vào giỏ hàng")
    })
}