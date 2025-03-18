package com.bookstore.admin.exporter;

import java.io.IOException;

import java.util.List;

import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.bookstore.entity.Brand;

import jakarta.servlet.http.HttpServletResponse;
@Component
public class BrandCSVExporter extends Exporter {
	public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "text/csv", ".csv", "brands_");
		
		ICsvBeanWriter csvWriter = new  CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Brand ID", "Brand Name"};
		String[] FieldMapping = {"id", "name"};
		
		csvWriter.writeHeader(csvHeader);
		
		for(Brand brand : listBrands) {
			csvWriter.write(brand, FieldMapping);
		}
		
		csvWriter.close();
 	}
	
}
