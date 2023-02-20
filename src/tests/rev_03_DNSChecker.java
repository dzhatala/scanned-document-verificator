package tests;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.Sanselan;
//import org.apache.sanselan.ImageInfo;
//import org.apache.sanselan.ImageReadException;
//import org.apache.sanselan.Sanselan;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.NodeList;

import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;

import dnsmatch.MatchInfo;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import utils.ImageUtils;
import utils.Metadata;
import utils.TextUtils;

/**
 *  last working 
 * @author User
 *
 */
public class rev_03_DNSChecker {

	private static int LETTER_WIDTH = 20; // TODO approximate letter width in pixels ?

	/**
	 * // TODO error not used
	 * 
	 * @param fName
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws org.apache.sanselan.ImageReadException 
	 */
	public static void imageInfoDPI(String fName) throws IOException, ImageReadException, org.apache.sanselan.ImageReadException {
		File input = new File(fName);

		System.out.println(" Check image : " + fName);
		ImageInputStream stream = ImageIO.createImageInputStream(input);
		Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);

		if (readers.hasNext()) {
			ImageReader reader = readers.next();
			reader.setInput(stream);

			IIOMetadata metadata = reader.getImageMetadata(0);
			IIOMetadataNode standardTree = (IIOMetadataNode) metadata
					.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
			IIOMetadataNode dimension = (IIOMetadataNode) standardTree.getElementsByTagName("Dimension").item(0);
			float horizontalPixelSizeMM = getPixelSizeMM(dimension, "HorizontalPixelSize");
			float verticalPixelSizeMM = getPixelSizeMM(dimension, "VerticalPixelSize");

			// TODO: Convert pixelsPerMM to DPI left as an exercise to the reader.. ;-)

			System.out.println("horizontalPixelSizeMM: " + horizontalPixelSizeMM);
			System.out.println("verticalPixelSizeMM: " + verticalPixelSizeMM);
		} else {
			System.err.printf("Could not read %s\n", input);
		}

		final ImageInfo imageInfo = Sanselan.getImageInfo(new File(fName));
		final int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
		final int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();
		System.out.println("sanselan physicalWidthDpi :" + physicalWidthDpi);
		System.out.println("sanselan physicalHeightDpi : " + physicalHeightDpi);
	}

	private static float getPixelSizeMM(final IIOMetadataNode dimension, final String elementName) {
		// NOTE: The standard metadata format has defined dimension to pixels per
		// millimeters, not DPI...
		NodeList pixelSizes = dimension.getElementsByTagName(elementName);

		int length = pixelSizes.getLength();
		System.out.println("Pixel sizes length: " + length);
		for (int i = 0; i < length; i++) {
			System.out.println(pixelSizes.item(i));

		}

		IIOMetadataNode pixelSize = pixelSizes.getLength() > 0 ? (IIOMetadataNode) pixelSizes.item(0) : null;
		return pixelSize != null ? Float.parseFloat(pixelSize.getAttribute("value")) : -1;
	}

	/***
	 * resample to higher DPI ?
	 * 
	 * @param bimg
	 * @return
	 * @throws Exception
	 */
	static BufferedImage resample(BufferedImage bimg) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("Not Implemented");
//		return null;
	}

	/**
	 * // TODO
	 * 
	 * @param templateFN
	 * @param imageFN
	 * @return field value cropped image
	 * 
	 */

	/*
	 * not return NULL **
	 */

	static BufferedImage[] fieldValueLines(BufferedImage haystack, BufferedImage[] fields) throws Exception {
		// TODO get ROI of all FIELDs

		// TODO get a line of field, expand to a line of field value pair

		BufferedImage[] ret = new BufferedImage[0];

		// iterate through single line here
//		minfo.source.

		return ret;
	}

	static String matchTemplate(String imageFN, String templateFN, boolean expanded_left_right) throws Exception {
		MatchInfo ret = matchTemplateInfo(imageFN, templateFN, expanded_left_right);
		return ret.writtenFoundFN;

	}

	/**
	 * 
	 * @param imageFN
	 * @param templateFN
	 * @return output Filename
	 * @throws Exception
	 */
	static MatchInfo matchTemplateInfo(String imageFN, String templateFN, boolean expanded_left_right)
			throws Exception {

		String toTesseractFN;
//		System.out.println("Single Image");
		Metadata m = Metadata.metadataFromFile(imageFN);
		if (m.getXDensity() < 300 || m.getYDensity() < 300)
			throw new Exception("Not sufficient DPI densities");

		BufferedImage bimg = ImageIO.read(new File(imageFN));
		bimg = ImageHelper.convertImageToBinary(bimg);

		System.out.println("Warning deskew is not performed");
		BufferedImage dskwdBimg = deskew(bimg);

		BufferedImage tImg = ImageIO.read(new File(templateFN));
		tImg = ImageHelper.convertImageToBinary(tImg);

		Mat matSrc = ImageUtils.LossyBufferedImage2Mat(dskwdBimg, Imgcodecs.IMREAD_GRAYSCALE);
		Mat matTpl = ImageUtils.LossyBufferedImage2Mat(tImg, Imgcodecs.IMREAD_GRAYSCALE);
		// deskew here

		// deskew here

		// bigger 4
		MatchInfo minfo = templateMatching(matTpl, matSrc);
		minfo.haystackFN = imageFN;
		minfo.templateFN = templateFN;

		// Draw rectangle on result image
		Mat source = minfo.source;
		Mat template = minfo.template;
		Mat scopy = source.clone();
		Point lt = new Point(minfo.rect.x, minfo.rect.y); // left top
		Point rb = new Point(lt.x + template.cols(), lt.y + template.rows()); // right bottom
		Imgproc.rectangle(scopy, lt, rb, new Scalar(255, 0, 0));
//		System.out.format(" rect :  (%f,%f) -> (%f,%f) \n", lt.x, lt.y, rt.x, rt.y);

		String output = imageFN + "_matched_noexp_" + FilenameUtils.getName(templateFN) + "."
				+ FilenameUtils.getExtension(templateFN);
//		Mat rectM = source.submat((int) lt.y, (int) (lt.y + template.rows()), (int) rb.x, (int) rb.y);
		Mat rectM = source.submat((int) lt.y, (int) rb.y, (int) lt.x - LETTER_WIDTH, (int) rb.x + LETTER_WIDTH);
		minfo.matchNoExpanded = rectM;
		minfo.matchNotExpandedFN = output;
		String rectS = output;
		if (CONFIG.outputNoExpanded) {
			Imgcodecs.imwrite(output, rectM);
			System.out.println("Rectangled_no_expanded. at " + rectS);
		}
		if (!expanded_left_right) {
			Mat right = source.submat((int) lt.y, (int) rb.y, (int) rb.x, source.cols()); // rb.x is a start since rb.x
																							// is and end
			output = imageFN + "_noexp_right_" + FilenameUtils.getName(templateFN) + "."
					+ FilenameUtils.getExtension(templateFN);

			Imgcodecs.imwrite(output, right);
			minfo.valuePortionFN = output;
			minfo.valuePortion=right;
			System.out.println("no exp, Right at: " + output);
		}

		Mat expanded = source.submat((int) lt.y, (int) (lt.y + template.rows()), (int) lt.x, (int) source.cols());
		if (CONFIG.outputExpanded) {
			output = imageFN + "_matched_" + FilenameUtils.getName(templateFN) + "_expanded."
					+ FilenameUtils.getExtension(templateFN);
			Imgcodecs.imwrite(output, expanded);
//		System.out.println(expanded);
			System.out.println("Expanded. at " + output);
			minfo.expanded = expanded;
			minfo.expandedFN=output;
		}
		Mat expanded_ivt = new Mat(expanded.rows(), expanded.cols(), expanded.type());
		Core.bitwise_not(expanded, expanded_ivt);
//		Core.bitwise_or(expanded, expanded_ivt, expanded_ivt);
		output = imageFN + FilenameUtils.getName(templateFN) + "_expanded_ivt."
				+ FilenameUtils.getExtension(templateFN);
		minfo.expanded_vit = expanded_ivt;
		if (CONFIG.outputExpandedIVT) {
		Imgcodecs.imwrite(output, expanded_ivt);
		System.out.println("Expanded_ivt. at " + output);
		}
		toTesseractFN = output;
		// erotion and dilation
		// https://docs.opencv.org/4.x/d9/d61/tutorial_py_morphological_ops.html
		Mat remove_horizontal = new Mat();
		Mat horz_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
		Imgproc.morphologyEx(expanded_ivt, remove_horizontal, Imgproc.MORPH_OPEN, horz_kernel);
		Imgproc.morphologyEx(remove_horizontal, remove_horizontal, Imgproc.MORPH_CLOSE, horz_kernel);
//		horz_kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 20));
//		Imgproc.morphologyEx(expanded_ivt, remove_horizontal, Imgproc.MORPH_OPEN, horz_kernel);
//		Imgproc.morphologyEx(remove_horizontal, remove_horizontal, Imgproc.MORPH_CLOSE, horz_kernel);

		if (CONFIG.outputMorphEx) {
			output = imageFN + "_er_dil_" + FilenameUtils.getName(templateFN) + "_morphEx_."
					+ FilenameUtils.getExtension(templateFN);
			Imgcodecs.imwrite(output, remove_horizontal);
			System.out.println("morph. at " + output);
		}
//		System.out.println("Ended: singleImage()");
		if (expanded_left_right)
			minfo.writtenFoundFN = toTesseractFN;
		else
			minfo.writtenFoundFN = rectS;

		return minfo;

	}

	static float validateIPKFromText(String ocred) {
		// TODO Auto-generated method stub
//		System.out.println(ocred);
		String[] lines = ocred.replaceAll(",", ".").split("\n");// use dot as decimal separator

		boolean indexFound = false, prestasiFound = false;
		for (int i = 0; i < lines.length; i++) {

			// comma might happen acc.
			String[] words = lines[i].split(" ");
			indexFound = false;
			prestasiFound = false;

			for (int iw = 0; iw < words.length; iw++) {

//				System.out.println(iw);
				int distance = TextUtils.levehnsteinDistance(words[iw], "Indek");
//				System.out.println("Indek diff " + words[iw] + "<- " + distance);
				if (distance < 3) {
//					System.out.println("Indek found ->" + words[iw] + "<- " + distance);
					indexFound = true;
				}

				distance = TextUtils.levehnsteinDistance(words[iw], "Prestasi");
//				System.out.println("Prestasi diff " + words[iw] + "<- " + distance);
				if (distance < 3) {
//					System.out.println("Prestasi found ->" + words[iw] + "<- " + distance);
					prestasiFound = true;
				}

				if (indexFound && prestasiFound) {
					break;
				}

			} // for
//			System.out.println(indexFound+ "  "+ prestasiFound);
			if (indexFound && prestasiFound) {
				float ipk = -1;
				for (int iw = 0; iw < words.length; iw++) {
					try {
						ipk = -1;
						ipk = Float.parseFloat(words[iw]);
						if (ipk > 0) {
//							System.out.println(" ipk found : " + words[iw]);
							return ipk;
						}
					} catch (java.lang.NumberFormatException ne) {
						// ne.printStackTrace();
					}
				}
			}
		} // for
		System.out.println("failed IPK \n" + ocred);
		return -1;

	}

	static List<NameValuePair> validateTTL(String ocred) {
		// TODO Auto-generated method stub
//		System.out.println("Validate TTL");
		if (ocred != null) {
//			System.out.println(ocred);
			String[] lines = ocred.split("\n");
			List<NameValuePair> pairs = getNameValuePairs(lines);
			return pairs;
		}

		return null;
	}

	private static List<NameValuePair> getNameValuePairs(String[] lines) {
		// TODO Auto-generated method stub

		ArrayList<NameValuePair> nvpL = new ArrayList<NameValuePair>();
		if (lines != null)
			for (int i = 0; i < lines.length; i++) {
//				System.out.println("gnVPrs()" +lines[i]);
				if (lines[i].equalsIgnoreCase("\n"))
					continue;
				if (lines[i].equalsIgnoreCase(" "))
					continue;
				if (lines[i].equalsIgnoreCase("\r"))
					continue;
				if (lines[i].trim().length() > 0) {

					NameValuePair p = getNV(lines[i]);
					if (p != null)
						nvpL.add(p);
				}
			}

		return nvpL;
	}

	/***
	 * 
	 * @param line
	 */
	private static NameValuePair getNV(String line) {
		// TODO Auto-generated method stub
//		 System.out.println("getNV:" + line.length() + " " + line);
		NameValuePair ret = null;

		String[] nva = line.trim().split(":");
		if (nva.length >= 2) {
			ret = new NameValuePair();
			ret.setName(nva[0]);
			ret.setValue(nva[1]);
			System.out.println(ret + "<- " + line);
		} else {
			//todo to overwrite last, newer must have lower distance
			ret = findNameValueLVHN(line, "NIM");
			if (ret == null) {
				ret = findNameValueLVHN(line, "NAMA");
			}
			if (ret == null) {
				ret = findNameValueLVHN(line, "TEMPAT");
			}

		}

		if(ret==null)
		System.err.println("no field for: " + line	);

		return ret;
	}

	/**
	 * 
	 * @param line
	 * @param keyword must in upper case
	 */
	private static NameValuePair findNameValueLVHN(String line, String keyword) {
//		System.out.println("findNameValueLVHN("+line+","+keyword+")");
		NameValuePair ret = null;
		String[] nva = line.trim().split(" ");

		int MAX_WORD_FROM_LEFT = 3;
		int half = Math.round(((float) keyword.length()) / 2);
//		System.out.println(half);se(0);

		int MAX = MAX_WORD_FROM_LEFT;
		if (nva.length < MAX)
			MAX = nva.length;//
		for (int iw = 0; iw < MAX; iw++) {
			int distance = TextUtils.levehnsteinDistance(nva[iw].toUpperCase(), keyword);
			if (distance <= half) {
				ret = new NameValuePair();
				ret.setName(keyword);
//				System.out.println("Set name " + keyword + " by " + distance + " <= " + half);
				ret.setValue("");
				for (int ival = iw + 1; ival < nva.length; ival++) {
					ret.setValue(ret.getValue() + " " + nva[ival]);
				}
				return ret;
			}
		}

		return null; // not found

	}

	private static void se(int i) {
		// TODO Auto-generated method stub
		System.exit(i);

	}

	public static BufferedImage deskew(final BufferedImage bimg) {
		BufferedImage ret = ImageUtils.deepCopy(bimg);
		ImageDeskew dskew = new ImageDeskew(ret);
		double imageSkewAngle = dskew.getSkewAngle();
		System.out.println("skew angle: " + imageSkewAngle);

		double skewThreshold = 0.05;

		if (imageSkewAngle > skewThreshold || imageSkewAngle < -skewThreshold) {
			System.out.println("rotating " + imageSkewAngle);
			ret = ImageUtil.rotate(ret, -imageSkewAngle, ret.getWidth() / 2, ret.getHeight() / 2);
		}

		return ret;

	}

	public static void main2(String args[]) throws IOException, TesseractException {
		String ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template.jpg";
		String fsrc = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/haystack.jpg";
		// fsrc = ftpl;
		String croppedFN = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/templateToTess.jpg";

		MatchInfo info = templateMatching(ftpl, fsrc);
		Rect found = info.rect;
		//
		BufferedImage bimg = ImageIO.read(new File(fsrc));
		// bimg = ImageHelper.convertImageToBinary(bimg);

		// BufferedImage crImg = bimg.getSubimage(found.x, found.y, found.width,
		// found.height);
		BufferedImage crImg = bimg.getSubimage(found.x, found.y, bimg.getWidth() - found.x, found.height);
		ImageIO.write(crImg, "jpg", new File(croppedFN));
		// String text = tesseract.doOCR(new File(croppedFN));

		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");

		// train specific fonts

		String lines = "Program Pendidikan : Diploma IV (D4) \r\n" + "Status Mahasiswa Biasa/Beasiswa/Tugas Belajar\r\n"
				+ "Nama : Wildan Heriadi\r\n" + "NIM 1321094061\r\n" + "Tempat, Tanggal Lahir Banda 18 Juni 2003 \r\n"
				+ "Alamat Rumah Wayame\r\n" + "Telepon/HP\r\n" + "";
		lines += "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789: ,./*";
		List<Character> charsList = lines.chars() // get chars as ints
				.sorted() // sort
				.distinct() // remove duplicates
				.mapToObj(c -> (char) c) // convert int to Character
				.collect(Collectors.toList()); // collect in a List

		String whiteList = charsList.toString().trim();
		whiteList = whiteList.replaceAll("\\s+", "");
		whiteList = whiteList.replaceAll(",", "");
		whiteList = ", " + whiteList;
		System.out.println("tess_white_list: " + whiteList);
		// TODO extract uniqchars
		tesseract.setVariable("tessedit_char_whitelist", whiteList);

		// TODO calculate words matching percentage

		// TODO aligning with template statements ...

		tesseract.setVariable("load_system_dawg", "F");
		tesseract.setVariable("load_freq_dawg", "F");

		// TODO not working
		tesseract.setVariable("user_words_suffix", "user-words");

		String text = tesseract.doOCR(crImg);
		System.out.println(text);

		// TODO levenhstein distance for accuracy

		System.out.println(utils.TextUtils.levehnsteinDistance("NIM", "NIM") + "");
		System.out.println(utils.TextUtils.levehnsteinDistance("NAM", "NIM"));
		System.out.println(utils.TextUtils.levehnsteinDistance("NM", "NIM"));
		System.out.println(utils.TextUtils.levehnsteinDistance("hello", "NIM"));
	}

	public static MatchInfo templateMatching(String needleFN, String hayFN) {

		Mat source = null;
		Mat template = null;

//        String filePath="C:\\Users\\mesutpiskin\\Desktop\\Object Detection\\Template Matching\\Sample Image\\";
//        Load image file
		source = Imgcodecs.imread(hayFN);
		template = Imgcodecs.imread(needleFN);

		MatchInfo ret = templateMatching(source, template);

		// Draw rectangle on result image
		Mat scopy = source.clone();
		Point lt = new Point(ret.rect.x, ret.rect.y);
		Point rt = new Point(lt.x + template.cols(), lt.y + template.rows());
		Imgproc.rectangle(scopy, lt, rt, new Scalar(255, 0, 0));

		// FilenameUtils.
		String output = hayFN + "_matched_" + FilenameUtils.getName(needleFN) + "."
				+ FilenameUtils.getExtension(needleFN);

		Imgcodecs.imwrite(output, scopy);
		System.out.println("Completed. at " + output);

		return ret;
	}

	/**
	 * find
	 * 
	 * @param template in
	 * @param source
	 * @return finding information
	 */
	public static MatchInfo templateMatching(Mat template, Mat source) {
//		System.out.format("Template matching %d in %d \n", template.height(), source.height());
//		https: // riptutorial.com/opencv/example/22915/template-matching-with-java
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat outputImage = new Mat();
		int machMethod = Imgproc.TM_CCOEFF;
		// Template matching method
		Imgproc.matchTemplate(source, template, outputImage, machMethod);
		MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
		Point matchLoc = mmr.maxLoc;
		MatchInfo ret = new MatchInfo();
		ret.rect = new Rect(matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()));
		ret.source = source;
		ret.template = template;
		ret.mmr = mmr;
		return ret;
	}

	public static void testCroppedROIOK(String[] args) throws IOException {
		Tesseract tesseract = new Tesseract();
		new Tesseract1();
		try {

			tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");
			String input = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220310_0011.jpg";
			input = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220825_0030.jpg";

			String output = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/deskew.jpg";
			String croppedFN = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/cropped_01.jpg";
			String binaryFN = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/binary.jpg";
			// the path of your tess data folder
			// inside the extracted file

			BufferedImage bimg = ImageIO.read(new File(input));
			bimg = ImageHelper.convertImageToBinary(bimg);
			System.out.println("loaded " + bimg.getWidth() + "x" + bimg.getHeight());

			String roiF = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/cropped1.jpg";

			ImageDeskew dskew = new ImageDeskew(bimg);
			double imageSkewAngle = dskew.getSkewAngle();
			System.out.println("skew angle: " + imageSkewAngle);

			double skewThreshold = 0.05;

			if (imageSkewAngle > skewThreshold || imageSkewAngle < -skewThreshold) {
				System.out.println("rotating " + imageSkewAngle);
				bimg = ImageUtil.rotate(bimg, -imageSkewAngle, bimg.getWidth() / 2, bimg.getHeight() / 2);
			}

			ImageIO.write(bimg, "jpg", new File(binaryFN));
//			BufferedImage crImg = bimg.getSubimage(448, 868, 802, 43);
			BufferedImage crImg = bimg.getSubimage(320, 830, 868, 43);
			crImg = bimg;// full no cropping
			ImageIO.write(crImg, "jpg", new File(croppedFN));
			// String text = tesseract.doOCR(new File(croppedFN));

			String text = tesseract.doOCR(crImg);
			System.out.println(text);
			String[] rets = runTesseract(croppedFN);
			// path of your image file
			System.out.print(rets[0]);

			System.out.println("QUIT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param imageFN filename of Image(jpg) file to be recognized
	 * @return [stdout,stderr]
	 * @throws IOException
	 */
	public static String[] runTesseract(String imageFN) throws IOException {

		String ret = "";
		Runtime rt = Runtime.getRuntime();
		String[] commands = { "\"c:\\Program Files\\Tesseract-OCR\\tesseract.exe\"", "--dpi","300", imageFN, "stdout" };
		System.out.println("runTesseract: " + imageFN);
		Process proc = rt.exec(commands);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		// Read the output from the command
//		System.out.println("Here is the standard output of the command:\n");
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			// System.out.println(s);
			ret += s + "\n";
		}

		String serr = null;
		s = null;
		// Read any errors from the attempted command
//		System.out.println("Here is the standard error of the command (if any):\n");
		while ((s = stdError.readLine()) != null) {
			// System.out.println(s);
			serr += "stder: " + s + "\n";
		}

		return new String[] { ret, serr };

	}

	public static void main(String[] args) throws Exception {
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
		String toTesseractFN = matchTemplate(fsrc, ftpl, true);
		if (toTesseractFN != null) {
			
			String[] ocred = runTesseract(toTesseractFN);
//			
			List<NameValuePair> list = validateTTL(ocred[0]);// stdout
			if (list != null) {
				ArrayList al = (ArrayList) list;
			}
		}

//		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK.jpg";
		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_NUM_01.jpg";
		toTesseractFN = matchTemplate(fsrc, ftpl, true);
		float ipk = -1;
		if (toTesseractFN != null) {
			String[] ocred = runTesseract(toTesseractFN);
			ipk = validateIPKFromText(ocred[0]);
//			System.out.println("ipk " + ipk);
		}

		ipk = (float) -1.0;
		ftpl = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/template_IPK_LEFT_TO_NUM_DOT.jpg";
		if (ipk < 0) {
			// validateFromContourPart(toTesseractFN);

			// more template matching
			MatchInfo info = matchTemplateInfo(toTesseractFN, ftpl, false);

			// System.out.println("Right on: "+info.);

		}
//		test03();
	}

	private static void validateFromContourPart(String toTesseractFN) {
		// TODO Auto-generated method stub
		System.out.println("VFCP " + toTesseractFN);

	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	static void test03() throws Exception {

		System.out.println("test03()");
		String needleFN = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/nim_ttl_values_white_on_black.jpg";

		Mat image = Imgcodecs.imread(needleFN, Imgcodecs.IMREAD_GRAYSCALE);
		// image.gra
		if (image.empty() == true) {
			System.out.println("Error: no image found!");
		}

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat image32S = new Mat();
		image.convertTo(image32S, CvType.CV_32SC1);

		System.out.println(image32S);

		Imgproc.findContours(image32S, contours, new Mat(), Imgproc.RETR_FLOODFILL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Draw all the contours such that they are filled in.
		Mat contourImg = new Mat(image32S.size(), image32S.type());
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(contourImg, contours, i, new Scalar(255, 255, 255), -1);
		}

//		Highgui.imwrite("debug_image.jpg", contourImg); // DEBUG

		// FilenameUtils.
		String output = needleFN + "_test03_" + FilenameUtils.getName(needleFN) + "." + "jpg";

		Imgcodecs.imwrite(output, contourImg);
		System.out.println("test03 Completed. at " + output);

	}
}
