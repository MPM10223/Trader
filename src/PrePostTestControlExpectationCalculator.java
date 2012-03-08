import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tvc.IExpectationCalculator;
import tvc.IMetricProvider;


public abstract class PrePostTestControlExpectationCalculator implements
		IExpectationCalculator<Integer> {

	@Override
	public Map<Integer, Double> calculateExpectation(Map<Integer, Integer[]> controlStrategy, Date eventDate, IMetricProvider<Integer> metricProvider) {
		
		HashMap<Integer, Double> expectations = new HashMap<Integer, Double>(controlStrategy.size());
		
		for(Integer t : controlStrategy.keySet()) {
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, -1 * this.getDaysPre());
			Date start = cal.getTime();
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, -1 * 1);
			Date end = cal.getTime();
			
			double controlPre = metricProvider.getAverageMetric(controlStrategy.get(t), start, end);
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, 0);
			start = cal.getTime();
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, this.getDaysPost());
			end = cal.getTime();
			
			Double controlPost = metricProvider.getAverageMetric(controlStrategy.get(t), start, end);

			cal.setTime(eventDate);
			cal.add(Calendar.DATE, -1 * this.getDaysPre());
			start = cal.getTime();
			
			cal.setTime(eventDate);
			cal.add(Calendar.DATE, -1 * 1);
			end = cal.getTime();
			
			Double testPre = metricProvider.getAverageMetric(t, start, end);
			
			if(testPre != null && controlPost != null) {
				expectations.put(t, this.calculateExpectation(controlPre, controlPost, testPre));
			} else {
				expectations.put(t, null);
			}
		}
		
		return expectations;
	}
	
	protected abstract int getDaysPre();
	protected abstract int getDaysPost();
	
	protected abstract double calculateExpectation(double controlPre, double controlPost, double testPre);

}
