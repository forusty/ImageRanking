package test;

import java.util.ArrayList;
import java.util.List;

public class CentoidCalculator {
	private List<Centoid> centoid = new ArrayList<Centoid>();
	private List<Bag> bags = new ArrayList<Bag>();
	private List<Image> imageSet=new ArrayList<Image>();
	public synchronized List<Centoid> getCentoid() {
		return centoid;
	}
	public synchronized void setCentoid(List<Centoid> centoid) {
		this.centoid = centoid;
	}
	public synchronized List<Bag> getBags() {
		return bags;
	}
	public synchronized void setBags(List<Bag> bags) {
		this.bags = bags;
	}
	public List<Image> getImageSet() {
		return imageSet;
	}
	public void setImageSet(List<Image> imageSet) {
		this.imageSet = imageSet;
	}
}
