/**
 * 
 */
package sparql.cg.trie;

import java.util.HashMap;
import java.util.Map;

/**
 * @author c_g
 *
 */
public class Config {
	private Map<String, Integer> imap = null;
	
	private Map<String, String> smap = null;
	
	public Config() {
		imap = new HashMap<String, Integer>();
		smap = new HashMap<String, String>();
	}
	
	public void set(String k, int v) {
		imap.put(k, v);
	}
	
	public void set(String k, String v) {
		smap.put(k, v);
	}
	
	public int getInt(String k) {
		return imap.get(k);
	}
	
	public String getString(String k) {
		return smap.get(k);
	}
	
}
