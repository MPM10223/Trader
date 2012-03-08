package tvc;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import statistics.Combinatorics;

public class NullTest<TSubject> extends Test<TSubject> {
	
	protected TSubject[] universe;
	
	public NullTest(TSubject[] universe, int testSize, Date earliest, Date latest, Class<TSubject> ct) {
		this(universe, testSize, earliest, latest, new Random(), ct);
	}
	
	public NullTest(TSubject[] universe, int testSize, Date earliest, Date latest, int seed, Class<TSubject> ct) {
		this(universe, testSize, earliest, latest, new Random(seed), ct);
	}
	
	public NullTest(TSubject[] universe, int testSize, Date earliest, Date latest, Random r, Class<TSubject> ct) {
		super(Combinatorics.randomSubset(universe, testSize, r, ct), randomDateBetween(earliest, latest, r));
	}

	public static Date randomDateBetween(Date earliest, Date latest, Random r) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(earliest);
		long min = cal.getTimeInMillis();
		
		cal.setTime(latest);
		long max = cal.getTimeInMillis();
		
		long rnd = (long)((r.nextDouble() * (max - min)) + min);
		
		return new Date(rnd);
	}

}
