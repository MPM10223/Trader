import java.util.Map;
import java.util.Vector;


public class SecurityProvider extends TraderDataProvider {
	
	public SecurityProvider() {
		super();
	}

	public Integer[] getAllSecurities() {
		String sql = "SELECT securityID FROM securities";
		Vector<Map<String,String>> results = traderDB.getQueryRows(sql);
		Integer[] securities = new Integer[results.size()];
		
		int i = 0;
		for(Map<String,String> r : results) {
			securities[i] = Integer.parseInt(r.get("securityID"));
			i++;
		}
		
		return securities;
	}

}
