package edu.stanford.cs276;

import java.io.Serializable;

public interface EditCostModel extends Serializable {

	public double editProbability(String original, String R, int distance);
}
