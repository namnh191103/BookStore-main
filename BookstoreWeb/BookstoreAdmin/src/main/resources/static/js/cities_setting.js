var buttonLoad;
var dropDownCity;
var buttonAddCity;
var buttonUpdateCity;
var buttonDeleteCity;
var labelCityName;
var fieldCityName;
var fieldCityCode;

$(document).ready(function () {
    buttonLoad = $("#buttonLoadCities");
    dropDownCity = $("#dropDownCities");
    buttonAddCity = $("#buttonAddCity");
    buttonUpdateCity = $("#buttonUpdateCity");
    buttonDeleteCity = $("#buttonDeleteCity");
    labelCityName = $("#labelCityName");
    fieldCityName = $("#fieldCityName");
    fieldCityCode = $("#fieldCityCode");

    buttonLoad.click(function () {
        loadCities();
    });

    dropDownCity.on("change", function () {
        changeFormDistrictToSelectedCity();
    });

    buttonAddCity.click(function () {
        if (buttonAddCity.val() == "Add") {
            addCity();
        } else {
            changeFormDistrictToNewCity();
        }
    });

    buttonUpdateCity.click(function () {
        updateCity();
    });

    buttonDeleteCity.click(function () {
        deleteCity();
    });
});

function deleteCity() {
    optionValue = dropDownCity.val();
    cityId = optionValue.split("-")[0];

    url = contextPath + "cities/delete/" + cityId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function () {
        $("#dropDownCities option[value='" + optionValue + "']").remove();
        changeFormDistrictToNewCity();
        showToastMessage("Thành phố đã xóa thành công!");
    }).fail(function () {
        showToastMessage("LỖI: Không thể kết nối tới máy chủ gặp lỗi");
    });
}

function updateCity() {
    if(!validateFormCity()) return; 

    url = contextPath + "cities/save";
    cityName = fieldCityName.val();
    cityCode = fieldCityCode.val();

    cityId = dropDownCity.val().split("-")[0];

    jsonData = { id: cityId, name: cityName, code: cityCode };

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (cityId) {
        $("#dropDownCities option:selected").val(cityId + "-" + cityCode);
        $("#dropDownCities option:selected").text(cityName);

        showToastMessage("Thành phố đã được cập nhật");

        changeFormDistrictToNewCity();
    }).fail(function () {
        showToastMessage("LỖI: Không thể kết nối tới máy chủ gặp lỗi");
    });
}

function validateFormCity() {
    formCity = document.getElementById("formCity");
    if (!formCity.checkValidity()) {
        formCity.reportValidity();
        return false;
    }

    return true; 
}

function addCity() {
    
    if(!validateFormCity()) return; 

    url = contextPath + "cities/save";
    cityName = fieldCityName.val();
    cityCode = fieldCityCode.val();
    jsonData = { name: cityName, code: cityCode };

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (cityId) {
        selectNewlyAddedCity(cityId, cityCode, cityName);
        showToastMessage("Thành phố mới đã được thêm vào");
    }).fail(function () {
        showToastMessage("LỖI: Không thể kết nối tới máy chủ gặp lỗi");
    });
}

function selectNewlyAddedCity(cityId, cityCode, cityName) {
    optionValue = cityId + "-" + cityCode;
    $("<option>").val(optionValue).text(cityName).appendTo(dropDownCity);

    $("#dropDownCities option[value='" + optionValue + "']").prop("selected", true);

    fieldCityCode.val("");
    fieldCityName.val("").focus();
}

function changeFormDistrictToNewCity() {
    buttonAddCity.val("Add");
    labelCityName.text("City Name");

    buttonUpdateCity.prop("disabled", true);
    buttonDeleteCity.prop("disabled", true);

    fieldCityCode.val("");
    fieldCityName.val("").focus();
}

function changeFormDistrictToSelectedCity() {
    buttonAddCity.prop("value", "New");
    buttonUpdateCity.prop("disabled", false);
    buttonDeleteCity.prop("disabled", false);

    labelCityName.text("Selected City");

    selectedCityName = $("#dropDownCities option:selected").text();
    fieldCityName.val(selectedCityName);

    cityCode = dropDownCity.val().split("-")[1];
    fieldCityCode.val(cityCode);
}

function loadCities() {
    url = contextPath + "cities/list";
    $.get(url, function (responseJSON) {
        dropDownCity.empty();

        $.each(responseJSON, function (index, city) {
            optionValue = city.id + "-" + city.code;
            $("<option>").val(optionValue).text(city.name).appendTo(dropDownCity);
        });
    }).done(function () {
        buttonLoad.val("Refresh City List");
        showToastMessage("Tất cả các thành phố đã được tải");
    }).fail(function () {
        showToastMessage("LỖI: Không thể kết nối tới máy chủ gặp lỗi");
    });
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}