package edu.stanford.cs276;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CandidateGenerator implements Serializable {


	private static CandidateGenerator cg_;

	// Don't use the constructor since this is a Singleton instance
	private CandidateGenerator() {}

	public static CandidateGenerator get() throws Exception{
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
	public List<String[]> getCandidates(String query) throws Exception {
		LanguageModel lm = LanguageModel.load();
		String [] tokens = query.split("\\s+");

		Map<String, Set<String>> candidates = new HashMap<String, Set<String>>();

		for (int i=0; i<tokens.length;i++) {
			candidates.put(tokens[i], lm.getCloseWords(tokens[i]));
		}

		List<String[]> results = new ArrayList<String[]>();
		getCartesianProducts(candidates,results,new String[candidates.size()],0, tokens);

		return results;
	}


	private void getCartesianProducts(Map<String, Set<String>> candidates,
			List<String[]> results, String[] current, int depth, String[] tokens) {

		for (int i = 0; i < candidates.get(tokens[depth]).size(); i++) {
			current[depth] = candidates.get(tokens[depth]).iterator().next();
			if (depth < candidates.keySet().size() - 1){
				getCartesianProducts(candidates, results, current, depth+1, tokens);
			} else {
				results.add(Arrays.copyOf(current,current.length));                
			}
		}

	}


}
