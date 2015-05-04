package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunCorrector {

	public static LanguageModel languageModel;
	public static NoisyChannelModel nsm;

	public static void main1(String[] args) throws Exception {
		boolean b = true;

		while(b) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while((input=br.readLine())!=null){
				if(input.endsWith("c")) 
					b = false;
				RunCorrector.main1(args);
			}
		}
	}



	public static void main(String[] args) throws Exception {

		long startTime = System.currentTimeMillis();

		// Parse input arguments
		String uniformOrEmpirical = null;
		String queryFilePath = null;
		String goldFilePath = null;
		String extra = null;
		BufferedReader goldFileReader = null;
		if (args.length == 2) {
			// Run without extra and comparing to gold
			uniformOrEmpirical = args[0];
			queryFilePath = args[1];
		}
		else if (args.length == 3) {
			uniformOrEmpirical = args[0];
			queryFilePath = args[1];
			if (args[2].equals("extra")) {
				extra = args[2];
			} else {
				goldFilePath = args[2];
			}
		} 
		else if (args.length == 4) {
			uniformOrEmpirical = args[0];
			queryFilePath = args[1];
			extra = args[2];
			goldFilePath = args[3];
		}
		else {System.err.println(
				"Invalid arguments.  Argument count must be 2, 3 or 4" +
						"./runcorrector <uniform | empirical> <query file> \n" + 
						"./runcorrector <uniform | empirical> <query file> <gold file> \n" +
						"./runcorrector <uniform | empirical> <query file> <extra> \n" +
						"./runcorrector <uniform | empirical> <query file> <extra> <gold file> \n" +
						"SAMPLE: ./runcorrector empirical data/queries.txt \n" +
						"SAMPLE: ./runcorrector empirical data/queries.txt data/gold.txt \n" +
						"SAMPLE: ./runcorrector empirical data/queries.txt extra \n" +
				"SAMPLE: ./runcorrector empirical data/queries.txt extra data/gold.txt \n");
		return;
		}

		if (goldFilePath != null ){
			goldFileReader = new BufferedReader(new FileReader(new File(goldFilePath)));
		}

		// Load models from disk
		languageModel = LanguageModel.load(); 
		languageModel.createRevDict();
		nsm = NoisyChannelModel.load();
		BufferedReader queriesFileReader = new BufferedReader(new FileReader(new File(queryFilePath)));
		nsm.setProbabilityType(uniformOrEmpirical);

		int totalCount = 0;
		int yourCorrectCount = 0;
		String query = null;

		/*
		 * Each line in the file represents one query.  We loop over each query and find
		 * the most likely correction
		 */
		while ((query = queriesFileReader.readLine()) != null) {

			String correctedQuery = getCorrectedQuery(query);

			if ("extra".equals(extra)) {
				/*
				 * If you are going to implement something regarding to running the corrector, 
				 * you can add code here. Feel free to move this code block to wherever 
				 * you think is appropriate. But make sure if you add "extra" parameter, 
				 * it will run code for your extra credit and it will run you basic 
				 * implementations without the "extra" parameter.
				 */	
			}


			// If a gold file was provided, compare our correction to the gold correction
			// and output the running accuracy
			if (goldFileReader != null) {
				String goldQuery = goldFileReader.readLine();
				if (goldQuery.equals(correctedQuery)) {
					yourCorrectCount++;
				}
				totalCount++;
			}
			System.out.println(correctedQuery);
		}
		
		
		
		queriesFileReader.close();
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		if(Config.analyzeCandGeneration) {
			analyze(totalTime, 100.0d * yourCorrectCount / totalCount);
		}
//		System.out.println("RUNNING TIME: "+totalTime/1000+" seconds ");
//		System.out.println(100.0d * yourCorrectCount / totalCount);
	}


	private static void analyze(long totalTime, double d) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(Config.analysisFile));
			fw.write("======================================================\n");
			fw.write("Total Candidate tokens processed: " + CandidateGenerator.totalTokenCandidates + "\n");
			fw.write("Total Query Candidates processed: " + CandidateGenerator.totalQueryCandidates + "\n");
			fw.write("RUNNING TIME: "+totalTime/1000+" seconds " + "\n");
			fw.write("Accuracy: " + d + "\n");
		} catch (IOException e) {
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
			}
		}
	}

	private static String getCorrectedQuery(String query) {
		CandidateGenerator cg;
		try {
			String result = null;
			cg = CandidateGenerator.get();
			result = cg.getCorrectedQuery(query); 
			return (result==null||result.length()==0)?query:result.trim();

		} catch (Exception e) {
			//			e.printStackTrace();
			return null;
		}

	}
}
