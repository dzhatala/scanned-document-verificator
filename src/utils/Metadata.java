package utils;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;

public class Metadata {

	Node[] roots = null;
	File file = null;
	int XDensity = -1;
	int YDensity = -1;

	/*
	 * public static void main2(String[] args) { Metadata meta = new Metadata(); int
	 * length = args.length; for (int i = 0; i < length; i++)
	 * meta.readAndDisplayMetadata(args[i]); }
	 */
	public static void main(String[] args) {
		String f = "F:\\rsync\\poltek\\dns_ok_scanned\\2019\\1319144005\\1319144005_SEMESTER_V_dns_scan__20220308_0025.jpg";
//		main2(new String[] { f });
		Metadata m = new Metadata();
		m.readMetadata(f, false);
		System.out.println("Density x:" + m.XDensity + " y:" + m.YDensity);
	}
	
	
	
	public static Metadata metadataFromFile(String f) {
//		String
		f = "F:\\rsync\\poltek\\dns_ok_scanned\\2019\\1319144005\\1319144005_SEMESTER_V_dns_scan__20220308_0025.jpg";
//		main2(new String[] { f });
		Metadata m = new Metadata();
		m.readMetadata(f, false);
//		System.out.println("Density x:" + m.XDensity + " y:" + m.YDensity);
		return m;
	}

	public void readMetadata(String fileName, boolean display) {
		try {

			file = new File(fileName);
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

			if (readers.hasNext()) {

				// pick the first available ImageReader
				ImageReader reader = readers.next();

				// attach source to the reader
				reader.setInput(iis, true);

				// read metadata of first image
				IIOMetadata metadata = reader.getImageMetadata(0);

				String[] names = metadata.getMetadataFormatNames();
				roots = new Node[names.length];
				int length = names.length;
				for (int i = 0; i < length; i++) {
					if (display)
						System.out.println("Format name: " + names[i]);
					roots[i] = metadata.getAsTree(names[i]);
//					if (display)
					displayMetadata(roots[i], display);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void displayMetadata(Node root, boolean display) {
		displayMetadata(root, 0, display);
	}

	void indent(int level) {
		for (int i = 0; i < level; i++)
			System.out.print("    ");
	}

	void displayMetadata(Node node, int level, boolean display) {
		// print open tag of element
		if (display)
			indent(level);
		if (display)
			System.out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null) {

			// print attribute values
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				Node attr = map.item(i);
//				System.out.print("gNN(): " + attr.getNodeName() + " gNV()" + attr.getNodeValue() + "\"");
				if (attr.getNodeName().equalsIgnoreCase("XDensity")) {
					XDensity = Integer.parseInt(attr.getNodeValue());
//					System.out.println("Filling XDensity="+XDensity);
				}
				if (attr.getNodeName().equalsIgnoreCase("YDensity")) {
					YDensity = Integer.parseInt(attr.getNodeValue());
//					System.out.println("Filling YDensity="+YDensity);

				}
				if (display)
					System.out.print(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
			}
		}

		Node child = node.getFirstChild();
		if (child == null) {
			// no children, so close element and return
			if (display)
				System.out.println("/>");
			return;
		}

		// children, so close current tag
		if (display)
			System.out.println(">");
		while (child != null) {
			// print children recursively

			displayMetadata(child, level + 1, display);
			child = child.getNextSibling();
		}

		// print close tag of element
		if (display)
			indent(level);
		if (display)
			System.out.println("</" + node.getNodeName() + ">");
	}

	public Node[] getRoots() {
		return roots;
	}
	
	public int getXDensity() {
		return XDensity;
	}
	public int getYDensity() {
		return YDensity;
	}
}