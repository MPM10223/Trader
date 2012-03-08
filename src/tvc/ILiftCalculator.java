package tvc;

import java.util.Date;
import java.util.Map;

public interface ILiftCalculator<TSubject> {

	Map<TSubject, Double> calculateLift(Map<TSubject, Double> expectations, Date eventDate, IMetricProvider<TSubject> metricProvider);
	Double calculateAggregateLift(Map<TSubject, Double> expectations, Map<TSubject, Double> lifts, IMetricProvider<TSubject> metricProvider);

}
