/**
 * 
 */
/**
 * @author tk2510

 *
 */
package sparql.cg.statistic;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sparql.cg.trie.InterNode;
import sparql.cg.trie.KeyAnalyzer;
import sparql.cg.trie.Trie;


public class Statistic{
	private static final int String = 0;
	private static final int BlockingQueue = 0;
	private  String file;
	private  int nbThreads;
//	private  int maxlines;
	private  long fileSize = 0;
	private  int totalLines;
	private  Trie totalpreftrie;
	private  Trie printrie;
	//	private  Trie Nourlpreftrie;
	private  int root=0;
	private  String triename;
	private	 int size;
	private	 int bsize;
	private static int inthread_num = 8; 

	public Statistic(String file, /*int maxline,*/int _nbThreads, int _size, int _bsize) throws FileNotFoundException {
		this.file = file;
		File rfile = new File(file);
		fileSize = rfile.length();
//		this.maxlines = maxline;
		nbThreads = _nbThreads;
		triename = file + "_Thread";
		totalpreftrie = null;
		size = _size;
		bsize = _bsize;
	}

	public void startcounting() throws FileNotFoundException{
		try {
			compute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
		merge();
		long end = System.currentTimeMillis();
		System.out.println("merge success: " + (end - start));
		//		System.out.println("merge success");
		//		RandomAccessFile printurlfile = new RandomAccessFile("printurlfile","rw");
		//		printrie = new Trie(new File(triename+ "urltrie.idx"), 1000, 5000,
		//				Trie.Mode.SEARCH,true);
		//		List<Pair<StringBuffer, Long>> urlpref_val = new ArrayList<Pair<StringBuffer, Long>>();
		//		try {
		//			try {
		//				printpref(urlpref_val,printurlfile);
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		} catch (CloneNotSupportedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		printpref(Nourlpref_val,Nourlpreftrie,printNourlfile);

	}

	private void printpref(List<Pair<StringBuffer, Long>> urlpref_val,RandomAccessFile printurlfile) throws CloneNotSupportedException, IOException{
		StringBuffer str = new StringBuffer();
		InterNode inode = (InterNode) printrie.cache.get(root);
		byte key = inode.key();
		urlpref_val.add(new Pair<StringBuffer,Long>(str.append((char)key),inode.getprefnum()));
		printSubTrie(urlpref_val,inode.getFirstSubNode(),str);
		System.out.println(urlpref_val.size());
		Pair prefpair = null;
		for(int i = 0;i < urlpref_val.size();i++){
			prefpair = urlpref_val.get(i);
			printurlfile.writeBytes(prefpair.first.toString()+"\t"+prefpair.second+"\n");
		}
	}
	
	private void printSubTrie(List<Pair<StringBuffer, Long>> urlpref_val,long current,StringBuffer curStr) throws CloneNotSupportedException {
		InterNode inode =  (InterNode) printrie.cache.get(current);
		byte key = inode.key();
		StringBuffer str = new StringBuffer();
		str.append(curStr);
		urlpref_val.add(new Pair<StringBuffer,Long>(str.append((char)key),inode.getprefnum()));
		if(inode.getFirstSubNode() != -1)
		{
			printSubTrie(urlpref_val,inode.getFirstSubNode(), str);
		}			
		InterNode previous = inode;
		long brother;
		while(previous.getBrotherNode()!=-1)
		{

			brother = previous.getBrotherNode();
			previous =  (InterNode) printrie.cache.get(brother);
			byte key0 = previous.key();
			StringBuffer str0 = new StringBuffer();
			str0.append(curStr);
			urlpref_val.add(new Pair<StringBuffer,Long>(str0.append((char)key0),previous.getprefnum()));
			if(previous.getFirstSubNode()!=-1){
				printSubTrie(urlpref_val,previous.getFirstSubNode(), str0);
			}
		}
	}

	private void _printSubTrie(List<Pair<StringBuffer, Long>> urlpref_val,long current,StringBuffer curStr, boolean isFirst) throws CloneNotSupportedException {
		InterNode inode = (InterNode)printrie.cache.get(current);
		//		InterNode previous = inode;
		byte key = inode.key();
		if (inode.getFirstSubNode() != -1) {
			inode = (InterNode)printrie.cache.get(inode.getFirstSubNode());
			StringBuffer strPath = new StringBuffer("");
			strPath.append(curStr);
			urlpref_val.add(new Pair<StringBuffer, Long>(strPath.append((char)key), inode.getprefnum()));
			//			_printSubTrie(urlpref_val, inode.getFirstSubNode(), strPath.append((char)key));
			_printSubTrie(urlpref_val, inode.seek(), strPath/*.append((char)key)*/, true);
			int i = 0;
		} else {
			StringBuffer strPath = new StringBuffer("");
			strPath.append(curStr);
			urlpref_val.add(new Pair<StringBuffer, Long>(strPath.append((char)key), inode.getprefnum()));
		}
		if (isFirst) {
			//		inode = previous;
			inode = (InterNode)printrie.cache.get(current);
			InterNode previous = inode;
			while (inode.getBrotherNode() != -1) {
				inode = (InterNode)printrie.cache.get(inode.getBrotherNode());
				StringBuffer strPath = new StringBuffer("");
				strPath.append(curStr);
				urlpref_val.add(new Pair<StringBuffer, Long>(strPath.append((char)key), inode.getprefnum()));
				_printSubTrie(urlpref_val,/*previous.getBrotherNode()*/inode.seek(), curStr, false);
				int i = 0;
			}
		}
		//		inode = previous;
	}
	private void compute() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileReader in = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(in);
		String strLine = null;
//		String strLine = reader.readLine();
//		while (strLine != null) {
//			totalLines++;
//			strLine = reader.readLine();
//		}
		//        totalLines = 967533602;

//		nbThreads = (totalLines % maxlines)>0 ? (totalLines/maxlines) + 1 : totalLines/maxlines ;

		ArrayList<BlockingQueue<String>> bStr = new ArrayList<BlockingQueue<String>>();
		bStr.clear();
		long start = System.currentTimeMillis();
		List<StatisticThread> 	objs = new ArrayList<StatisticThread>();
		List<Long> 					threads=new ArrayList<Long>();
		for (int i = 0; i < nbThreads; ++i)
		{
			bStr.add(new ArrayBlockingQueue<String>(10000));
			StatisticThread st = new StatisticThread(file,i,/* maxlines,*/nbThreads, triename, size, bsize, bStr.get(i));	       
			objs.add(st);
			if(!st.isAlive()) st.start();
			threads.add(st.getId());
		}

		//        reader.reset();
		reader.close();
		in.close();
//		br = new BufferedReader(new FileReader(file));
//		in = new FileReader(file);
//		reader = new LineNumberReader(in);
////		strLine = reader.readLine();
//		while ((strLine = br.readLine()) != null) {
//			totalLines++;
////			strLine = reader.readLine();
//			try {
////				System.out.println((totalLines % nbThreads) + ": " + bStr.get(totalLines % nbThreads).remainingCapacity() + ": " + strLine);
//				bStr.get(totalLines % nbThreads).put(strLine);
////				Thread.sleep(1);
////				if (totalLines == 500)
////					System.out.print(bStr.get(totalLines % nbThreads).size());
//			} catch (InterruptedException e) {
//				System.out.println(totalLines);
//				e.printStackTrace();
//			}
//		}
//		reader.close();
//		in.close();
		List<SplitThread> 	sobjs = new ArrayList<SplitThread>();
		List<Long> 			sthreads=new ArrayList<Long>();
		
//		for (int i = 0; i < inthread_num; ++i) {
//			int fstart = i * (totalLines / inthread_num);
//			int fend = ((i + 1) * (totalLines / inthread_num) > totalLines) ? (totalLines + 1) : ((i + 1) * (totalLines / inthread_num));
//			SplitThread spt = new SplitThread(bStr, file, fstart, fend, nbThreads);
//			sobjs.add(spt);
//			if(!spt.isAlive()) spt.start();
//			sthreads.add(spt.getId());
//		}
		for (int i = 0; i < inthread_num; ++i) {
			long fstart = i * (fileSize / inthread_num);
			long fend = ((i + 1) * (fileSize / inthread_num) > fileSize) ? (fileSize + 1) : ((i + 1) * (fileSize / inthread_num));
			SplitThread spt = new SplitThread(bStr, file, fstart - 1, fend, nbThreads);
			sobjs.add(spt);
			if(!spt.isAlive()) spt.start();
			sthreads.add(spt.getId());
		}

		for(int i = 0; i< sobjs.size();i++) {
			try {
				sobjs.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < nbThreads; i++) {
			try {
				bStr.get(i).put("exit");
			} catch (InterruptedException e) {
				System.out.println(totalLines);
				e.printStackTrace();
			}
		}

		for(int i = 0; i< objs.size();i++) {
			try {
				objs.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("compute success: " + (end - start));
		br.close();
		System.out.println("computeBloc is finished");		
	}

	private void merge(){
		String filesuffix = "urltrie.idx";
		totalpreftrie = new Trie(new File(triename+filesuffix), size, bsize,
				Trie.Mode.CREATE,true);
		MergeTrie st = new MergeTrie(triename,0,nbThreads,totalpreftrie);	
		if(!st.isAlive()) st.start();
		try {
			st.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	//	private void merge(){
	//		List<MergeThread> 	mergeobjs = new ArrayList<MergeThread>();
	//        List<Long> 					mergethread=new ArrayList<Long>();
	List<MergeTrie> 	mergeobjs = new ArrayList<MergeTrie>();
	List<Long> 					mergethread=new ArrayList<Long>();

	//        for (int i = 0; i < 1; ++i)
	//	    {      
	//        	String filesuffix = null ; 
	//    		if(i == 0 ){
	//    			filesuffix = "urltrie.idx"; 
	//    		}
	//    		else filesuffix ="Nourltrie.idx";
	//    		totalpreftrie = new Trie(new File(triename+filesuffix), 1000, 5000,
	//    				Trie.Mode.CREATE,true);
	//    		MergeTrie st = new MergeTrie(triename,i,nbThreads,totalpreftrie);	
	////    		MergeThread st = new MergeThread(triename,i,nbThreads,totalpreftrie[i]);
	//	        mergeobjs.add(st);
	//	        if(!st.isAlive()) st.start();
	//	       mergethread.add(st.getId());
	//	    }
	//		for(int i = 0; i< mergeobjs.size();i++) {
	//			try {
	//				 mergeobjs.get(i).join();
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	public static void main(String args[]) throws FileNotFoundException{
		//		String sread = "University";
		//		String sread = "/home/hadoop/testjar/University.nt1";
		//		String sread = "/home/hadoop/Research/Rdf3x/rdf3x/bin/data/dbpedia_all.nt";
		String sread = args[0];
//		int maxline = Integer.parseInt(args[1]);
		int nbThreads = Integer.parseInt(args[1]);
		int size = Integer.parseInt(args[2]);
		int bsize = Integer.parseInt(args[3]);
		new Statistic(sread,nbThreads, size, bsize).startcounting();		
	}


}

