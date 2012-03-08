package tvc;

import java.util.Map;

public class SimulationResult<TSubject> {
	protected NullTest<TSubject> t;
	protected Map<TSubject, TSubject[]> controlStrategy;
	protected Map<TSubject, Double> expectations;
	protected Map<TSubject, Double> lifts;
	Double aggregateLift;

	public SimulationResult(NullTest<TSubject> t, Map<TSubject, TSubject[]> controlStrategy, Map<TSubject, Double> expectations, Map<TSubject, Double> lifts, Double aggregateLift) {
		super();
		this.t = t;
		this.controlStrategy = controlStrategy;
		this.expectations = expectations;
		this.lifts = lifts;
		this.aggregateLift = aggregateLift;
	}
	
	public Double getAggregateLift() {
		return aggregateLift;
	}

	public void setAggregateLift(Double aggregateLift) {
		this.aggregateLift = aggregateLift;
	}
	
}
