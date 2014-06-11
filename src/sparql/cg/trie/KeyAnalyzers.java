package sparql.cg.trie;

import java.util.ArrayList;
import java.util.List;

public class KeyAnalyzers {
	public static byte ENDMARK = Byte.parseByte((int)'$' + "");
	private int keylength = 0;
	private int count = 0;
	private List<Long> values;
	private byte[] keys;
	
	public long[] getValues() {
		long[] lvalues = new long[values.size()];
		for (int i = 0; i < count; i++) {
			lvalues[i] = values.get(i).longValue();
		}
		return lvalues;
	}
	// Obsoleted interface for single value version
	public void setValue(long[] values) {
		this.values.clear();
		this.count = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] == -1)
				break;
			this.values.add(new Long(values[i]));
			this.count++;
		}
	}
	
	public void addValue(long value) {
		count++;
		this.values.add(new Long(value));
	}
	
	public int getCount() {
		return count;
	}

	private int ptr = -1;
	public KeyAnalyzers() {
		this.values = new ArrayList<Long>();
	}
	
	// Notice: values need to be null as required
	public KeyAnalyzers(String key/*, long[] values*/) {
		this();
		keys = key.getBytes();
		keylength = keys.length;
//		this.values = values;
	}

	public byte key() {
		ptr++;
		return keys[ptr];
	}
	public int next() {
		if (ptr > keylength)
			return -1;
		return ptr;
	}
	public byte getKey(int id) {
		return keys[id];
	}
	public byte current() {
		return keys[ptr];
	}
	public int length() {
		return Long.SIZE;
	}
	
	public String toString() {
		String svalues = new String("");;
		for (int i = 0; i < count; i++)
			svalues += " " + values.get(i).longValue();
		return new String(keys) + ", Values: " + svalues;
	}
	
	public String getKey() {
		return new String(keys, 0 ,keylength);
	}
}
