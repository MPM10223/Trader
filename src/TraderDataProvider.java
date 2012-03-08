import sqlWrappers.SQLDatabase;


public class TraderDataProvider {
	
	protected SQLDatabase traderDB;
	
	public TraderDataProvider() {
		this.traderDB = new SQLDatabase("192.168.1.114:1433", "Trader", "simulator", "pwd");
	}

}
