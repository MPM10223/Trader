import java.lang.reflect.Array;

import tvc.IControlSelector;


public abstract class NonTestControlSelector<TSubject> implements IControlSelector<TSubject> {

	protected TSubject[] getNonTest(TSubject[] universe, TSubject[] testSet, Class<TSubject> c) {
		TSubject[] nonTest = (TSubject[])Array.newInstance(c, universe.length - testSet.length);
		
		int index = 0;
		for(int i = 0; i < universe.length; i++) {
			boolean isTest = false;
			for(int j = 0; j < testSet.length; j++) {
				if(universe[i].equals(testSet[j])) {
					isTest = true;
					break;
				}
			}
			if(!isTest) {
				nonTest[index] = universe[i];
				index++;
			}
		}
		
		return nonTest;
	}

}
