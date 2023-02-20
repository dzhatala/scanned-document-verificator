package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextUtils {

	public static void main(String args[]) {
		String str1 = "Fatmasia";
		String str2 = "Hat}asia";
		System.out.println(levehnsteinDistance(str1, str2));
		System.out.println((float) str1.length() / 2);
		System.out.println((float) str1.length() / 3);
	}

	public static int levehnsteinDistance(String s1, String s2) {
		return dist(s1.toCharArray(), s2.toCharArray());
	}

	public static int dist(char[] s1, char[] s2) {

		// distance matrix - to memoize distances between substrings
		// needed to avoid recursion
		int[][] d = new int[s1.length + 1][s2.length + 1];

		// d[i][j] - would contain distance between such substrings:
		// s1.subString(0, i) and s2.subString(0, j)

		for (int i = 0; i < s1.length + 1; i++) {
			d[i][0] = i;
		}

		for (int j = 0; j < s2.length + 1; j++) {
			d[0][j] = j;
		}

		for (int i = 1; i < s1.length + 1; i++) {
			for (int j = 1; j < s2.length + 1; j++) {
				int d1 = d[i - 1][j] + 1;
				int d2 = d[i][j - 1] + 1;
				int d3 = d[i - 1][j - 1];
				if (s1[i - 1] != s2[j - 1]) {
					d3 += 1;
				}
				d[i][j] = Math.min(Math.min(d1, d2), d3);
			}
		}
		return d[s1.length][s2.length];
	}
	
	/**
	 * 
	 * @param imageFN filename of Image(jpg) file to be recognized
	 * @return [stdout,stderr]
	 * @throws IOException
	 */
	public static String[] runTesseract(String imageFN) throws IOException {

		String ret = "";
		Runtime rt = Runtime.getRuntime();
		String[] commands = { "\"c:\\Program Files\\Tesseract-OCR\\tesseract.exe\"", "--dpi","300", imageFN, "stdout" };
		System.out.println("runTesseract: " + imageFN);
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
			serr += "stder: " + s + "\n";
		}

		return new String[] { ret, serr };

	}


}
