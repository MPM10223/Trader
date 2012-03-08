
public class AbsoluteChangeExpectationCalculator extends PrePostTestControlExpectationCalculator {

	@Override
	protected int getDaysPre() {
		return 7;
	}

	@Override
	protected int getDaysPost() {
		return 7;
	}

	@Override
	protected double calculateExpectation(double controlPre, double controlPost, double testPre) {
		return testPre + (controlPost - controlPre);
	}

}
