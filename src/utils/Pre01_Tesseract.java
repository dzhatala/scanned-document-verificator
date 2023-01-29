package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;

import net.sourceforge.tess4j.util.ImageHelper;

/** Preprocessing for tesseract sequence 01 **/
public class Pre01_Tesseract {

	public static void main(String[] args) throws IOException {

		if (args.length < 2) {
			System.out.println(" usage:  java (-d | -f)  file_folder_name [suffix] ");
			System.exit(0);
		}

		boolean use_files = args[0].equalsIgnoreCase("-f");
		boolean use_dirs = args[0].equalsIgnoreCase("-d");

		if (!(use_files | use_dirs)) {
			System.out.println(" bad switch:  " + args[0]);
			System.exit(0);
		}

		if (use_dirs) {
//			System.out.println("using dirs not implemented");
//			System.exit(-1);
		}

		if (use_files)
			System.out.println("using files");

		// using files
		for (int i = 1; i < args.length; i++) {
			System.out.println("Pre01 " + args[i]);
			singleFilePre01(args[i]);
		}

		System.exit(-1);

	}

	static String singleFilePre01(String input) {
		try {

//			String input = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220310_0011.jpg";

			String output = FilenameUtils.removeExtension(input) + "_pre01." + FilenameUtils.getExtension(input);
			// the path of your tess data folder
			// inside the extracted file

			BufferedImage bimg = ImageIO.read(new File(input));
			bimg = ImageHelper.convertImageToBinary(bimg);
//			System.out.println("loaded " + bimg.getWidth() + "x" + bimg.getHeight());

			ImageDeskew dskew = new ImageDeskew(bimg);
			double imageSkewAngle = dskew.getSkewAngle();
//			System.out.println("skew angle: " + imageSkewAngle);

			double skewThreshold = 0.05;

			if (imageSkewAngle > skewThreshold || imageSkewAngle < -skewThreshold) {
				System.out.println("rotating " + imageSkewAngle);
				bimg = ImageUtil.rotate(bimg, -imageSkewAngle, bimg.getWidth() / 2, bimg.getHeight() / 2);
			}

			ImageIO.write(bimg, "jpg", new File(output));
			System.out.println("write: " + output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	static Rect templateMatching(String needleFN, String hayFN) {
		https: // riptutorial.com/opencv/example/22915/template-matching-with-java

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = null;
		Mat template = null;

//        String filePath="C:\\Users\\mesutpiskin\\Desktop\\Object Detection\\Template Matching\\Sample Image\\";
//        Load image file
		source = Imgcodecs.imread(hayFN);
		template = Imgcodecs.imread(needleFN);

		Mat outputImage = new Mat();
		int machMethod = Imgproc.TM_CCOEFF;
		// Template matching method
		Imgproc.matchTemplate(source, template, outputImage, machMethod);

		MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
		Point matchLoc = mmr.maxLoc;
		// Draw rectangle on result image
		Imgproc.rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()),
				new Scalar(255, 255, 255));

		String output = FilenameUtils.removeExtension(hayFN) + "_matched." + FilenameUtils.getExtension(needleFN);

		Imgcodecs.imwrite(output, source);
		System.out.println("Completed.");
		return null;
	}

}
