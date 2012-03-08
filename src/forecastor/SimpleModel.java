package forecastor;

import neuralnetwork.IModel;

public class SimpleModel implements IModel {

	protected double prediction;
	
	public SimpleModel(double prediction) {
		this.prediction = prediction;
	}
	
	@Override
	public double predict(double[] ivs) {
		return this.prediction;
	}

}
