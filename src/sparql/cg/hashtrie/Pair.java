/**
 * 
 */
package sparql.cg.hashtrie;

import java.util.Map.Entry;

/**
 * @author c_g
 *
 */
public class Pair<K, V> implements Entry<K, V> {
	private K key;
	
	private V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}

}
