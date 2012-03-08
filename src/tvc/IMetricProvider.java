package tvc;

import java.util.Date;

public interface IMetricProvider<TSubject> {

	Date getEarliestDate();
	Date getLatestDate();
	Double getAverageMetric(TSubject[] objects, Date start, Date end);
	Double getAverageMetric(TSubject object, Date start, Date end);
}
