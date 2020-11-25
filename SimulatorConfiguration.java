
public class SimulatorConfiguration {
	
	// imports
	int BLOCKSIZE;
	int L1_SIZE;
	int L1_ASSOC;
	int L2_SIZE;
	int L2_ASSOC;
	int REPLACEMENT_POLICY;
	int INCLUSION_PROPERTY;
	String TRACE_FILE;

	// computed
	int L1_SETS;
	int L2_SETS;
	boolean L1_INUSE;
	boolean L2_INUSE;
		
	public SimulatorConfiguration(int blocksize, int l1size, int l1assoc, int l2size, int l2assoc, int replacementpolicy, int inclusionproperty, String tracefile)
	{
		BLOCKSIZE = blocksize;
		L1_SIZE = l1size;
		L1_ASSOC = l1assoc;
		L2_SIZE = l2size;
		L2_ASSOC = l2assoc;
		REPLACEMENT_POLICY = replacementpolicy;
		INCLUSION_PROPERTY = inclusionproperty;
		TRACE_FILE = tracefile;

		ComputeCacheUsage();
		ComputeSetCounts();
	}
	
	// Reference 3.1 Configurable Parameters for Set Count Formula
	private void ComputeSetCounts(){
		if(L1_INUSE)
		L1_SETS = (L1_SIZE / (L1_ASSOC * BLOCKSIZE));
		if(L2_INUSE)
		L1_SETS = (L2_SIZE / (L2_ASSOC * BLOCKSIZE));
	}

	private void ComputeCacheUsage(){
		L1_INUSE = L1_SIZE > 0;
		L2_INUSE = L2_SIZE > 0;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("===== Simulator configuration =====\n");
		sb.append("BLOCKSIZE:             "+BLOCKSIZE+"\n");
		sb.append("L1_SIZE:               "+L1_SIZE+"\n");
		sb.append("L1_ASSOC:              "+L1_ASSOC+"\n");
		sb.append("L2_SIZE:               "+L2_SIZE+"\n");
		sb.append("L2_ASSOC:              "+L2_ASSOC+"\n");
		sb.append("REPLACEMENT POLICY:    "+toStringReplacementPolicy(REPLACEMENT_POLICY)+"\n");
		sb.append("INCLUSION PROPERTY:    "+toStringInclusionProperty(INCLUSION_PROPERTY)+"\n");
		sb.append("trace_file:            "+TRACE_FILE+"\n");
		return sb.toString();
	}

	public String toStringReplacementPolicy(int replacementpolicy)
	{
		switch(replacementpolicy)
		{
			case 0: return "LRU";
			case 1: return "Pseudo-LRU";
			case 2: return "Optimal";
			default: return "";
		}
	}
	
	public String toStringInclusionProperty(int inclusionproperty)
	{
		switch(inclusionproperty)
		{
			case 0: return "non-inclusive";
			case 1: return "inclusive";
			default: return "";
		}
	}
}
