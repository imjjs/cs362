package cs362;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cs362.hw1.EvenOddClassifier;
import cs362.hw1.MajorityClassifier;
import cs362.hw2.LinearRegression;
import cs362.hw2.NaiveBayesClassifier;
import cs362.hw2.PerceptronClassifier;
import cs362.hw3.GaussianKernelLogisticRegression;
import cs362.hw3.LinearKernelLogisticRegression;
import cs362.hw3.PolynomialKernelLogisticRegression;
import cs362.hw4.LambdaMeansPredictor;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Learn {
	static public LinkedList<Option> options = new LinkedList<Option>();
	
	public static void main(String[] args) throws IOException {
		// Parse the command line.
		String[] manditory_args = { "mode"};
		createCommandLineOptions();
		CommandLineUtilities.initCommandLineParameters(args, Learn.options, manditory_args);
	
		String mode = CommandLineUtilities.getOptionValue("mode");
		String data = CommandLineUtilities.getOptionValue("data");
		String predictions_file = CommandLineUtilities.getOptionValue("predictions_file");
		String algorithm = CommandLineUtilities.getOptionValue("algorithm");
		String model_file = CommandLineUtilities.getOptionValue("model_file");
		String task = CommandLineUtilities.getOptionValue("task"); // classification vs. regression

		boolean classify = true;
		
		if (task != null && task.equals("regression")) {
		    classify = false;
		}
		
		if (mode.equalsIgnoreCase("train")) {
			if (data == null || algorithm == null || model_file == null) {
				System.out.println("Train requires the following arguments: data, algorithm, model_file");
				System.exit(0);
			}
			// Load the training data.
			DataReader data_reader = new DataReader(data, classify);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Train the model.
			Predictor predictor = train(instances, algorithm);
			saveObject(predictor, model_file);		
			
		} else if (mode.equalsIgnoreCase("test")) {
			if (data == null || predictions_file == null || model_file == null) {
				System.out.println("Train requires the following arguments: data, predictions_file, model_file");
				System.exit(0);
			}
			
			// Load the test data.
			DataReader data_reader = new DataReader(data, classify);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Load the model.
			Predictor predictor = (Predictor)loadObject(model_file);
			evaluateAndSavePredictions(predictor, instances, predictions_file);
		} else {
			System.out.println("Requires mode argument.");
		}
	}
	

	private static Predictor train(List<Instance> instances, String algorithm) {

        Map<String, Predictor> algo = new HashMap <String, Predictor>();
        //hw1------------------------------------------------------------------------------------------------
        Predictor majority = new MajorityClassifier();
        Predictor even_odd = new EvenOddClassifier();
        //hw2---------------------------------------------------------------------------------------------------
        Predictor linerR = new LinearRegression();

        double lambda = 1d;
        if (CommandLineUtilities.hasArg("lambda"))
            lambda = CommandLineUtilities.getOptionValueAsFloat("lambda");

        Predictor naiveBayes = new NaiveBayesClassifier(lambda);

        double online_learning_rate = 1d;
        if (CommandLineUtilities.hasArg("online_learning_rate"))
            online_learning_rate = CommandLineUtilities.getOptionValueAsFloat("online_learning_rate");
        int online_training_iterations = 1;
        if (CommandLineUtilities.hasArg("online_training_iterations"))
            online_training_iterations = CommandLineUtilities.getOptionValueAsInt("online_training_iterations");

        Predictor perceptron = new PerceptronClassifier(online_learning_rate, online_training_iterations);

        //hw3-----------------------------------------------------------------------------------------------------
        double gradient_ascent_learning_rate = 0.01;
        if (CommandLineUtilities.hasArg("gradient_ascent_learning_rate"))
            gradient_ascent_learning_rate =
                    CommandLineUtilities.getOptionValueAsFloat("gradient_ascent_learning_rate");

        int gradient_ascent_training_iterations = 5;
        if (CommandLineUtilities.hasArg("gradient_ascent_training_iterations"))
            gradient_ascent_training_iterations =
                    CommandLineUtilities.getOptionValueAsInt("gradient_ascent_training_iterations");

        Predictor logistic_regression;
        String kernel = "linear_kernel";
        if (CommandLineUtilities.hasArg("kernel"))
            kernel = CommandLineUtilities.getOptionValue("kernel");
        if(kernel.equals("linear_kernel"))
            logistic_regression = new LinearKernelLogisticRegression(gradient_ascent_learning_rate, gradient_ascent_training_iterations);
        else if(kernel.equals("polynomial_kernel")){
            double polynomial_kernel_exponent = 2;
            if (CommandLineUtilities.hasArg("polynomial_kernel_exponent"))
                polynomial_kernel_exponent = CommandLineUtilities.getOptionValueAsFloat("polynomial_kernel_exponent");

            logistic_regression = new PolynomialKernelLogisticRegression(gradient_ascent_learning_rate, gradient_ascent_training_iterations, polynomial_kernel_exponent);
        }
        else if(kernel.equals("gaussian_kernel")){
            double gaussian_kernel_sigma = 1;
            if (CommandLineUtilities.hasArg("gaussian_kernel_sigma"))
                gaussian_kernel_sigma = CommandLineUtilities.getOptionValueAsFloat("gaussian_kernel_sigma");

            logistic_regression = new GaussianKernelLogisticRegression(gradient_ascent_learning_rate, gradient_ascent_training_iterations, gaussian_kernel_sigma);
        }
        else
            logistic_regression = null;
        //------------hw4-----------------------------------------------
        double cluster_lambda = 0.0;
        if (CommandLineUtilities.hasArg("cluster_lambda"))
            cluster_lambda = CommandLineUtilities.getOptionValueAsFloat("cluster_lambda");
        int clustering_training_iterations = 10;
        if (CommandLineUtilities.hasArg("clustering_training_iterations"))
            clustering_training_iterations = CommandLineUtilities.getOptionValueAsInt("clustering_training_iterations");
        Predictor lambda_means = new LambdaMeansPredictor(cluster_lambda, clustering_training_iterations);
        //-------------------------------------------------------------------
        algo.put("majority", majority);
        algo.put("even_odd", even_odd);
        algo.put("linear_regression", linerR);
        algo.put("naive_bayes", naiveBayes);
        algo.put("perceptron", perceptron);
        algo.put("logistic_regression", logistic_regression);
        algo.put("lambda_means", lambda_means);
        //-------------------------------------------------------------------
        Predictor selected_algo = algo.get(algorithm);
        selected_algo.train(instances);
	    return selected_algo;
	}

	private static void evaluateAndSavePredictions(Predictor predictor,
			List<Instance> instances, String predictions_file) throws IOException {
		PredictionsWriter writer = new PredictionsWriter(predictions_file);
		// TODO Evaluate the model if labels are available. 
		
		for (Instance instance : instances) {
			Label label = predictor.predict(instance);
			writer.writePrediction(label);
		}
		
		writer.close();
		
	}

	public static void saveObject(Object object, String file_name) {
		try {
			ObjectOutputStream oos =
				new ObjectOutputStream(new BufferedOutputStream(
						new FileOutputStream(new File(file_name))));
			oos.writeObject(object);
			oos.close();
		}
		catch (IOException e) {
			System.err.println("Exception writing file " + file_name + ": " + e);
		}
	}

	/**
	 * Load a single object from a filename. 
	 * @param file_name
	 * @return
	 */
	public static Object loadObject(String file_name) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(file_name))));
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (IOException e) {
			System.err.println("Error loading: " + file_name);
		} catch (ClassNotFoundException e) {
			System.err.println("Error loading: " + file_name);
		}
		return null;
	}
	
	public static void registerOption(String option_name, String arg_name, boolean has_arg, String description) {
		OptionBuilder.withArgName(arg_name);
		OptionBuilder.hasArg(has_arg);
		OptionBuilder.withDescription(description);
		Option option = OptionBuilder.create(option_name);
		
		Learn.options.add(option);		
	}
	
	private static void createCommandLineOptions() {
		registerOption("data", "String", true, "The data to use.");
		registerOption("mode", "String", true, "Operating mode: train or test.");
		registerOption("predictions_file", "String", true, "The predictions file to create.");
		registerOption("algorithm", "String", true, "The name of the algorithm for training.");
		registerOption("model_file", "String", true, "The name of the model file to create/load.");
		registerOption("task", "String", true, "The name of the task (classification or regression).");
        registerOption("lambda", "double", true, "The level of smoothing for Naive Bayes.");
        registerOption("online_learning_rate", "double", true, "The LTU learning rate.");
        registerOption("online_training_iterations", "int", true, "The number of training iterations for LTU.");
        //--------------hw3------------------------------------------------------------------------------------------------------
        registerOption("gradient_ascent_learning_rate", "double", true, "The learning rate for logistic regression.");
        registerOption("kernel", "String", true, "The kernel for Kernel Logistic regression [linear_kernel, polynomial_kernel, gaussian_kernel].");
        registerOption("gradient_ascent_training_iterations", "int", true, "The number of training iterations.");

        registerOption("polynomial_kernel_exponent", "double", true, "The exponent of the polynomial kernel.");
        registerOption("gaussian_kernel_sigma", "double", true, "The sigma of the Gaussian kernel.");
        //--------------hw4-----------------------------------------------------------------------------------------------------------------
        registerOption("cluster_lambda", "double", true, "The value of lambda in lambda-means.");
        registerOption("clustering_training_iterations", "int", true, "The number of lambda-means EM iterations.");

        // Other options will be added here.
	}
}
