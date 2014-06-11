/**
 * 
 */
package sparql.cg.hashtrie;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sparql.cg.hashtrie.Hash;
import sparql.cg.memtrie.EntryPair;
import sparql.cg.memtrie.MemTrie;

/**
 * @author c_g
 *
 */
class MultiHash extends Hash<String> {
	private MemTrie trie;
	
	public MultiHash(List<Map.Entry<String, Integer>> hashlist) {
		//read hash file and create memtrie
		trie = new MemTrie();
		initTrie(hashlist);
	}
	
	public void initTrie(List<Map.Entry<String, Integer>> hashlist) {
		int size = hashlist.size();
		boolean[] visit = new boolean[size];
		
		for (int i = 0; i < size; i++) 
			visit[i] = false;
		for (int i = size / 2 + size % 2; i > 0; i = i / 2)
			for (int j = i; j < size; j += i) {
				if (!visit[j]) {
					visit[j] = true;
					Map.Entry<String, Integer> entry = hashlist.get(j);
					trie.insert(entry.getKey(), entry.getValue());
				}
			}
		Map.Entry<String, Integer> entry = hashlist.get(0);
		trie.insert(entry.getKey(), entry.getValue());
	}

	@Override
	public int hash(String key) {
		//IDС��0˵������trie��
		int id = trie.search(key);
		if (id < 0)
			id = 0;
		return id;
	}
	
	public static Hash<String> instance(String file) {
		List<Map.Entry<String, Integer>> hashlist = new ArrayList<Map.Entry<String, Integer>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null) {
				String splits[] = line.split(" : ");
				Map.Entry<String, Integer> entry = new EntryPair<String, Integer>(splits[0].trim(), Integer.parseInt(splits[2].trim()) + 1);
				hashlist.add(entry);
				line = br.readLine();
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
			
		MultiHash hash = new MultiHash(hashlist);
		hashlist.clear();
		return hash;
	}
		
}
