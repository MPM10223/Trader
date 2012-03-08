package tvc;

import java.util.Map;
import java.util.Random;

public class Simulation<TSubject> {
	
	protected Class<TSubject> c;
	protected TSubject[] universe;
	protected int testSize;
	protected IControlSelector<TSubject>[] controlOptions;
	protected IExpectationCalculator<TSubject>[] expectationOptions;
	protected ILiftCalculator<TSubject>[] liftOptions;
	protected IMetricProvider<TSubject> metricProvider;
	protected int iterations;
	protected Integer seed;
	
	public Simulation(Class<TSubject> c, TSubject[] universe, int testSize, IControlSelector<TSubject>[] controlOptions, IExpectationCalculator<TSubject>[] expectationOptions, ILiftCalculator<TSubject>[] liftOptions, IMetricProvider<TSubject> metricProvider, int iterations) {
		this(c, universe, testSize, controlOptions, expectationOptions, liftOptions, metricProvider, iterations, null);
	}
	
	public Simulation(Class<TSubject> c, TSubject[] universe, int testSize, IControlSelector<TSubject>[] controlOptions, IExpectationCalculator<TSubject>[] expectationOptions, ILiftCalculator<TSubject>[] liftOptions, IMetricProvider<TSubject> metricProvider, int iterations, Integer seed) {
		super();
		this.c = c;
		this.universe = universe;
		this.testSize = testSize;
		this.controlOptions = controlOptions;
		this.expectationOptions = expectationOptions;
		this.liftOptions = liftOptions;
		this.metricProvider = metricProvider;
		this.iterations = iterations;
		this.seed = seed;
	}
	
	public SimulationResults<TSubject> run() {
		Random r = (seed == null) ? new Random() : new Random(seed);
		SimulationResults<TSubject> results = new SimulationResults<TSubject>(controlOptions.length * expectationOptions.length * liftOptions.length, iterations);
		
		for(int i = 0; i < iterations; i++) {
			System.out.println(String.format("Executing null test #%d of %d", i+1, iterations));
			
			// create null test
			NullTest<TSubject> t = new NullTest<TSubject>(universe, testSize, metricProvider.getEarliestDate(), metricProvider.getLatestDate(), r, c);
			
			// iterate over control options
			for(IControlSelector<TSubject> c : this.controlOptions) {
				// iterate over expectation options
				for(IExpectationCalculator<TSubject> e : this.expectationOptions) {
					// iterate over lift options
					for(ILiftCalculator<TSubject> l : this.liftOptions) {
						
						//System.out.println(String.format("Trying %s | %s | %s", c.getClass().getName(), e.getClass().getName(), l.getClass().getName()));
						
						// select control
						Map<TSubject, TSubject[]> controlStrategy = c.selectControl(t.getTestSet(), universe, t.getTestDate(), metricProvider);
						
						// calculate expectations
						Map<TSubject, Double> expectations = e.calculateExpectation(controlStrategy, t.getTestDate(), metricProvider);
						
						// measure lift
						Map<TSubject, Double> lifts = l.calculateLift(expectations, t.getTestDate(), metricProvider); 
						
						// calculate aggregate lift
						Double aggregateLift = l.calculateAggregateLift(expectations, lifts, metricProvider);
						
						// record results
						SimulationResult<TSubject> result = new SimulationResult<TSubject>(t, controlStrategy, expectations, lifts, aggregateLift);
						results.recordResult(c, e, l, result);
					}
				}
			}
		}
		
		return results;
	}
}
