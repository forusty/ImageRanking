package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import libsvm.*;

public class SVMGeneration {
	public SVMGeneration() {

	}

	private static final int numberOfInstances = 15;
	
	@SuppressWarnings("rawtypes")
	private static List<Vector> AddAllNodes(List<Image> allBag) {
		// TODO Auto-generated method stub
		List<Vector> vxy = new ArrayList<>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		Vector<Double> vy = new Vector<Double>();
		// for each bag in positive bag
		for (Image img : allBag) {
			// get the visual and textual info for that one instances
			double[] pcaVisual = img.getPcaVisual();
			double[] textual = img.getTextFeature();
			// create a svm node to store all the dimension in it
			svm_node[] node = new svm_node[pcaVisual.length + textual.length];
			int counter = 0;
			// for each dimension in pca
			for (int j = 0; j < pcaVisual.length; j++) {
				// create a new node
				node[counter] = new svm_node();
				// set the index of the dimension and value of it
				// aka 1:100 2:200 etc.
				node[counter].index = counter + 1;
				node[counter].value = pcaVisual[j];
				counter++;
			}
			for (int k = 0; k < textual.length; k++) {
				// same as above 1:1 2:2
				node[counter] = new svm_node();
				node[counter].index = counter + 1;
				node[counter].value = textual[k];
				counter++;
			}
			// Add it to a vector double
			vx.addElement(node);
			// assign the label for the image +1/-1
			vy.add(1.0);
		}
		vxy.add(vx);
		vxy.add(vy);
		return vxy;
	}
	//for all bags for predicting
	@SuppressWarnings("rawtypes")
	public static svm_problem GenerateSVMProblemAllBag(List<Image> allBag) {
		// TODO Auto-generated method stub
		svm_problem problem = new svm_problem();
		// Set problem size
		problem.l = allBag.size();
		// Set number of labels for each image
		problem.y = new double[problem.l];
		// set the number of images to store dimension for
		problem.x = new svm_node[problem.l][];
		List<Vector> vxy = AddAllNodes(allBag);
		for (int i = 0; i < problem.l; i++) {
			problem.x[i] = (svm_node[]) vxy.get(0).elementAt(i);
		}
		for (int i = 0; i < problem.l; i++) {
			problem.y[i] = (double) vxy.get(1).elementAt(i);
		}
		return problem;
	}
	@SuppressWarnings("rawtypes")
	private static List<Vector> AddNodes(List<Bag> posBag, List<Bag> negBag,
			int numOfBags) {
		// TODO Auto-generated method stub
		List<Vector> vxy = new ArrayList<>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		Vector<Double> vy = new Vector<Double>();
		// for each bag in positive bag
		for (int l = 0; l < numOfBags; l++) {
			// for each instance in the bag
			Bag bg = posBag.get(l);
			for (int i = 0; i < bg.getImage().size(); i++) {
				// get the visual and textual info for that one instances
				double[] pcaVisual = bg.getImage().get(i).getPcaVisual();
				double[] textual = bg.getImage().get(i).getTextFeature();
				// create a svm node to store all the dimension in it
				svm_node[] node = new svm_node[pcaVisual.length
						+ textual.length];
				int counter = 0;
				// for each dimension in pca
				for (int j = 0; j < pcaVisual.length; j++) {
					// create a new node
					node[counter] = new svm_node();
					// set the index of the dimension and value of it
					// aka 1:100 2:200 etc.
					node[counter].index = counter + 1;
					node[counter].value = pcaVisual[j];
					counter++;
				}
				for (int k = 0; k < textual.length; k++) {
					// same as above 1:1 2:2
					node[counter] = new svm_node();
					node[counter].index = counter + 1;
					node[counter].value = textual[k];
					counter++;
				}
				// Add it to a vector double
				vx.addElement(node);
				// assign the label for the image +1/-1
				//vy.add(1.0);
			}
		}
		// for each bag in negative bag
		for (int l = 0; l < numOfBags; l++) {
			// for each instance in the bag
			Bag bg = negBag.get(l);
			// for each instance in the bag
			for (int i = 0; i < bg.getImage().size(); i++) {
				// get the visual and textual info for that one instances
				double[] pcaVisual = bg.getImage().get(i).getPcaVisual();
				double[] textual = bg.getImage().get(i).getTextFeature();
				// create a svm node to store all the dimension in it
				svm_node[] node = new svm_node[pcaVisual.length
						+ textual.length];
				int counter = 0;
				// for each dimension in pca
				for (int j = 0; j < pcaVisual.length; j++) {
					// create a new node
					node[counter] = new svm_node();
					// set the index of the dimension and value of it
					// aka 1:100 2:200 etc.
					node[counter].index = counter + 1;
					node[counter].value = pcaVisual[j];
					counter++;
				}
				for (int k = 0; k < textual.length; k++) {
					// same as above 1:1 2:2
					node[counter] = new svm_node();
					node[counter].index = counter + 1;
					node[counter].value = textual[k];
					counter++;
				}
				// Add it to a vector double
				vx.addElement(node);
				// assign the label for the image +1/-1
				//vy.add(-1.0);
			}
		}
		vxy.add(vx);
		vxy.add(vy);
		return vxy;
	}

	// For training bag
	@SuppressWarnings("rawtypes")
	public svm_problem GenerateSVMProblem(List<Bag> posBag, List<Bag> negBag,
			double[] prediction, int numOfBags) {
		// TODO Auto-generated method stub
		svm_problem problem = new svm_problem();
		if (posBag.size() + negBag.size() < numOfBags) {
			numOfBags = posBag.size() + negBag.size();
		}
		// Set problem size
		problem.l = (numOfBags * numberOfInstances);
		// Set number of labels for each image
		problem.y = new double[problem.l];
		// set the number of images to store dimension for
		problem.x = new svm_node[problem.l][];
		List<Vector> vxy = AddNodes(posBag, negBag, numOfBags/2);
		for (int i = 0; i < problem.l; i++) {
			problem.x[i] = (svm_node[]) vxy.get(0).elementAt(i);
		}
		for (int i = 0; i < problem.l; i++) {
			problem.y[i] = prediction[i];
		}
		return problem;
	}

	@SuppressWarnings("rawtypes")
	public static svm_problem GenerateSVMProblem(List<Bag> posBag) {
		// TODO Auto-generated method stub
		svm_problem problem = new svm_problem();
		// Set problem size
		problem.l = posBag.size() * numberOfInstances;
		// Set number of labels for each image
		problem.y = new double[problem.l];
		// set the number of images to store dimension for
		problem.x = new svm_node[problem.l][];
		List<Vector> vxy = AddNodes(posBag);
		for (int i = 0; i < problem.l; i++) {
			problem.x[i] = (svm_node[]) vxy.get(0).elementAt(i);
		}
		for (int i = 0; i < problem.l; i++) {
			problem.y[i] = (double) vxy.get(1).elementAt(i);
		}
		return problem;
	}

	@SuppressWarnings("rawtypes")
	private static List<Vector> AddNodes(List<Bag> posBag) {
		@SuppressWarnings("rawtypes")
		List<Vector> vxy = new ArrayList<>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		Vector<Double> vy = new Vector<Double>();
		// for each instance in the bag
		for (Bag bg : posBag) {
			for (int i = 0; i < bg.getImage().size(); i++) {
				// get the visual and textual info for that one instances
				double[] pcaVisual = bg.getImage().get(i).getPcaVisual();
				double[] textual = bg.getImage().get(i).getTextFeature();
				// create a svm node to store all the dimension in it
				svm_node[] node = new svm_node[pcaVisual.length
						+ textual.length];
				int counter = 0;
				// for each dimension in pca
				for (int j = 0; j < pcaVisual.length; j++) {
					// create a new node
					node[counter] = new svm_node();
					// set the index of the dimension and value of it
					// aka 1:100 2:200 etc.
					node[counter].index = counter + 1;
					node[counter].value = pcaVisual[j];
					counter++;
				}
				for (int k = 0; k < textual.length; k++) {
					// same as above 1:1 2:2
					node[counter] = new svm_node();
					node[counter].index = counter + 1;
					node[counter].value = textual[k];
					counter++;
				}
				// Add it to a vector double
				vx.addElement(node);
				// assign the label for the image +1/-1
				vy.add(1.0);
			}
		}
		vxy.add(vx);
		vxy.add(vy);
		return vxy;
	}

	private static svm_print_interface svm_print_null = new svm_print_interface() {
		public void print(String s) {
		}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface() {
		public void print(String s) {
			System.out.print(s);
		}
	};
	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) {
		svm_print_string.print(s);
	}

	@SuppressWarnings("unused")
	public static double[] predict(svm_problem allBag, svm_linear_model model,
			int predict_probability) {
		int correct = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] newValues = new double[allBag.y.length];
		int svm_type = model.svm_type;
		int nr_class = model.nr_class;
		double[] prob_estimates = null;

		if (predict_probability == 1) {
			if (svm_type == svm_parameter.EPSILON_SVR
					|| svm_type == svm_parameter.NU_SVR) {
				SVMGeneration
						.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
								+ svm_linear.svm_get_svr_probability(model)
								+ "\n");
			} else {
				int[] labels = new int[nr_class];
				svm_linear.svm_get_labels(model, labels);
				prob_estimates = new double[nr_class];
				System.out.println("Labels");
				for (int j = 0; j < nr_class; j++)
					System.out.println(" " + labels[j] + "\n");
				// output.writeBytes("labels");
				/*
				 * for(int j=0;j<nr_class;j++) output.writeBytes(" "+labels[j]);
				 * output.writeBytes("\n");
				 */
			}
		}
		for (int i = 0; i < allBag.l; i++) {
			double v;
			double target = allBag.y[i];
			if (predict_probability == 1
					&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
				v = svm_linear.svm_predict_probability(model, allBag.x[i],
						prob_estimates);
				newValues[i] = v;
				/*
				 * output.writeBytes(v+" "); for(int j=0;j<nr_class;j++)
				 * output.writeBytes(prob_estimates[j]+" ");
				 * output.writeBytes("\n");
				 */
			} else {
				v = svm_linear.svm_predict(model, allBag.x[i]);
				newValues[i] = v;
				// output.writeBytes(v+"\n");
			}

			if (v == target)
				++correct;
			error += (v - target) * (v - target);
			sumv += v;
			sumy += target;
			sumvv += v * v;
			sumyy += target * target;
			sumvy += v * target;
			++total;
		}
		if (svm_type == svm_parameter.EPSILON_SVR
				|| svm_type == svm_parameter.NU_SVR) {
			SVMGeneration.info("Mean squared error = " + error / total
					+ " (regression)\n");
			SVMGeneration.info("Squared correlation coefficient = "
					+ ((total * sumvy - sumv * sumy) * (total * sumvy - sumv
							* sumy))
					/ ((total * sumvv - sumv * sumv) * (total * sumyy - sumy
							* sumy)) + " (regression)\n");
		} else {
			SVMGeneration.info("Accuracy = " + (double) correct / total * 100
					+ "% (" + correct + "/" + total + ") (classification)\n");
		}
		return newValues;
	}

	public svm_parameter getParam(int gamma) {
		// TODO Auto-generated method stub
		// default values
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = 0;
		param.degree = 0;
		param.gamma = 0.00313479623; // 1/num_features
		param.coef0 = 0;
		param.nu = 1;
		param.cache_size = 1000;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 0;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		return param;
	}

	public svm_linear_model svmTrain(svm_problem trainBag, svm_parameter param) {
		// TODO Auto-generated method stub
		svm_model model = new svm_model();
		model = trainModel(trainBag, param);
		svm_linear_model model2 = svm_linear.convert_model_to_linear(model);
		model2.param = param;
		return model2;
	}

	private svm_model trainModel(svm_problem trainBag, svm_parameter param) {
		svm_model model = new svm_model();
		model = svm.svm_train(trainBag, param);
		return model;
	}
}
