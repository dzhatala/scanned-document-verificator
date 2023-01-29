package example;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.opencv.core.Rect;

import example.ZoomAndPanePanel.ZoomHandler;

public class ZoomAndPanePanel extends JPanel {
	protected final AffineTransform zoomTransform = new AffineTransform();
	protected transient Image img;
	protected Rectangle imgrect;
	protected transient ZoomHandler handler;
	protected transient DragScrollListener listener;
	private List<Rect> _rects = null;

	protected ZoomAndPanePanel(Image img) {
		super();
		setImage(img);
	}

	public void setImage(Image img) {
		this.img = img;
		this.imgrect = new Rectangle(img.getWidth(this), img.getHeight(this));

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setPaint(new Color(0x55_FF_00_00, true));
		Rectangle r = new Rectangle(500, 140, 150, 150);

		// use: AffineTransform#concatenate(...) and Graphics2D#setTransform(...)
		// https://docs.oracle.com/javase/8/docs/api/java/awt/geom/AffineTransform.html#concatenate-java.awt.geom.AffineTransform-
		// AffineTransform at = g2.getTransform();
		// at.concatenate(zoomTransform);
		// g2.setTransform(at);
		// g2.drawImage(img, 0, 0, this);
		// g2.fill(r);

		// or use: Graphics2D#drawImage(Image, AffineTransform, ImageObserver)
		// https://docs.oracle.com/javase/8/docs/api/java/awt/Graphics2D.html#drawImage-java.awt.Image-java.awt.geom.AffineTransform-java.awt.image.ImageObserver-
		g2.drawImage(img, zoomTransform, this); // or: g2.drawRenderedImage((RenderedImage) img, zoomTransform);
		g2.fill(zoomTransform.createTransformedShape(r));

		// BAD EXAMPLE
		// g2.setTransform(zoomTransform);
		// g2.drawImage(img, 0, 0, this);

		if (this._rects != null) {
			// draw contoures
			for (Rect rect : _rects) {
				g2.drawRect(rect.x, rect.y, rect.width, rect.height);
			}
		}

		g2.dispose();
	}

	@Override
	public Dimension getPreferredSize() {
		Rectangle r = zoomTransform.createTransformedShape(imgrect).getBounds();
		return new Dimension(r.width, r.height);
	}

	@Override
	public void updateUI() {
		removeMouseListener(listener);
		removeMouseMotionListener(listener);
		removeMouseWheelListener(handler);
		super.updateUI();
		listener = new DragScrollListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
		handler = new ZoomHandler();
		addMouseWheelListener(handler);
	}

	protected class ZoomHandler extends MouseAdapter {
		private static final double ZOOM_MULTIPLICATION_FACTOR = 1.2;
		private static final int MIN_ZOOM = -10;
		private static final int MAX_ZOOM = 10;
		private static final int EXTENT = 1;
		private final BoundedRangeModel zoomRange = new DefaultBoundedRangeModel(0, EXTENT, MIN_ZOOM,
				MAX_ZOOM + EXTENT);

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int dir = e.getWheelRotation();
			int z = zoomRange.getValue();
			zoomRange.setValue(z + EXTENT * (dir > 0 ? -1 : 1));
			if (z != zoomRange.getValue()) {
				Component c = e.getComponent();
				Container p = SwingUtilities.getAncestorOfClass(JViewport.class, c);
				if (p instanceof JViewport) {
					JViewport vport = (JViewport) p;
					Rectangle ovr = vport.getViewRect();
					double s = dir > 0 ? 1d / ZOOM_MULTIPLICATION_FACTOR : ZOOM_MULTIPLICATION_FACTOR;
					zoomTransform.scale(s, s);
					// double s = 1d + zoomRange.getValue() * .1;
					// zoomTransform.setToScale(s, s);
					Rectangle nvr = AffineTransform.getScaleInstance(s, s).createTransformedShape(ovr).getBounds();
					Point vp = nvr.getLocation();
					vp.translate((nvr.width - ovr.width) / 2, (nvr.height - ovr.height) / 2);
					vport.setViewPosition(vp);
					c.revalidate();
					c.repaint();
				}
			}
		}
	}

	public void zoom(double scale, int dir) {
		final double ZOOM_MULTIPLICATION_FACTOR = 1.2;
		final int MIN_ZOOM = -10;
		final int MAX_ZOOM = 10;
		final int EXTENT = 1;
		final BoundedRangeModel zoomRange = new DefaultBoundedRangeModel(0, EXTENT, MIN_ZOOM, MAX_ZOOM + EXTENT);

		// int dir = e.getWheelRotation();
		int z = zoomRange.getValue();
		zoomRange.setValue(z + EXTENT * (dir > 0 ? -1 : 1));
		if (z != zoomRange.getValue()) {
			// Component c = e.getComponent();
			// Container p = SwingUtilities.getAncestorOfClass(JViewport.class, c);
			Container p = SwingUtilities.getAncestorOfClass(JViewport.class, this);
			if (p instanceof JViewport) {
				JViewport vport = (JViewport) p;
				Rectangle ovr = vport.getViewRect();
				double s = dir > 0 ? 1d / ZOOM_MULTIPLICATION_FACTOR : ZOOM_MULTIPLICATION_FACTOR;
				zoomTransform.scale(s, s);
				// double s = 1d + zoomRange.getValue() * .1;
				// zoomTransform.setToScale(s, s);
				Rectangle nvr = AffineTransform.getScaleInstance(s, s).createTransformedShape(ovr).getBounds();
				Point vp = nvr.getLocation();
				vp.translate((nvr.width - ovr.width) / 2, (nvr.height - ovr.height) / 2);
				vport.setViewPosition(vp);
				this.revalidate();
				this.repaint();
			}
		}
	}

	public void setRects(List<Rect> rects) {
		// TODO Auto-generated method stub
		this._rects = rects;
		this.revalidate();
		this.repaint();
	}

}

class DragScrollListener extends MouseAdapter {
	private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private final Point pp = new Point();

	@Override
	public void mouseDragged(MouseEvent e) {
		Component c = e.getComponent();
		Container p = SwingUtilities.getUnwrappedParent(c);
		if (p instanceof JViewport) {
			JViewport vport = (JViewport) p;
			Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
			Point vp = vport.getViewPosition();
			vp.translate(pp.x - cp.x, pp.y - cp.y);
			((JComponent) c).scrollRectToVisible(new Rectangle(vp, vport.getSize()));
			pp.setLocation(cp);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Component c = e.getComponent();
		c.setCursor(hndCursor);
		Container p = SwingUtilities.getUnwrappedParent(c);
		if (p instanceof JViewport) {
			JViewport vport = (JViewport) p;
			Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
			pp.setLocation(cp);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		e.getComponent().setCursor(defCursor);
	}
}
