/**
 * 
 */
package sparql.cg.hashtrie;

import java.io.BufferedReader;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sparql.cg.trie.Config;

/**
 * @author c_g
 *
 */
public class Job {
	private List<LockedQueue> lqs;
	
	private String jobname = "job";
	
	private Config conf;
	
	private int trie_nums;
	
	private int hash_nums;
	
	private HashThread[] hts;
	
	private TrieThread[] tts;
	
	private LockedQueue[] hlqs;
	
	private Hash<String> hash;
	
	private int rrs_id = 0;//Round Robin Schedule
	
	public Job(Hash<String> hash, Config conf) {
		this.conf = conf;
		this.hash = hash;
		trie_nums = conf.getInt("TrieThread");
		hash_nums = conf.getInt("HashThread");
		jobname = conf.getString("JobName");
		initTries();
		initHashs();
	}
	
	public String getJobname() {
		return jobname;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname;
	}

	protected void initTries() {
		lqs = new ArrayList<LockedQueue>();
		tts = new TrieThread[trie_nums];
		int trie_queue_size = conf.getInt("TrieQueueSize");
		for (int i = 0; i < trie_nums; i++) {
			LockedQueue tmp = new LockedQueue(trie_queue_size);
			lqs.add(tmp);
			tts[i] = new TrieThread(tmp, conf);
			tts[i].setName("tix-" + i + ".idx");
		}
	}
	
	protected void initHashs() {
		hts = new HashThread[hash_nums];
		hlqs = new LockedQueue[hash_nums];
		int hash_queue_size = conf.getInt("HashQueueSize");
		for (int i = 0; i < hash_nums; i++) {
			hlqs[i] = new LockedQueue(hash_queue_size);
			hts[i] = new HashThread(hlqs[i], lqs, hash, conf);
		}
	}
	
	public void startAll() {
		for (TrieThread tt : tts)
			tt.start();
		for (HashThread ht : hts)
			ht.start();
	}
	
	public void joinAll() throws IOException {
		try {
			for (TrieThread tt : tts){
				tt.join();
			}
			StringBuffer str = convertStr(TrieThread.balance);
			String path = conf.getString("IndexPath");
			RandomAccessFile trieStrNum = new RandomAccessFile(path+"/trie-balance","rws");
			long len = trieStrNum.length();
			trieStrNum.seek(len);
			trieStrNum.writeBytes("\n"+new String(str));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public StringBuffer convertStr(ArrayList<Long> list){
		StringBuffer str = new StringBuffer(); 
		for(Long sum:list){
			str.append(sum+"|");
		}
		return str;
	}
	public void rrsPut(List<String> ins) {
		rrs_id = rrs_id % hash_nums;
		try {
			hlqs[rrs_id].put(ins);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		rrs_id++;
	}
	
	public void putOver() {
		try {
			for (LockedQueue lq : hlqs) 
				lq.put(new LinkedList<String>());
			for (HashThread ht : hts)
				ht.join();
			for (LockedQueue lq : lqs) 
				lq.put(new LinkedList<String>());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static long startJob(Config conf, Hash<String> hash) {
		
		long begin = System.currentTimeMillis();
		long read = begin;
		String file = conf.getString("InputFile");
		int hash_list_size = conf.getInt("HashListSize");
		Job job = new Job(hash, conf);
		job.startAll();
		List<String> list = new LinkedList<String>();
 		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				String[] splits = line.split(" ");
				for (String s : splits) {
					if(list.size() > hash_list_size) {
						job.rrsPut(list);
						list = new LinkedList<String>();
					}
					list.add(s);
				}
				line = br.readLine();
			}
			if (!list.isEmpty())
				job.rrsPut(list);
			read = System.currentTimeMillis();
			job.putOver();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long pend = System.currentTimeMillis();
		try {
			job.joinAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long jend = System.currentTimeMillis();
		System.out.println(job.getJobname() + "Read Time: " + (read - begin) + "ms");
		System.out.println(job.getJobname() + "Hash Time: " + (pend - begin) + "ms");
		System.out.println(job.getJobname() + "Run Time: " + (jend - begin) + "ms");
		return jend - begin;
	}
	
}
