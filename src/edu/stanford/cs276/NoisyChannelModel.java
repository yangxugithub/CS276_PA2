package edu.stanford.cs276;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class NoisyChannelModel implements Serializable {
	
	private static NoisyChannelModel ncm_ = null;
	EditCostModel ecm_ = null;

	EmpiricalCostModel empiricalCostModel = null;
	UniformCostModel uniformCostModel = null;
	
	/* Feel free to add more members here.
	 * You need to implement more methods here as needed.
	 * 
	 * Your code here ...
	 */
	
	// Don't call the constructor directly (singleton instance)
	private NoisyChannelModel(String editsFile) throws Exception {
		empiricalCostModel = new EmpiricalCostModel(editsFile);
		uniformCostModel = new UniformCostModel();
	}

	public static NoisyChannelModel create(String editsFile) throws Exception {
		if (ncm_ == null) {
			ncm_ = new NoisyChannelModel(editsFile);
		}
		return ncm_;
	}
	
	public static NoisyChannelModel load() throws Exception {
		try {
			// Don't load NVM from disk if it's already been loaded.
			if (ncm_ == null) {
				FileInputStream fiA = new FileInputStream(Config.noisyChannelFile);
				ObjectInputStream oisA = new ObjectInputStream(fiA);
				ncm_ = (NoisyChannelModel) oisA.readObject();
				oisA.close();
			}
		} catch (Exception e){
			throw new Exception("Unable to load noise channel model.  You may have not run build corrector");
		}
		return ncm_;
	}
	
	// Saves this object to disk.  All subdata (empiricalcostmodel) will be saved 
	public void save() throws Exception{
		FileOutputStream saveFile = new FileOutputStream(Config.noisyChannelFile);
		ObjectOutputStream save = new ObjectOutputStream(saveFile);
		save.writeObject(this);
		save.close();
	}

	public void setProbabilityType(String type) throws Exception {
		if (type.equals("empirical")) {
			ecm_ = this.empiricalCostModel;
		} else if (type.equals("uniform")) {
			ecm_ = this.uniformCostModel;
		} else {
			throw new Exception("Invalid noisy channel probability type "
					+ "- must be one of <uniform | empirical>");
		}
	}
}
