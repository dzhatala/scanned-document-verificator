package dnsmatch;

import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import dnsmatch.TemplateValueExtractor.Expansion;

public class MatchInfo {
	public String haystackFN; // cache haystack filename
	public String templateFN;// cache template filename
	public String writtenFoundFN; // template found written filename
	public String valuePortionFN; // filename of outer right
	public String matchNotExpandedFN; // exatcly found on haystack with no expansion
	public String expandedFN;
	public Rect rect;
	public Mat source;
	public Mat template;	
	public MinMaxLocResult mmr;
	public Mat matchNoExpanded;
	public Mat valuePortion;
	public Mat expanded;
	public Mat expanded_vit;
	public Expansion expansion=Expansion.RIGHT;

}
