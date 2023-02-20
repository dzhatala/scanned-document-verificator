package opencvTest01;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import example.MainPanel;
import utils.ImageUtils;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

public class HelloCVTest extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static BufferedImage bufImage = null;
	private static Mat src;
	MainPanel imgPanel = new MainPanel();
	private JTextField inputDegree;

	public HelloCVTest(MainPanel pnl) {
		this.imgPanel = pnl;
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		getContentPane().add(imgPanel, BorderLayout.CENTER);//
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnNewButton_1 = new JButton("-");
		panel_1.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imgPanel.getZoomPanel().zoom(2, 1);
			}
		});

		JButton btnNewButton = new JButton("+");
		panel_1.add(btnNewButton);

		JButton rtLeft = new JButton("left");
		rtLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotate(Integer.parseInt(inputDegree.getText()));
				// ImageUtils.deskew(getIconImage(), ABORT);
			}
		});
		panel_1.add(rtLeft);

		JButton rtRight = new JButton("right");
		rtRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotate(-1 * Integer.parseInt(inputDegree.getText()));
			}
		});
		panel_1.add(rtRight);

		inputDegree = new JTextField();
		inputDegree.setText("10");
		panel_1.add(inputDegree);
		inputDegree.setColumns(5);

		JButton btOCR = new JButton("OCR");
		btOCR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOCR();
			}
		});

		JButton btContours = new JButton("contours");
		btContours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					drawContours(pnl);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_1.add(btContours);
		panel_1.add(btOCR);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				imgPanel.getZoomPanel().zoom(2, -1);
			}
		});
	}

	protected void drawContours(MainPanel pnl) throws IOException {
		// TODO Auto-generated method stub
		Mat m = fromBufferedImage(bufImage);
		System.out.println("from bI -> mat : w" + m.width() + " h:" + m.height());
//		Highgui.im
//		if (1 == 1)
//			return;
		List<MatOfPoint> contours = findCountours(m);

		ArrayList<Rect> rects = new ArrayList();
		for (int i = 0; i < contours.size(); i++) {
			if (Imgproc.contourArea(contours.get(i)) > 50) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				rects.add(rect);
//				 cont_area[i] = Imgproc.contourArea(contours.get(i));

				System.out.println(rect.x + "-" + rect.y + "-" + rect.height + "-" + rect.width);
			}
		}

		pnl.setRects(rects);

	}

	protected void doOCR() {
		// TODO Auto-generated method stub
		// org.opencv.
	}

	protected void rotate(int parseInt) {
		// TODO Auto-generated method stub
		System.out.println("rotate : " + parseInt);
		src = ImageUtils.deskew(src, parseInt);
		bufImage = toBufferedImage(src);
		imgPanel.getZoomPanel().setImage(bufImage);
		revalidate();
		repaint();

	}

	public static void testCV(String[] args) {
		System.out.println("running ... ");
//		// TODO Auto-generated method stub
		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
		System.out.println("mat = " + mat.dump());

		String input = "C:/EXAMPLES/OpenCV/sample.jpg";

	}

//	https://stackoverflow.com/questions/26814069/how-to-set-region-of-interest-in-opencv-java
	public static void testFindCountours(String[] args) {
//		testLEFTRight(args);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String dns_file = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220310_0011.jpg";
		Mat img_grayROI = Imgcodecs.imread(dns_file, Imgcodecs.IMREAD_GRAYSCALE);

		Imgproc.GaussianBlur(img_grayROI, img_grayROI, new Size(15, 15), 50.00);
		// Imgproc.THRESH_BINARY_INV, 15, 4);

		Imgproc.threshold(img_grayROI, img_grayROI, -1, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

		Imgproc.dilate(img_grayROI, img_grayROI, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));

		Mat heirarchy = new Mat();
		Point shift = new Point(150, 0);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(img_grayROI, contours, heirarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		double[] cont_area = new double[contours.size()];

		for (int i = 0; i < contours.size(); i++) {
			if (Imgproc.contourArea(contours.get(i)) > 50) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				cont_area[i] = Imgproc.contourArea(contours.get(i));

//				if (rect.height > 25) {
//					Core.rectangle(result, new Point(rect.x, rect.y),
//							new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));

				System.out.println(rect.x + "-" + rect.y + "-" + rect.height + "-" + rect.width);
//					HighGui.imwrite(ROI_file, result);
//				}
			}
		}
	}

	public static List<MatOfPoint> findCountours(Mat img_grayROI) {
//		testLEFTRight(args);
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		String imageFN = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220310_0011.jpg";
//		Mat img_grayROI = Imgcodecs.imread(imageFN, Imgcodecs.IMREAD_GRAYSCALE);

		Imgproc.GaussianBlur(img_grayROI, img_grayROI, new Size(15, 15), 50.00);
		// Imgproc.THRESH_BINARY_INV, 15, 4);

		Imgproc.threshold(img_grayROI, img_grayROI, -1, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

		Imgproc.dilate(img_grayROI, img_grayROI, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));

		Mat heirarchy = new Mat();
		Point shift = new Point(150, 0);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(img_grayROI, contours, heirarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		return contours;

	}

	public static void main(String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//		String input = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/dns_scan__20220310_0011.jpg";
		String input = "F:/rsync/RESEARCHS/text_recognition_ocr_dns_scan/data/deskew.jpg";

		// Reading the image
		src = Imgcodecs.imread(input, Imgcodecs.IMREAD_GRAYSCALE);
//		src = ImageUtils.computeSkew(input);

		int cols = src.cols();
		int rows = src.rows();
		System.out.println("cols:" + cols + " , rows:" + rows);
		if (rows <= 0 | cols <= 0) {
			System.exit(-1);
		}
		byte[] data1 = new byte[src.rows() * src.cols() * (int) (src.elemSize())];
		src.get(0, 0, data1);

		bufImage = toBufferedImage(src);

		HelloCVTest frame = new HelloCVTest(new MainPanel(bufImage));

		// frame.pack();
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static BufferedImage toBufferedImage(Mat m) {
		if (!m.empty()) {
			int type = BufferedImage.TYPE_BYTE_GRAY;
			if (m.channels() > 1) {
				type = BufferedImage.TYPE_3BYTE_BGR;
			}
			int bufferSize = m.channels() * m.cols() * m.rows();
			byte[] b = new byte[bufferSize];
			m.get(0, 0, b); // get all the pixels
			BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
			final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			System.arraycopy(b, 0, targetPixels, 0, b.length);
			return image;
		}

		return null;
	}

	public Mat fromBufferedImage(BufferedImage image) throws IOException {
//		https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_ANYCOLOR);

//		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//		
//		System.out.println("");
//
//		Mat m = new Mat();
//
//		m.put(0, 0, pixels);
//
//		return m;

	}
}
