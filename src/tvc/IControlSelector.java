package tvc;

import java.util.Date;
import java.util.Map;

public interface IControlSelector<TSubject> {

	Map<TSubject, TSubject[]> selectControl(TSubject[] testSet, TSubject[] universe, Date eventDate, IMetricProvider<TSubject> metricProvider);

}
