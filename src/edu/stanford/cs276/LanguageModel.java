package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import edu.stanford.cs276.util.Pair;

/**
 * Without Bigram overlap, the LanguageModel File = 13MB
 * With Bigram overlap, 135MB
 * @author hemal
 */
public class LanguageModel implements Serializable {

	private static int THRESHOLD = 4;

	private static LanguageModel lm_;

	private long T = 0;

	private Integer tokenId = 0;

	// TokenId <-> Count
	private Map<Integer, Integer> unigram 
	= new HashMap<Integer, Integer>();

	// Bigram <-> Count
	private Map<Pair<Integer, Integer>, Integer> bigram 
	= new HashMap<Pair<Integer, Integer>, Integer>();

	// Bigram Chars <-> Set of tokenIds 
	private Map<String, Set<Integer>> bigramChars
	= new HashMap<String, Set<Integer>>();

	// Token <-> TokenId
	private Map<String, Integer> tokenDict 
	= new HashMap<String, Integer>();

	private Map<Integer, String> revTokenDict 
	= new HashMap<Integer, String>();

	private void addToUnigram (String token) {
		T++;
		int id = getTokenId(token);
		unigram.compute(id, (k,v)->{
			Integer vtemp = (v==null?1:(v+1));
			return vtemp;
		});
		addToBigramChars(token);
	}

	private int getTokenId(String token) {
		int id;
		if(!tokenDict.containsKey(token)) {
			id = tokenId;
			tokenDict.put(token, id);
			tokenId++;
		} else {
			id = tokenDict.get(token);
		}
		return id;
	}
	
	
	private void addToBigramChars(String token) {
		for (int i = 0; i < token.length()-1; i++) {
			String str = token.substring(i, i+2);
			bigramChars.compute(str, (k,v)->{
				Set<Integer> vtemp = (v==null?new HashSet<Integer>():v);
				vtemp.add(tokenDict.get(token));
				return vtemp;
			});
		}
	}

	public double getBigramProbability(String token1, 
			String token2, float lambda) {
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(tokenDict.get(token1),tokenDict.get(token2));
		double prob = lambda * getUnigramProbability(token2) +
				(1-lambda) * ((double)bigram.get(p))/unigram.get(tokenDict.get(token1));
		return prob;
	}

	public double getUnigramProbability(String token) {
		double result = ((double)unigram.get(tokenDict.get(token)).intValue())/T;
		return result;
	}

	private void addToBigram(String token1, String token2) {
		int id1 = getTokenId(token1);
		int id2 = getTokenId(token2);
		
		Pair<Integer, Integer> p = new Pair<Integer, Integer>(id1, id2);
		bigram.compute(p, (k,v)->{
			Integer vtemp = (v==null?1:(v+1));
			return vtemp;
		});
	}

	// Do not call constructor directly since this is a Singleton
	private LanguageModel(String corpusFilePath) throws Exception {
		constructDictionaries(corpusFilePath);
	}


	public void constructDictionaries(String corpusFilePath)
			throws Exception {

		System.out.println("Constructing dictionaries...");
		File dir = new File(corpusFilePath);
		for (File file : dir.listFiles()) {
			if (".".equals(file.getName()) || "..".equals(file.getName()) || file.getName().startsWith(".")) {
				continue; // Ignore the self and parent aliases.
			}
			System.out.println("Reading data file "+file.getName()+" ...\n");
//			System.out.println("Unigram Size" + unigram.size());
//			System.out.println("Bigram Size" + bigram.size());
//			System.out.println("Bigram Chars Size" + bigramChars.size());
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] tokens = line.split("\\s+");

				for (int i = 0; i < tokens.length-1; i++) {
					addToUnigram(tokens[i]);
					addToBigram(tokens[i],tokens[i+1]);
				}
				addToUnigram(tokens[tokens.length-1]);

			}
			input.close();
		}
		createRevDict();
		System.out.println("Done.");
	}


	private void createRevDict() {
		tokenDict.forEach((k,v)->{
			revTokenDict.put(v, k);
		});
	}

	// Loads the object (and all associated data) from disk
	public static LanguageModel load() throws Exception {
		try {
			if (lm_==null){
				FileInputStream fiA = new FileInputStream(Config.languageModelFile);
				ObjectInputStream oisA = new ObjectInputStream(fiA);
				lm_ = (LanguageModel) oisA.readObject();
			}
		} catch (Exception e){
			throw new Exception("Unable to load language model.  You may have not run build corrector");
		}
		return lm_;
	}

	// Saves the object (and all associated data) to disk
	public void save() throws Exception{
		FileOutputStream saveFile = new FileOutputStream(Config.languageModelFile);
		ObjectOutputStream save = new ObjectOutputStream(saveFile);
		save.writeObject(this);
		save.close();
	}

	// Creates a new lm object from a corpus
	public static LanguageModel create(String corpusFilePath) throws Exception {
		if(lm_ == null ){
			lm_ = new LanguageModel(corpusFilePath);
		}
		return lm_;
	}

	public Set<String> getCloseWords(String token) {
		Set<String> result = new HashSet<String>();

		if (token.length() == 1) {
			result.add(token);
			return result;
		}
		int minIntersect = token.length()/THRESHOLD;

		List<String> bigramlist = new ArrayList<String>();
		for (int i = 0; i < token.length()-1; i++) {
			String str = token.substring(i, i+2);
			bigramlist.add(str);
		}
		
//	boolean b = unigram.containsKey(tokenDict.get("quad"));
		

		Map<String, Integer> candidates = new TreeMap<String, Integer>();

		for (String bigram : bigramlist) {
			bigramChars.get(bigram).forEach(candidateInt->{
				String candidate = revTokenDict.get(candidateInt); 
						if(candidate.length()>=token.length()-2 && candidate.length() <= token.length()+2) {
							candidates.compute(candidate, (k,v)->{
								Integer vtemp = (v==null?1:v++);
								return vtemp;
							});
						}
			});
		}

		result = candidates
				.entrySet()
				.stream()
				.filter(entry->entry.getValue()>=minIntersect)
				.map(entry->entry.getKey())
				.collect(Collectors.toSet());

		return result;
	}
}
