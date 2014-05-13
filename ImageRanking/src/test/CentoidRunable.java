package test;

public class CentoidRunable implements Runnable {
	CentoidCalculator cr;
	private int start;
	private int end;

	public CentoidRunable(int start, int end, CentoidCalculator cr) {
		super();
		this.start = start;
		this.end = end;
		this.cr=cr;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = start; i < end; i++) {
			Image iTemp = cr.getImageSet().get(i);
			int clusterID = 0;
			int j=0;
			double min = Integer.MAX_VALUE;
			for (Centoid cTemp : cr.getCentoid()) {
				double visualTotal = getVTotal(iTemp.getPcaVisual(),
						cTemp.getVisual());
				double textTotal = getTTotal(iTemp.getTextFeature(),
						cTemp.getTextual());
				double total = Math
						.sqrt((((0.1 * 0.1) * visualTotal) + textTotal));
				if (total < min) {
					min = total;
					clusterID = j;
				}
				j++;
			}
			cr.getBags().get(clusterID).getImage().add(iTemp);
		}
	}

	private static double getTTotal(double[] tempText, double[] tempText2) {
		// TODO Auto-generated method stub
		double total = 0;
		for (int i = 0; i < tempText.length; i++) {
			double difference = tempText[i] - tempText2[i];
			total += Math.pow(difference, 2.0);
		}
		return total;
	}

	private static double getVTotal(double[] tempVisual, double[] tempVisual2) {
		// TODO Auto-generated method stub
		double total = 0;
		for (int i = 0; i < tempVisual.length; i++) {
			double difference = tempVisual[i] - tempVisual2[i];
			total += Math.pow(difference, 2.0);
		}
		return total;
	}

}
