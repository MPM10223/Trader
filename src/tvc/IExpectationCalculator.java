package tvc;

import java.util.Date;
import java.util.Map;

public interface IExpectationCalculator<TSubject> {

	Map<TSubject, Double> calculateExpectation(Map<TSubject, TSubject[]> controlStrategy, Date eventDate, IMetricProvider<TSubject> metricProvider);

}
