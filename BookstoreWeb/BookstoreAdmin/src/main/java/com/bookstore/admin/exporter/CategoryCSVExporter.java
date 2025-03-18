package com.bookstore.admin.exporter;

import java.io.IOException;

import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.bookstore.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryCSVExporter extends Exporter {
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "text/csv", ".csv", "categories_");
		
		ICsvBeanWriter csvWriter = new  CsvBeanWriter(response.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Category ID", "Category Name"};
		String[] FieldMapping = {"id","name"};
		
		csvWriter.writeHeader(csvHeader);
		
		for(Category category : listCategories) {
			category.setName(category.getName().replace("--", "  "));
			csvWriter.write(category, FieldMapping);
		}
		
		csvWriter.close();
 	}
	
}
