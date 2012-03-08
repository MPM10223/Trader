import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import tvc.IMetricProvider;


public class SecurityPriceProvider extends TraderDataProvider implements IMetricProvider<Integer> {
	
	protected Date startTime;
	protected Date endTime;
	
	public SecurityPriceProvider(Date startTime, Date endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Date getEarliestDate() {
		return startTime;
	}

	public Date getLatestDate() {
		return endTime;
	}
	
	public Map<Integer, Double> getAverageMetrics(Integer[] objects, Date start, Date end) {
		
		String securityIDList = Arrays.toString(objects).replace("[", "").replace("]", "");
		String startString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(start);
		String endString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(end);
		
		String sql = String.format("SELECT securityID, AVG(price) as avgPrice FROM securityMetrics WHERE securityID IN (%s) AND date BETWEEN '%s' AND '%s' GROUP BY securityID", securityIDList, startString, endString);
		
		Vector<Map<String,String>> results = traderDB.getQueryRows(sql);

		HashMap<Integer,Double> averages = new HashMap<Integer, Double>(results.size());
		for(Map<String,String> row : results) {
			if(row.get("avgPrice") != null) {
				averages.put(Integer.parseInt(row.get("securityID")), Double.parseDouble(row.get("avgPrice")));
			} else {
				averages.put(Integer.parseInt(row.get("securityID")), null);
			}
		}
		
		return averages;
	}

	public Double getAverageMetric(Integer object, Date start, Date end) {
		Map<Integer, Double> results = this.getAverageMetrics(new Integer[] {object}, start, end);
		return results.get(object);
	}
	
	public Double getAverageMetric(Integer[] objects, Date start, Date end) {
		SummaryStatistics ss = new SummaryStatistics();
		
		Map<Integer, Double> averages = this.getAverageMetrics(objects, start, end);
		
		for(Integer o : objects) {
			if(averages.containsKey(o) && averages.get(o) != null) {
				ss.addValue(averages.get(o));
			} else {
				//NoOp
			}
		}
		
		return ss.getMean();
	}
}
