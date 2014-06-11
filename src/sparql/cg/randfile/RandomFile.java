package sparql.cg.randfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sparql.cg.statistic.SplitThread;
import sparql.cg.statistic.StatisticThread;

public class RandomFile {
	public ArrayList<BlockingQueue<String>> bStr;
	public String file;
	public int nbThreads;
	
	public RandomFile(String file,int nbThread) {
		// TODO Auto-generated constructor stub
		this.file = file;
		nbThreads = nbThread;
	}
	
	public void startRand(){
		List<StatisticThread> 	objs = new ArrayList<StatisticThread>();
		List<Long> 					threads=new ArrayList<Long>();
		for (int i = 0; i < nbThreads; ++i)
		{
			bStr.add(new ArrayBlockingQueue<String>(10000));
			
			objs.add(st);
			if(!st.isAlive()) st.start();
			threads.add(st.getId());
		}
		
		List<SplitThread> 	sobjs = new ArrayList<SplitThread>();
		List<Long> 			sthreads=new ArrayList<Long>();
		
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
	}
	
	
	public static void main(String[] args){
		String file = "University";
		RandomFile rand = new RandomFile(file);
		
		rand.startRand();
	}
}
