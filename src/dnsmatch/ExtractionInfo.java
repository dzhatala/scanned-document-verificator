package dnsmatch;

import org.opencv.core.Mat;

import dnsmatch.TemplateValueExtractor.Expansion;

public class ExtractionInfo {
	
	public ExtractionInfo() {
		
	}
	public ExtractionInfo(String field,Mat haystack, Mat tall, Mat tfield) {
		// TODO Auto-generated constructor stub
		this.field=field;
		this.haystack=haystack;
		this.templateAll=tall;
		this.templateField=tfield;
	}
	String field;
	Mat haystack;
	Mat templateAll;
	Mat templateField;
	public String value;//a value for a field after extracted after extracted
	public MatchInfo info;//level 1 info
	public MatchInfo info2;//level 2 info
	public Expansion expansion=Expansion.RIGHT;
	
	public String toString() {
		return "haystack: "+haystack +
				"\n" + "templateAll: " + templateAll+
				"\n" +"templateField: "+templateField; 
				
	}
}
