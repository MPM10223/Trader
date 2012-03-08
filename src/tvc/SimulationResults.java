package tvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

public class SimulationResults<TSubject> {
	
	protected HashMap<SimulationConfiguration<TSubject>, Vector<SimulationResult<TSubject>>> resultMap;
	private HashMap<SimulationConfigurationClass, SimulationConfiguration<TSubject>> configurations;
	protected Integer iterations;
	
	public SimulationResults() {
		this.resultMap = new HashMap<SimulationConfiguration<TSubject>, Vector<SimulationResult<TSubject>>>();
		this.configurations = new HashMap<SimulationConfigurationClass, SimulationConfiguration<TSubject>>();
	}
	
	public SimulationResults(int options, int iterations) {
		this.resultMap = new HashMap<SimulationConfiguration<TSubject>, Vector<SimulationResult<TSubject>>>(options);
		this.configurations = new HashMap<SimulationConfigurationClass, SimulationConfiguration<TSubject>>(options);
		this.iterations = iterations;
	}
	
	public void recordResult(IControlSelector<TSubject> c, IExpectationCalculator<TSubject> e, ILiftCalculator<TSubject> l, SimulationResult<TSubject> r) {
		recordResult(getSimulationConfiguration(c, e, l), r);
	}

	public void recordResult(SimulationConfiguration<TSubject> c, SimulationResult<TSubject> r) {
		if(!resultMap.containsKey(c))
			resultMap.put(c, new Vector<SimulationResult<TSubject>>(iterations));
		resultMap.get(c).add(r);
	}
	
	public Map<SimulationConfiguration<TSubject>, Double> getStandardDeviations() {
		Map<SimulationConfiguration<TSubject>, SummaryStatistics> cs = getConfigurationStatistics();
		HashMap<SimulationConfiguration<TSubject>, Double> sds = new HashMap<SimulationConfiguration<TSubject>, Double>(resultMap.size());
		
		for(SimulationConfiguration<TSubject> c : cs.keySet()) {
			SummaryStatistics ss = cs.get(c);
			sds.put(c, ss.getStandardDeviation());
			//sds.put(c, Math.sqrt(ss.getSecondMoment() / ss.getN()));
		}
		return sds;
	}
	
	public long getMinSampleSize() {
		Map<SimulationConfiguration<TSubject>, SummaryStatistics> cs = getConfigurationStatistics();
		
		long n = Long.MAX_VALUE;
		for(SimulationConfiguration<TSubject> c : cs.keySet()) {
			SummaryStatistics ss = cs.get(c);
			long cn = ss.getN();
			if(cn < n) n = cn;
		}
		return n;
	}
	
	protected Map<SimulationConfiguration<TSubject>, SummaryStatistics> getConfigurationStatistics() {
		HashMap<SimulationConfiguration<TSubject>, SummaryStatistics> cs = new HashMap<SimulationConfiguration<TSubject>, SummaryStatistics>(resultMap.size());
		for(SimulationConfiguration<TSubject> c : resultMap.keySet()) {
			SummaryStatistics ss = new SummaryStatistics();
			Vector<SimulationResult<TSubject>> lifts = resultMap.get(c);
			for(SimulationResult<TSubject> r : lifts) {
				if(r.getAggregateLift() != null && !r.getAggregateLift().equals(Double.NaN) && !r.getAggregateLift().equals(Double.POSITIVE_INFINITY) && !r.getAggregateLift().equals(Double.NEGATIVE_INFINITY)) {
					ss.addValue(r.getAggregateLift().doubleValue());
				}
			}
			cs.put(c, ss);
		}
		return cs;
	}
	
	private SimulationConfiguration<TSubject> getSimulationConfiguration(IControlSelector<TSubject> c, IExpectationCalculator<TSubject> e, ILiftCalculator<TSubject> l) {
		SimulationConfigurationClass css = new SimulationConfigurationClass(c.getClass(), e.getClass(), l.getClass());
		if(!configurations.containsKey(css))
			configurations.put(css, new SimulationConfiguration<TSubject>(c, e, l));
		return configurations.get(css);
	}
	
	private class SimulationConfigurationClass {
		private Class<? extends IControlSelector> c;
		private Class<? extends IExpectationCalculator> e;
		private Class<? extends ILiftCalculator> l;

		public SimulationConfigurationClass(Class<? extends IControlSelector> c, Class<? extends IExpectationCalculator> e, Class<? extends ILiftCalculator> l) {
			this.c = c;
			this.e = e;
			this.l = l;
		}

		public Class<? extends IControlSelector> getC() {
			return c;
		}

		public void setC(Class<? extends IControlSelector> c) {
			this.c = c;
		}

		public Class<? extends IExpectationCalculator> getE() {
			return e;
		}

		public void setE(Class<? extends IExpectationCalculator> e) {
			this.e = e;
		}

		public Class<? extends ILiftCalculator> getL() {
			return l;
		}

		public void setL(Class<? extends ILiftCalculator> l) {
			this.l = l;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof SimulationResults.SimulationConfigurationClass) {
				SimulationConfigurationClass other = (SimulationConfigurationClass)obj;
				return this.c.equals(other.c) && this.e.equals(other.e) && this.l.equals(other.l);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return c.hashCode() + e.hashCode() + l.hashCode() % Integer.MAX_VALUE;
		}
	}
}
