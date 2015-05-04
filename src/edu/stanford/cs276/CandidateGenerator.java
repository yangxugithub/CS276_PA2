package edu.stanford.cs276;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CandidateGenerator implements Serializable {


	private static final float LAMBDA = Config.LAMBDA;
	public static final double param = Config.MU;
	
	private static boolean analyze = Config.analyzeCandGeneration;
	
	
	public static Long totalTokenCandidates = 0l;
	public static Long totalQueryCandidates = 0l;

	private static CandidateGenerator cg_;

	// Don't use the constructor since this is a Singleton instance
	private CandidateGenerator() {}

	public static CandidateGenerator get() throws Exception {
		if (cg_ == null ){
			cg_ = new CandidateGenerator();
		}
		return cg_;
	}


//	public static final Character[] alphabet = {
//		'a','b','c','d','e','f','g','h','i','j','k','l','m','n',
//		'o','p','q','r','s','t','u','v','w','x','y','z',
//		'0','1','2','3','4','5','6','7','8','9',
//		' ',','};


	// Generate all candidates for the target query
	public String getCorrectedQuery(String query) throws Exception {
		
		LanguageModel lm = LanguageModel.load();
		String [] tokens = query.split("\\s+");

		Map<String, Set<String>> candidates = new LinkedHashMap<String, Set<String>>();
		for (int i=0; i<tokens.length;i++) {
			Set<String> set = lm.getCloseWords(tokens[i].trim());
			candidates.put(tokens[i].trim(), set);
			if(analyze) {
				totalTokenCandidates = totalTokenCandidates + set.size();
			}
		}
		
		String str1 = getCartesianProducts(candidates,new String[candidates.size()],0, tokens, query);
		double prob1 = currentProb;
		currentQuery = "";
		currentProb = -1.0d/0;
		
		Map<String, Object> spaceDelete = handleSpaceDeletes(query, str1, prob1, candidates, lm, tokens);

		if(Config.handleSpaceInserts) {
			Map<String, Object> spaceInsert = handleSpaceInserts(query, spaceDelete, candidates, lm, tokens);
			return (String) spaceInsert.get("query");
		} else {
			return (String) spaceDelete.get("query");
		}

		//		return str1;
	}


	private Map<String, Object> handleSpaceInserts(String query, Map<String, Object> spaceDelete,
			final Map<String, Set<String>> candidates, LanguageModel lm,
			String[] origTokens) {
		Map<String, Object> result = new HashMap<String, Object>(spaceDelete);

		String [] tokens = null;
		for (int i=0;i<origTokens.length;i++) {
			for (int j=0;j<origTokens[i].length();j++){
				String one = origTokens[i].substring(0, j+1);
				String two = origTokens[i].substring(j+1,origTokens[i].length());
				if(two.length()!=0&&one.length()!=0) {
					tokens = processPair(one,two,origTokens,i,j,lm);
					
					// Array is populated
					if (tokens != null) {
						StringBuilder s = new StringBuilder("");
						Arrays.stream(tokens).forEach(str ->{
							s.append(str+" ");
						});
						
						Map<String, Set<String>> candidateCopy = populateCandidateMap(candidates,tokens,lm);
						
						String str3 = getCartesianProducts(candidateCopy, new String [candidateCopy.size()], 0, tokens, s.toString().trim());
						if (currentProb > (Double) result.get("prob")) {
							result.put("query",str3.trim());
							result.put("prob",currentProb);
						}
						currentQuery = "";
						currentProb = -1.0d/0;
					}
				}
			}
		}
		return result;
	}

	private String[] processPair(String one, String two, String[] origTokens,
			int i, int j, LanguageModel lm) {
		
		if(!lm.unigramExists(one) || lm.getUnigramCount(one)<Config.unigramThreshold
				|| !lm.unigramExists(two) || lm.getUnigramCount(two)<Config.unigramThreshold
				|| !lm.bigramExists(one,two)) {
			return null;
		}
		String[] result = new String [origTokens.length+1];
		for(int x=0; x<i;x++) {
			result[x]=origTokens[x];
		}
		result[i] = one;
		result[i+1] = two;
		for(int y=i+2;y<origTokens.length+1;y++) {
			result[y] = origTokens[y-1];
		}
		return result;
	}

//	public static void main(String[] args) {
//		new CandidateGenerator().handleSpaceInserts(null, null, null, null, new String []{"hemal","Thakkar"});
//	}

	private Map<String, Object> handleSpaceDeletes (String query, String str1, double prob1, final Map<String, Set<String>> candidates, 
			LanguageModel lm, String[] origTokens) {
		Map<String, Object> result = new HashMap<String, Object>();
		String finalQuery = str1;
		double finalProb = prob1;


		String [] tokens = new String [origTokens.length-1];

		for (int i=0; i<origTokens.length-1;i++) {
			boolean done = false;
			for(int j=0; j<origTokens.length-1;j++){
				if(i==j) {
					tokens[j] = origTokens[j]+origTokens[j+1];
					if(!lm.unigramExists(tokens[j])) {
						break;
					}
					done = true;
				} else {
					tokens[j] = origTokens[done?j+1:j];
				}
			}
			if(done) {
				// Array is populated
				StringBuilder s = new StringBuilder("");
				Arrays.stream(tokens).forEach(str ->{
					s.append(str+" ");
				});
				Map<String, Set<String>> candidateCopy = populateCandidateMap(candidates,tokens,lm);

				String str2 = getCartesianProducts(candidateCopy, new String [candidateCopy.size()], 0, tokens, s.toString().trim());
				if (currentProb > finalProb) {
					finalQuery = str2.trim();
					finalProb = currentProb;
				}
				currentQuery = "";
				currentProb = -1.0d/0;
			}
		}
		result.put("query", finalQuery);
		result.put("prob", finalProb);
		return result;
	}


	private Map<String, Set<String>> populateCandidateMap(
			final Map<String, Set<String>> candidates, String[] tokens,
			LanguageModel lm) {
		Map<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
		for (String token : tokens) {
			if(candidates.containsKey(token)) {
				result.put(token, candidates.get(token));
			} else {
				result.put(token, lm.getCloseWords(token));
			}
		}
		return result;
	}




	String currentQuery = "";
	double currentProb = -1.0d/0;
	private String getCartesianProducts(Map<String, Set<String>> candidates,
			String[] current, int depth, String[] tokens, String q) {
		Iterator<String> iter = candidates.get(tokens[depth]).iterator();


		while(iter.hasNext()) {
			current[depth] = iter.next();
			if (depth < candidates.keySet().size() - 1) {
				int distance = 0;
				for (int i=0;i<=depth;i++){
					distance = distance + UniformCostModel.getEditDistance(current[i], tokens[i]);
					if(distance>Config.queryDistance) {
						current[depth] = null;
						return null;
					}
				}
				getCartesianProducts(candidates, current, depth+1, tokens, q);
			} else {
				if(analyze) {
					totalQueryCandidates = totalQueryCandidates + 1;
				}
				
				StringBuilder r = new StringBuilder("");

				Arrays.stream(current).sequential().forEach(st->r.append(st).append(" "));

				if (UniformCostModel.getEditDistance(r.toString().trim(), q.toString().trim()) > Config.queryDistance) {
					return null;
				}

				double prob = Math.log10(RunCorrector.nsm.ecm_.editProbability(q.toString(), r.toString(), 1));
				prob=prob+ param * Math.log10(RunCorrector.languageModel.getUnigramProbability(current[0]));
				for(int i=0; i<current.length-1;i++) {
					prob = prob + param * Math.log10(RunCorrector.languageModel.getBigramProbability(current[i], current[i+1], LAMBDA));
				}

				if(prob > currentProb) {
					currentProb = prob;
					currentQuery = r.toString();
				}
			}
		}

		String resultQuery = currentQuery;
		return resultQuery;
	}
}
