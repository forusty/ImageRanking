package test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Bag {
	private List<Image> Image;
	private double bagScore;
	
	public Bag()
	{
		setImage(new ArrayList<Image>());
	}
	public Bag(List<Image> image)
	{
		setImage(image);
	}
	public List<Image> getImage() {
		return Image;
	}

	public void setImage(List<Image> image) {
		Image = image;
	}

	public double getBagScore() {
		return bagScore;
	}

	public void setBagScore(double bagScore) {
		this.bagScore = bagScore;
	}
	// Comparator
    public static class CompRank implements Comparator<Bag> {
        @Override
        public int compare(Bag arg0, Bag arg1) {
            if(arg0.getBagScore()<arg1.getBagScore()) return 1;
            if(arg0.getBagScore()>arg1.getBagScore()) return -1;
            return 0;
        }
    }
}
