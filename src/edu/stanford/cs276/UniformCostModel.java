package edu.stanford.cs276;

public class UniformCostModel implements EditCostModel {
	
	@Override
	public double editProbability(String original, String R, int distance) {
		return 0.5;
		/*
		 * Your code here
		 */
	}
}
