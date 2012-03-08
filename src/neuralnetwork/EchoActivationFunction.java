package neuralnetwork;

public class EchoActivationFunction extends ActivationFunction {

	@Override
	public double process(double[] input) {
		return this.getLinearSum(input);
	}

}
