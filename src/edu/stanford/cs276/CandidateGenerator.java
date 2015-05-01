package edu.stanford.cs276;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CandidateGenerator implements Serializable {


	private static final float LAMBDA = 0.2f;
	public static final double param = 1.0d;
	
	private static CandidateGenerator cg_;

	// Don't use the constructor since this is a Singleton instance
	private CandidateGenerator() {}

	public static CandidateGenerator get() throws Exception {
		if (cg_ == null ){
			cg_ = new CandidateGenerator();
		}
		return cg_;
	}


	public static final Character[] alphabet = {
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n',
		'o','p','q','r','s','t','u','v','w','x','y','z',
		'0','1','2','3','4','5','6','7','8','9',
		' ',','};
	

	// Generate all candidates for the target query
	public String getCorrectedQuery(String query) throws Exception {
		LanguageModel lm = LanguageModel.load();
		String [] tokens = query.split("\\s+");

		Map<String, Set<String>> candidates = new LinkedHashMap<String, Set<String>>();

		for (int i=0; i<tokens.length;i++) {
			candidates.put(tokens[i].trim(), lm.getCloseWords(tokens[i].trim()));
		}
		
		String str = getCartesianProducts(candidates,new String[candidates.size()],0, tokens, query);
		currentQuery = "";
		currentProb = -1.0d/0;
		return str.trim();
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
					if(distance>2) {
						current[depth] = null;
						return null;
					}
				}
				getCartesianProducts(candidates, current, depth+1, tokens, q);
			} else {
				StringBuilder r = new StringBuilder("");
				
				Arrays.stream(current).sequential().forEach(st->r.append(st).append(" "));
				
				if (UniformCostModel.getEditDistance(r.toString().trim(), q.toString().trim()) > 2) {
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
