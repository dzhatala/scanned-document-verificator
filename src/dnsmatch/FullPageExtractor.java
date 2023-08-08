package dnsmatch;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import net.sourceforge.tess4j.Tesseract;
import utils.ImageUtils;

public class FullPageExtractor implements FieldValueExtractor {

	private Tesseract tesseract = null;
	private Mat haystack = null;
	private String outputDir = null; // to place output
	String fullPageStr = null;
	String[] lineKeys = new String[] { "NIM13", "NAMA", "INDEKS PRESTASI(IP)" }; // TODO

	String NIM = null, NAMA = null;
	String IPK = null;

	public FullPageExtractor(String ld, Mat toTess, Tesseract t) {
		this.haystack = toTess;
		this.tesseract = t;
		this.outputDir = ld;
	}

	// do ocr once
	public void init() throws Exception {
		BufferedImage toTess = ImageUtils.LossyMat2BufferedImage(haystack);
		if (outputDir != null) {
			String outjpg = this.outputDir + "/full_page_haystack.jpg";
			System.out.println("writing to " + outjpg);
			Imgcodecs.imwrite(outjpg, haystack);
		}
		System.err.println("full page ocr with tesseract ....");
		fullPageStr = this.tesseract.doOCR(toTess);
		System.err.println("tesseract done");
		System.err.println(fullPageStr);

		// iterate over entire pages

		BufferedReader br = new BufferedReader(new StringReader(fullPageStr));
		String line = br.readLine();
		while (line != null) {
			if (NIM == null)
				NIM = lineFindNIM(line);
			if (NAMA == null)
				NAMA = foundNAMA(line);
			if (IPK == null)
				IPK = lineFindIPK(line);
			
			if(NIM!=null&&NAMA!=null&&IPK!=null)break;
			line = br.readLine();
		}
	}

	/**
	 * find NIM in line of text
	 * 
	 * @param ln
	 * @return
	 */
	public static String lineFindNIM(String ln) {
		String ret = null;
		String as[] = ln.split(":");

		for (int i = 0; i < as.length; i++) {
			if (as[i].toUpperCase().indexOf("NIM")>= 0
					| as[i].toUpperCase().indexOf("INM")
					>= 0) {
				ret = "";
				for (int j = 1; j < as.length; j++) {
					ret = ret + as[j];
				}
				ret = ret.replace(" ", "").trim();
				if(ret.startsWith("13"))ret=ret.substring(0,10); //not finding empty NIM: 1322094015) l: 11
				System.out.println("NIM found:" + ret);
				return ret;
			}
		}

		return ret;
	}

	public static String foundNAMA(String ln) {
		String ret = null;
		String hst = ln.trim().replace(" ", "");
//		hst=hst.replace(".", "");
		String as[] = ln.split(":");

		for (int i = 0; i < as.length; i++) {
			if (as[i].toUpperCase().indexOf("NAMA") >= 0) {
				ret = "";
				for (int j = 1; j < as.length; j++) {
					ret = ret + as[j];
				}
				System.out.println("NAMA found:" + ret);
				return ret;
			}
		}
		return ret;
	}

	public static String lineFindIPK(String ln) {
		String ipk = null;

		if ( (ln.indexOf("Prestasi") > 0 | ln.indexOf("IP") > 0 | 
				ln.indexOf("Indek")>=0) & ln.indexOf("=")>0
				) {
			System.err.println("ipk line detected: "+ln);
			int commaPos = ln.indexOf(",");
			if (commaPos <= 0)
				commaPos = ln.indexOf(".");
			System.err.println("check 1");
			if(commaPos>=0) {
				return (ln.substring(commaPos-1,commaPos+2));
			}
			System.err.println("check 2");
			
			if (commaPos<0) {
				System.err.println("check 3");
				String ars[]=ln.split("=");
				ars=ars[ars.length-1].trim().split(" ");
				for (int i=ars.length-1; i>=0; i--) {
					String gipk=ars[i].trim();
					System.err.println("check 4"+i+" "+ars[i]+" gipk.ln="+gipk.length());
					if (gipk.length()==3) { //missing dot
						System.err.println("iterate ipk splitted: "+gipk);
						ipk=gipk.substring(0,1)+"."+gipk.substring(1,3); //321 == 3.21
						try {
							Float.parseFloat(ipk);
						}catch (NumberFormatException fe) {
							System.err.println("length 3 but not number:" +ipk);
							continue;
						}
						return ipk;
					}
				}
			}
		}
		
		return ipk;
	}

	public void setTesseract(Tesseract t) {
		this.tesseract = t;
	}

	@Override
	public Object extractValue(String field) throws Exception {
		// TODO Auto-generated method stub
//		throw new Exception("not implemented");
		ExtractionInfo info = new ExtractionInfo();
		switch (field) {
		case "NIM":
			info.value = NIM;
			return info;
		case "NAMA":
			info = new ExtractionInfo();
			info.value = NAMA;
			return info;
		case "IPK":
			info = new ExtractionInfo();
			info.value = IPK + "";
			return info;
		default:
			return null;
		}

	}
	
	public static void main(String args[]) {
		String hy="Indek Prestasi (IP) = 19 = 321 Lulus Penuh";
		System.err.println(lineFindIPK(hy));
	}

}
