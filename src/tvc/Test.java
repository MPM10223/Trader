package tvc;

import java.util.Date;

public class Test<TSubject> {
	
	protected TSubject[] testSet;
	protected Date testDate;
	
	public Test(TSubject[] testSet, Date testDate) {
		this.testSet = testSet;
		this.testDate = testDate;
	}
	
	public TSubject[] getTestSet() {
		return testSet;
	}

	public void setTestSet(TSubject[] testSet) {
		this.testSet = testSet;
	}

	public Date getTestDate() {
		return testDate;
	}

	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}

}
