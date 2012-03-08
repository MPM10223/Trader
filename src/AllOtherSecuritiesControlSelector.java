import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tvc.IMetricProvider;


public class AllOtherSecuritiesControlSelector extends NonTestControlSelector<Integer> {

	public Map<Integer, Integer[]> selectControl(Integer[] testSet, Integer[] universe, Date eventDate, IMetricProvider<Integer> metricProvider) {
		
		Integer[] nonTest = getNonTest(universe, testSet, Integer.class);
		/*Integer[] nonTestSecurities = new Integer[nonTest.length];
		for(int i = 0; i < nonTest.length; i++) {
			nonTestSecurities[i] = (Integer)nonTest[i];
		}*/
		
		HashMap<Integer, Integer[]> control = new HashMap<Integer, Integer[]>(testSet.length);
		for(int i = 0; i < testSet.length; i++) {
			control.put(testSet[i], nonTest);
		}
		
		return control;
	}

}
