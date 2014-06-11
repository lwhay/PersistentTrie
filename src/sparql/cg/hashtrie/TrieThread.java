/**
 * 
 */
package sparql.cg.hashtrie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sparql.cg.trie.Config;
import sparql.cg.trie.KeyAnalyzer;
import sparql.cg.trie.Trie;

/**
 * @author c_g
 *
 */
public class TrieThread extends Thread {
	//������У��������뽨���Ĵ�
	private LockedQueue bq = null;
	//������
	private Trie trie = null;
	//�̱߳�ʶ
	private boolean running = true;
	//����һЩ������Ϣ����������Ŀ¼��LRU����Ĵ�С��ADD����Ĵ�С��
	private Config conf;
	public static ArrayList<Long> balance = new ArrayList<Long>();
	
	public TrieThread(LockedQueue bq, Config conf) {
		this.bq = bq;
		this.conf = conf;
		
	}

	@Override
	public void run() {
		int size = conf.getInt("CacheSize");
		int asize = conf.getInt("BufferSize");
		String path = conf.getString("IndexPath") + "/" + this.getName();
		int mode = conf.getInt("TrieMode");
		
		if (mode == 0)
			try {
				insert(path, size, asize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if (mode == 1)
			search(path, size, asize);
	}
	
	public void insert(String file, int size, int asize) throws IOException {
		try {
			trie = new Trie(new File(file), size, asize,Trie.Mode.CREATE,true);
			long sum = 0;
			while(running) {
				List<String> tmp = bq.take();
				if (tmp.isEmpty())
					running = false;
				else {
					for (String s : tmp) {
//						KeyAnalyzer key = new KeyAnalyzer(s+"$",
//								0/* 19 */);
//						trie.insert(key);             //construct trie
						sum++;
					}
				}
			}
//			System.out.println(sum);
			balance.add(sum);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			trie.close();
		}
	}
 	
	public void search(String file, int size, int asize) {
		try {
			trie = new Trie(new File(file), size, asize,Trie.Mode.SEARCH,true);
			int find = 0;
			while(running) {
				List<String> tmp = bq.take();
				if (tmp.isEmpty())
					running = false;
				else {
					for (String s : tmp) {
						KeyAnalyzer key = new KeyAnalyzer(s+"$",-1);
						if(trie.searchStr(key))
						{
							find++;
						}
//						System.out.println("Search : " + ret);
					}
//					System.out.println(tmp);
				}
			}
			System.out.println("find:"+find);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			trie.close();
		}
	}
}
