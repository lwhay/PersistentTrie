package sparql.cg.statistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sparql.cg.hashtrie.MultiDemo;


public class MultiThreadStatisticMem extends Thread {
	private long startbytes;
	private long endbytes;
	private String file;
	private int totalLines = 0;
	private long chars = 0;
	private long localChars = 0;
	private int currentId = 0;
	private int nbThreads;
	private Map<String, Integer> basis;
	//	private int[] step_interval = {1,3,5,9,17,25,33,41,49,57,65};
	private int[] step_interval = {4,8,12,16,20,24,28,32,36,40,44,48,52,56,60,64};
	private int level = -1;

	// shash List level0 for Stage; Level1 for statistic thread; Map for substring, value;
	static public List<List<Map<String, Integer>>> shash = null;
	static public List<List<String>> loadedRows= null;
	static public int thread_num = 10;
	static public int default_build_thread_num = 32;
	static public int default_sample_granule = 10240;
	static public int default_sample_count = 100;

	public MultiThreadStatisticMem(String infile, long start, long end, int _nbThreads, int _currentId, int _level, Map<String, Integer> _basis){
		this.file = infile;
		this.startbytes = start;
		this.endbytes = end;
		this.nbThreads = _nbThreads;
		this.currentId = _currentId;
		this.basis = _basis;
		this.level = _level;
	}

	public void load(long start, long end) {
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			reader.skip((start - 1 > 0) ? (start - 1) : 0);
			String strLine = null;
			chars = 0;
			String strLineBefore = reader.readLine();

			boolean foundFirst = false;//matcher.find();

			if (strLineBefore.equals("") || (start == 0))
				foundFirst = true;

			long begin = start;

			// Bound region need to be cut of from the statistic.
			if (foundFirst) {
//				System.out.println("Thread: " + currentId + "Found first: " + start);
				totalLines++;
				if (start == 0) {
					chars += strLineBefore.length();
					chars++; // Return character
					loadedRows.get(currentId).add(strLineBefore);
				} else {
					begin = start + strLineBefore.length();
				}
			} else {
				begin = start + strLineBefore.length() + 1;
			}

			while ((strLine = reader.readLine()) != null && begin + chars < end) {
				localChars += strLine.length();
				localChars++;
				totalLines++;
				chars += strLine.length();
				chars++; // Return character

				loadedRows.get(currentId).add(strLine);
			}
			reader.close();
		}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sample(int level) {
		for (String strLine : loadedRows.get(currentId)) {
			String splits[] = strLine.split(" ");
			for (int i = 0; i < 3; i++) {
				Integer value = new Integer(1);

				String strBasis = null;
				String strPrefix = null;
				if (splits[i].length() > step_interval[level])
					strBasis = splits[i].substring(0, step_interval[level]);
				else 
					strBasis = splits[i];
				if (splits[i].length() > step_interval[level + 1])
					strPrefix = splits[i].substring(0, step_interval[level + 1]);
				else strPrefix = splits[i];

				// basis is null means that current is level 0 such that no overflow string need to be split and further statistic.
				if (null == basis) {
					if (shash.get(level).get(currentId).containsKey(strPrefix)) {
						value = shash.get(level).get(currentId).get(strPrefix) + 1;
					}
					shash.get(level).get(currentId).put(strPrefix, value);
				} else if (basis.containsKey(strBasis)) {
					if (shash.get(level).get(currentId).containsKey(strPrefix)) {
						value = shash.get(level).get(currentId).get(strPrefix) + 1;
					}
					shash.get(level).get(currentId).put(strPrefix, value);
				}
			}
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < default_sample_count; i++) {
			if (0 == level) {
				load((long)(startbytes + i * (endbytes - startbytes) / default_sample_count), (long)(startbytes + i * (endbytes - startbytes) / default_sample_count + default_sample_granule));
				/*for (int j = 0; j < thread_num; j++) {
					System.out.println("&&&&&&&&" + loadedRows.get(j).size());
				}*/
			}
		}
		sample(level);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sread = "University.dist";
		File file = new File(sread);
		long len = file.length();
		long totalCount = 0;
		long threshold = /*(long)(0.7 * len / default_build_thread_num)*/0;
		boolean satisfied = false;
		List<Map<String, Integer>> result = null;
		Map<String, Integer> basis = null;
//		System.out.println(len);
		
		thread_num = Integer.parseInt(args[0]);
		default_sample_granule = Integer.parseInt(args[1]);
		default_sample_count = Integer.parseInt(args[2]);	
		String outstatfile = "outhash-" + args[0] + "-" + args[1] + "-" + args[2] + ".log";
		
//		thread_num = 10;
//		default_sample_granule = 10240;
//		default_sample_count = 10;
//		
//		String outstatfile = "outhash-" + 10 + "-" + 10240 + "-" + 10 + ".log";
//		
		long starttime = System.currentTimeMillis();
		List<MultiThreadStatisticMem> mtss = new ArrayList<MultiThreadStatisticMem>();
		shash = new ArrayList<List<Map<String, Integer>>>();
		loadedRows = new ArrayList<List<String>>();
		for (int i = 0; i < thread_num; i++) {
			loadedRows.add(new ArrayList<String>());
		}

		int level = 0;
		result = new ArrayList<Map<String, Integer>>();
		while (!satisfied) {
			// for level
			shash.add(new ArrayList<Map<String, Integer>>());

			for (int i = 0; i < thread_num; i++) {
				long end = (((long)i + 1) * len / (long)thread_num > len) ? len : (((long)i + 1) * len / (long)thread_num);
				MultiThreadStatisticMem mts = new MultiThreadStatisticMem(sread, i * len / thread_num, end, default_build_thread_num, i, level, basis);
				// for thread
				shash.get(level).add(new HashMap<String, Integer>());
				mtss.add(mts);
				if (!mts.isAlive())
					mts.start();
			}

			result.add(new HashMap<String, Integer>());
			try {
				for (int i = 0; i < thread_num; i++) {
					mtss.get(i).join();
					Integer count = 0;

					if (shash.get(level).get(i).isEmpty())
						continue;
					for (Map.Entry<String, Integer> strEntry : shash.get(level).get(i).entrySet()) {
						if (null != (count = result.get(level).get(strEntry.getKey()))) {
							result.get(level).put(strEntry.getKey(), count + strEntry.getValue());
							if (level == 0)
								totalCount += strEntry.getValue();
						} else {
							result.get(level).put(strEntry.getKey(), strEntry.getValue());
							if (level == 0)
								totalCount += strEntry.getValue();
						}
					}
				}
				if (level == 0)
					threshold = (long)((totalCount) / default_build_thread_num);
//				System.out.println(totalCount + "_________________" + threshold);
				basis = null;
				satisfied = true;
				if (!result.get(level).isEmpty()) {
					Map<String, Integer> cutoff = new HashMap<String, Integer>();
					for (Map.Entry<String, Integer> strEntry : result.get(level).entrySet()) {
						if (strEntry.getValue() > threshold) {
							mtss.clear();
							cutoff.put(strEntry.getKey(), strEntry.getValue());
							satisfied = false;
							if (basis == null)
								basis = new HashMap<String, Integer>();
							basis.put(strEntry.getKey(), strEntry.getValue());
//							System.out.println(strEntry.getKey() + "->" + strEntry.getValue());
						}
					}
					for (Map.Entry<String, Integer> strEntry : cutoff.entrySet()) {
						result.get(level).remove(strEntry.getKey());
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			level++;
		}

		String output = outstatfile/*"out.hash"*/;
		BufferedWriter wr = null;
		try {
			wr = new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int current_thread_num = 0;
		int current_count_num = 0;
		try {
			Map<String, Integer> rst = new HashMap<String, Integer>();
			for (int lvl = 0; lvl < level; lvl++) {
				for (String str : result.get(lvl).keySet()) {
					rst.put(str, result.get(lvl).get(str));
				}
			}

			Object[] sortedEntry = rst.keySet().toArray();
			Arrays.sort(sortedEntry);

			for (Object obj : sortedEntry) {
				if (current_count_num >= threshold) {
//					System.out.println("***************" + current_count_num + " : " + threshold + " : " + rst.get(obj).intValue());
					current_count_num = rst.get(obj);
					current_thread_num++;
				} else {
					current_count_num += rst.get(obj);
				}
				wr.write(obj + " : " + rst.get(obj) + " : " + current_thread_num +"\n");
			}

			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endtime = System.currentTimeMillis();
		
		System.out.println(args[0] + "\t" + args[1] + "\t" + args[2] + "\t" + (endtime - starttime));
		
		MultiDemo md = new MultiDemo();
		md.start(sread,output);
	}
}
