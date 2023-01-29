package dnsmatch;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import utils.XLSUtils;

public class XLSDNSTable {
	
	String xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
	String xls_fname = "pdf2xls_semester 6.xlsx";

	String sheetfn = "VI_B"; // SEMESTEr will use this
	int row_begin = 8;
	int row_end = 28;
	String NAMA_col = "B"; // B
	String NIM_col = "C";// C
	String IPK_col = "V";

}
