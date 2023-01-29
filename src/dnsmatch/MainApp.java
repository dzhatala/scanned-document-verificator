package dnsmatch;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.opencv.core.Mat;

import net.sourceforge.tess4j.Tesseract;
import utils.TextUtils;
import utils.XLSUtils;

public class MainApp {
	static Hashtable<String, DNSRecord> NIM2DNS = new Hashtable<String, DNSRecord>();
	static Tesseract tesseract = null;

	public static void main(String args[]) throws Exception {
		ArrayList<XLSDNSTable> lsh = all_sheets();
		for (int i = 0; i < lsh.size(); i++)
			load_xls_database(lsh.get(i));
		String dirPolnam = "F:/rsync/poltek/dns_ok_scanned/canon_lide_output";
		initialize_config();
		extract_and_check_dir(dirPolnam);

	}

	private static ArrayList<XLSDNSTable> all_sheets() {
		// TODO Auto-generated method stub
		ArrayList<XLSDNSTable> ret = new ArrayList<XLSDNSTable>();
		XLSDNSTable tb = new XLSDNSTable();// VI_B
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester 6.xlsx";
		tb.sheetfn = "VI_A"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 28;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "V";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester 6.xlsx";
		tb.sheetfn = "VI_C"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 25;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "V";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester_4.xlsx";
		tb.sheetfn = "IV_A"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 34;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "V";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester_4.xlsx";
		tb.sheetfn = "IV_B"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 31;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "V";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester_4.xlsx";
		tb.sheetfn = "IV_C"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 36;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "V";
		ret.add(tb);

		// semester 2
		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester_2.xlsx";
		tb.sheetfn = "II_A"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 36;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AB";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester_2.xlsx";
		tb.sheetfn = "II_B"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 36;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AB";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\genap\\kaprodi_evaluasi\\DNS";
		tb.xls_fname = "pdf2xls_semester_2.xlsx";
		tb.sheetfn = "II_C"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 36;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AB";
		ret.add(tb);

		return ret;
	}

	/**
	 * check all jpg in dir
	 * 
	 * @throws Exception
	 */
	public static void extract_and_check_dir(String dirPolnam) throws Exception {
		String hFN = dirPolnam + "/dns_scan__20220825_0006.jpg";

		File dir = new File(dirPolnam);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			}
		});
		Arrays.sort(files, Comparator.comparingLong(File::lastModified));
		for (int i = 0; i < files.length; i++) {
			hFN = files[i].getAbsolutePath();
			System.out.println(new Date());
			System.out.println("Processing: " + hFN);
			DNSRecord found = extract_and_check_single(hFN);
			if (found != null) {
				move_found(found);
			} else {
				System.out.println("found failed: " + hFN);
				continue;
//				throw new Exception("found failed: " + hFN);
			}
//			System.exit(-1); //uncoment for single one by one
		}

	}

	static String base_dir = "F:\\rsync\\poltek\\dns_ok_scanned";

	private static void move_found(DNSRecord found) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("moving " + found);
		if (found.NIM.length() != 10) {
			throw new Exception("error NIM length: " + found);
		}
		int year = found.extract_year();
		if (year < 0)
			throw new Exception("'year is not found': " + found);
		switch (found.semester) {
		case "I":
			break;
		case "II":
			break;
		case "III":
			break;
		case "IV":
			break;
		case "V":
			break;
		case "VI":
			break;
		case "VII":
			break;
		case "VIII":
			break;
		default:
			throw new Exception("'Bad semester': " + found);
		}

		String target_dir = base_dir + "/" + year + "/" + found.NIM;
		File dir2create = new File(target_dir);
		dir2create.mkdirs();
//		System.out.println("target dir:" + target_dir);
		File oriF = new File(found.ocr_image_filename);
		String oriShortFname = oriF.getName();
		String moveFN = target_dir + "/" + found.NIM + "_" + "semester_" + found.semester + "_" + oriShortFname;
//		System.out.println("new loc" + moveFN);
		File moveF = new File(moveFN);
		boolean success = oriF.renameTo(moveF);
		if (success) {
			System.out.println("success:" + moveFN);
		} else {
			System.out.println("error:" + moveFN);
		}
	}

	private static void load_xls_database(XLSDNSTable tb) throws IOException {
		// TODO Auto-generated method stub

		String xls_dir = tb.xls_dir;
		String xls_fname = tb.xls_fname;

		String sheetfn = tb.sheetfn; // SEMESTEr will use this
		int row_begin = tb.row_begin;
		int row_end = tb.row_end;
		String NAMA_col = tb.NAMA_col; // B
		String NIM_col = tb.NIM_col;// C
		String IPK_col = tb.IPK_col;
		XSSFSheet sheet = XLSUtils.readWorkbook(xls_dir + "/" + xls_fname, sheetfn);
		for (int i = row_begin; i <= row_end; i++) {
			// https://stackoverflow.com/questions/5578535/get-cell-value-from-excel-sheet-with-apache-poi
			CellReference crNAMA = new CellReference(NAMA_col + i);
			Row row = sheet.getRow(crNAMA.getRow());
			Cell cell = row.getCell(crNAMA.getCol());
			String nama = cell.getStringCellValue();

			CellReference crNIM = new CellReference(NIM_col + i);
			row = sheet.getRow(crNIM.getRow());
			cell = row.getCell(crNIM.getCol());
			cell.setCellType(CellType.STRING);
			String NIM = cell.getStringCellValue();

			CellReference crIPK = new CellReference(IPK_col + i);
			row = sheet.getRow(crIPK.getRow());
			cell = row.getCell(crIPK.getCol());
			cell.setCellType(CellType.STRING);
			float ipk = Float.parseFloat(cell.getStringCellValue().replace(",", "."));

			// System.out.println("NAMA: "+nama+", NIM:"+NIM+", IPK="+ipk);
			String[] sem = sheetfn.split("_");
			DNSRecord rec = new DNSRecord(NIM, nama, ipk, sem[0]);
			String p = NIM.trim().toUpperCase().replaceAll("\\s", "");

			NIM2DNS.put(p, rec);
			p = nama.trim().toUpperCase().replaceAll("\\s", "");
			NIM2DNS.put(p, rec);

//			System.out.println(rec);
		}

		sheet.getWorkbook().close();

	}

	static String dirTPL = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl";
	static Mat tfieldSEMESTER;
	static Mat tfieldNIM;
	static Mat tfieldNAMA;
	static Mat tfieldTTL;
	static Mat tfieldIPK;
	static Mat tAllIPK;

	static void initialize_config() throws Exception {
		dirTPL = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl";
		tfieldSEMESTER = TemplateValueExtractor.readDeskew(dirTPL + "/semester/semester_field.jpg");
		tfieldNIM = TemplateValueExtractor.readDeskew(dirTPL + "/nim/nim_field.jpg");
		tfieldNAMA = TemplateValueExtractor.readDeskew(dirTPL + "/nama/nama_field.jpg");
		tfieldTTL = TemplateValueExtractor.readDeskew(dirTPL + "/ttl/ttl_field.jpg");
		tfieldIPK = TemplateValueExtractor.readDeskew(dirTPL + "/ipk/ipk_field.jpg");
		tAllIPK = TemplateValueExtractor.readDeskew(dirTPL + "/ipk/ipk_all.jpg");
	}

	public static DNSRecord extract_and_check_single(String hFN) throws Exception {
		DNSRecord found = null;
		String LEVHN_NIM = null, LEVHN_NAMA = null;
//		String hFN = dirTPL + "/haystack.jpg";

		Mat haystack = TemplateValueExtractor.readDeskew(hFN);
//		x.setOutputDirectory(dir);
		TemplateValueExtractor x = new TemplateValueExtractor(null);
		x.setTesseract(getTesseract());
		tesseract.setVariable("user_defined_dpi", "300");

//		x.getTesseract().setPageSegMode(7); // for semester is good with 7
//		x.getTesseract().setVariable("tessedit_char_whitelist", " IV|");// unset white list character
//		extract_right(dir+"/semester","SEMESTER",haystack,tfieldSEMESTER,x);
//		extract_right(null, "SEMESTER", haystack, tfieldSEMESTER, x);

		x.getTesseract().setPageSegMode(7); // for semester is good with 7
		x.getTesseract().setVariable("tessedit_char_whitelist", " 0123456789|");// unset white list character
//		extract_right(dir+"/nim","NIM",haystack,tfieldNIM,x);
		extract_right(null, "NIM", haystack, tfieldNIM, x);

		x.getTesseract().setPageSegMode(1); // for semester is good with 7
		x.getTesseract().setVariable("tessedit_char_whitelist", "");// unset white list character
		extract_right(null, "NAMA", haystack, tfieldNAMA, x);

		x.getTesseract().setPageSegMode(1); // for semester is good with 7
		x.getTesseract().setVariable("tessedit_char_whitelist", "");// unset white list character
		extract_right(null, "TTL", haystack, tfieldTTL, x);

//		extract_right_inside(dirTPL+"/ipk","IPK",haystack,tAllIPK,tfieldIPK,x);
		x.getTesseract().setVariable("tessedit_char_whitelist", " ,0123456789");// unset white list character
		extract_right_inside(null, "IPK", haystack, tAllIPK, tfieldIPK, x);

		// exact matches of NIM and nama
		ExtractionInfo info = (ExtractionInfo) x.extractValue("NIM");
		String[] sar = info.value.split("\n");
		info.value = sar[0];
		sar = info.value.split(" ");
		if (sar[0].length() == 10)
			info.value = sar[0]; // NIM on first word ==> 1320144033 7 4 7
		String s = info.value.trim().toUpperCase().replaceAll("\\s", "").replace("|", "");
		if (s.length() > 5)
			LEVHN_NIM = s; // half for later check
		if (info.value != null && info.value.length() == 10) {

			found = NIM2DNS.getOrDefault(s, null);
			if (found != null) {
//				System.out.println("exact FOUND => " + found);
				found.ocr_image_filename = hFN;
//				return found;
			}
		} else {
			System.out.println("not finding empty NIM: " + info.value + " l: " + info.value.length());

		}

		if (found == null) {// exact NIM no ?
			info = (ExtractionInfo) x.extractValue("NAMA");
			sar = info.value.split("\n");
			info.value = sar[0];
			LEVHN_NAMA=info.value;
			if (info.value != null && info.value.length() > 0) {
				s = info.value.trim().toUpperCase().replaceAll("\\s", "");
				found = NIM2DNS.getOrDefault(s, null);
//			System.out.println("try find: #" + s + "#");
				if (found != null) {
//				System.out.println("exact FOUND => " + found);
					found.ocr_image_filename = hFN;
//				return found;
				} else {
					LEVHN_NAMA = s;
				}
			} else {
				System.out.println("not search for empty NAMA");

			}
		}
		// NO_EXACT_MATCH use edit distance
		if (found == null) {
			found = levehnsteinFind(LEVHN_NIM, LEVHN_NAMA);
		}

		if (found != null) {
			found.ocr_image_filename = hFN;
			info = (ExtractionInfo) x.extractValue("IPK");
			String ipkstr = info.value.replace(",", ".").trim().replace("|", "");
			if (Float.parseFloat(ipkstr) != found.ipk) {
				if (ipkstr.trim().length() == 3 && Float.parseFloat(ipkstr) > 0) {
					ipkstr = ipkstr.substring(0, 1) + "." + ipkstr.substring(1, 3);// \dns_scan__20220825_0015.jpg 323
				} else
					throw new Exception("IPK not same: ->" + ipkstr + "!=" + found.ipk + "<- " + found);
			}
		}
		return found;

	}

	private static DNSRecord levehnsteinFind(String LEVHN_NIM, String LEVHN_NAMA) {
		// TODO Auto-generated method stub
		System.out.println("Levehnstein find(" + LEVHN_NIM + "," + LEVHN_NAMA + ")");
		Set<String> setOfKeys = NIM2DNS.keySet();

		// Iterating through the Hashtable
		// object using for-Each loop
		for (String key : setOfKeys) {
			float score_nama = (float) TextUtils.levehnsteinDistance(key, LEVHN_NAMA);
			float athird = (float) key.length() / 3;
			float half = (float) key.length() / 2;
			// if name less than 1/3
			if (score_nama < athird) {
				// System.out.println(LEVHN_NAMA+"?"+key + " "+score_nama +"<"+half);
				return NIM2DNS.getOrDefault(key, null);
			} else {
				if (score_nama < half)
					System.out.println(LEVHN_NAMA + "?" + key + " " + score_nama + "<" + half + " REJECTED??");
			}

			// nama half AND NIM half ?
			float nama_half = (float) key.length() / 3;

			if (score_nama < nama_half) {
				DNSRecord pass_nama_half = NIM2DNS.getOrDefault(key, null);
				float score_nim = (float) TextUtils.levehnsteinDistance(pass_nama_half.NIM, LEVHN_NIM);
				float nim_half = pass_nama_half.NIM.trim().length() / 2;
				if (score_nim < nim_half)
					return pass_nama_half; // now return 0.5 * 0.5 confidence

			}

		}

		return null;

	}

	public static void extract_right_inside(String dir, String field, Mat haystack, Mat tall, Mat tfield,
			TemplateValueExtractor x) throws Exception {
		x.setOutputDirectory(dir);
		x.register(field, haystack, tall, tfield);
		ExtractionInfo eipk = (ExtractionInfo) x.extractValue(field);
		System.out.println(eipk.value);

	}

	public static void extract_right(String dir, String field, Mat haystack, Mat tField, TemplateValueExtractor x)
			throws Exception {

//		System.out.println(tall);
		ExtractionInfo ei = new ExtractionInfo("pre_" + field, haystack, tField, null);
		x.setOutputDirectory(dir);
		x.register(ei);
		x.extractValue("pre_" + field);
		Mat tall = ei.info.expanded;
//		System.out.println(tall);
//		System.out.println(tfield);
		ei = new ExtractionInfo(field, haystack, tall, tField);
		x.register(ei);
		ei = (ExtractionInfo) x.extractValue(field);
		System.out.println(ei.value);
	}

	static Tesseract getTesseract() {
		if (tesseract == null) {
			tesseract = new Tesseract();
			tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");

			tesseract.setVariable("tessedit_char_whitelist", " 0123456789. ,");
			tesseract.setVariable("user_defined_dpi", "300");
			int seg_mode = 1; /* 1: automatic, 11: single text line */
			tesseract.setPageSegMode(seg_mode);
		}
		return tesseract;

	}
}
