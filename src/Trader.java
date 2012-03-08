import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import tvc.IControlSelector;
import tvc.IExpectationCalculator;
import tvc.ILiftCalculator;
import tvc.Simulation;
import tvc.SimulationConfiguration;
import tvc.SimulationResults;


public class Trader {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/******** Parse Arguments ********/
		// 1) security universe
		Integer[] universe;
		
		if(args[0].equals("all")) {
			SecurityProvider sp = new SecurityProvider();
			universe = sp.getAllSecurities();
		} else {
			String[] universeList = args[0].split(",");
			universe = new Integer[universeList.length];
			for(int i = 0; i < universeList.length; i++) {
				universe[i] = Integer.parseInt(universeList[i]);
			}
		}
		
		// 2) # impacted stocks
		int testSize = (int)Integer.parseInt(args[1]);
		
		// 3) Control components
		String[] controlList = args[2].split(",");
		IControlSelector<Integer>[] controls = new IControlSelector[controlList.length];
		for(int i = 0; i < controlList.length; i++) {
			switch(Integer.parseInt(controlList[i])) {
				case 1:
					controls[i] = new AllOtherSecuritiesControlSelector();
					break;
				case 2: 
					controls[i] = new SisterSecurityControlSelector();
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		
		// 4) Expectation components
		String[] expectList = args[3].split(",");
		IExpectationCalculator<Integer>[] expects = new IExpectationCalculator[expectList.length];
		for(int i = 0; i < expectList.length; i++) {
			switch(Integer.parseInt(expectList[i])) {
				case 1:
					expects[i] = new PercentChangeExpectationCalculator();
					break;
				case 2: 
					expects[i] = new AbsoluteChangeExpectationCalculator();
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		
		// 5) Lift components
		String[] liftList = args[4].split(",");
		ILiftCalculator<Integer>[] lifts = new ILiftCalculator[liftList.length];
		for(int i = 0; i < liftList.length; i++) {
			switch(Integer.parseInt(liftList[i])) {
				case 1:
					lifts[i] = new PercentLiftCalculator();
					break;
				case 2: 
					lifts[i] = new AbsoluteImpactCalculator();
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		
		// 6) Timeframe start
		SimpleDateFormat f = new SimpleDateFormat("mm/dd/yyyy");
		
		String start = args[5];
		Date startTime;
		try {
			startTime = f.parse(start);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		// 7) Timeframe end
		String end = args[6];
		Date endTime;
		try {
			endTime = f.parse(end);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		SecurityPriceProvider spp = new SecurityPriceProvider(startTime, endTime);
		
		// 8) # iterations
		int iterations = (int)Integer.parseInt(args[7]);
		
		// 9) seed [optional]
		Integer seed = null;
		if(args.length > 8)
			seed = (int)Integer.parseInt(args[8]);
		
		/******** Execute Simulation ********/
		Simulation<Integer> s = new Simulation<Integer>(Integer.class, universe, testSize, controls, expects, lifts, spp, iterations, seed);
		SimulationResults<Integer> r = s.run();
		
		/******** Save / Display Results ********/
		Map<SimulationConfiguration<Integer>, Double> standardDeviations = r.getStandardDeviations();
		System.out.println(String.format("SIMULATION RESULTS (N = %d)", r.getMinSampleSize()));
		System.out.println(String.format("-----------------------------"));
		for(SimulationConfiguration<Integer> c : standardDeviations.keySet()) {
			System.out.println(String.format("%s: %f", c.toString(), standardDeviations.get(c)));
		}
	}

}
