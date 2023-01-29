package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public final class ImageUtils {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * // TODO not working rotation using 360 degree angle
	 **/
	public static Mat deskew(Mat src, double angle) {
		Point center = new Point(src.width() / 2, src.height() / 2);
		Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
		// 1.0 means 100 % scale
		Size size = new Size(src.width(), src.height());
		Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
		return src;
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/**
	 * NOT USED deskew by opencv
	 * 
	 * @param inFile
	 * @return
	 */
	public static Mat computeSkew(String inFile) {
		// Load this image in grayscale

		Mat img = Imgcodecs.imread(inFile, Imgcodecs.IMREAD_GRAYSCALE);
		// Mat img2 = Imgcodecs.imread(inFile, Imgcodecs.IMREAD_GRAYSCALE);

		// Binarize it
		// Use adaptive threshold if necessary
		// Imgproc.adaptiveThreshold(img, img, 255, ADAPTIVE_THRESH_MEAN_C,
		// THRESH_BINARY, 15, 40);
		Imgproc.threshold(img, img, 200, 255, Imgproc.THRESH_BINARY);

		// Invert the colors (because objects are represented as white pixels, and the
		// background is represented by black pixels)
		Core.bitwise_not(img, img);
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

		// We can now perform our erosion, we must declare our rectangle-shaped
		// structuring element and call the erode function
		Imgproc.erode(img, img, element);

		// Find all white pixels
		Mat wLocMat = Mat.zeros(img.size(), img.type());
		Core.findNonZero(img, wLocMat);

		// Create an empty Mat and pass it to the function
		MatOfPoint matOfPoint = new MatOfPoint(wLocMat);

		// Translate MatOfPoint to MatOfPoint2f in order to user at a next step
		MatOfPoint2f mat2f = new MatOfPoint2f();
		matOfPoint.convertTo(mat2f, CvType.CV_32FC2);

		// Get rotated rect of white pixels
		RotatedRect rotatedRect = Imgproc.minAreaRect(mat2f);

		Point[] vertices = new Point[4];
		rotatedRect.points(vertices);
		List<MatOfPoint> boxContours = new ArrayList<>();
		boxContours.add(new MatOfPoint(vertices));
		Imgproc.drawContours(img, boxContours, 0, new Scalar(128, 128, 128), -1);

		double resultAngle = rotatedRect.angle;
		if (rotatedRect.size.width > rotatedRect.size.height) {
			rotatedRect.angle += 90.f;
		}

		// Or
		// rotatedRect.angle = rotatedRect.angle < -45 ? rotatedRect.angle + 90.f :
		// rotatedRect.angle;

		Mat result = deskew(Imgcodecs.imread(inFile, Imgproc.THRESH_BINARY), rotatedRect.angle);
		// Imgcodecs.imwrite( outputFile, result );
		return result;

		// rotatedRect.angle=3.14;
//		System.out.println("Rotated angle: " + rotatedRect.angle);
//		img2 = deskew(Imgcodecs.imread(inFile, Imgproc.THRESH_BINARY), rotatedRect.angle);
//		return img2;

	}

	// https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv

	public static Mat LossyBufferedImage2Mat(BufferedImage image) throws IOException {
		return LossyBufferedImage2Mat(image,Imgcodecs.IMREAD_UNCHANGED);
	}
		public static Mat LossyBufferedImage2Mat(BufferedImage image, int type) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()),  type);
	}

		public static BufferedImage mat2Img(Mat in)
	    {
	        BufferedImage out;
	        int h=in.height();
	        int w=in.width();
	        byte[] data = new byte[w * h * (int)in.elemSize()];
	        int type;
	        in.get(0, 0, data);

	        if(in.channels() == 1)
	            type = BufferedImage.TYPE_BYTE_GRAY;
	        else
	            type = BufferedImage.TYPE_3BYTE_BGR;

	        out = new BufferedImage(w, h, type);

	        out.getRaster().setDataElements(0, 0, w, h, data);
	        return out;
	    } 
	// https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv
	public static BufferedImage LossyMat2BufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
	}

	/**
	 * remove lines from grayscale 'img'
	 * 
	 * @param img
	 * @return
	 */
	public static void removeLines(final Mat img) {
		// Load this image in grayscale

//		Mat img = Imgcodecs.imread(inFile, Imgcodecs.IMREAD_GRAYSCALE);

		// Mat img2 = Imgcodecs.imread(inFile, Imgcodecs.IMREAD_GRAYSCALE);

		// Binarize it
		// Use adaptive threshold if necessary
		// Imgproc.adaptiveThreshold(img, img, 255, ADAPTIVE_THRESH_MEAN_C,
		// THRESH_BINARY, 15, 40);
		Imgproc.threshold(img, img, 200, 255, Imgproc.THRESH_BINARY);

		// Invert the colors (because objects are represented as white pixels, and the
		// background is represented by black pixels)
		Core.bitwise_not(img, img);
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

		// We can now perform our erosion, we must declare our rectangle-shaped
		// structuring element and call the erode function
		Imgproc.erode(img, img, element);

		// Find all white pixels
		Mat wLocMat = Mat.zeros(img.size(), img.type());
		Core.findNonZero(img, wLocMat);

		// Create an empty Mat and pass it to the function
		MatOfPoint matOfPoint = new MatOfPoint(wLocMat);

		// Translate MatOfPoint to MatOfPoint2f in order to user at a next step
		MatOfPoint2f mat2f = new MatOfPoint2f();
		matOfPoint.convertTo(mat2f, CvType.CV_32FC2);

		// Get rotated rect of white pixels
		RotatedRect rotatedRect = Imgproc.minAreaRect(mat2f);

		Point[] vertices = new Point[4];
		rotatedRect.points(vertices);
		List<MatOfPoint> boxContours = new ArrayList<>();
		boxContours.add(new MatOfPoint(vertices));
		Imgproc.drawContours(img, boxContours, 0, new Scalar(128, 128, 128), -1);

		double resultAngle = rotatedRect.angle;
		if (rotatedRect.size.width > rotatedRect.size.height) {
			rotatedRect.angle += 90.f;
		}
	}

}
