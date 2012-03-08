package tvc;

public class SimulationConfiguration<TSubject> {
	protected IControlSelector<TSubject> c;
	protected IExpectationCalculator<TSubject> e;
	protected ILiftCalculator<TSubject> l;
	
	public SimulationConfiguration(IControlSelector<TSubject> c, IExpectationCalculator<TSubject> e, ILiftCalculator<TSubject> l) {
		super();
		this.c = c;
		this.e = e;
		this.l = l;
	}

	@Override
	public String toString() {
		return String.format("%s | %s | %s", c.getClass().getName(), e.getClass().getName(), l.getClass().getName());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SimulationConfiguration) {
			SimulationConfiguration other = (SimulationConfiguration)obj;
			return c.equals(other.c) && e.equals(other.e) && l.equals(other.l);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return c.hashCode() + e.hashCode() + l.hashCode() % Integer.MAX_VALUE;
	}
}
