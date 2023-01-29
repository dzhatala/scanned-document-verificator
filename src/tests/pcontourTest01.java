package tests;

import java.util.ArrayList;

import pcontour.*;
import pcontour.PContour.Contour;
import pcontour.PContour.Point;

public class pcontourTest01 {

	int[] bitmap = new int[] { 0, 0, 0, 0, 0, 0, 0, 0,
							   0, 0, 0, 0, 1, 1, 0, 0, 
							   0, 1, 1, 1, 1, 1, 0, 0, 
							   0, 1, 1, 0, 0, 1, 1, 0, 
							   0, 1, 1, 0, 0, 1, 1, 0, 
							   0, 1, 1, 1, 1, 1, 0, 0, 
							   0, 0, 0, 1, 1, 0, 0, 0, 
							   0, 0, 0, 0, 0, 0, 0, 0, };

	int width = 8;
	int height = 8;

	public ArrayList<Contour> test01() {
		// find contours
		ArrayList<PContour.Contour> contours = new PContour().findContours(bitmap, width, height);

		// simplify the polyline
		for (int i = 0; i < contours.size(); i++) {
			//contours.get(i).points = new PContour().approxPolyDP(contours.get(i).points, 1);
			contours.get(i).points = new PContour().approxPolySimple(contours.get(i).points);
		}

		return contours;
	}

	public static void main(String args[]) {
		System.out.println("contour test lin dong 01");
		ArrayList<Contour>contours=new pcontourTest01().test01();
		System.out.println("c size: " +contours.size());
		
		for (int i=0 ; i <contours.size(); i++) {
			System.out.println("contours "+i );
				Contour c=contours.get(i);
			ArrayList<Point> ps=c.points;
			for (int ip=0 ; ip<ps.size();ip++) {
				Point p=ps.get(ip);
//				p.x=p.x+1;
//				p.y=p.y+1;
				System.out.println("plot("+p.x+","+p.y+",'square')");
			}
			
		}
	}
}
