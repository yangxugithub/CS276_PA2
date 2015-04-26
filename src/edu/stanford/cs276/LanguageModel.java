package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.cs276.util.Pair;


public class LanguageModel implements Serializable {

	private static LanguageModel lm_;
	
	private Map<String, Integer> unigram = new TreeMap<String, Integer>();
	private Map<Pair, Integer> bigram = new TreeMap<Pair, Integer>();
	
	
	private void addToUnigram (String token) {
		unigram.compute(token, (k,v)->{
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
			if (".".equals(file.getName()) || "..".equals(file.getName())) {
				continue; // Ignore the self and parent aliases.
			}
			System.out.printf("Reading data file %s ...\n", file.getName());
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null) {
				
			}
			input.close();
		}
		System.out.println("Done.");
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
}
