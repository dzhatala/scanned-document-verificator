// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// https://ateraimemo.com/Swing/ZoomAndPanPanel.html

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.Rect;

import utils.ImageUtils;

public final class MainPanel extends JPanel {
	JScrollPane jscrl = null;
	private ZoomAndPanePanel zoomPanel = null;
	private Image _img = null;

	public ZoomAndPanePanel getZoomPanel() {
		return zoomPanel;
	}

	public MainPanel() {
		this(null);
	}

	public MainPanel(Image imgPrm) {
		super(new BorderLayout());
		try {
			_img = imgPrm;
			if (imgPrm == null)
				_img = ImageIO.read(getClass().getResource("CRW_3857_JFR.jpg"));
			zoomPanel = new ZoomAndPanePanel(_img);
			jscrl = new JScrollPane(zoomPanel);
			add(jscrl);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		setPreferredSize(new Dimension(320, 240));
	}

	public static void main(String... args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGui();
			}
		});
	}

	public static void createAndShowGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		JFrame frame = new JFrame("ZoomAndPanPanel");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new MainPanel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setRects(List<Rect> rects) {
		// TODO Auto-generated method stub
		zoomPanel.setRects(rects);
		
	}

}
