package sparql.cg.memtrie;

import java.util.Map.Entry;

public class EntryPair<K, V> implements Entry<K, V> {
	
		private K key;
		
		private V value;
		
		public EntryPair(K key, V value) {
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

