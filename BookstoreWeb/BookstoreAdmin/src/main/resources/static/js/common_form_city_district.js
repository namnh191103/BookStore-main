var dropdownCities;
var dropdownDistricts;

$(document).ready(function() {
	dropdownCities = $("#city");
	dropdownDistricts = $("#listDistricts");

	dropdownCities.on("change", function() {
		loadDistrictsForCity();
		$("#district").val("").focus();
	});	
	
	loadDistrictsForCity();
});

function loadDistrictsForCity() {
	selectedCity = $("#city option:selected");
	cityId = selectedCity.val();
	
	url = contextPath + "districts/list_by_city/" + cityId;
	
	$.get(url, function(responseJson) {
		dropdownDistricts.empty();
		
		$.each(responseJson, function(index, district) {
			$("<option>").val(district.name).text(district.name).appendTo(dropdownDistricts);
		});
	}).fail(function() {
		showErrorModal("Có lỗi khi tải các quận của thành phố đã chọn.");
	})	
}
