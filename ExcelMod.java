import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelMod extends JFrame {
	private String inputFile;
	private File inputWorkBook;

	public String getInputFile() {
		return this.inputFile;
	}

	public ExcelMod(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getFilter(int i) {
		return "";
	}

	public int getNumberOfFilters() throws IOException {
		int result = 0;
		this.inputWorkBook = new File(this.getInputFile());
		Workbook w;
		try {
			w = Workbook.getWorkbook(this.inputWorkBook);
			Sheet sheet = w.getSheet(0);
			result = sheet.getColumns();

		} catch (BiffException e) {
			JOptionPane.showMessageDialog(this,
					"Please convert excel file to a .xls file");
		}

		return result;
	}

	public String getContent(int i) throws IOException {
		String parameter = "";
		this.inputWorkBook = new File(this.getInputFile());
		Workbook w;
		try {
			w = Workbook.getWorkbook(this.inputWorkBook);
			Sheet sheet = w.getSheet(0);
			Cell c = sheet.getCell(i, 0);
			parameter = c.getContents();

		} catch (BiffException e) {
			e.printStackTrace();
		}
		return parameter;

	}

}
