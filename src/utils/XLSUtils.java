package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//https://stackoverflow.com/questions/6966106/reading-data-from-excel-sheet-2007-in-java

public class XLSUtils {
	public static void main(String[] args) {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			String fName = "testPOIOut.xlsx";
			String dirName = "F:/rsync/poltek/21-22/genap/kaprodi_evaluasi/DNS";
			String fullName = dirName + "/" + fName;
//            fos = new FileOutputStream(new File("D:\\prac\\sample1.xlsx"));
			fos = new FileOutputStream(new File(fullName));
			XSSFWorkbook wb = new XSSFWorkbook();

			for (int m = 0; m < 3; m++) {
				if (m == 0) {
					XSSFSheet sh = wb.createSheet("Sachin");
					System.out.println(" Sheet NO:" + m);

					for (int k = 0; k < 30; k++) {
						XSSFRow row = sh.createRow((short) k);
						for (int i = 0; i < 30; i++) {
							XSSFCell cell = row.createCell((short) i);
							cell.setCellValue(wb.getSheetName(m) + i);
						}
					}

				} else if (m == 1) {
					XSSFSheet sh1 = wb.createSheet("Dravid");
					System.out.println(" Sheet NO:" + m);

					for (int k = 0; k < 30; k++) {
						XSSFRow row = sh1.createRow((short) k);
						for (int i = 0; i < 30; i++) {
							XSSFCell cell = row.createCell((short) i);
							cell.setCellValue(wb.getSheetName(m) + i);
						}
					}

				} else {

					XSSFSheet sh2 = wb.createSheet("Dhoni");
					System.out.println(" Sheet NO:" + m);

					for (int k = 0; k < 30; k++) {
						XSSFRow row = sh2.createRow((short) k);
						for (int i = 0; i < 30; i++) {
							XSSFCell cell = row.createCell((short) i);
							cell.setCellValue(wb.getSheetName(m) + i);
						}
					}
				}
			}

			wb.write(fos);
			fos.close();

//            fis= new FileInputStream(new File("D:\\prac\\sample1.xlsx"));
			fis = new FileInputStream(new File(fullName));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			java.util.Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.rowIterator();
			int number = sheet.getLastRowNum();
			System.out.println(" number of rows" + number);

			while (rows.hasNext()) {
				XSSFRow row = ((XSSFRow) rows.next());
				int r = row.getRowNum();
				System.out.println(" Row NO:" + r);
				java.util.Iterator<org.apache.poi.ss.usermodel.Cell> cells = row.cellIterator();

				while (cells.hasNext()) {
					XSSFCell cell = (XSSFCell) cells.next();
					String Value = cell.getStringCellValue();
					System.out.println(Value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static XSSFSheet readWorkbook(String fullName, int sheetNo) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fullName));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		return workbook.getSheetAt(sheetNo);
	}
	
	public static XSSFSheet readWorkbook(String fullName, String sheet) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fullName));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		
		return workbook.getSheet(sheet);
	}

	
}