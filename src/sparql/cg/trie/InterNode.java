/**
 * 
 */
package sparql.cg.trie;

import java.nio.ByteBuffer;
import java.util.List;

import sparql.cg.cache.Block;
import sparql.cg.cache.BlockHeader;
import sparql.cg.statistic.Pair;

/**
 * @author C_G
 *
 */
public class InterNode extends Block  implements Cloneable {
//	public static int TYPE = 1;
	private static int capacity = 95;
	public static int first = 32;
	private byte key;
	private long[] childs;
	private long firstSubNode;
	private long brotherNode;
	private long pref;
	public InterNode() {
		this.TYPE = 1;
		childs = new long[capacity];
		for (int i = 0; i < capacity; i++) {
			childs[i] = -1;
		}
		firstSubNode = brotherNode = -1; 
		pref = 0;
		header.headType(TYPE);
	}
	public InterNode(byte key) {
		this();
		this.key = key;		
	}
	public InterNode(BlockHeader header) {
		this();
		this.header = header;
	}
	public byte key() {
		return key;
	}
	public long getFirstSubNode(){
		return this.firstSubNode;
	}
	public long getBrotherNode(){
		return this.brotherNode;
	}
	public void setFirstSubNode(long firstsubnode){
		 this.firstSubNode = firstsubnode;
	}
	public void setBrotherNode(long brother){
		 this.brotherNode = brother;
	}
	public long child(byte key) {
		return childs[key - first];
	}
	public void child(byte key, long child) {
		childs[key - first] = child;
	}
	public long getchild(int key){
		return childs[key];
	}
	public void setprefnum(){
		pref++;
	}
	public void setprefnumbers(long pref){
		this.pref = pref; 
	}
	public long getprefnum(){
		return this.pref ;
	}
	
	public Pair insertSubNode(TrieCache cache, byte key){
		
		InterNode firstNode = (InterNode) cache.get(firstSubNode);
		InterNode previous = firstNode;
		if(firstNode.key() > key){
//			Trie.subNode = seek;
			InterNode inode0 = new InterNode(key);
			inode0.setBrotherNode(firstNode.seek());
			cache.put(inode0, firstNode);
			long seek = inode0.seek();
			return new Pair<Boolean,Long>(false,seek);
		}
		else if(firstNode.key() == key ){
//			Trie.subNode = firstNode.seek();
			
			return new Pair<Boolean,Long>(true, firstNode.seek());
		}
		else 
			return firstNode._insertSubNode(cache,previous,key);		
	}
	
	public Pair _insertSubNode(TrieCache cache,InterNode previous , byte key){
		if(brotherNode != -1){
			InterNode brother = (InterNode) cache.get(brotherNode);
			if(brother.key() > key){
				InterNode inode0 = new InterNode(key);
				inode0.setBrotherNode(brother.seek());
				cache.put(inode0, previous);
				long seek = inode0.seek();
				previous.setBrotherNode(seek);
				previous.dirty(true);
//				Trie.subNode = seek;
				return new Pair<Boolean,Long>(true, seek);
			}
			else if(brother.key() == key ){
//				Trie.subNode = brother.seek() ;
//				brother.setprefnum();
//				brother.dirty(true);
				return new Pair<Boolean,Long>(true, brother.seek());
			}
			else return brother._insertSubNode(cache,brother,key);
		}
		else{
			InterNode inode0 = new InterNode(key);
			cache.put(inode0, previous);
			long seek = inode0.seek();
			previous.setBrotherNode(seek);
			previous.dirty(true);
//			Trie.subNode = seek;
			return new Pair<Boolean,Long>(true, seek);
		}
			
	}
	
	public boolean isLastNode(TrieCache cache) {
		// TODO Auto-generated method stub
		if(cache.get(firstSubNode).TYPE == 0)
			return false;
		else return true;
	}
	
	
	
	@Override
	public void serialize(ByteBuffer buffer) {
		buffer.put(key);
		buffer.putLong(pref);
		buffer.putLong(firstSubNode);
		buffer.putLong(brotherNode);
//		for (long child : childs)
//			buffer.putLong(child);
	}

	@Override
	public void deserialize(ByteBuffer buffer) {
		key = buffer.get();
		pref = buffer.getLong();
		firstSubNode = buffer.getLong();
		brotherNode = buffer.getLong();
//		for (int i = 0; i < capacity; i++) {
//			childs[i] = buffer.getLong();
//		}
	}

	@Override
	public int length() {
		return BlockHeader.SIZE + (Byte.SIZE + Long.SIZE * 3 ) / 8;
	}

	@Override
	public int type() {
		return TYPE;
	}
	public String toString() {
		return "type: " + type() + ", key: " + (char)key + ", " + super.toString() + allChilds();
	}
	
	public byte[] allChildKeys() {
		int childcount = 0;
		for (int i = 0; i < capacity; i++) {
			if (-1 != childs[i]) {
				childcount++;
			}
		}

		byte[] retkeys = new byte[childcount];

		for (byte i = 94; i >= 0; i--) {
			if (-1 != childs[i]) {
				retkeys[childcount - 1] = i;
				retkeys[childcount - 1] += 32;
				childcount--;
			}
		}
		return retkeys;
	}
	
	private String allChilds() {
		String ret = "";
		String tmp = "";
		for (int i = 0; i < capacity; i++) {
			if (-1 != childs[i]) {
				tmp = " childs[" + (char)(i + first) + "]=" + childs[i];
				ret += tmp;
			}
		}
		return ret;
	}
	
	public Object clone() throws CloneNotSupportedException{
		InterNode inode = new InterNode(key);
		inode.setFirstSubNode(firstSubNode);
		inode.setBrotherNode(brotherNode);
		inode.setprefnumbers(pref);
		inode.header  = (BlockHeader) header.clone();
		return inode;
	}
	
}
