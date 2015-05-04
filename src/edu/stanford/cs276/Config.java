package edu.stanford.cs276;

public class Config {
	public static final String noisyChannelFile = "noisyChannel";
	public static final String languageModelFile = "languageModel";
	public static final String candidateGenFile = "candidateGenerator";
	public static final String analysisFile = "analysis.txt";
	public static final boolean analyzeCandGeneration = false;			// Default: false
	
	public static final float unigramSmoothing = 1.0f; 					// Best 1.0f
	public static final int wordEditDistance = 1; 							// Best 1
	public static final int queryDistance = 2;								// Best 2
	public static final int DeenominatorThreshold = 4;						// Best 4
	public static final float LAMBDA = 0.01f;								// Best 0.05 ~ 0.01
	public static final double MU = 1.0d;									// Best 1.0 ~ 1.05
	
	public static final double ProbDiff = 0.01;								// Best 0.05 ~ 0.01
	public static final double ProbSame = 0.95;								// Best 0.9 ~ 0.95
	
	public static final int unigramThreshold = 10;							// Best 10
	public static boolean handleSpaceInserts = false;						// Best false
}
