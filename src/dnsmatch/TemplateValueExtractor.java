package dnsmatch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import tests.rev_03_DNSChecker;
import utils.ImageUtils;
import utils.Metadata;

/**
 * Extract a value
 * 
 * @author yoga520
 *
 */
public class TemplateValueExtractor implements FieldValueExtractor {

	public static enum Expansion {
		RIGHT, LEFT, TOP, DOWN
	};

	private static final int LETTER_WIDTH = 20; // TODO approximate letter width in pixels ?
	String outputDir; // output directory for debugging
	ArrayList<ExtractionInfo> fields = new ArrayList<ExtractionInfo>();
	Hashtable<String, ExtractionInfo> field2Info = new Hashtable<String, ExtractionInfo>();
	private Tesseract tesseract = null;// OCR

	public Tesseract getTesseract() {
		return tesseract;
	}

	public void setTesseract(Tesseract tesseract) {
		this.tesseract = tesseract;
	}

	public TemplateValueExtractor(String outputDir) {
		this.outputDir = outputDir; // instance vs params (can be override by static)
	}

	// register field to be found in Mat
	/**
	 * 
	 * @param field         label or field or keyword
	 * @param haystack      big document
	 * @param templateAll   portion of haystack contain field + value
	 * @param templateField portion of templateAll contain only field without value
	 *                      ?
	 */
	public void register(String field, Mat haystack, Mat templateAll, Mat templateField, Expansion ex)
			throws Exception {
		ExtractionInfo info = new ExtractionInfo();
		info.field = field;
		info.haystack = haystack;
		info.templateAll = templateAll;
		info.templateField = templateField;
		info.expansion = ex;
		register(info);
	}

	public void register(String field, Mat haystack, Mat templateAll, Mat templateField) throws Exception {
		register(field, haystack, templateAll, templateField, Expansion.RIGHT);
	}

	protected void templateExtract(ExtractionInfo info) {

	}

	@Override
	/**
	 * 
	 */
	public Object extractValue(String field) throws Exception {
		
		ExtractionInfo ret = field2Info.getOrDefault(field, null);
		if (ret != null) {
			if (ret.value == null) {
				System.out.println("xV() "+field);
//				System.out.println(ret.haystack);
//				System.out.println(ret.templateAll);
				MatchInfo info = templateMatching(ret.templateAll, ret.haystack);
				if (outputDir != null) {
					System.out.println("Write to: " + outputDir);
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_NEXP_LR_1ST.jpg", info.matchNoExpanded);
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_EXP_LR_1ST.jpg", info.expanded);
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_haystack.jpg", ret.haystack);
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_tall_1ST.jpg", ret.templateAll);
				}
				ret.info = info;
				//RETURNING ... nothing to extract
				if (ret.templateField == null) {
					System.err.println("xv() tField is "+ret.templateField);
					return ret;
				}
				System.out.println("creating info2");
				
				//second level if feld is NOT null
				MatchInfo info2 = templateMatching(ret.templateField, info.matchNoExpanded);
				ret.info2 = info2;
				if (outputDir != null) {
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_NEXP_LR_2ND.jpg", info2.matchNoExpanded);
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_EXP_R_2ND.jpg", info2.valuePortion);
					Imgcodecs.imwrite(this.outputDir + "/" + field + "_Tfield.jpg", ret.templateField);
				}

				BufferedImage toTess = ImageUtils.LossyMat2BufferedImage(info2.valuePortion);

				System.out.println("ocr : teseract="+tesseract);
				System.out.println("ocr : goTess="+toTess);
				if (toTess != null && tesseract != null) {
					try {
						
						ret.value = tesseract.doOCR(toTess);
						// do ocr failed but cygwin tess ok ?
						String tessFname =null;
						if (outputDir != null) {
							tessFname = this.outputDir + "/" + field + "_toTess.jpg";
							System.out.println("write to " + tessFname);
							ImageIO.write(toTess, "jpg", new File(tessFname));
							
						}
						if (tessFname!=null && ret.value.length()==0 ) {
							System.err.println("cmd: tesseract.exe "+tessFname);
							utils.TextUtils.runTesseract(tessFname);
						}
						System.err.println(ret.value);
						return ret;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				return ret;

			} else {
				return ret;
			}
		} else {
			throw new Exception("Not found: " + field);
		}
	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * 
	 * @param template to be found in source
	 * @param source
	 * @return
	 * @throws IOException 
	 */
	public static MatchInfo templateMatching(Mat template, Mat source, Expansion ex) throws IOException {
		//System.out.format("Template matching %d in %d \n", template.height(), source.height());
//		https: // riptutorial.com/opencv/example/22915/template-matching-with-java
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat outputImage = new Mat();
		int machMethod = Imgproc.TM_CCOEFF;
		// Template matching method
		Imgproc.matchTemplate(source, template, outputImage, machMethod);
		MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
		Point matchLoc = mmr.maxLoc;
		MatchInfo ret = new MatchInfo();
		ret.expansion = ex;
		ret.rect = new Rect(matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()));
		ret.source = source;
		ret.template = template;
		ret.mmr = mmr;
		Mat scopy = source.clone();
		Point lt = new Point(ret.rect.x, ret.rect.y); // left top
		Point rb = new Point(lt.x + template.cols(), lt.y + template.rows()); // right bottom

		ret.matchNoExpanded = scopy.submat((int) lt.y, (int) rb.y, (int) lt.x, (int) rb.x);
//		ImageIO.write(ImageUtils.LossyMat2BufferedImage(ret.matchNoExpanded),"jpg",
//				new File("F:/rsync/eclipse2022_wspace/opencvTest01/logROI/log1.jpg" ));
		if (ret.expansion == Expansion.LEFT || ret.expansion == Expansion.RIGHT)
			ret.expanded = scopy.submat((int) lt.y, (int) (lt.y + template.rows()), (int) lt.x, (int) source.cols());
		if (ret.expansion == Expansion.RIGHT) {
			ret.valuePortion = scopy.submat((int) lt.y, (int) rb.y, (int) rb.x, source.cols()); // rb.x is a start
		}
		return ret;
	}

	public static MatchInfo templateMatching(Mat template, Mat source) throws Exception {
		return templateMatching(template, source, Expansion.RIGHT);
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
	

	

	/**
	 * read and deskew
	 * 
	 * @throws Exception
	 **/
	public static Mat readDeskew(String imageFN, boolean doSkew) throws Exception {

		System.out.println("Read Deskew");
		Metadata m = Metadata.metadataFromFile(imageFN);
		System.out.println("Density: " + m.getXDensity() + " " + m.getYDensity());
		if (m.getXDensity() < 300 || m.getYDensity() < 300)
			throw new Exception("Not sufficient DPI densities");

		BufferedImage bimg = ImageIO.read(new File(imageFN));
		bimg = ImageHelper.convertImageToBinary(bimg);

//		System.out.println("Warning deskew is not performed");
		
		if(doSkew) 
		bimg = deskew(bimg);

		Mat matSrc = ImageUtils.LossyBufferedImage2Mat(bimg, Imgcodecs.IMREAD_GRAYSCALE);

		return matSrc;

	}

	public static void testNotExpandedRight() throws Exception {
		String dir = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/test/testTemplate";
		String hFN = dir + "/haystack.jpg";
		String tFN = dir + "/template.jpg";
		Mat haystack = readDeskew(hFN,true);
		Mat template = readDeskew(tFN,true);
//		System.out.println(haystack);
//		System.out.println(template);
		MatchInfo info = templateMatching(template, haystack);
		Imgcodecs.imwrite(dir + "/EXP_LR.jpg", info.expanded);
		Imgcodecs.imwrite(dir + "/NOTEXP_LR.jpg", info.valuePortion);

		BufferedImage toTess = ImageUtils.LossyMat2BufferedImage(info.valuePortion);

		Tesseract tesseract = new Tesseract();
		// Error opening data file ./eng.traineddata
		tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");
		tesseract.setVariable("tessedit_char_whitelist", "0123456789.,");
		tesseract.setVariable("user_defined_dpi", "300");
		String outputs = tesseract.doOCR(toTess);
		System.out.println(outputs);
	}

	public void register(ExtractionInfo info) throws Exception {
		// TODO Auto-generated method stub
		if (field2Info.get(info.field) != null)
			throw new Exception("Already Exist: " + info.field);
		if (info.field == null)
			throw new Exception("Can't register null field");
		if (info.haystack == null)
			throw new Exception("Can't register null haystack");
		if (info.templateAll == null)
			throw new Exception("Can't register null templateAll");
		if (info.templateField == null)
			System.err.println("Warning templateField is " + info.templateField);

		fields.add(info);
		field2Info.put(info.field, info);

	}

	public void setOutputDirectory(String dir) {
		// TODO Auto-generated method stub
		this.outputDir = dir;
	}

	public static void main(String[] args) throws Exception {
		// testExtractor();
	}

}
