package test;

import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import libsvm.*;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Main {
	private static final int numberOfInstances = 15;
	private static final int numTrainBag = 10;
	private static final String currentdir = Paths.get("").toAbsolutePath()
			.toString();
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	private List<Image> initialRanking;
	private List<Image> rankingWithScore;
	private List<Image> rankingWithSVM;
	private Text txtSearchTerm;

	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		final Shell shlImageReranking = new Shell();
		shlImageReranking.setSize(450, 300);
		shlImageReranking.setText("Image Re-Ranking");

		Label lblSearchTerm = new Label(shlImageReranking, SWT.NONE);
		lblSearchTerm.setBounds(10, 24, 72, 15);
		lblSearchTerm.setText("Search Term :");

		txtSearchTerm = new Text(shlImageReranking, SWT.BORDER);
		txtSearchTerm.setBounds(88, 21, 131, 21);
		final Button btnInitialResult = new Button(shlImageReranking, SWT.NONE);
		btnInitialResult.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DisplayImages dis = new DisplayImages();
				dis.open(initialRanking, "Initial Result");
			}
		});
		btnInitialResult.setBounds(10, 54, 85, 25);
		btnInitialResult.setText("Initial Result");

		final Button btnResultAfterRanking = new Button(shlImageReranking,
				SWT.NONE);
		btnResultAfterRanking.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DisplayImages dis = new DisplayImages();
				dis.open(rankingWithScore, "Ranking with Score");
			}
		});
		btnResultAfterRanking.setText("Ranked Result");
		btnResultAfterRanking.setBounds(101, 54, 91, 25);

		final Button btnSvmResult = new Button(shlImageReranking, SWT.NONE);
		btnSvmResult.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DisplayImages dis = new DisplayImages();
				dis.open(rankingWithSVM, "Ranking with SVM");
			}
		});
		btnSvmResult.setBounds(198, 54, 75, 25);
		btnSvmResult.setText("SVM Result");

		final Button btnSearch = new Button(shlImageReranking, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (txtSearchTerm.getText().isEmpty() == true) {
						MessageDialog.openError(shlImageReranking,
								"Empty Search", "Search Term is empty");
					} else {
						btnSearch.setEnabled(false);
						btnInitialResult.setEnabled(false);
						btnResultAfterRanking.setEnabled(false);
						btnSvmResult.setEnabled(false);

						begin(txtSearchTerm.getText(), shlImageReranking,
								btnInitialResult, btnResultAfterRanking,
								btnSvmResult);

						btnSearch.setEnabled(true);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					MessageDialog.openError(shlImageReranking, "File Error",
							"Unable to find File");
					e1.printStackTrace();
				} catch (PCAException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		txtSearchTerm.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
					try {
						if (txtSearchTerm.getText().isEmpty() == true) {
							MessageDialog.openError(shlImageReranking,
									"Empty Search", "Search Term is empty");
						} else {
							btnSearch.setEnabled(false);
							btnInitialResult.setEnabled(false);
							btnResultAfterRanking.setEnabled(false);
							btnSvmResult.setEnabled(false);

							begin(txtSearchTerm.getText(), shlImageReranking,
									btnInitialResult, btnResultAfterRanking,
									btnSvmResult);

							btnSearch.setEnabled(true);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						MessageDialog.openError(shlImageReranking,
								"File Error", "Unable to find File");
						e1.printStackTrace();
					} catch (PCAException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnSearch.setBounds(225, 19, 75, 25);
		btnSearch.setText("Search");

		Button btnQuit = new Button(shlImageReranking, SWT.NONE);
		btnQuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlImageReranking.close();
			}
		});
		btnQuit.setBounds(311, 19, 75, 25);
		btnQuit.setText("Quit");

		shlImageReranking.open();
		shlImageReranking.layout();
		while (!shlImageReranking.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void begin(String searchTerm, Shell shlImageReranking,
			Button btnInitialResult, Button btnResultAfterRanking,
			Button btnSvmResult) throws IOException, PCAException {
		BufferedReader reader = new BufferedReader(new FileReader(currentdir
				+ "/searchTerm.txt"));
		String line = null;
		List<String> concept = new ArrayList<>();
		while ((line = reader.readLine()) != null) {
			concept.add(line);
		}
		if (concept.contains(searchTerm)) {
			FindResult(searchTerm,btnInitialResult,btnResultAfterRanking,btnSvmResult);
		} else {
			BufferedWriter bwOut = new BufferedWriter(new FileWriter(currentdir
					+ "/searchTerm.txt", true));
			bwOut.write(searchTerm + "\n");
			bwOut.close();
			System.out.println("Number of instance in 1 bag "
					+ numberOfInstances);
			long startTime = System.currentTimeMillis();
			// search the text file for tag for the term
			System.out.println("Searching for terms " + searchTerm);
			List<Image> imageSet = searchForTerm(searchTerm.toLowerCase());
			if (imageSet.size() == 0) {
				MessageDialog.openError(shlImageReranking, "No Term",
						"No Such Search Found");
			} else {
				// imageSet = ExtractUrl(imageSet);
				initialRanking = new ArrayList<Image>(imageSet);
				FileOutputStream fileOut = new FileOutputStream(currentdir
						+ "/Results/" + searchTerm + "_initialRanking");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(initialRanking);
				out.close();
				fileOut.close();
				btnInitialResult.setEnabled(true);
				// WriteUrl(imageSet, searchTerm, "Before Ranking Score");
				long endTime = System.currentTimeMillis();
				System.out.println("That took " + (endTime - startTime)
						+ " milliseconds");
				// Calculate ranking score based on given rating score
				System.out.println("Calculating ranking score");
				imageSet = rankImage(imageSet);
				// WriteUrl(imageSet, searchTerm, "After Ranking Score");
				endTime = System.currentTimeMillis();
				// System.out.println("That took " + (endTime - startTime)+
				// " milliseconds");
				// Before image re-ranked

				// Re-rank the images based on rank score
				// System.out.println("Re-ranking image based on score");
				Collections.sort(imageSet, new Image.CompRank());
				rankingWithScore = new ArrayList<Image>(imageSet);
				fileOut = new FileOutputStream(currentdir + "/Results/" + searchTerm
						+ "_rankingScore");
				out = new ObjectOutputStream(fileOut);
				out.writeObject(rankingWithScore);
				out.close();
				fileOut.close();
				btnResultAfterRanking.setEnabled(true);
				endTime = System.currentTimeMillis();

				System.out.println("Extracting Textual Feacture");
				String[] matrix = ExtractTextFeature(imageSet);
				endTime = System.currentTimeMillis();
				System.out.println("That took " + (endTime - startTime)
						+ " milliseconds");

				System.out.println("Creating obervation matrix");
				// Create Matrix to calculate Covariance
				RealMatrix observation = CreateMatrix(imageSet.size(), imageSet
						.get(0).combineVisualFeatures().split("\\s+").length,
						imageSet);
				System.out.println("Creating Covariance Matrix");
				// Calculate Covariance Matrix by passing in observation
				Covariance cv = new Covariance(observation);
				PCA principal = new PCA();
				principal.setObservations(observation);
				System.out.println("PCA In Progress...");
				principal.transform(cv.getCovarianceMatrix());
				RealMatrix principalComponent = principal
						.getPrincipalComponents();
				// Assign textual and visual feature back to the images
				imageSet = AssignPCA(imageSet, principalComponent, matrix);
				// Cluster the intances/image into 15 per bag and lopp based on
				// iteration times using k-means
				int iteration = 20;
				System.out.println("Clustering...");
				List<Bag> initBag = KMeansClustering(imageSet, imageSet.size()
						/ numberOfInstances, iteration);
				if (initBag.size() < numTrainBag
						|| initBag.size() == numTrainBag) {
					MessageDialog.openError(shlImageReranking,
							"Low Positive Bag",
							"There are too little positive bags");
					System.out.println("Unable to get a correct read");
				} else {
					// Take out only bags with 15 or more instances inside and
					// use
					// only
					// the top 15 instance
					System.out.println("Initial Number of bags "
							+ initBag.size());
					System.out.println("Processing positive bag");
					List<Bag> posBag = ProcessBag(initBag);
					System.out.println("Number of bags " + posBag.size());
					System.out.println("Processing negative bag");
					// Create same number of negative bags
					List<Bag> negBag = CreateNegativeBag(posBag.size(),
							searchTerm);

					double[] svmprediction;
					//double[] miSVMPrediction;
					svm_linear_model model;
					svm_linear_model model1 = null;
					svm_problem predictBag = SVMGeneration
							.GenerateSVMProblem(posBag);
					svm_problem predictAllBag = SVMGeneration
							.GenerateSVMProblemAllBag(imageSet);
					System.out.println();

					System.out.println("With " + numTrainBag + " training bag");
					svmprediction = InitPrediction(numTrainBag * 2);
					//creating model for prediction
					model = trainModel(imageSet.get(0).getPcaVisual().length
							+ imageSet.get(0).getTextFeature().length, posBag,
							negBag, numTrainBag * 2, svmprediction, 1);
					System.out.println("Normal Prediction");
					//for predition
					svmprediction = SVMGeneration.predict(predictAllBag, model,
							0);
					List<Image> svmSet = PredictSVM(imageSet, svmprediction);
					rankingWithSVM = new ArrayList<Image>(svmSet);
					fileOut = new FileOutputStream(currentdir + "/Results/"
							+ searchTerm + "_rankingSVM");
					out = new ObjectOutputStream(fileOut);
					out.writeObject(rankingWithSVM);
					out.close();
					fileOut.close();
					btnSvmResult.setEnabled(true);
					
					/*double[] AP = GetPrecision(svmSet);
					BufferedWriter out1 = new BufferedWriter(new FileWriter(
							currentdir+"/precision.txt", true));
					out1.write("After normal SVM with 1 bag\n");
					out1.write(searchTerm + "\n");
					out1.write((AP[0] * 100) + "% " + (AP[1] * 100) + "% "
							+ (AP[2] * 100) + "% " + (AP[3] * 100) + "% "
							+ (AP[4] * 100) + "% " + (AP[5] * 100) + "%\n");
					out1.write("\n");
					out1.close();*/
					/*
					 * System.out.println();
					 * System.out.println("Primal Prediction"); svmprediction =
					 * InitPrediction(numTrainBag * 2); model1 =
					 * trainModel(imageSet.get(0).getPcaVisual().length +
					 * imageSet.get(0).getTextFeature().length, posBag, negBag,
					 * numTrainBag * 2, svmprediction, 1); miSVMPrediction =
					 * Predict(predictBag, model1, svmprediction, 0); //int
					 * count = 1; /* while (miSVMPrediction[predictBag.l] != 0)
					 * { /* model =
					 * trainModel(imageSet.get(0).getPcaVisual().length +
					 * imageSet.get(0).getTextFeature().length, posBag, negBag,
					 * numTrainBag * 2, svmprediction, 1);
					 * System.out.println("Normal Prediction"); svmprediction =
					 * SVMGeneration.predict(predictAllBag, model, 0);
					 */
					/*
					 * for (int i = 0; i < numTrainBag * 15; i++) {
					 * svmprediction[i] = miSVMPrediction[i]; } model1 =
					 * trainModel(imageSet.get(0).getPcaVisual().length +
					 * imageSet.get(0).getTextFeature().length, posBag, negBag,
					 * numTrainBag * 2, svmprediction, 1);
					 * System.out.println("Primal Prediction"); // Positive Bag
					 * only miSVMPrediction = Predict(predictBag, model1,
					 * miSVMPrediction, count); count++;
					 * 
					 * }
					 *//*
						 * miSVMPrediction = FinalPrediction(predictAllBag,
						 * model1); List<Image> miSVMSet = PredictSVM(imageSet,
						 * miSVMPrediction);
						 */

			
				}
				/*
				 * System.out.println("Top 20"); for (int i = 0; i < 20; i++) {
				 * BufferedWriter out = new BufferedWriter(new FileWriter(
				 * "C:/Users/ycheng/Downloads/RankedImages.txt", true));
				 * out.write("i = " + (i + 1) + "for search term " + searchTerm
				 * + "\n"); out.write("Initial Ranked              " +
				 * cloneImageSet.get(i).getFilename() + "\n");
				 * out.write("Initial Ranked with ranking " +
				 * imageSet.get(i).getFilename() + "\n");
				 * out.write("After SVM Prediction        " +
				 * svmSet.get(i).getFilename() + "\n"); out.close(); }
				 * //WriteUrl(svmSet, searchTerm, "SVM with 10 Bags"); double[]
				 * AP = GetPrecision(cloneImageSet); System.out.println(AP[0] +
				 * " " + AP[1] + " " + AP[2] + " " + AP[3] + " " + AP[4] + " " +
				 * AP[5]); AP = GetPrecision(imageSet); System.out.println(AP[0]
				 * + " " + AP[1] + " " + AP[2] + " " + AP[3] + " " + AP[4] + " "
				 * + AP[5]); AP = GetPrecision(svmSet); System.out.println(AP[0]
				 * + " " + AP[1] + " " + AP[2] + " " + AP[3]);
				 */}
		}
	}

	private void FindResult(String searchTerm, Button btnInitialResult, Button btnResultAfterRanking, Button btnSvmResult) {
		// TODO Auto-generated method stub
		try {
			//get initial ranking file
			FileInputStream fileIn = new FileInputStream(currentdir + "/Results/"
					+ searchTerm + "_initialRanking");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			initialRanking = (List<Image>) in.readObject();
			btnInitialResult.setEnabled(true);
			
			fileIn = new FileInputStream(currentdir + "/Results/"
					+ searchTerm + "_rankingScore");
			in = new ObjectInputStream(fileIn);
			rankingWithScore = (List<Image>) in.readObject();
			btnResultAfterRanking.setEnabled(true);;
			in.close();
			fileIn.close();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileInputStream fileIn = new FileInputStream(currentdir + "/Results/"
					+ searchTerm + "_rankingSVM");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			rankingWithSVM = (List<Image>) in.readObject();
			btnSvmResult.setEnabled(true);
			in.close();
			fileIn.close();
			double[] AP = GetPrecision(rankingWithSVM);
			BufferedWriter out1 = new BufferedWriter(new FileWriter(
					currentdir+"/precision.txt", true));
			out1.write("After normal SVM with 1 bag\n");
			out1.write(searchTerm + "\n");
			out1.write((AP[0] * 100) + "% " + (AP[1] * 100) + "% "
					+ (AP[2] * 100) + "% " + (AP[3] * 100) + "% "
					+ (AP[4] * 100) + "% " + (AP[5] * 100) + "%\n");
			out1.write("\n");
			out1.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<Image> ExtractUrl(List<Image> imageSet)
			throws IOException {
		// TODO Auto-generated method stub
		String line = null;
		BufferedReader reader = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/AmmendedUrls.txt"));
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			for (int i = 0; i < imageSet.size(); i++) {
				String filename = imageSet.get(i).getUrl();
				if (parts[0].equals(filename)) {
					imageSet.get(i).setUrl(parts[2]);
					break;
				}
			}
		}
		reader.close();
		return imageSet;
	}

	private static void WriteUrl(List<Image> imageSet, String searchTerm,
			String message) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter out = new BufferedWriter(new FileWriter(
				"C:/Users/ycheng/Downloads/SearchTerm_" + searchTerm
						+ "_Url.txt", true));
		out.write(message + "\n");
		for (int i = 0; i < 20; i++) {
			out.write("Image Position : " + imageSet.get(i).getImagePosition()
					+ "\n");
			out.write(imageSet.get(i).getUrl() + "\n");
		}
		out.close();
	}

	private static List<Image> PredictSVM(List<Image> imageSet,
			double[] svmprediction) {
		List<Image> negativeSet = new ArrayList<Image>();
		List<Image> fullSet = new ArrayList<Image>();
		for (int i = 0; i < imageSet.size(); i++) {
			if (svmprediction[i] == 1) {
				fullSet.add(imageSet.get(i));
			} else {
				negativeSet.add(imageSet.get(i));
			}
		}
		for (Image temp : negativeSet) {
			fullSet.add(temp);
		}
		// TODO Auto-generated method stub
		return fullSet;
	}

	/*
	 * System.out.println(); ; } { } System.out.println(); /*
	 * System.out.println("With 3 training bag"); numTrainBag = 3; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag,
	 * numTrainBag*2,prediction, 1); predictBag =
	 * SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction"); predictBag =
	 * SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * System.out.println("With 5 training bag"); numTrainBag = 5; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag, numTrainBag, 1);
	 * predictBag = SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction");
	 * predictBag=SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * System.out.println("With 7 training bag"); numTrainBag = 7; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag, numTrainBag, 0);
	 * predictBag = SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction");
	 * predictBag=SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * System.out.println("With 7 training bag"); numTrainBag = 7; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag, numTrainBag, 5);
	 * predictBag = SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction");
	 * predictBag=SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * System.out.println("With 7 training bag"); numTrainBag = 7; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag, numTrainBag,
	 * 10); predictBag = SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction");
	 * predictBag=SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * System.out.println("With 9 training bag"); numTrainBag = 9; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag, numTrainBag, 1);
	 * predictBag = SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction");
	 * predictBag=SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * System.out.println("With 12 training bag"); numTrainBag = 12; model = new
	 * svm_linear_model(); model =
	 * trainModel(imageSet.get(0).getPcaVisual().length +
	 * imageSet.get(0).getTextFeature().length, posBag, negBag, numTrainBag, 1);
	 * predictBag = SVMGeneration.GenerateSVMProblemAllBag(imageSet);
	 * System.out.println("Normal Prediction"); prediction =
	 * SVMGeneration.predict(predictBag, model, 0); System.out.println();
	 * System.out.println("Primal Prediction");
	 * predictBag=SVMGeneration.GenerateSVMProblem(posBag); prediction2 =
	 * Predict(predictBag, model); System.out.println();
	 * 
	 * // Predict(predictBag,model); // SVMGeneration.predict(predictBag, model,
	 * 0); endTime = System.currentTimeMillis(); System.out.println("end");
	 * System.out.println("That took " + (endTime - startTime) / 1000 +
	 * " seconds");
	 */

	private static double[] GetPrecision(List<Image> imageSet) {
		// TODO Auto-generated method stub
		double numPositive = 0.0;
		double result = 0.0;
		double[] fResult = new double[6];
		double[] label = new double[imageSet.size()];
		for (int i = 0; i < imageSet.size(); i++) {
			label[i] = imageSet.get(i).getPreLabel();
		}
		for (int i = 0; i < 20; i++) {
			if (label[i] == 1) {
				numPositive++;
				result += (double) (numPositive / (i + 1));
			}
		}
		result = (1 / numPositive) * result;
		fResult[0] = result;
		numPositive = 0.0;
		result = 0.0;
		for (int i = 0; i < 40; i++) {
			if (label[i] == 1) {
				numPositive++;
				result += (double) (numPositive / (i + 1));
			}
		}
		result = (1 / numPositive) * result;
		fResult[1] = result;
		numPositive = 0.0;
		result = 0.0;
		for (int i = 0; i < 60; i++) {
			if (label[i] == 1) {
				numPositive++;
				result += (double) (numPositive / (i + 1));
			}
		}
		result = (1 / numPositive) * result;
		fResult[2] = result;
		numPositive = 0.0;
		result = 0.0;
		for (int i = 0; i < 80; i++) {
			if (label[i] == 1) {
				numPositive++;
				result += (double) (numPositive / (i + 1));
			}
		}
		result = (1 / numPositive) * result;
		fResult[3] = result;
		numPositive = 0.0;
		result = 0.0;
		for (int i = 0; i < 100; i++) {
			if (label[i] == 1) {
				numPositive++;
				result += (double) (numPositive / (i + 1));
			}
		}
		result = (1 / numPositive) * result;
		fResult[4] = result;
		numPositive = 0.0;
		result = 0.0;
		for (int i = 0; i < imageSet.size(); i++) {
			if (label[i] == 1) {
				numPositive++;
				result += (double) (numPositive / (i + 1));
			}
		}
		result = (1 / numPositive) * result;
		fResult[5] = result;
		return fResult;
	}

	private static double[] FinalPrediction(svm_problem predictAllBag,
			svm_linear_model model) {
		// TODO Auto-generated method stub
		double[] yi = new double[predictAllBag.l];
		int counter = 0;
		int negative = 0;
		System.out.println(predictAllBag.l / numberOfInstances);
		if (model.label[1] == -1) {
			negative = 1;
		}
		for (svm_node[] node : predictAllBag.x) {
			double tempFi = CalculateFi(model, node, negative);
			yi[counter] = Math.signum(tempFi);
			counter++;
		}
		return yi;
	}

	private static double[] InitPrediction(int numTrainBag) {
		// TODO Auto-generated method stub
		double[] prediction = new double[numTrainBag * numberOfInstances];
		int posBagCount = (numTrainBag / 2 * numberOfInstances);
		for (int i = 0; i < (numTrainBag * numberOfInstances); i++) {
			if (i < posBagCount) {
				prediction[i] = 1;
			} else {
				prediction[i] = -1;
			}
		}
		return prediction;
	}

	private static double[] Predict(svm_problem predictBag,
			svm_linear_model model, double[] miSVMPrediction, int count) {
		// TODO Auto-generated method stub
		double[] yi = new double[predictBag.l + 1];
		double[] fi = new double[predictBag.l];
		double[] result = new double[predictBag.l];
		int counter = 0;
		int negative = 0;
		int change = 0;
		System.out.println(predictBag.l / numberOfInstances);
		if (model.label[1] == -1) {
			negative = 1;
		}
		for (svm_node[] node : predictBag.x) {
			double tempFi = CalculateFi(model, node, negative);
			fi[counter] = tempFi;
			yi[counter] = Math.signum(tempFi);
			if (count != 0) {
				if (yi[counter] != miSVMPrediction[counter]) {
					change = 1;
				}
			}
			result[counter] = (1 + yi[counter]) / 2;
			counter++;
		}
		for (int i = 0; i < predictBag.l; i = i + numberOfInstances) {
			int sum = sumResult(result, i);
			System.out.println(sum);
			if (sum == 0) {
				System.out.println("Found sum=0 at " + i + " to "
						+ (i + numberOfInstances));
				int maxFi = getMax(fi, i);
				yi[maxFi] = 1;
			}
		}
		/*
		 * int sum = 0; for (int i = 0; i < yi.length - 1; i++) { sum += yi[i] -
		 * miSVMPrediction[i]; } if (sum != 0) { change = 1; }
		 */
		if (count == 20) {
			change = 0;
		}
		if (change == 0 && count != 0) {
			yi[predictBag.l] = 0;
		} else {
			yi[predictBag.l] = 1;
		}
		// System.out.println("done Error " + total + "%");
		return yi;
	}

	private static int getMax(double[] fi, int start) {
		// TODO Auto-generated method stub
		double max = fi[start];
		int index = start;
		for (int i = start; i < numberOfInstances + start; i++) {
			if (fi[i] > max) {
				max = fi[i];
				index = i;
			}
		}
		return index;
	}

	private static int sumResult(double[] result, int start) {
		// TODO Auto-generated method stub
		int temp = 0;
		for (int i = start; i < numberOfInstances + start; i++) {
			temp += result[i];
		}
		return temp;
	}

	private static svm_linear_model trainModel(double max_indx,
			List<Bag> posBag, List<Bag> negBag, int numTrainBag,
			double[] prediction, int eps) {
		// SVM Generation class for creating svm problem based on the bags
		SVMGeneration generate = new SVMGeneration();
		svm_linear_model model = new svm_linear_model();
		svm_problem trainBag = generate.GenerateSVMProblem(posBag, negBag,
				prediction, numTrainBag);
		svm_parameter param = generate.getParam(eps);
		if (param.gamma == 0 && max_indx > 0) {
			param.gamma = 1.0 / max_indx;
		}
		model = generate.svmTrain(trainBag, param);
		/*
		 * boolean changes = true; // Combine positive and negative bag into
		 * svm_problem while (changes == true) { svm_problem trainBag =
		 * generate.GenerateSVMProblem(posBag, negBag); svm_parameter param =
		 * generate.getParam(); if (param.gamma == 0 && max_indx > 0) {
		 * param.gamma = 1.0 / max_indx; } model = generate.svmTrain(trainBag,
		 * param); double[] yi=new
		 * double[(posBag.size()+negBag.size())*numberOfInstances]; int
		 * counter=0; for(svm_node[] node:trainBag.x) { double
		 * fi=CalculateFi(model,node); fi=Math.signum(fi); yi[counter]=fi+1/2;
		 * counter++; } System.out.println("done"); }
		 */
		return model;
	}

	private static double CalculateFi(svm_linear_model model, svm_node[] node,
			int negative) {
		// TODO Auto-generated method stub
		double result = 0.0;
		for (int i = 0; i < node.length; i++) {
			if (negative == 1) {
				result += (-model.w[0][i]) * node[i].value;
			} else {

			}
		}
		return result + (-model.rho[0]);
	}

	private static List<Bag> CreateNegativeBag(int size, String searchTerm)
			throws IOException, PCAException {
		// TODO Auto-generated method stub
		List<Bag> bag = new ArrayList<Bag>();
		List<Image> imageSet = GenerateNegativeImage(searchTerm, size
				* numberOfInstances);
		String[] matrix = ExtractTextFeature(imageSet);

		// Create Matrix to calculate Covariance
		RealMatrix observation = CreateMatrix(imageSet.size(), imageSet.get(0)
				.combineVisualFeatures().split("\\s+").length, imageSet);

		// Calculate Covariance Matrix by passing in observation
		// System.out.println("Creating Covariance Matrix");
		Covariance cv = new Covariance(observation);

		PCA principal = new PCA();
		principal.setObservations(observation);
		// System.out.println("PCA In Progress...");
		principal.transform(cv.getCovarianceMatrix());
		RealMatrix principalComponent = principal.getPrincipalComponents();

		// Assign textual and visual feature back to the images
		imageSet = AssignPCA(imageSet, principalComponent, matrix);

		List<Image> tempImage = new ArrayList<Image>();
		for (int i = 0; i < imageSet.size(); i++) {
			tempImage.add(imageSet.get(i));
			if (tempImage.size() == numberOfInstances) {
				bag.add(new Bag(tempImage));
				tempImage = new ArrayList<Image>();
			}
		}

		return bag;
	}

	private static List<Bag> ProcessBag(List<Bag> initBag) {
		// TODO Auto-generated method stub
		List<Bag> bag = new ArrayList<Bag>();
		for (int i = 0; i < initBag.size(); i++) {
			if (initBag.get(i).getImage().size() >= numberOfInstances) {
				Collections.sort(initBag.get(i).getImage(),
						new Image.CompRank());
				List<Image> tempImage = new ArrayList<Image>();
				for (int j = 0; j < numberOfInstances; j++) {
					tempImage.add(initBag.get(i).getImage().get(j));
				}
				bag.add(new Bag(tempImage));
			}

		}
		double total = 0.0;
		for (Bag b : bag) {
			total = 0.0;
			for (Image img : b.getImage()) {
				total += img.getRankScore();
			}
			b.setBagScore(total / numberOfInstances);
		}
		Collections.sort(bag, new Bag.CompRank());
		return bag;
	}

	private static List<Bag> KMeansClustering(List<Image> imageSet,
			int numBags, int iteration) {
		// TODO Auto-generated method stub
		List<Centoid> centoid = new ArrayList<Centoid>();
		Random gen = new Random();
		List<Integer> imagepos = new ArrayList<>();
		List<Bag> bags = new ArrayList<Bag>();
		int size = 0;
		if (imageSet.size() > 4000) {
			size = 4000;
			numBags = size / numberOfInstances;
		} else if (numBags < numTrainBag) {
			return bags;
		} else {
			size = imageSet.size();
		}
		// randomly add n number of data point where n is the number of bags
		for (int i = 0; i < numBags; i++) {
			int value = gen.nextInt(imageSet.size());
			Centoid temp = new Centoid();
			while (imagepos.contains(value)) {
				value = gen.nextInt(imageSet.size());
			}
			temp.setTextual(imageSet.get(value).getTextFeature());
			temp.setVisual(imageSet.get(value).getPcaVisual());
			centoid.add(temp);
			imagepos.add(value);
		}
		System.out.println("number of bags for iteration " + numBags);
		// create bags based on numBags
		bags = EmptyBag(numBags);
		int threadCount = 10;
		Thread tr[] = new Thread[threadCount];
		CentoidCalculator calculate = new CentoidCalculator();
		calculate.setImageSet(imageSet);
		int i;
		for (int k = 0; k < iteration; k++) {
			System.out.println("Iteration " + k);
			long startTime = System.currentTimeMillis();
			bags = EmptyBag(numBags);
			calculate.setBags(bags);
			calculate.setCentoid(centoid);
			int start = 0;
			int end = size / threadCount;
			for (i = 0; i < threadCount; i++) {
				if (start + end > size) {
					CentoidRunable runCentoid = new CentoidRunable(start, size,
							calculate);
					tr[i] = new Thread(runCentoid);
					break;
				} else {
					CentoidRunable runCentoid = new CentoidRunable(start, start
							+ end, calculate);
					tr[i] = new Thread(runCentoid);
					start = start + end + 1;
				}
			}
			for (i = 0; i < threadCount; i++) {
				if (tr[i] != null) {
					tr[i].start();
				}
			}

			for (i = 0; i < threadCount; i++) {
				try {
					if (tr[i] != null) {
						tr[i].join();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			bags = calculate.getBags();
			centoid = calculate.getCentoid();
			centoid = CalculateNewCentoid(bags, centoid);
			long endTime = System.currentTimeMillis();
			System.out.println("That took " + (endTime - startTime)
					+ " milliseconds");
		}/*
		 * for (i = 0; i < bags.size(); i++) { System.out.println("Bag " + i +
		 * " has " + bags.get(i).getImage().size() + " number of instances"); }
		 */
		return bags;
	}

	private static List<Bag> EmptyBag(int numBags) {
		// TODO Auto-generated method stub
		// create bags based on numBags
		List<Bag> bags = new ArrayList<Bag>();
		for (int i = 0; i < numBags; i++) {
			List<Image> cluster = new ArrayList<Image>();
			bags.add(new Bag(cluster));
		}
		return bags;
	}

	private static List<Centoid> CalculateNewCentoid(List<Bag> bags,
			List<Centoid> centoid2) {
		// TODO Auto-generated method stub
		// New Centoid values for each bags based on the new bag
		List<Centoid> centoid = new ArrayList<Centoid>();
		for (int i = 0; i < bags.size(); i++) {
			// initalize centoid for bag n and get the centoid
			if (bags.get(i).getImage().size() == 0) {
				centoid.add(centoid2.get(i));
			} else {
				Centoid temp = CalculateCentoid(bags.get(i).getImage());
				centoid.add(temp);
			}
		}
		return centoid;
	}

	@SuppressWarnings("finally")
	private static Centoid CalculateCentoid(List<Image> list) {
		// TODO Auto-generated method stub
		Centoid newCentoid = new Centoid();
		// check for null pointer exception
		try {
			double[] visual = new double[list.get(0).getPcaVisual().length];
			double[] text = new double[list.get(0).getTextFeature().length];
			for (Image temp : list) {
				for (int i = 0; i < visual.length; i++) {
					visual[i] += (double) temp.getPcaVisual()[i];
				}
				for (int j = 0; j < text.length; j++) {
					text[j] += (double) temp.getTextFeature()[j];
				}
			}
			for (int i = 0; i < visual.length; i++) {
				visual[i] = visual[i] / list.size();
			}
			for (int i = 0; i < text.length; i++) {
				text[i] = text[i] / list.size();
			}
			newCentoid.setTextual(text);
			newCentoid.setVisual(visual);
		} catch (NullPointerException e) {
			System.out.println("Happen at " + list.get(0).getFilename());
		} finally {
			return newCentoid;
		}
	}

	private static List<Image> AssignPCA(List<Image> terms,
			RealMatrix principalComponent, String[] matrix) {
		// TODO Auto-generated method stub
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		for (int i = 0; i < terms.size(); i++) {
			double[] pcaVisual = new double[119];
			for (int j = 0; j < pcaVisual.length; j++) {
				pcaVisual[j] = Double.parseDouble(twoDForm
						.format(principalComponent.getRow(i)[j]));
			}
			terms.get(i).setPcaVisual(pcaVisual);
			double[] textInfo = ConvertTextToDouble(matrix[i]);
			terms.get(i).setTextFeature(textInfo);
		}
		return terms;
	}

	private static double[] ConvertTextToDouble(String tempText) {
		// TODO Auto-generated method stub
		String[] parts = tempText.split("\\s+");
		double[] finalText = new double[parts.length];
		for (int i = 0; i < parts.length; i++) {
			finalText[i] = Double.parseDouble(parts[i]);
		}
		return finalText;
	}

	private static RealMatrix CreateMatrix(int row, int column,
			List<Image> cloneterms) throws IOException {
		// TODO Auto-generated method stub
		RealMatrix rm = new BlockRealMatrix(row, column);
		// for each image add in the values of the visual feature into each row
		// n number of dimension n number
		// of columns
		for (int i = 0; i < row; i++) {
			String[] visualImage = cloneterms.get(i).combineVisualFeatures()
					.split("\\s+");
			for (int j = 0; j < visualImage.length; j++) {
				double value = Double.parseDouble(visualImage[j]);
				rm.setEntry(i, j, value);
			}
		}
		return rm;
	}

	private static String[] ExtractTextFeature(List<Image> terms)
			throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(
				new FileReader(
						"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Tags/Final_Tag_List.txt"));
		String line = null;
		int counter = 0;
		String[] matrix = new String[terms.size()];
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			counter++;
			if (counter == 201) {
				break;
			}
			for (int i = 0; i < parts.length; i++) {
				for (int j = 0; j < terms.size(); j++) {
					String[] tempParts = terms.get(j).getParts();
					boolean check = false;
					for (int k = 1; k < tempParts.length; k++) {
						if (tempParts[k].equals(parts[i])) {
							check = true;
							break;
						}
					}
					if (check == true) {
						if (counter == 1) {
							matrix[j] = "1";
						} else {
							matrix[j] += " 1";
						}
					} else {
						if (counter == 1) {
							matrix[j] = "0";
						} else {
							matrix[j] += " 0";
						}
					}
				}
			}
		}
		reader.close();
		return matrix;
	}

	private static void GenerateVocab() throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(
				new FileReader(
						"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Tags/Final_Tag_List.txt"));
		String line = null;
		List<String> words = new ArrayList<>();
		List<TopVocab> top = new ArrayList<TopVocab>();
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			counter++;
			System.out.println("line" + counter);
			if (counter == 200) {
				break;
			}
			for (int i = 0; i < parts.length; i++) {
				List<Image> terms = new ArrayList<Image>();
				if (top.size() == 0) {
					terms = searchForTerm(parts[i]);
					TopVocab tv = new TopVocab();
					tv.setWord(parts[i]);
					tv.setCount(terms.size());
					top.add(tv);
					words.add(parts[i]);
				} else {
					if (words.contains(parts[i]) == false) {
						terms = searchForTerm(parts[i]);
						TopVocab tv = new TopVocab();
						tv.setWord(parts[i]);
						tv.setCount(terms.size());
						top.add(tv);
						words.add(parts[i]);
					}
				}
			}
		}
		reader.close();
	}

	private static List<Image> searchForTerm(String searchTerm)
			throws IOException {
		// TODO Auto-generated method stub
		List<Image> terms = new ArrayList<Image>();
		int counter = 0;
		// Tags file
		BufferedReader reader = new BufferedReader(
				new FileReader(
						"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Tags/All_Tags.txt"));
		// Get URL of files first
		BufferedReader urlReader = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/Imagelist.txt"));
		// Visual Feature Files
		BufferedReader vReader = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Low_Level_Features/"
						+ "Low_Level_Features/Normalized_EDH.dat"));
		BufferedReader vReader2 = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Low_Level_Features/"
						+ "Low_Level_Features/Normalized_WT.dat"));
		BufferedReader vReader3 = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Low_Level_Features/"
						+ "Low_Level_Features/Normalized_CM55.dat"));
		// GroundTruth Files
	
		/* BufferedReader gReader = new BufferedReader(new FileReader(
		 "C:/Users/ycheng/Downloads/NUS-WIDE-Lite/Groundtruth/AllLabels/Labels_"
		 + searchTerm + ".txt"));*/
		 
		String line = null;
		String vline = null;
		String vline2 = null;
		String vline3 = null;
		//String gline = null;
		String urlLine = null;
		while ((line = reader.readLine()) != null
				&& (vline = vReader.readLine()) != null
				&& (vline2 = vReader2.readLine()) != null
				&& (vline3 = vReader3.readLine()) != null
				/* && (gline = gReader.readLine()) != null*/ 
				&& (urlLine = urlReader.readLine()) != null) {
			counter++;
			List<String> parts = new ArrayList<String>();
			for (String temp : line.split("\\s+")) {
				parts.add(temp);
			}
			if (parts.contains(searchTerm)) {
				Image img = new Image();
				img.setImagePosition(counter);
				img.setTagPosition(parts.indexOf(searchTerm));
				img.setTotalTag(parts.size() - 1);
				img.setFilename(parts.get(0));
				img.setParts(line.split("\\s+"));
				img.setVisualF1(vline);
				img.setVisualF2(vline2);
				img.setVisualF3(vline3);
				img.setUrl(urlLine);
				//img.setPreLabel(Integer.parseInt(gline));
				terms.add(img);
			}
			/*
			 * for (int i = 1; i < parts.length; i++) { if
			 * (name.equals(parts[i])) { Image img = new Image();
			 * img.setImagePosition(counter); img.setTagPosition(i);
			 * img.setTotalTag(parts.length - 1); img.setFilename(parts[0]);
			 * img.setParts(parts); img.setVisualF1(vline);
			 * img.setVisualF2(vline2); img.setVisualF3(vline3); terms.add(img);
			 * break; } }
			 */
		}
		reader.close();
		vReader.close();
		vReader2.close();
		vReader3.close();
		// gReader.close();
		// terms = GetVisualFeature(terms);
		System.out.println(terms.size());
		return terms;
	}

	private static List<Image> GenerateNegativeImage(String searchTerm, int size)
			throws IOException {
		// TODO Auto-generated method stub
		List<Image> terms = new ArrayList<Image>();
		int counter = 0;
		BufferedReader reader = new BufferedReader(
				new FileReader(
						"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Tags/All_Tags.txt"));
		BufferedReader vReader = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Low_Level_Features/"
						+ "Low_Level_Features/Normalized_EDH.dat"));
		BufferedReader vReader2 = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Low_Level_Features/"
						+ "Low_Level_Features/Normalized_WT.dat"));
		BufferedReader vReader3 = new BufferedReader(new FileReader(
				"C:/Users/ycheng/Downloads/NUS-WIDE-Lite/NUS_WID_Low_Level_Features/"
						+ "Low_Level_Features/Normalized_CM55.dat"));
		String line = null;
		String vline = null;
		String vline2 = null;
		String vline3 = null;
		while ((line = reader.readLine()) != null
				&& (vline = vReader.readLine()) != null
				&& (vline2 = vReader2.readLine()) != null
				&& (vline3 = vReader3.readLine()) != null) {
			if (terms.size() == size) {
				break;
			}
			counter++;
			List<String> parts = new ArrayList<String>();
			for (String temp : line.split("\\s+")) {
				parts.add(temp);
			}
			if (parts.contains(searchTerm) == false) {
				Image img = new Image();
				img.setImagePosition(counter);
				img.setTagPosition(0);
				img.setTotalTag(parts.size() - 1);
				img.setFilename(parts.get(0));
				img.setParts(line.split("\\s+"));
				img.setVisualF1(vline);
				img.setVisualF2(vline2);
				img.setVisualF3(vline3);
				terms.add(img);
			}
		}
		reader.close();
		vReader.close();
		vReader2.close();
		vReader3.close();
		// terms = GetVisualFeature(terms);
		// System.out.println(terms.size());
		return terms;
	}

	private static List<Image> rankImage(List<Image> terms) {
		// TODO Auto-generated method stub
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		for (int i = 0; i < terms.size(); i++) {
			Image img = new Image();
			img = terms.get(i);
			double score;
			score = 0 - img.getTagPosition()
					+ (1.0 / (double) img.getTotalTag());
			score = Double.parseDouble(twoDForm.format(score));
			img.setRankScore(score);
			terms.set(i, img);
		}
		return terms;
	}
}
