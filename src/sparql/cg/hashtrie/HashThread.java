/**
 * 
 */
package sparql.cg.hashtrie;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sparql.cg.trie.Config;

/**
 * @author c_g
 *
 */
public class HashThread extends Thread {
	
	private LockedQueue lq = null;
	
	private List<LockedQueue> list;
	
	private Hash<String> hash;
	
	private boolean running = true;
	
	private int trie_list_size = 0;
	
	private int trieThread = 0;
	
	private Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
	
	public HashThread(LockedQueue lq, List<LockedQueue> list, Hash<String> hash, Config conf) {
		this.lq = lq;
		this.list = list;
		this.hash = hash;
		trie_list_size = conf.getInt("TrieListSize");
		trieThread = conf.getInt("TrieThread");
		
		for (int i = 0; i < trieThread; i++)
			map.put(i, new LinkedList<String>());
		
	}
	
	@Override
	public void run() {
		try {
			while (running) {
				List<String> tmp = lq.take();
				if (tmp.isEmpty()) {
					for (int i = 0; i < trieThread; i++) {
						List<String> l = map.get(i);
						
						if (!l.isEmpty())
							list.get(i).put(l);
					}
					running = false;
				}
				else {
					for (String s : tmp) {
						int id = hash.hash(s);
						id = id % trieThread;
						List<String> l = map.get(id);
						
						if (l.size() > trie_list_size) {
							
							list.get(id).put(l);
							l = new LinkedList<String>();
							map.put(id, l);
						}
						l.add(s);
					}
//					for (LockedQueue lq : list) 
//						lq.put(tmp);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
