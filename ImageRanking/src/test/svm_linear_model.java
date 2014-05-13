package test;

import java.io.Serializable;

import libsvm.svm_parameter;

@SuppressWarnings("serial")
public class svm_linear_model implements Serializable  {
	/** SVM type. */
	public int svm_type;
	public svm_parameter param;
	/** Number of classes. */
	public int nr_class;

	/** Number of features. */
	public int nr_feature;

	/** Pairwise weight vectors. If there are k classes and n features, then
	    this array has k * (k - 1) / 2 elements, each an array with n
	    elements. */
	public double[][] w;

	/** Constants in decision functions. If there are k classes, then this
	    array has k * (k - 1) / 2 elements. */
	public double[] rho;

	/** Parameter A of the sigmoid function for computing pairwise
	    probabilities. If there are k classes, then this array has
	    k * (k - 1) / 2 elements. */
	public double[] probA;

	/** Parameter B of the sigmoid function for computing pairwise
	    probabilities. If there are k classes, then this array has
	    k * (k - 1) / 2 elements. */
	public double[] probB;

	/** Labels of each class. If there are k classes, then this array has k
	    elements. */
	public int[] label;

	/**
	 * Constructs a new linear SVM model. No fields are initialized.
	 */
	public svm_linear_model() {
	}
	
}
