package tests;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.util.ImageHelper;

public class TestTESS4J_ROI_OK {
	public static void main(String[] args) throws IOException {
		Tesseract tesseract = new Tesseract();
		new Tesseract1();
		try {

			tesseract.setDatapath("c:/cygwin32/usr/share/tessdata");
			String input = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220310_0011.jpg";

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
			BufferedImage crImg = bimg.getSubimage(448, 868, 802, 43);
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

	public static String[] runTesseract(String imageFN) throws IOException {

		String ret = "";
		Runtime rt = Runtime.getRuntime();
		String[] commands = { "\"c:\\Program Files\\Tesseract-OCR\\tesseract.exe\"", imageFN, "stdout" };
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
			serr += s;
		}

		return new String[] { ret, serr };

	}

}
