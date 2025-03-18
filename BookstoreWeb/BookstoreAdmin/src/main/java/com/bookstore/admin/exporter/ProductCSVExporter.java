package com.bookstore.admin.exporter;

import java.io.IOException;


import java.util.List;

import org.springframework.stereotype.Component;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.bookstore.entity.product.Product;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class ProductCSVExporter extends Exporter {
	public void export(List<Product> listProducts, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "text/csv", ".csv", "product_");
		
		ICsvBeanWriter csvWriter = new  CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Product ID", "Product Name"};
		String[] FieldMapping = {"id","name"};
		
		csvWriter.writeHeader(csvHeader);
		
		for(Product product : listProducts) {
			product.setName(product.getName().replace("--", "  "));
			csvWriter.write(product, FieldMapping);
		}
		
		csvWriter.close();
 	}
	
}
