import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import tvc.ILiftCalculator;
import tvc.IMetricProvider;


public class AbsoluteImpactCalculator implements ILiftCalculator<Integer> {

	public Map<Integer, Double> calculateLift(Map<Integer, Double> expectations, Date eventDate, IMetricProvider<Integer> metricProvider) {
		
		HashMap<Integer, Double> lifts = new HashMap<Integer, Double>(expectations.size());
		
		for(Integer t : expectations.keySet()) {
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, 0);
			Date start = cal.getTime();
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, 7);
			Date end = cal.getTime();
			
			Double testActualPost = metricProvider.getAverageMetric(t, start, end);
			Double testExpectedPost = expectations.get(t);
			
			if(testActualPost != null && testExpectedPost != null) {
				lifts.put(t, testActualPost - testExpectedPost);
			} else {
				lifts.put(t, null);
			}
		}
		
		return lifts;
	}
	
	public Double calculateAggregateLift(Map<Integer, Double> expectations, Map<Integer, Double> lifts, IMetricProvider<Integer> metricProvider) {
		SummaryStatistics ss = new SummaryStatistics();
		for(Integer t : lifts.keySet()) {
			if(lifts.get(t) != null) {
				ss.addValue(lifts.get(t));
			}
		}
		return ss.getMean();
	}

}
