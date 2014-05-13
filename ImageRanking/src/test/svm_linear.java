package test;

import libsvm.*;
import java.util.Arrays;
import java.util.logging.Logger;

public class svm_linear {
	/** Logger. */
	private static final Logger LOGGER = Logger.getLogger(svm_linear.class.getName());

	/**
	 * Private constructor.
	 */
	private svm_linear() {
	}

	/**
	 * Computes the probability value given the decision value f, using the
	 * sigmoid function 1 / (1 + exp(A * f + B)).
	 * <p>
	 * This method is copied from the method with the same name in
	 * svm.java from LIBSVM.
	 *
	 * @param decision_value Decision value.
	 * @param A Parameter A of the sigmoid function.
	 * @param B Parameter B of the sigmoid function.
	 */
	private static double sigmoid_predict(double decision_value, double A, double B) {
		double fApB = decision_value * A + B;
		if (fApB >= 0)
			return Math.exp(-fApB) / (1.0 + Math.exp(-fApB));
		else
			return 1.0 / (1 + Math.exp(fApB));
	}

	/**
	 * Computes multiclass probabilities from pairwise probabilities.
	 * <p>
	 * This method is copied from the method with the same name in
	 * svm.java from LIBSVM.
	 *
	 * @param nr_class Number of classes.
	 * @param pairwise_prob Pairwise probabilities.
	 * @param prob_estimates Output multiclass probabilities.
	 */
	private static void multiclass_probability(int nr_class, double[][] pairwise_prob, double[] prob_estimates) {
		int iter = 0, max_iter = Math.max(100, nr_class);
		double[][] Q = new double[nr_class][nr_class];
		double[] Qp = new double[nr_class];
		double pQp, eps = 0.005 / nr_class;
		for (int t = 0; t < nr_class; t++) {
			prob_estimates[t] = 1.0 / nr_class;
			Q[t][t] = 0;
			for (int j = 0; j < t; j++) {
				Q[t][t] += pairwise_prob[j][t] * pairwise_prob[j][t];
				Q[t][j] = Q[j][t];
			}
			for (int j = t + 1; j < nr_class; j++) {
				Q[t][t] += pairwise_prob[j][t] * pairwise_prob[j][t];
				Q[t][j] = -pairwise_prob[j][t] * pairwise_prob[t][j];
			}
		}
		for (iter = 0; iter < max_iter; iter++) {
			pQp = 0;
			for (int t = 0; t < nr_class; t++) {
				Qp[t] = 0;
				for (int j = 0; j < nr_class; j++)
					Qp[t] += Q[t][j] * prob_estimates[j];
				pQp += prob_estimates[t] * Qp[t];
			}
			double max_error = 0;
			for (int t = 0; t < nr_class; t++) {
				double error = Math.abs(Qp[t] - pQp);
				if (error > max_error)
					max_error = error;
			}
			if (max_error < eps)
				break;
			for (int t = 0; t < nr_class; t++) {
				double diff = (-Qp[t] + pQp) / Q[t][t];
				prob_estimates[t] += diff;
				pQp = (pQp + diff * (diff * Q[t][t] + 2 * Qp[t])) / (1 + diff) / (1 + diff);
				for (int j = 0; j < nr_class; j++) {
					Qp[j] = (Qp[j] + diff * Q[t][j]) / (1 + diff);
					prob_estimates[j] /= (1 + diff);
				}
			}
		}
		if (iter >= max_iter)
			LOGGER.warning("Exceeds max_iter in multiclass_probability.");
	}
	public static double svm_get_svr_probability(svm_linear_model model)
	{
		if ((model.param.svm_type == svm_parameter.EPSILON_SVR || model.param.svm_type == svm_parameter.NU_SVR) &&
		    model.probA!=null)
		return model.probA[0];
		else
		{
			System.err.print("Model doesn't contain information for SVR probability inference\n");
			return 0;
		}
	}
	/**
	 * Converts the given LIBSVM model to a linear SVM model.
	 *
	 * @param libsvm_model LIBSVM model.
	 */
	public static svm_linear_model convert_model_to_linear(svm_model libsvm_model) {
		int svm_type = libsvm_model.param.svm_type;
		int kernel_type = libsvm_model.param.kernel_type;
		if (svm_type != svm_parameter.C_SVC && svm_type != svm_parameter.NU_SVC)
			throw new IllegalArgumentException("Model svm_type must be C_SVC or NU_SVC.");
		if (kernel_type != svm_parameter.LINEAR)
			throw new IllegalArgumentException("Model kernel_type must be LINEAR.");
		int nr_class = libsvm_model.nr_class;		
		int nr_sv = libsvm_model.l;
		int max_idx = 0;
		for (int i = 0; i < nr_sv; i++) {
			svm_node[] nodes = libsvm_model.SV[i];
			for (int j = 0; j < nodes.length; j++) {
				int idx = nodes[j].index;
				if (idx > max_idx)
					max_idx = idx;
			}
		}
		int nr_feature = max_idx;
		int[] start = new int[nr_class];
		start[0] = 0;
		for (int i = 1; i < nr_class; i++)
			start[i] = start[i - 1] + libsvm_model.nSV[i - 1];
		double[][] w = new double[nr_class * (nr_class - 1) / 2][nr_feature];
		int p = 0;
		for (int i = 0; i < nr_class; i++) {
			for (int j = i + 1; j < nr_class; j++) {
				int si = start[i];
				int sj = start[j];
				int ci = libsvm_model.nSV[i];
				int cj = libsvm_model.nSV[j];
				double[] coef1 = libsvm_model.sv_coef[j - 1];
				double[] coef2 = libsvm_model.sv_coef[i];
				for (int k = 0; k < ci; k++) {
					double alpha = coef1[si + k];
					svm_node[] nodes = libsvm_model.SV[si + k];
					for (int l = 0; l < nodes.length; l++) {
						int idx = nodes[l].index;
						double val = nodes[l].value;
						w[p][idx - 1] += (alpha * val);
					}
				}
				for (int k = 0; k < cj; k++) {
					double alpha = coef2[sj + k];
					svm_node[] nodes = libsvm_model.SV[sj + k];
					for (int l = 0; l < nodes.length; l++) {
						int idx = nodes[l].index;
						double val = nodes[l].value;
						w[p][idx - 1] += (alpha * val);
					}
				}
				p++;
			}
		}
		svm_linear_model model = new svm_linear_model();
		model.svm_type = libsvm_model.param.svm_type;
		model.nr_class = libsvm_model.nr_class;
		model.nr_feature = nr_feature;
		model.w = w;
		if (libsvm_model.probA != null && libsvm_model.probB != null) {
			model.probA = Arrays.copyOf(libsvm_model.probA, libsvm_model.probA.length);
			model.probB = Arrays.copyOf(libsvm_model.probB, libsvm_model.probB.length);
		}
		model.rho = Arrays.copyOf(libsvm_model.rho, libsvm_model.rho.length);
		model.label = Arrays.copyOf(libsvm_model.label, libsvm_model.label.length);
		return model;
	} 

	/**
	 * Convenience method for training a linear SVM model. This method
	 * simply calls {@code svm.svm_train()} to obtain a LIBSVM model, and
	 * then calls {@code convert_model_to_linear()} to convert the model
	 * into a linear SVM model.
	 *
	 * @param prob SVM problem.
	 * @param param SVM parameters.
	 */
	public static svm_linear_model svm_train(svm_problem prob, svm_parameter param) {
		if (param.svm_type != svm_parameter.C_SVC && param.svm_type != svm_parameter.NU_SVC)
			throw new IllegalArgumentException("Model svm_type must be C_SVC or NU_SVC.");
		if (param.kernel_type != svm_parameter.LINEAR)
			throw new IllegalArgumentException("Model kernel_type must be LINEAR.");
		svm_model libsvm_model = svm.svm_train(prob, param);
		svm_linear_model model = convert_model_to_linear(libsvm_model);
		return model;
	}

	/**
	 * Classifies for the given instance using the given linear SVM model
	 * and returns its class label. The computed decision values are output
	 * as well.
	 * <p>
	 * This method is largely copied from the method with the same name in
	 * svm.java from LIBSVM, with modifications to compute decision values
	 * from the svm_linear SVM model instead of the LIBSVM model.
	 *
	 * @param model Linear SVM model.
	 * @param x Instance.
	 * @param decision_values Output decision values. This is an array of
	 *        k * (k - 1) / 2 elements, where k is the number of classes.
	 */
	public static double svm_predict_values(svm_linear_model model, svm_node[] x, double[] decision_values) {
		if (model.svm_type != svm_parameter.C_SVC && model.svm_type != svm_parameter.NU_SVC)
			throw new IllegalArgumentException("Model svm_type must be C_SVC or NU_SVC.");
		int nr_class = model.nr_class;
		int nr_feature = model.nr_feature;
		int[] vote = new int[nr_class];
		for (int i = 0; i < nr_class; i++)
			vote[i] = 0;
		int p = 0;
		for (int i = 0; i < nr_class; i++) {
			for (int j = i + 1; j < nr_class; j++) {
				double sum = 0;
				for (int k = 0; k < x.length; k++) {
					int idx = x[k].index;
					double val = x[k].value;
					if (idx >= 1 && idx <= nr_feature)
						sum += (model.w[p][idx - 1] * val);
				}
				sum -= model.rho[p];
				decision_values[p] = sum;
				if (decision_values[p] > 0)
					++vote[i];
				else
					++vote[j];
				p++;
			}
		}
		int vote_max_idx = 0;
		for (int i = 1; i < nr_class; i++)
			if (vote[i] > vote[vote_max_idx])
				vote_max_idx = i;
		return model.label[vote_max_idx];
	}

	/**
	 * Classifies for the given instance using the given linear SVM model
	 * and returns its class label.
	 * <p>
	 * This method is largely copied from the method with the same name in
	 * svm.java from LIBSVM, with modifications to compute decision values
	 * from the svm_linear SVM model instead of the LIBSVM model.
	 *
	 * @param model Linear SVM model.
	 * @param x Instance.
	 */
	public static double svm_predict(svm_linear_model model, svm_node[] x) {
		if (model.svm_type != svm_parameter.C_SVC && model.svm_type != svm_parameter.NU_SVC)
			throw new IllegalArgumentException("Model svm_type must be C_SVC or NU_SVC.");
		int nr_class = model.nr_class;
		double[] dec_values = new double[nr_class * (nr_class - 1) / 2];
		double pred_result = svm_predict_values(model, x, dec_values);
		return pred_result;
	}

	/**
	 * Classifies for the given instance using the given linear SVM model
	 * based on probability estimates, and returns the class label with the
	 * highest probability estimates. The computed probability estimates are
	 * output as well.
	 * <p>
	 * This method is largely copied from the method with the same name in
	 * svm.java from LIBSVM, with modifications to compute decision values
	 * from the svm_linear SVM model instead of the LIBSVM model.
	 *
	 * @param model Linear SVM model.
	 * @param x Instance.
	 * @param prob_estimates Probability estimates. This is an array of k
	 *        elements, where k is the number of classes.
	 */
	public static double svm_predict_probability(svm_linear_model model, svm_node[] x, double[] prob_estimates) {
		if (model.svm_type != svm_parameter.C_SVC && model.svm_type != svm_parameter.NU_SVC)
			throw new IllegalArgumentException("Model svm_type must be C_SVC or NU_SVC.");
		if (model.probA == null || model.probB == null)
			throw new IllegalArgumentException("Model contains no probability information.");
		int nr_class = model.nr_class;
		double[] dec_values = new double[nr_class * (nr_class - 1) / 2];
		svm_predict_values(model, x, dec_values);
		double min_prob = 1e-7;
		double[][] pairwise_prob = new double[nr_class][nr_class];
		int k = 0;
		for (int i = 0; i < nr_class; i++) {
			for (int j = i + 1; j < nr_class; j++) {
				pairwise_prob[i][j] = Math.min(Math.max(sigmoid_predict(dec_values[k], model.probA[k], model.probB[k]), min_prob), 1 - min_prob);
				pairwise_prob[j][i] = 1 - pairwise_prob[i][j];
				k++;
			}
		}
		multiclass_probability(nr_class, pairwise_prob, prob_estimates);
		int prob_max_idx = 0;
		for (int i = 1; i < nr_class; i++)
			if (prob_estimates[i] > prob_estimates[prob_max_idx])
				prob_max_idx = i;
		return model.label[prob_max_idx];
	}
	public static void svm_get_labels(svm_linear_model model, int[] label)
	{
		if (model.label != null)
			for(int i=0;i<model.nr_class;i++)
				label[i] = model.label[i];
	}
}
