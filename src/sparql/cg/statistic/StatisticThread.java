package sparql.cg.statistic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;

import sparql.cg.trie.KeyAnalyzer;
import sparql.cg.trie.Trie;

public class StatisticThread extends Thread {
	private final String file;
	private int thread_num;
	private String trie_name;
	private int maxline;
	private Trie urltrie;
	private Trie Nourltrie;
	private RandomAccessFile printString;
	private final boolean statistic= true;
	private int size;
	private int bsize;
	private BlockingQueue<String> queue;

	public StatisticThread(String file,int threadnum,int maxlines,String triename, int _size, int _bsize, BlockingQueue<String> _queue) throws FileNotFoundException{
		this.file= file;
		this.thread_num = threadnum;
		this.maxline = maxlines;
		this.trie_name = triename;
		this.size = _size;
		this.bsize = _bsize;
		this.queue = _queue;
		//		printString = new RandomAccessFile("printstring.txt", "rw");
	}	
	public void run(){
		try {
			//			Nourltrie = new Trie(new File(trie_name+thread_num+"Nourltrie.idx"), 1000, 5000, Trie.Mode.CREATE,statistic);
			urltrie = new Trie(new File(trie_name+thread_num+"urltrie.idx"), size, bsize, Trie.Mode.CREATE,statistic);
			BufferedReader br = new BufferedReader(new FileReader(file));
			long value = /*(long)2000000000 * 10*/0;
			String line = null;
			int lines = 0;
			int start_row = maxline*thread_num;
			/*try {
				while(lines <start_row && br.readLine()!= null) 
				{ 
					lines++; 
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			long start = System.currentTimeMillis();
			RandomAccessFile no_duplicate = new RandomAccessFile("NoduplicateFile", "rws");
			//			while (lines < maxline *(thread_num + 1) && (line = br.readLine()) != null) {

			try {
				while ((line = queue.take()) != "exit") {
					String splits[] = line.split(" ");
					for (int i = 0; i < 3; i++) {
						KeyAnalyzer key = new KeyAnalyzer(splits[i]+"$",
								value * thread_num + 0/* 19 */);
						if (urltrie.insert(key)) {
							value += 1;
							//						no_duplicate.writeBytes(key.getKey()+"\n");
						}
						//					splits[i]=splits[i].replace("http://www.","");
						//					splits[i]=splits[i].replace(">", "");
						//					splits[i]=splits[i].replace("<", "");
						//					splits[i]=splits[i].replace("\"","");
						//					String subsplits[] = splits[i].split("\\.");
						//					for(int j=0;j<subsplits.length;j++){
						//						KeyAnalyzer key = new KeyAnalyzer(subsplits[j]+"$",
						//								value * thread_num + 0/* 19 */);
						//						if (urltrie.insert(key)) {
						//							value += 1;
						////							no_duplicate.writeBytes(key.getKey()+"\n");
						//						}
						//					}
					}
					lines++;
//					if (lines % 100 == 0)
//						System.out.print(lines);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();
//			System.out.println("Main--->lines: " + lines + "count: " + value + " Time: " + (end - start));
			//			Nourltrie.close();
			//			search("Department");
			urltrie.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}	
	public void search(String key) {
		boolean find = false;
		KeyAnalyzer value = new KeyAnalyzer(key+"$", -1);
		find = urltrie.searchStr(value);
		if (!find)
			System.out.println("not success");
		else System.out.println("search success!");
	}
}

