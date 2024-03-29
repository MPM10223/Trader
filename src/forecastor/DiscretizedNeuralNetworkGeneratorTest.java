package forecastor;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import junit.framework.Assert;

import neuralnetwork.DiscretizedNeuralNetwork;

import org.junit.Test;

import statistics.Combinatorics;

public class DiscretizedNeuralNetworkGeneratorTest {

	@Test
	public void test_minMax1() {
		DiscretizedNeuralNetworkGenerator g = new DiscretizedNeuralNetworkGenerator(1);
		Assert.assertEquals(8 * (1 * 1 + 1 + (1 * 4)), g.getDNASize());
	}

	@Test
	public void test_minMax12() {
		DiscretizedNeuralNetworkGenerator g = new DiscretizedNeuralNetworkGenerator(12);
		Assert.assertEquals(8 * (12 * 12 + 12 + (4 * 12)), g.getDNASize());
	}
	
	@Test
	public void test_generate1() {
		DiscretizedNeuralNetworkGenerator g = new DiscretizedNeuralNetworkGenerator(1);
		
		byte[] dna = new byte[g.getDNASize()];
		DiscretizedNeuralNetwork dnn = g.generate(dna);
		
		Assert.assertEquals(1, dnn.getThreshholds().length);
		org.junit.Assert.assertArrayEquals(new double[] {0.0, 0.0, 0.0, 0.0}, dnn.getThreshholds()[0], 0.0);
	}
	
	@Test
	public void test_generate12() {
		DiscretizedNeuralNetworkGenerator g = new DiscretizedNeuralNetworkGenerator(12);
		
		Random r = new Random(94025);
		byte[] dna = new byte[g.getDNASize()];
		r.nextBytes(dna);
		g.generate(dna);
	}
}
