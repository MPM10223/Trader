package forecastor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import neuralnetwork.DiscretizedNeuralNetwork;
import neuralnetwork.IModel;
import neuralnetwork.Observation;
import neuralnetwork.SQLTrainingDataEvaluator;
import search.GeneticSearch;
import search.RepeatedRandom;
import sqlWrappers.SQLDatabase;

public class Forecastor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testDaily(90);
	}
	
	public static void testDaily(int preDays) {
		SimpleDateFormat f = new SimpleDateFormat("MM-dd-yyyy");
		Date start;
		Date end;
		try {
			start = f.parse("01-04-1989");
			end = f.parse("01-19-2012");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		int daysBetween = getDaysBetween(start, end);
		int iterations = daysBetween - preDays;
		
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		
		SummaryStatistics model = new SummaryStatistics();
		SummaryStatistics benchmark = new SummaryStatistics();
		
		System.out.println("N|date|trainingAccuracy|actual|modelCorrect|bmCorrect|actualReturns|modelCumulative|bmCumulative");
		
		for(int i = 0; i < iterations; i++) {
			c.add(Calendar.DAY_OF_MONTH, -1);
			Date d = c.getTime();
			
			ForecastorEvaluation e = testForDate(f.format(d), preDays, 1);
			
			if(e != null && !Double.isNaN(e.getBenchmarkAccuracy())) {
				model.addValue(e.getForecastAccuracy());
				benchmark.addValue(e.getBenchmarkAccuracy());
				System.out.println(String.format("%d|%s|%f|%f|%f|%f|%s|%f|%f", model.getN(), f.format(d), e.getTrainingAccuracy(), e.getActuals()[0], e.getForecastAccuracy(), e.getBenchmarkAccuracy(), e.getActualDetails()[0], model.getMean(), benchmark.getMean()));
			}
			//System.out.println(String.format("N = %d || Model: %.4f +/- %.4f, Benchmark: %.4f +/- %.4f", model.getN(), model.getMean(), model.getStandardDeviation(), benchmark.getMean(), benchmark.getStandardDeviation()));
		}
	}
	
	protected static int getDaysBetween(Date d1, Date d2) {
		Calendar start = Calendar.getInstance();
		start.setTime(d1);
		
		Calendar end = Calendar.getInstance();
		end.setTime(d2);
		
		int daysBetween = 0;
		while(start.before(end)) {
			start.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		
		return daysBetween;
	}
	
	public static void testAnnual() {
		String[] partitionDates = new String[] {"10-20-1989", "10-19-1990", "10-19-1991", "10-20-1992", "10-20-1993", "10-21-1994", "10-20-1995", "10-20-1996", "10-20-1997", "10-20-1998", "10-20-1999", "10-20-2000", "10-20-2001", "10-20-2002", "10-20-2003", "10-20-2004", "10-20-2005", "10-20-2006", "10-20-2007", "10-20-2008", "10-20-2009", "10-20-2010", "10-20-2011"};
		for(String partitionDate : partitionDates) {
			testForYear(partitionDate);
		}
	}
	
	public static ForecastorEvaluation testForDate(String partitionDate, int daysPre, int daysPost) {
		String trainingPredicate = String.format("date between dateadd(dd, %d, '%s') and dateadd(dd, %d, '%s')", -1 * daysPre, partitionDate, 0, partitionDate);
		String holdoutPredicate = String.format("date between dateadd(dd, %d, '%s') and dateadd(dd, %d, '%s')", 1, partitionDate, daysPost, partitionDate);
		
		return buildKOSPIPredictionModel(trainingPredicate, holdoutPredicate);
	}
	
	public static ForecastorEvaluation testForYear(String partitionDate) {
		String year = partitionDate.substring(6, 10);
		
		String trainingPredicate = "year(date) = "+year+" AND date < '"+partitionDate+"'";
		String holdoutPredicate = "year(date) = "+year+" AND date >= '"+partitionDate+"'";
		
		return buildKOSPIPredictionModel(trainingPredicate, holdoutPredicate);
	}
	
	public static ForecastorEvaluation buildKOSPIPredictionModel(String trainingSQLPredicate, String holdoutSQLPredicate) {

		SQLDatabase db = new SQLDatabase("192.168.1.142", "Trader", "simulator", "pwd");
		String[] ivs = new String[] {"[Stochastic %K]", "[Stochastic %D]", "[Stochastic slow %D]", "[Momentum]", "[Rate of Change]", "[LW %R]", "[A/D Oscillator]", "[Disparity 5d]", "[Disparity 10d]", "[OSCP]", "[CCI]", "[RSI]"};
		
		SQLTrainingDataEvaluator t = new SQLTrainingDataEvaluator(db, "etl_kospi", ivs, "DV", trainingSQLPredicate, "DVReturn", true);
		SQLTrainingDataEvaluator h = new SQLTrainingDataEvaluator(db, "etl_kospi", ivs, "DV", holdoutSQLPredicate, "DVReturn", true);
		
		if(h.getNumberOfObservations() == 0 || t.getNumberOfObservations() == 0) {
			return null;
		}

		double[] inputRange = getInputRange(t);
		
		DiscretizedNeuralNetworkGenerator g = new DiscretizedNeuralNetworkGenerator(12, -5.0, 5.0, 2 * inputRange[0], 2 * inputRange[1]);
		
		//RepeatedRandom<DiscretizedNeuralNetwork> s = new RepeatedRandom<DiscretizedNeuralNetwork>(g, t, 100000);
		GeneticSearch<DiscretizedNeuralNetwork> s = new GeneticSearch<DiscretizedNeuralNetwork>(g, t, 100, 500, 0.05, 0.1, 0.5, 0.7, DiscretizedNeuralNetwork.class);
		
		DiscretizedNeuralNetwork dnn = s.search();
		
		Observation[] trainingIterations = t.getPerformanceDetails(dnn);
		
		/*
		System.out.println("TRAINING ITERATIONS:");
		System.out.println("--------------------");
		for(Observation o : trainingIterations) {
			System.out.println(String.format("Predicted: %f, Actual: %f", o.getPrediction(), o.getDependentVariable()));
		}
		
		Observation[] holdoutIterations = h.getPerformanceDetails(dnn);
		
		System.out.println("HOLDOUT ITERATIONS:");
		System.out.println("--------------------");
		for(Observation o : holdoutIterations) {
			System.out.println(String.format("Predicted: %f, Actual: %f", o.getPrediction(), o.getDependentVariable()));
		}
		*/
		
		//System.out.println(dnn.toString());
		
		// Model accuracy and benchmarking
		double trainingDVMean = t.getDVMean();
		SimpleModel naive = new SimpleModel(trainingDVMean);
		double bmTrainingAccuracy = t.evaluate(naive);
		double bmHoldoutAccuracy = h.evaluate(naive);
		//System.out.println(String.format("BENCHMARK:       Training: %f, Holdout: %f", bmTrainingAccuracy, bmHoldoutAccuracy));
		
		double trainingAccuracy = t.evaluate(dnn);
		double holdoutAccuracy = h.evaluate(dnn);
		//System.out.println(String.format("NEURAL NETWORK:  Training: %f, Holdout: %f", trainingAccuracy, holdoutAccuracy));
		
		//System.out.println(String.format("YEAR %s: Training: %f, Holdout: %f, Benchmark H/O: %f", trainingSQLPredicate.substring(13, 17), trainingAccuracy, holdoutAccuracy, bmHoldoutAccuracy));
		
		ForecastorEvaluation e = new ForecastorEvaluation(dnn, naive, holdoutAccuracy, bmHoldoutAccuracy, h.getForecasts(), h.getActualDetails(), h.getActuals(), trainingAccuracy);
		
		return e;
	}
	
	private static double[] getInputRange(SQLTrainingDataEvaluator e) {
		double[] range = new double[] { Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
		String[] ivs = e.getIVs();
		
		for(int i = 0; i < ivs.length; i++) {
			double[] ivRange = e.getRangeOfIV(i);
			if(ivRange[0] < range[0]) range[0] = ivRange[0];
			if(ivRange[1] > range[1]) range[1] = ivRange[1];
		}
		
		return range;
	}

	public static void evaluateKOSPIPredictionModel(DiscretizedNeuralNetwork dnn) {
		//TODO
	}

}
