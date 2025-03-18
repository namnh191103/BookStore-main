var dropDownCity;
var dataListDistrict;
var fieldDistrict;

$(document).ready(function () {
    dropDownCity = $("#city");
    dataListDistrict = $("#listDistricts");
    fieldDistrict = $("#district");

    dropDownCity.on("change", function () {
        loadDistrictsForCity();
        fieldDistrict.val("").focus();
    });
});

function loadDistrictsForCity() {
    var selectedCity = $("#city option:selected");
    var cityId = selectedCity.val();
    var url = contextPath + "settings/list_districts_by_city/" + cityId;

    $.get(url, function (responseJSON) {
        dataListDistrict.empty();

        $.each(responseJSON, function (index, district) {
            $("<option>").val(district.name).text(district.name).appendTo(dataListDistrict);
        });
    }).fail(function () {
        alert("Kết nối server thất bại!");
    });
}


function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value != $("#password").val()) {
        confirmPassword.setCustomValidity("Mật khẩu không trùng khớp!");
    } else {
        confirmPassword.setCustomValidity("");
    }
}
