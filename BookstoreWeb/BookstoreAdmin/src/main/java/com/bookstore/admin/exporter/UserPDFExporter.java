package com.bookstore.admin.exporter;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import com.bookstore.entity.User;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

public class UserPDFExporter extends Exporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "applicaton/pdf", ".pdf", "users_");
		
		
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		 Paragraph paragraph = new Paragraph("List Of Users", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(paragraph);
		 
		PdfPTable table = new PdfPTable(6);	
		table.setWidthPercentage(100f);
		table.setWidths(new float[] {1.5f ,3.5f, 3.0f, 3.0f, 3.0f, 1.5f});
		table.setSpacingBefore(10);
		writeTableHeader(table);
		wirteTableData(table, listUsers);
		document.add(table);
		
		
		document.close();
	}

	private void wirteTableData(PdfPTable table, List<User> listUsers) {
		for(User user :listUsers) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(user.getEmail());
			table.addCell(user.getFirstName());
			table.addCell(user.getLastName());
			table.addCell(user.getRoles().toString());
			table.addCell(String.valueOf(user.isEnabled()));
			
		}
		
	}

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.darkGray);
		cell.setPadding(5);
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);
		
		cell.setPhrase(new Phrase("User ID", font));
		 table.addCell(cell);
		 cell.setPhrase(new Phrase("Email", font));
		 table.addCell(cell);
		 cell.setPhrase(new Phrase("First Name", font));
		 table.addCell(cell);
		 cell.setPhrase(new Phrase("Last Name", font));
		 table.addCell(cell);
		 cell.setPhrase(new Phrase("Roles", font));
		 table.addCell(cell);
		 cell.setPhrase(new Phrase("Enabled", font));
		 table.addCell(cell);
		 
		
	}

	
	
	
	
}
