/**
 * 
 */
package sparql.cg.statistic;

import java.io.BufferedReader;
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
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Michael
 *
 */
public class MultiThreadStatistic extends Thread {
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
//	private int STEP = 0;
	private int level = -1;

	/*static public List<List<String>> loadedRows = null;*/
	// shash List level0 for Stage; Level1 for statistic thread; Map for substring, value;
	static public List<List<Map<String, Integer>>> shash = null;
	static public int thread_num = 10;
	static public int default_build_thread_num = 32;
	static public int default_sample_granule = 10240;
	static public int default_sample_count = 100;

	public MultiThreadStatistic(String infile, long start, long end, int _nbThreads, int _currentId, int _level, Map<String, Integer> _basis){
		this.file = infile;
		this.startbytes = start;
		this.endbytes = end;
		this.nbThreads = _nbThreads;
		this.currentId = _currentId;
		this.basis = _basis;
		this.level = _level;
//		this.STEP = step_interval[level];
		//		this.exclusiveSet = exclude;
	}
	
	/*public void load(long start, long end) {
		
	}
	
	public void sample(int level) {
		
	}*/

	public void sample(long start, long end, int level) {
		//produce messages
		try {
			//			BufferedReader br = new BufferedReader(new FileReader(file));
//			long starttime = System.currentTimeMillis();
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			long skipped = reader.skip((start - 1 > 0) ? (start - 1) : 0);
//			long endtime = System.currentTimeMillis();
//			System.out.println(start + "-" + end + "-" + skipped + "-" + (endtime - starttime) + "-" + level + "-" + currentId);
			String strLine = null;
			chars = 0;

//			int ttt = 0;
//			if (level == 0 && currentId == 0 && start > 100)
//				ttt = 1;
			
			String strLineBefore = reader.readLine();

			//			Pattern pattern = Pattern.compile("(?m)^.");
			//			Matcher matcher = pattern.matcher(strLine);
			boolean foundFirst = false;//matcher.find();

			if (strLineBefore.equals("") || (start == 0))
				foundFirst = true;

			long begin = start;

			// Bound region need to be cut of from the statistic.
			if (foundFirst) {
				System.out.println("Thread: " + currentId + "Found first: " + start);
				totalLines++;
				if (start == 0) {
					chars += strLineBefore.length();
					chars++; // Return character

					String splits[] = strLineBefore.split(" ");
					for (int i = 0; i < 3; i++) {
						Integer value = new Integer(1);

						String strBasis = null;
						String strPrefix = null;
						if (splits[i].length() > /*level * STEP*/step_interval[level])
							strBasis = splits[i].substring(0, /*level * STEP*/step_interval[level]);
						else 
							strBasis = splits[i];
						if (splits[i].length() > /*(level + 1) * STEP*/step_interval[level + 1])
							strPrefix = splits[i].substring(0, /*(level + 1) * STEP*/step_interval[level + 1]);
						else strPrefix = splits[i];

//						if (level == 0 && currentId == 0 && start > 100)
//							ttt = 1;
						
						// basis is null means that current is level 0 such that no overflow string need to be split and further statistic.
						if (null == basis) {
							if (shash.get(level).get(currentId).containsKey(strPrefix/*splits[i]*/)) {
								value = shash.get(level).get(currentId).get(strPrefix/*splits[i]*/) + 1;
							}
							shash.get(level).get(currentId).put(/*splits[i]*/strPrefix, value);
						} else if (basis.containsKey(strBasis)) {
							if (shash.get(level).get(currentId).containsKey(strPrefix/*splits[i]*/)) {
								value = shash.get(level).get(currentId).get(strPrefix/*splits[i]*/) + 1;
							}
							shash.get(level).get(currentId).put(/*splits[i]*/strPrefix, value);
						}
					}
					/*Integer value = new Integer(1);
					if (shash.get(currentId).containsKey(new String("temp")))
						value = shash.get(currentId).get(new String("temp")) + 1;

					shash.get(currentId).put(new String("temp"), value);*/
					//					begin = start + chars;
				} else {
					begin = start + strLineBefore.length();
				}
			} else {
				begin = start + strLineBefore.length() + 1;
			}

			//			while ((strLine = /*reader.readLine()*/reader.readLine()) != null && start + totalLines < end) {
			while ((strLine = /*reader.readLine()*/reader.readLine()) != null && begin + chars/* + strLine.length() + 1*/ < end) {
				localChars += strLine.length();
				localChars++;
				totalLines++;
				chars += strLine.length();
				chars++; // Return character


				String splits[] = strLine.split(" ");
				for (int i = 0; i < 3; i++) {
					Integer value = new Integer(1);

					String strBasis = null;
					String strPrefix = null;

					if (splits[i].length() > /*level * STEP*/step_interval[level])
						strBasis = splits[i].substring(0, /*level * STEP*/step_interval[level]);
					else 
						strBasis = splits[i];
					if (splits[i].length() > /*(level + 1) * STEP*/step_interval[level + 1])
						strPrefix = splits[i].substring(0, /*(level + 1) * STEP*/step_interval[level + 1]);
					else strPrefix = splits[i];

//					if (level == 0 && currentId == 0 && start > 100)
//						ttt = 1;
					
					if (null == basis) {
						if (shash.get(level).get(currentId).containsKey(strPrefix/*splits[i]*/)) {
							value = shash.get(level).get(currentId).get(strPrefix/*splits[i]*/) + 1;
						}
						shash.get(level).get(currentId).put(/*splits[i]*/strPrefix, value);
					} else if (basis.containsKey(strBasis)) {
						if (shash.get(level).get(currentId).containsKey(strPrefix/*splits[i]*/)) {
							value = shash.get(level).get(currentId).get(strPrefix/*splits[i]*/) + 1;
						}
						shash.get(level).get(currentId).put(/*splits[i]*/strPrefix, value);
					}
				}
				/*for (int i = 0; i < 3; i++) {
					Integer value = new Integer(1);
					if (shash.get(level).get(currentId).containsKey(splits[i])) {
						value = shash.get(level).get(currentId).get(splits[i]) + 1;
					}
					shash.get(level).get(currentId).put(splits[i], value);
				}*/

				/*Integer value = new Integer(1);
				if (shash.get(currentId).containsKey(new String("temp")))
					value = shash.get(currentId).get(new String("temp")) + 1;

				shash.get(currentId).put(new String("temp"), value);*/
				// process here

				//				strLine = reader.readLine();
				//				try {
				//					
				//				} catch (InterruptedException e) {
				//					System.out.println(totalLines);
				//					e.printStackTrace();
				//				}
			}
			reader.close();
			//			System.out.println(chars);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		System.out.println(totalLines + "-" + chars +"\t" + localChars);
	}

	@Override
	public void run() {
		for (int i = 0; i < default_sample_count; i++) {
//			Integer sum = new Integer(0);
//			for (Map.Entry<String, Integer> strEntry : shash.get(level).get(currentId).entrySet()) {
//				sum += strEntry.getValue();
//			}
//			System.out.println("\t<" + (startbytes + i * (endbytes - startbytes) / default_sample_count) + "-" + (startbytes + i * (endbytes - startbytes) / default_sample_count + default_sample_granule) + " & " + level + ":" + i + ":" + currentId + "->" + sum);
			sample((long)(startbytes + i * (endbytes - startbytes) / default_sample_count),
					(long)(startbytes + i * (endbytes - startbytes) / default_sample_count + default_sample_granule),
					level);
//			sum = new Integer(0);
//			for (Map.Entry<String, Integer> strEntry : shash.get(level).get(currentId).entrySet()) {
//				sum += strEntry.getValue();
//			}
//			System.out.println("\t>" + (startbytes + i * (endbytes - startbytes) / default_sample_count) + "-" + (startbytes + i * (endbytes - startbytes) / default_sample_count + default_sample_granule) + " & " + level + ":" + i + ":" + currentId + "->" + sum);
//			System.out.println(level);
		}
		//			while (level == currentLevel)
		//				try {
		//					Thread.sleep(20);
		//				} catch (InterruptedException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
	}

	//	@Override
	//	public void run() {
	//		//produce messages
	//		try {
	//			//			BufferedReader br = new BufferedReader(new FileReader(file));
	//			LineNumberReader reader = new LineNumberReader(new FileReader(file));
	//			reader.skip((start - 1 > 0) ? (start - 1) : 0);
	//			String strLine = null;
	//
	//			String strLineBefore = reader.readLine();
	//
	//			//			Pattern pattern = Pattern.compile("(?m)^.");
	//			//			Matcher matcher = pattern.matcher(strLine);
	//			boolean foundFirst = false;//matcher.find();
	//
	//			if (strLineBefore.equals("") || (start == 0))
	//				foundFirst = true;
	//
	//			long begin = start;
	//
	//			if (foundFirst) {
	//				System.out.println("Found first");
	//				totalLines++;
	//				if (start == 0) {
	//					chars += strLineBefore.length();
	//					chars++; // Return character
	//					Integer value = new Integer(1);
	//					if (shash.get(currentId).containsKey(new String("temp")))
	//						value = shash.get(currentId).get(new String("temp")) + 1;
	//
	//					shash.get(currentId).put(new String("temp"), value);
	//					//					begin = start + chars;
	//				} else {
	//					begin = start + strLineBefore.length();
	//				}
	//			} else {
	//				begin = start + strLineBefore.length() + 1;
	//			}
	//
	//			//			while ((strLine = /*reader.readLine()*/reader.readLine()) != null && start + totalLines < end) {
	//			while ((strLine = /*reader.readLine()*/reader.readLine()) != null && begin + chars/* + strLine.length() + 1*/ < end) {
	//				localChars += strLine.length();
	//				localChars++;
	//				totalLines++;
	//				chars += strLine.length();
	//				chars++; // Return character
	//				
	//				Integer value = new Integer(1);
	//				if (shash.get(currentId).containsKey(new String("temp")))
	//					value = shash.get(currentId).get(new String("temp")) + 1;
	//
	//				shash.get(currentId).put(new String("temp"), value);
	//				// process here
	//
	//				//				strLine = reader.readLine();
	//				//				try {
	//				//					
	//				//				} catch (InterruptedException e) {
	//				//					System.out.println(totalLines);
	//				//					e.printStackTrace();
	//				//				}
	//			}
	//			System.out.println(chars);
	//		} catch (FileNotFoundException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		System.out.println(totalLines + "-" + chars +"\t" + localChars);
	//	}

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
		System.out.println(len);
		/*MultiThreadStatistic mts = new MultiThreadStatistic(sread, 0, len / 2, 1);
		mts.start();
		MultiThreadStatistic mts1 = new MultiThreadStatistic(sread, len / 2, len, 1);
		mts1.start();*/
		long starttime = System.currentTimeMillis();
		List<MultiThreadStatistic> mtss = new ArrayList<MultiThreadStatistic>();
		shash = new ArrayList<List<Map<String, Integer>>>();

		int level = 0;
		result = new ArrayList<Map<String, Integer>>();
		while (!satisfied) {
			// for level
			shash.add(new ArrayList<Map<String, Integer>>());

			for (int i = 0; i < thread_num; i++) {
				long end = (((long)i + 1) * len / (long)thread_num > len) ? len : (((long)i + 1) * len / (long)thread_num);
				MultiThreadStatistic mts = new MultiThreadStatistic(sread, i * len / thread_num, end, default_build_thread_num, i, level, basis);
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
//					System.out.println("************" + totalCount);
				}
				if (level == 0)
					threshold = (long)((/*0.7 * */totalCount) / default_build_thread_num);
				System.out.println(totalCount + "_________________" + threshold);
				basis = null;
				satisfied = true;
				if (!result.get(level).isEmpty()) {
					Map<String, Integer> cutoff = new HashMap<String, Integer>();
					for (Map.Entry<String, Integer> strEntry : result.get(level).entrySet()) {
						if (strEntry.getValue() > threshold) {
							mtss.clear();
							//							result.get(level).remove(strEntry.getKey());
							cutoff.put(strEntry.getKey(), strEntry.getValue());
							satisfied = false;
							if (basis == null)
								basis = new HashMap<String, Integer>();
							basis.put(strEntry.getKey(), strEntry.getValue());
							System.out.println(strEntry.getKey() + "->" + strEntry.getValue());
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
			//
			//			String output = "out.hash";
			//			BufferedWriter wr = null;
			//			try {
			//				wr = new BufferedWriter(new FileWriter(output));
			//			} catch (IOException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//
			//			try {
			//				for (int i = 0; i < thread_num; i++) {
			//					try {
			//						mtss.get(i).join();
			//						for (String str : shash.get(level).get(i).keySet()) {
			//							//					System.out.println(str + " : " + shash.get(i).get(str));
			//							wr.write(str + " : " + shash.get(level).get(i).get(str) + "\n");
			//						}
			//
			//					} catch (InterruptedException e) {
			//						// TODO Auto-generated catch block
			//						e.printStackTrace();
			//					}
			//				}
			//				wr.close();
			//			} catch (IOException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			//			long endtime = System.currentTimeMillis();
			//			System.out.println("Elipse: " + (endtime - starttime));

			level++;
		}
		String output = "out.hash";
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
					//					System.out.println(str + " : " + shash.get(i).get(str));
//					wr.write(str + " : " + result.get(lvl).get(str) + "\n");
					rst.put(str, result.get(lvl).get(str));
				}
			}
			
			Object[] sortedEntry = rst.keySet().toArray();
			Arrays.sort(sortedEntry);
			
			for (Object obj : sortedEntry) {
				if (current_count_num >= threshold) {
					System.out.println("***************" + current_count_num + " : " + threshold + " : " + rst.get(obj).intValue());
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
		System.out.println("Elipse: " + (endtime - starttime));
	}

}
