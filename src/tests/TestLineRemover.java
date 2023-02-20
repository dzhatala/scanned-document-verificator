package tests;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TestLineRemover {

	public static void main(String args[]) {

//		if (args.length < 1) {
//			System.out.println("usage: java TestLineRemover filename");
//			System.exit(0);
//		}

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String imageFN = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/deskew.jpg";
		Mat picture = Imgcodecs.imread(imageFN);
		Imgproc.cvtColor(picture, picture, Imgproc.COLOR_BGR2GRAY);
		Imgcodecs.imwrite("/home/meik/Pictures/asdfGray.png", picture);

		Mat blackhatElement = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(7, 7));

		Imgproc.morphologyEx(picture, picture, Imgproc.MORPH_BLACKHAT, blackhatElement);
		Imgproc.GaussianBlur(picture, picture, new Size(5, 3), 0);
		Imgproc.threshold(picture, picture, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

		System.out.println("Finished");

	}
}
