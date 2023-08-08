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

import com.github.jaiimageio.impl.common.ImageUtil;

import net.sourceforge.tess4j.Tesseract;
import utils.TextUtils;
import utils.XLSUtils;

public class MainApp {
	static 		String logDir="F:\\rsync\\eclipse2022_wspace\\opencvTest01\\logROI";
	private static final int ERROR_PARSE=7;
	static Hashtable<String, DNSRecord> NIM2DNS = new Hashtable<String, DNSRecord>();
	static Tesseract tesseract = null;
//	static String dirTgtBase = "F:\\rsync\\poltek\\dns_ok_scanned";
	static String dirTgtBase = "./moved";
	static String dirSrc="./problems";
	static boolean moveAfterCheck=false ; // moved and arrange into dirTgtBase
	public static void main(String args[]) throws Exception {
		System.out.println("Source directory : "+dirSrc);
		System.out.println("Target Base directory : "+dirTgtBase);
		
//		ArrayList<XLSDNSTable> lsh = all_sheets_2122_genap();
//		ArrayList<XLSDNSTable> lsh = all_sheets_2122_ganjil();
//		ArrayList<XLSDNSTable> lsh = all_sheets_2223_ganjil();
		ArrayList<XLSDNSTable> lsh = default_sheets();
		for (int i = 0; i < lsh.size(); i++) {
			load_xls_database(lsh.get(i));
			
		}
		
//		String dirSrc = "F:/rsync/poltek/dns_ok_scanned/canon_lide_output/22_23_ganjil";
//		dirSrc+="/problems";

//		String dirPolnam = "F:/rsync/poltek/dns_ok_scanned/canon_lide_output/21_22_ganjil";
		if (args.length > 1) {
			if (args[0].equals("-d")) {
				dirSrc = args[1];
			} else {
				System.out.println(args[0] + " is wrong");
				System.out.println("java : java dnsmatch.MainApp -d dirname");
				System.exit(-1);
			}
		}
		
		initialize_config();
		extract_and_check_dir(dirSrc);
		System.out.println("Done.. no more files to process");

	}

	protected static ArrayList<XLSDNSTable> default_sheets() {
		ArrayList<XLSDNSTable> ret = new ArrayList<XLSDNSTable>();
		XLSDNSTable tb = new XLSDNSTable();// VI_B
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "./problems/";
		tb.xls_fname = "example.xlsx";
		tb.sheetfn = "I_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 127;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AD";
		ret.add(tb);

		return ret;
	}
	private static ArrayList<XLSDNSTable> all_sheets_2122_ganjil() {
		// TODO Auto-generated method stub
		ArrayList<XLSDNSTable> ret = new ArrayList<XLSDNSTable>();
		XLSDNSTable tb = new XLSDNSTable();// VI_B
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\ganjil\\evaluasi_kaprodi";
		tb.xls_fname = "evaluasi_21_22_ganjil.xlsx";
		tb.sheetfn = "I_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 127;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AD";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\ganjil\\evaluasi_kaprodi";
		tb.xls_fname = "evaluasi_21_22_ganjil.xlsx";
		tb.sheetfn = "III_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 118;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "V";
		ret.add(tb);

		
		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\ganjil\\evaluasi_kaprodi";
		tb.xls_fname = "evaluasi_21_22_ganjil.xlsx";
		tb.sheetfn = "V_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 95;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "T";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\21-22\\ganjil\\evaluasi_kaprodi";
		tb.xls_fname = "evaluasi_21_22_ganjil.xlsx";
		tb.sheetfn = "VII_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 105;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "T";
		ret.add(tb);

		
		return ret;
	}

	private static ArrayList<XLSDNSTable> all_sheets_2122_genap() {
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

	
	private static ArrayList<XLSDNSTable> all_sheets_2223_ganjil() {
		// TODO Auto-generated method stub
		ArrayList<XLSDNSTable> ret = new ArrayList<XLSDNSTable>();
		XLSDNSTable tb = new XLSDNSTable();// VI_B
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\22_23\\ganjil\\kaprodi_evaluasi\\kajur";
		tb.xls_fname = "dns EDIT_Kajur-Prodi Teknik Informatika_Ganjil 2022-2023 -.xlsx";
		tb.sheetfn = "I_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 142;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AP";
		ret.add(tb);

		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\22_23\\ganjil\\kaprodi_evaluasi\\kajur";
		tb.xls_fname = "dns EDIT_Kajur-Prodi Teknik Informatika_Ganjil 2022-2023 -.xlsx";
		tb.sheetfn = "III_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 136;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AS";
		ret.add(tb);
		
		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\22_23\\ganjil\\kaprodi_evaluasi\\kajur";
		tb.xls_fname = "dns EDIT_Kajur-Prodi Teknik Informatika_Ganjil 2022-2023 -.xlsx";
		tb.sheetfn = "V_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 128;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AA";
		ret.add(tb);
		
		tb = new XLSDNSTable();
		tb.xls_dir = "F:\\rsync\\poltek\\22_23\\ganjil\\kaprodi_evaluasi\\kajur";
		tb.xls_fname = "dns EDIT_Kajur-Prodi Teknik Informatika_Ganjil 2022-2023 -.xlsx";
		tb.sheetfn = "VII_ABC"; // SEMESTEr will use this
		tb.row_begin = 8;
		tb.row_end = 109;
		tb.NAMA_col = "B"; // B
		tb.NIM_col = "C";// C
		tb.IPK_col = "AA";
		ret.add(tb);

		

		return ret;
	}

	/**
	 * check all jpg in dir
	 * 
	 * @throws Exception
	 */
	static boolean debug_stop=true;
	public static void extract_and_check_dir(String dirPolnam) throws Exception {
		System.out.println("check dns dir= "+dirPolnam);
		String hFN = dirPolnam + "/dns_scan__20220825_0006.jpg";

		File dir = new File(dirPolnam);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			}
		});
		if(files==null) {
			System.err.println("no files to verify, source dir: "+dirSrc);
			System.exit(-3);
		}
		Arrays.sort(files, Comparator.comparingLong(File::lastModified));
		for (int i = 0; i < files.length; i++) {
			hFN = files[i].getAbsolutePath();
			System.out.println(new Date());
			System.out.println("Processing: " + hFN);
			DNSRecord found = null;
			
			if(found ==null) {
				found = fullpage_check_single(hFN);
			}

			if (found==null) {
				found=ROI_check_single(hFN);
			}
			
			/*
			if(found ==null) {
				found = collective_check_single(hFN);
			}*/
			
			
			if (found != null) {
				if(moveAfterCheck) {
				move_found(found);
				}else {
					System.out.println(hFN);
					System.out.println("Found but not moved");
				}
				
			} else {
				System.out.println("found failed: " + hFN);
				if(debug_stop) {
					System.err.println("Exiting ... by debug_stop="+debug_stop);
					System.exit(-1);
				}
				continue;
//				throw new Exception("found failed: " + hFN);
			}
//			System.exit(-1); //uncoment for single one by one
		}

	}


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

		String target_dir = dirTgtBase + "/" + year + "/" + found.NIM;
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
			System.err.println("error:" + moveFN);
		}
	}

	private static void load_xls_database(XLSDNSTable tb) throws IOException {
		// TODO Auto-generated method stub

		System.out.println("load xls: "+tb.sheetfn);
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
			if(crNAMA==null)continue;
			Row row = sheet.getRow(crNAMA.getRow());
			if(row==null)continue;
			Cell cell = row.getCell(crNAMA.getCol());
			if(cell==null) {
				System.err.println( "NAMA null at " +tb.sheetfn+ " row "+ i);
				continue;
			}
			String nama = cell.getStringCellValue();
			if(nama.length()==0)continue;
			
			CellReference crNIM = new CellReference(NIM_col + i);
			if(crNIM==null)continue;
			row = sheet.getRow(crNIM.getRow());
			if(row==null)continue;
			cell = row.getCell(crNIM.getCol());
			if(cell==null) {
				System.err.println( "NIM null at " +tb.sheetfn+ " row "+ i);
				continue;
			}
			cell.setCellType(CellType.STRING);
			String NIM = cell.getStringCellValue();
			if(NIM.length()==0)continue;
			
			CellReference crIPK = new CellReference(IPK_col + i);
			if(crIPK==null)continue;

			row = sheet.getRow(crIPK.getRow());
			if(row==null)continue;
			cell = row.getCell(crIPK.getCol());
			if(cell==null) {
				System.err.println( "IPK null at " +tb.sheetfn+ " row "+ i);
				continue;
			}
			cell.setCellType(CellType.STRING);
			String ipkS=cell.getStringCellValue().replace(",", ".");
			if(ipkS.length()==0)continue;

			float ipk=(float)0.0;
			try{
				ipk = Float.parseFloat(ipkS);
			}catch (NumberFormatException ex) {
				System.err.println("BAD IPK for nama: "+nama+ ", nim: "+NIM);
				continue;
			}
			
			if(ipk==0) {
				System.err.println("warning BAD IPK="+ipk);
			}

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
	static Mat tfieldSEMESTER=null;
	static Mat tfieldCOLLECTIVE=null;
	static Mat tfieldNIM=null;
	static Mat tfieldNAMA=null;
	static Mat tfieldTTL=null;
	static Mat tfieldIPK=null;
	static Mat tAllIPK=null;

	static void initialize_config() throws Exception {
//		dirTPL = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl";
		dirTPL = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/21_061";
//		tfieldSEMESTER = TemplateValueExtractor.readDeskew(dirTPL + "/semester/semester_field.jpg");
		tfieldNIM = TemplateValueExtractor.readDeskew(dirTPL + "/nim/NIM_field.jpg",false);
//		tfieldCOLLECTIVE = TemplateValueExtractor.readDeskew(dirTPL + "/COLLECTIVE_field.jpg",true);
//		tfieldCOLLECTIVE = TemplateValueExtractor.readDeskew(dirTPL + "/COLLECTIVE_L_field.jpg",true);
		tfieldCOLLECTIVE = TemplateValueExtractor.readDeskew(dirTPL + "/COLLECTIVE_field.jpg",true);
		tfieldNAMA = TemplateValueExtractor.readDeskew(dirTPL + "/nama/nama_field.jpg",false);
		tfieldTTL = TemplateValueExtractor.readDeskew(dirTPL + "/ttl/ttl_field.jpg",false);
		tfieldIPK = TemplateValueExtractor.readDeskew(dirTPL + "/ipk/ipk_field.jpg",false);
		tAllIPK = TemplateValueExtractor.readDeskew(dirTPL + "/ipk/ipk_all.jpg",false);
		System.out.println("Templates read from "+dirTPL);
	}


public static DNSRecord ROI_check_single(String hFN) throws Exception {
		DNSRecord found = null;
		String LEVHN_NIM = null, LEVHN_NAMA = null;
//		String hFN = dirTPL + "/haystack.jpg";

		Mat haystack = TemplateValueExtractor.readDeskew(hFN,true);
//		x.setOutputDirectory(dir);
		TemplateValueExtractor x = new TemplateValueExtractor(null);
		getTesseract().setVariable("user_defined_dpi", "300");

		// TODO looping here if collective is a directory ...
		ExtractionInfo ei=extract_right(logDir+"/COLLECTIVE","COLLECTIVE",haystack,tfieldCOLLECTIVE,x,true);

		
		Mat expanded1=ei.info.expanded;
		x.setTesseract(getTesseract());
		x.getTesseract().setPageSegMode(7); // for semester is good with 7
		x.getTesseract().setVariable("tessedit_char_whitelist", " 0123456789|:");// unset white list character
		extract_right(logDir+"/nim","NIM",expanded1,tfieldNIM,x);

		x.getTesseract().setPageSegMode(1); // for semester is good with 7
		x.getTesseract().setVariable("tessedit_char_whitelist", "");// unset white list character
		extract_right(logDir+"/nama", "NAMA", expanded1, tfieldNAMA, x);

		x.getTesseract().setPageSegMode(1); // for semester is good with 7
		x.getTesseract().setVariable("tessedit_char_whitelist", "");// unset white list character
		extract_right(logDir+"/ttl", "TTL", expanded1, tfieldTTL, x);

//		String tessW64="C:/Program Files/Tesseract-OCR";
		x.getTesseract().setPageSegMode(7); // 7 : single text line
		x.getTesseract().setVariable("tessedit_char_whitelist", " .,0123456789|");// unset white list character
//		extract_right_inside(tessW64,"IPK",haystack,tAllIPK,tfieldIPK,x);
		extract_right_inside(logDir+"/ipk","IPK",haystack,tAllIPK,tfieldIPK,x);
//		extract_right_inside(null, "IPK", haystack, tAllIPK, tfieldIPK, x);

		// exact matches of NIM and nama
		ExtractionInfo info = (ExtractionInfo) x.extractValue("NIM");
		String[] sar = info.value.split("\n");
		info.value = sar[0];
		sar = info.value.split(" ");	
		if (sar[0].length() == 10)
			info.value = sar[0]; // NIM on first word ==> 1320144033 7 4 7
		String s = info.value.trim().toUpperCase().replaceAll("\\s", "").replace("|", "");
		s=s.replace(",", "").replace(":", "").replace("|", "");
		info.value=s;
	
		//first is ':'   not finding empty NIM: :3121094056 l: 11
		if (info.value.startsWith(":")) {
			System.err.println("removing first colon" + info.value);
			
			
			info.value=info.value.substring(1);
			s=info.value;
		}
		
		//flipped '13'
		if (info.value.length()==10 && info.value.startsWith("31")) {
			info.value="13"+info.value.substring(2);
			s=info.value;	
		}
			
		
		
		//more than length NIM ?
		if(s.length()>10 && s.indexOf("13")>=0) {
			int start13=s.indexOf("13");
			System.out.println("finding 13");
			s=s.substring(start13,start13+10);
			info.value=s;
		}
		
		if (s.length() > 5)
			LEVHN_NIM = s; // half for later check
		if (info.value != null && info.value.length() == 10) {

			System.out.println("l_10 => " + info.value);
			found = NIM2DNS.getOrDefault(s, null);
			if (found != null) {
				found.ocr_image_filename = hFN;
//				return found;
			}else {
				System.err.println("NIM(10) BUT NOT in DB => " + info.value);
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
				System.out.println("EXACT MATCH=> " + found);
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
			String ipkstr = info.value;
			ipkstr=ipkstr.replace("|", "").replace(",", ".");
			ipkstr=ipkstr.trim();
			
			///ipk : 3.34 |  3.34 1
			String ar[]=ipkstr.split(" ");
			ipkstr=ar[0];
			for (int i=1 ; i<ar.length; i++) {
				if(ar[i].length()>ipkstr.length()) {
					ipkstr=ar[i];
				}
			}
			// 2 digit precision
			if (Math.abs(Float.parseFloat(ipkstr) - found.ipk)>=0.01) {
				if (ipkstr.trim().length() == 3 && Float.parseFloat(ipkstr) > 0) {
					ipkstr = ipkstr.substring(0, 1) + "." + ipkstr.substring(1, 3);// \dns_scan__20220825_0015.jpg 323
				} else
					throw new Exception("IPK not same: ->" + ipkstr + " (ocr) !=" + found.ipk + " (Excel) <- " + found);
			}
		}
		return found;

	}
	
	/* no collective? */
	
	public static DNSRecord fullpage_check_single(String hFN) throws Exception {
		DNSRecord found = null;
		String LEVHN_NIM = null, LEVHN_NAMA = null;

		Mat haystack = TemplateValueExtractor.readDeskew(hFN,true);
		getTesseract().setVariable("user_defined_dpi", "300");
		getTesseract().setVariable("tessedit_char_whitelist", "");// unset white list character
		
		FullPageExtractor xtr = new FullPageExtractor(logDir, haystack, getTesseract());
		xtr.init();
		// exact matches of NIM and nama
		ExtractionInfo info = (ExtractionInfo) xtr.extractValue("NIM");
		
		if(info==null||info.value==null)return null;
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
			info = (ExtractionInfo) xtr.extractValue("NAMA");
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
			info = (ExtractionInfo) xtr.extractValue("IPK");
			String ipkstr = info.value.replace(",", ".").trim().replace("|", "");
			
			if(ipkstr.equalsIgnoreCase("null") || ipkstr.length()<2) {
				System.err.println("Error parsing IPK : \""+ipkstr+"\"");
//				System.exit(ERROR_PARSE);
				return null ; // TODO ??
			}
			System.out.println("IPK String :" +ipkstr+", l: "+ipkstr.length());
			float ipkFloat=Float.parseFloat(ipkstr);
			if (( ipkFloat- found.ipk)>0.009 ) {

				if (ipkstr.trim().length() == 3 && ipkFloat > 0) {
					ipkstr = ipkstr.substring(0, 1) + "." + ipkstr.substring(1, 3);// \dns_scan__20220825_0015.jpg 323
				} else
					throw new Exception("IPK not same: ->" + ipkstr + "(ocred) !=" + found.ipk + " (EXCEL) <- " + found);
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
		System.out.println("xri() "+field);
		x.setOutputDirectory(dir);
		x.register(field, haystack, tall, tfield);
		ExtractionInfo eipk = (ExtractionInfo) x.extractValue(field);
		System.out.println("eri: "+field+"="+eipk.value);

	}

	public static ExtractionInfo extract_right(String dir, String field, Mat haystack, Mat tField, TemplateValueExtractor x)
			throws Exception {
		return extract_right(dir, field, haystack, tField, x,false);
	}
	public static ExtractionInfo extract_right(String dir, String field, Mat haystack, Mat tField, TemplateValueExtractor x,boolean noOCR)
			throws Exception {

		System.out.println("Extract right :"+field);
		String newName=noOCR? field:"pre_" + field;
		ExtractionInfo ei = new ExtractionInfo(newName, haystack, tField, null);
		System.out.println(ei);
		x.setOutputDirectory(dir);
		x.register(ei);
		x.extractValue(newName);
		if(noOCR) {
			System.err.println("warning exit without 2nd extract: "+field);
			return ei ;// no need to extract .. 
		}
		Mat tall = ei.info.expanded;
		System.out.println("2nd extract: "+field);
		System.out.println(tall);
		System.out.println(tField);
		ei = new ExtractionInfo(field, haystack, tall, tField);
		x.register(ei);
		x.extractValue(field);
		return ei;
	}

	 
	static Tesseract getTesseract() {
		if (tesseract == null) {
			tesseract = new Tesseract();
//			tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");
//			tesseract.setDatapath("c:/cygwin64/usr/share/tessdata");
			tesseract.setDatapath("c:/rps/cygwin64/usr/share/tessdata");

			tesseract.setVariable("tessedit_char_whitelist", " 0123456789. ,");
			tesseract.setVariable("user_defined_dpi", "300");
			int seg_mode = 1; /* 1: automatic, 11: single text line */
			tesseract.setPageSegMode(seg_mode);
		}
		return tesseract;

	}
}
