package forecastor;

import neuralnetwork.IModel;

public class ForecastorEvaluation {
	
	protected IModel forecastor;
	protected IModel benchmark;
	protected double forecastAccuracy;
	protected double benchmarkAccuracy;
	
	protected Double[] forecasts;
	protected String[] actualDetails;
	protected double[] actuals;
	protected double trainingAccuracy;
	
	public ForecastorEvaluation(IModel forecastor, IModel benchmark, double forecastAccuracy, double benchmarkAccuracy, Double[] forecasts, String[] actualDetails, double[] actuals, double trainingAccuracy) {
		super();
		this.forecastor = forecastor;
		this.benchmark = benchmark;
		this.forecastAccuracy = forecastAccuracy;
		this.benchmarkAccuracy = benchmarkAccuracy;
		this.forecasts = forecasts;
		this.actualDetails = actualDetails;
		this.actuals = actuals;
		this.trainingAccuracy = trainingAccuracy;
	}

	public IModel getForecastor() {
		return forecastor;
	}

	public void setForecastor(IModel forecastor) {
		this.forecastor = forecastor;
	}

	public IModel getBenchmark() {
		return benchmark;
	}

	public void setBenchmark(IModel benchmark) {
		this.benchmark = benchmark;
	}

	public double getForecastAccuracy() {
		return forecastAccuracy;
	}

	public void setForecastAccuracy(double forecastAccuracy) {
		this.forecastAccuracy = forecastAccuracy;
	}

	public double getBenchmarkAccuracy() {
		return benchmarkAccuracy;
	}

	public void setBenchmarkAccuracy(double benchmarkAccuracy) {
		this.benchmarkAccuracy = benchmarkAccuracy;
	}

	public Double[] getForecasts() {
		return this.forecasts;
	}
	
	public String[] getActualDetails() {
		return this.actualDetails;
	}

	public double[] getActuals() {
		return this.actuals;
	}

	public double getTrainingAccuracy() {
		return this.trainingAccuracy;
	}

}
