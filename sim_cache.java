import java.io.*;
import java.util.ArrayList; 


public class sim_cache {
	public static ArrayList<String> lines;


	public static void main(String[] args) {
		
		// Parker Scott - CDA 5106 - Fall 2020 - Machine Problem 1

		// In this machine problem, you will implement a flexible cache and memory hierarchy simulator and
		// use it to compare the performance, area, and energy of different memory hierarchy configurations,
		// using a subset of the SPEC-2000 benchmark suite.


		// import of args into configuration class
		// sim_cache <BLOCKSIZE> <L1_SIZE> <L1_ASSOC> <L2_SIZE> <L2_ASSOC> <REPLACEMENT_POLICY> <INCLUSION_PROPERTY> <trace_file>
		// ex: sim_cache 32 8192 4 262144 8 0 0 gcc_trace.txt

		SimulatorConfiguration config = new SimulatorConfiguration(
			Integer.parseInt(args[0]),
			Integer.parseInt(args[1]),
			Integer.parseInt(args[2]),
			Integer.parseInt(args[3]),
			Integer.parseInt(args[4]),
			Integer.parseInt(args[5]),
			Integer.parseInt(args[6]),
			args[7]
		);
		

		// collect input from file
		lines = new ArrayList<String>();
		// begin processing input from trace file
		// Boilerplate code from: https://caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
		String line = null;
        try {
			FileReader fileReader = 
					new FileReader(config.TRACE_FILE);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
				new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				if(line.length() > 0){
					lines.add(line);
				}
			}   
			// Always close files.
			bufferedReader.close(); 
		}
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + config.TRACE_FILE + "'");
		}
		
		// object to pass to access lines from this file for optimal replacement policy
		SimulatorLineAccessor simulatorLineAccessor = new SimulatorLineAccessor(lines);

		// create caches with given properties
		Cache l1cache = null;
		Cache l2cache = null;

		if(config.L1_INUSE)
		{
			l1cache = new Cache(
				config.BLOCKSIZE,
				config.L1_SIZE,
				config.L1_ASSOC,
				config.REPLACEMENT_POLICY,
				config.INCLUSION_PROPERTY,
				simulatorLineAccessor
			);
		}   

		if(config.L2_INUSE)
		{
			l2cache = new Cache(
				config.BLOCKSIZE,
				config.L2_SIZE,
				config.L2_ASSOC,
				config.REPLACEMENT_POLICY,
				config.INCLUSION_PROPERTY,
				simulatorLineAccessor
			); 
			l1cache.SetChild(l2cache);
			l2cache.SetParent(l1cache);
		}

		for(int x = 0; x < lines.size(); x++)
		{
			String lineElement = lines.get(x);
			boolean isWriteAddress = lineElement.contains("w");
			boolean isReadAddress = lineElement.contains("r");
			String address = FormatAddress(lineElement.substring(2));
			if(isWriteAddress)
			{
				l1cache.WriteAddress(address);
			}
			if(isReadAddress)
			{
				l1cache.ReadAddress(address);
			}
		}


		System.out.print(config.toString());
		if(config.L1_INUSE){
			System.out.println("===== L1 contents =====");
			for(int i = 0; i < l1cache.NUM_SETS; i++)
			{
				System.out.print("Set     "+i+":\t");
				for(int j = 0; j < l1cache.sets[i].NUM_BLOCKS; j++)
				{
					System.out.print(l1cache.sets[i].blocks[j].tag);
					if(l1cache.sets[i].blocks[j].isDirty)
						System.out.print(" D");
					else
						System.out.print("  ");
					System.out.print("\t");
				}
				System.out.println("");
			}
		}

		if(config.L2_INUSE){
			System.out.println("===== L2 contents =====");
			for(int i = 0; i < l2cache.NUM_SETS; i++)
			{
				System.out.print("Set     "+i+":\t");
				for(int j = 0; j < l2cache.sets[i].NUM_BLOCKS; j++)
				{
					System.out.print(l2cache.sets[i].blocks[j].tag);
					if(l2cache.sets[i].blocks[j].isDirty)
						System.out.print(" D");
					else
						System.out.print("  ");
					System.out.print("\t");
				}
				System.out.println("");
			}
		}

		int totalMemoryTraffic = 0;
		System.out.println("===== Simulation results (raw) =====");
		System.out.println("a. number of L1 reads:        " + (config.L1_INUSE ? l1cache.num_reads : 0));
		System.out.println("b. number of L1 read misses:  " + (config.L1_INUSE ? l1cache.num_read_misses : 0));
		System.out.println("c. number of L1 writes:       " + (config.L1_INUSE ? l1cache.num_writes : 0));
		System.out.println("d. number of L1 write misses: " + (config.L1_INUSE ? l1cache.num_write_misses : 0));
		System.out.println("e. L1 miss rate:              " + (config.L1_INUSE ? String.format("%.6f" ,l1cache.ComputeMissRate()) : 0));
		System.out.println("f. number of L1 writebacks:   " + (config.L1_INUSE ? l1cache.num_writebacks : 0));
		System.out.println("g. number of L2 reads:        " + (config.L2_INUSE ? l2cache.num_reads : 0));
		System.out.println("h. number of L2 read misses:  " + (config.L2_INUSE ? l2cache.num_read_misses : 0));
		System.out.println("i. number of L2 writes:       " + (config.L2_INUSE ? l2cache.num_writes : 0));
		System.out.println("j. number of L2 write misses: " + (config.L2_INUSE ? l2cache.num_write_misses : 0));
		System.out.println("k. L2 miss rate:              " + (config.L2_INUSE ? String.format("%.6f" ,l2cache.ComputeMissRateL2()) : 0));
		System.out.println("l. number of L2 writebacks:   " + (config.L2_INUSE ? l2cache.num_writebacks : 0));
		System.out.print("m. total memory traffic:      " + (config.L2_INUSE ? l2cache.ComputeTotalMemoryTraffic() : l1cache.ComputeTotalMemoryTraffic()));
	}


	// this was used by the TestCache class to run the experiments 1,2,3, and 4
	public static double[] testSimulator(String[] args) {
		
		// Parker Scott - CDA 5106 - Fall 2020 - Machine Problem 1

		// In this machine problem, you will implement a flexible cache and memory hierarchy simulator and
		// use it to compare the performance, area, and energy of different memory hierarchy configurations,
		// using a subset of the SPEC-2000 benchmark suite.


		// import of args into configuration class
		// sim_cache <BLOCKSIZE> <L1_SIZE> <L1_ASSOC> <L2_SIZE> <L2_ASSOC> <REPLACEMENT_POLICY> <INCLUSION_PROPERTY> <trace_file>
		// ex: sim_cache 32 8192 4 262144 8 0 0 gcc_trace.txt

		SimulatorConfiguration config = new SimulatorConfiguration(
			Integer.parseInt(args[0]),
			Integer.parseInt(args[1]),
			Integer.parseInt(args[2]),
			Integer.parseInt(args[3]),
			Integer.parseInt(args[4]),
			Integer.parseInt(args[5]),
			Integer.parseInt(args[6]),
			args[7]
		);
		

		// collect input from file
		lines = new ArrayList<String>();
		// begin processing input from trace file
		// Boilerplate code from: https://caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
		String line = null;
        try {
			FileReader fileReader = 
					new FileReader(config.TRACE_FILE);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
				new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				if(line.length() > 0){
					lines.add(line);
				}
			}   
			// Always close files.
			bufferedReader.close(); 
		}
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + config.TRACE_FILE + "'");
		}
		
		// object to pass to access lines from this file for optimal replacement policy
		SimulatorLineAccessor simulatorLineAccessor = new SimulatorLineAccessor(lines);

		// create caches with given properties
		Cache l1cache = null;
		Cache l2cache = null;

		if(config.L1_INUSE)
		{
			l1cache = new Cache(
				config.BLOCKSIZE,
				config.L1_SIZE,
				config.L1_ASSOC,
				config.REPLACEMENT_POLICY,
				config.INCLUSION_PROPERTY,
				simulatorLineAccessor
			);
		}   

		if(config.L2_INUSE)
		{
			l2cache = new Cache(
				config.BLOCKSIZE,
				config.L2_SIZE,
				config.L2_ASSOC,
				config.REPLACEMENT_POLICY,
				config.INCLUSION_PROPERTY,
				simulatorLineAccessor
			); 
			l1cache.SetChild(l2cache);
			l2cache.SetParent(l1cache);
		}

		for(int x = 0; x < lines.size(); x++)
		{
			String lineElement = lines.get(x);
			boolean isWriteAddress = lineElement.contains("w");
			boolean isReadAddress = lineElement.contains("r");
			String address = FormatAddress(lineElement.substring(2));
			if(isWriteAddress)
			{
				l1cache.WriteAddress(address);
			}
			if(isReadAddress)
			{
				l1cache.ReadAddress(address);
			}
		}

		return new double[]{ l1cache.num_read_misses, l1cache.num_write_misses, l1cache.num_reads, l1cache.num_writes, l1cache.ComputeMissRate() };
	}

	
	public static String FormatAddress(String address)
	{
		String formattedAddress = ("00000000" + address).substring(address.length());
		return formattedAddress;
	}
}
