package tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.NameValuePair;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import dnsmatch.ExtractionInfo;
import dnsmatch.FieldValueExtractor;
import dnsmatch.MatchInfo;
import dnsmatch.TemplateValueExtractor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import utils.ImageUtils;

public class rev_05 {

	public static void check_file_dir(String[] args) throws Exception {
		String fsrc = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/haystack.jpg";
		if (args.length > 1) {
			if (args[0].equals("-f")) {
				fsrc = args[1];
			} else {
				System.out.println(args[0] + " is wrong");
				System.out.println("java : java DNSChecker -f filename");
				System.exit(-1);
			}
		}

		// imageInfoDPI(fsrc);
		String ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_nama_nim_ttl.jpg";
		CONFIG.outputExpandedIVT = true;// for TTL
		CONFIG.outputExpanded = true;
//		String toTesseractFN = rev_03_DNSChecker.matchTemplate(fsrc, ftpl, true);
		MatchInfo minfo = rev_03_DNSChecker.matchTemplateInfo(fsrc, ftpl, true);
		String toTesseractFN = minfo.expandedFN;
		if (toTesseractFN != null) {
			String[] ocred = rev_03_DNSChecker.runTesseract(toTesseractFN);
			List<NameValuePair> list = rev_03_DNSChecker.validateTTL(ocred[0]);// stdout
			if (list != null) {
				ArrayList al = (ArrayList) list;
//				System.out.println(al);
			} else {
				System.err.println("can't extract any TTL");
				System.exit(-1);
			}
		} else {
			System.err.println("TTL filename is null");
			System.exit(-1);

		}

//		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK.jpg";
		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_NUM_01.jpg";
		CONFIG.outputNoExpanded = true; // if ipk can't be found from tesseract, file must be use
		// to ROI template matching ...
		MatchInfo info = rev_03_DNSChecker.matchTemplateInfo(fsrc, ftpl, true);
		float ipk = -1;
		if (toTesseractFN != null) {
			String[] ocred = rev_03_DNSChecker.runTesseract(toTesseractFN);
			ipk = rev_03_DNSChecker.validateIPKFromText(ocred[0]);
			System.out.println("ipk " + ipk);
		}

//		ipk = (float) -1.0; //uncomment for test only
		if (ipk < 0) {
			// more template matching
			ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_LEFT_TO_NUM_DOT.jpg";
			info = rev_03_DNSChecker.matchTemplateInfo(info.matchNotExpandedFN, ftpl, false);
//			System.out.println("Right on: "+info.notExpandedRightFN);

			String[] ocred = rev_03_DNSChecker.runTesseract(info.valuePortionFN);
			System.out.println(ocred[0]);

		}
	}

	public static void main3(String[] args) throws Exception {
		String fsrc = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/test/b/b.jpg";
		String ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_NUM_01.jpg";

		CONFIG.outputNoExpanded = true;

		MatchInfo info = rev_03_DNSChecker.matchTemplateInfo(fsrc, ftpl, true);

		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_LEFT_TO_NUM_DOT.jpg";
		info = rev_03_DNSChecker.matchTemplateInfo(info.matchNotExpandedFN, ftpl, false);
//		System.out.println("####"+info.writtenFoundFN+"######");

//		System.out.println("Right on: "+info.notExpandedRightFN);
		String[] ocred = rev_03_DNSChecker.runTesseract(info.valuePortionFN);
		System.out.println(ocred[0]);

	}

	public static void test_limit_char(String[] args) throws Exception {
		String fsrc = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/test/b/b.jpg";
		String ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_NUM_01.jpg";

		CONFIG.outputNoExpanded = true;

		MatchInfo info = rev_03_DNSChecker.matchTemplateInfo(fsrc, ftpl, true);

		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_LEFT_TO_NUM_DOT.jpg";
		info = rev_03_DNSChecker.matchTemplateInfo(info.matchNotExpandedFN, ftpl, false);

//		String[] ocred = rev_03_DNSChecker.runTesseract(info.notExpandedRightFN);
//		System.out.println(ocred[0]);
//		
		Tesseract tesseract = new Tesseract();

		// Error opening data file ./eng.traineddata
		tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");

		BufferedImage img = ImageUtils.LossyMat2BufferedImage(info.valuePortion);
		tesseract.setVariable("tessedit_char_whitelist", "0123456789.,");
		String text = tesseract.doOCR(img);
//		System.out.println(text);
		float ipk = Float.parseFloat(text.replace(",", "."));

		System.out.println("ipk=" + ipk);

	}

	
	
	
	public static void testExtractor() throws Exception {

		
		String dir = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/ipk";
		
		TemplateValueExtractor x = new TemplateValueExtractor(dir);

		String hFN = dir + "/haystack.jpg";
		String tAllFN = dir + "/ipk_all.jpg";
		String tFieldFN = dir + "/ipk_field.jpg";
		Mat haystack = TemplateValueExtractor.readDeskew(hFN,true);
		Mat tall = TemplateValueExtractor.readDeskew(tAllFN,false);
		Mat tfield = TemplateValueExtractor.readDeskew(tFieldFN,false);

		x.register("IPK", haystack, tall, tfield);
		x.setTesseract(getTesseract());
		ExtractionInfo eipk = (ExtractionInfo) x.extractValue("IPK");

		System.out.println(eipk.value);
//		System.out.println(x.extractValue("IPK"));
		if(1==1)return ;
		dir = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl/nim";
//		x.setOutputDirectory(dir);
		x.setOutputDirectory(null);
		tFieldFN = dir + "/nim_field.jpg";
		tfield = TemplateValueExtractor.readDeskew(tFieldFN,false);
//		System.out.println(tall);
		ExtractionInfo ei = new ExtractionInfo("pre_NIM", haystack, tfield, null);
		x.register(ei);
		x.extractValue("pre_NIM");
		tall = ei.info.expanded;
//		System.out.println(tall);
//		System.out.println(tfield);
		ei = new ExtractionInfo("NIM", haystack, tall, tfield);
		x.register(ei);
		x.setTesseract(getTesseract());
		x.getTesseract().setPageSegMode(1);
		ei = (ExtractionInfo) x.extractValue("NIM");
		System.out.println(ei.value);
//		System.out.println(tess(ei.info2.notExpanded_Right));

		
		dir = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl/ttl";
//		x.setOutputDirectory(dir);
		x.setOutputDirectory(null);
		tFieldFN = dir + "/ttl_field.jpg";
		tfield = TemplateValueExtractor.readDeskew(tFieldFN,false);
//		System.out.println(tall);
		ei = new ExtractionInfo("pre_TTL", haystack, tfield, null);
		x.register(ei);
		x.extractValue("pre_TTL");
		tall = ei.info.expanded;
//		System.out.println(tall);
//		System.out.println(tfield);
		ei = new ExtractionInfo("TTL", haystack, tall, tfield);
		x.register(ei);
		x.setTesseract(getTesseract());
		x.getTesseract().setPageSegMode(1);
		x.getTesseract().setVariable("tessedit_char_whitelist", "");//unset white list character
		ei = (ExtractionInfo) x.extractValue("TTL");
		System.out.println(ei.value);


		dir = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl/nama";
//		x.setOutputDirectory(dir);
		x.setOutputDirectory(null);
		tFieldFN = dir + "/nama_field.jpg";
		tfield = TemplateValueExtractor.readDeskew(tFieldFN,false);
//		System.out.println(tall);
		ei = new ExtractionInfo("pre_NAMA", haystack, tfield, null);
		x.register(ei);
		x.extractValue("pre_NAMA");
		tall = ei.info.expanded;
//		System.out.println(tall);
//		System.out.println(tfield);
		ei = new ExtractionInfo("NAMA", haystack, tall, tfield);
		x.register(ei);
		x.setTesseract(getTesseract());
		x.getTesseract().setPageSegMode(1);
		x.getTesseract().setVariable("tessedit_char_whitelist", "");//unset white list character
		ei = (ExtractionInfo) x.extractValue("NAMA");
		System.out.println(ei.value);


		
		if (1 == 1)
			return;

		/*
		 * Error opening data file ./eng.traineddata
		 */
		int re = ei.info2.valuePortion.rows();
//		Mat w01=ei.info2.notExpanded_Right.submat(0, re, 0, 400);
//		Mat w01=ei.info2.notExpanded_Right.submat(0, re, 0, 200);
		Mat w01 = ei.info2.valuePortion;
//		BufferedImage toTess = ImageUtils.mat2Img(w01);
		BufferedImage toTess = ImageUtils.LossyMat2BufferedImage(w01);

		if (toTess != null)
			try {
				Tesseract tesseract = new Tesseract();
				tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");

				tesseract.setVariable("tessedit_char_whitelist", " 0123456789. ,");
				tesseract.setVariable("user_defined_dpi", "300");
				int seg_mode = 11; /* 1: automatic, 11: single text line */
				tesseract.setPageSegMode(seg_mode); //
				String outputs = null;
				System.out.println(toTess);
				outputs = tesseract.doOCR(toTess);
				System.out.println("tessout: " + outputs);
				tesseract = null;
				System.gc();

			} catch (TesseractException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	static Tesseract tesseract = null;

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

	static String tess(Mat right) throws IOException {

		BufferedImage toTess = ImageUtils.LossyMat2BufferedImage(right);
		if (toTess != null)
			try {
				Tesseract tesseract = new Tesseract();
				tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");

				tesseract.setVariable("tessedit_char_whitelist", " 0123456789. ,");
				tesseract.setVariable("user_defined_dpi", "300");
				int seg_mode = 11; /* 1: automatic, 11: single text line */
				tesseract.setPageSegMode(seg_mode); //
				String outputs = null;
//				System.out.println(toTess);
				outputs = tesseract.doOCR(toTess);
//				System.out.println("tessout: " + outputs);
				tesseract = null;
				System.gc();
				return outputs;
			} catch (TesseractException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return null;
	}

	static void weird_01() throws Exception {
		String dir = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templates/nim_nama_ttl";
		Mat weird = TemplateValueExtractor.readDeskew(dir + "/weird.jpg",false);

//		Core.bitwise_not(weird, weird);
		BufferedImage toTess = ImageUtils.LossyMat2BufferedImage(weird);
//		BufferedImage toTess = ImageIO.read(new File(dir+"/weird.jpg"));
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");

		tesseract.setVariable("tessedit_char_whitelist", "0123456789. ,");
		tesseract.setVariable("user_defined_dpi", "120");
		System.out.println(tesseract.doOCR(toTess));
	}

	public static void main(String args[]) throws Exception {
//		test_limit_char(args);
//		check_file_dir(args);
		testExtractor();
//		weird_01();
	}

}
