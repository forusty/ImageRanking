package test;

import java.io.Serializable;
import java.util.Comparator;

public class Image implements Serializable {
	private int imagePosition;
	private int tagPosition;
	private int totalTag;
	private double rankScore;
	private String filename;
	private String[] parts;
	private String visualF1;
	private String visualF2;
	private String visualF3;
	private String url;
	private double[] pcaVisual;
	private double[] textFeature;
	private int preLabel;
	public int getImagePosition() {
		return imagePosition;
	}
	public void setImagePosition(int imagePosition) {
		this.imagePosition = imagePosition;
	}
	public int getTagPosition() {
		return tagPosition;
	}
	public void setTagPosition(int tagPosition) {
		this.tagPosition = tagPosition;
	}
	public int getTotalTag() {
		return totalTag;
	}
	public void setTotalTag(int totalTag) {
		this.totalTag = totalTag;
	}
	public double getRankScore() {
		return rankScore;
	}
	public void setRankScore(double rankScore) {
		this.rankScore = rankScore;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String[] getParts() {
		return parts;
	}
	public void setParts(String[] parts) {
		this.parts = parts;
	}
	public String getVisualF1() {
		return visualF1;
	}
	public void setVisualF1(String visualF1) {
		this.visualF1 = visualF1;
	}
	public String getVisualF2() {
		return visualF2;
	}
	public void setVisualF2(String visualF2) {
		this.visualF2 = visualF2;
	}
	public String getVisualF3() {
		return visualF3;
	}
	public void setVisualF3(String visualF3) {
		this.visualF3 = visualF3;
	}
	public String combineVisualFeatures() {
		return visualF1+" "+visualF2+" "+visualF3;
	}
	public double[] getPcaVisual() {
		return pcaVisual;
	}
	public void setPcaVisual(double[] pcaVisual) {
		this.pcaVisual = pcaVisual;
	}
	public double[] getTextFeature() {
		return textFeature;
	}
	public void setTextFeature(double[] textFeature) {
		this.textFeature = textFeature;
	}
	public int getPreLabel() {
		return preLabel;
	}
	public void setPreLabel(int preLabel) {
		this.preLabel = preLabel;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	// Comparator
    public static class CompRank implements Comparator<Image> {
        @Override
        public int compare(Image arg0, Image arg1) {
            if(arg0.getRankScore()<arg1.getRankScore()) return 1;
            if(arg0.getRankScore()>arg1.getRankScore()) return -1;
            if(arg0.getRankScore()==arg1.getRankScore())
            {
            	if(arg0.getTotalTag()<arg1.getTotalTag())return 1;
            	if(arg0.getTotalTag()>arg1.getTotalTag())return -1;
            }
            return 0;
        }
    }
}
