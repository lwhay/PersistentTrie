/**
 * 
 */
package sparql.cg.trie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import sparql.cg.statistic.BPair;
import sparql.cg.statistic.Pair;

/**
 * @author C_G
 * 
 */
public class Trie {

	private static final String InterNode = null;
	public TrieCache cache;
	public  long currentblock;
//	public static long subNode ;
	private RandomAccessFile file;
	private int size;
	private long root = 0;
	private int trienum;
	private int bsize;
	private RandomAccessFile printfile;	
	private boolean statistic;
	public Trie(File sfile, int size, int bsize, int mode,boolean statistic,int trienum){
		this(sfile,size,bsize,mode,statistic);
		this.trienum = trienum;
	}
	public Trie(File sfile, int size, int bsize, int mode,boolean statistic) {
		this.size = size;
		this.bsize = bsize;
		this.statistic = statistic;
		try {
			file = new RandomAccessFile(sfile, "rw");
//			printfile = new RandomAccessFile("printfile.txt", "rw");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (mode == Mode.CREATE)
			create();
		else if (mode == Mode.APPEND)
			append();
		else if (mode == Mode.SEARCH)
			search();
	}

	public boolean insert(KeyAnalyzer key) throws IOException {
		return _insert(key, root);
	}
	public long insertkey(int alph,long iblock,long prefnum)  // tk2510
	{
		byte key = (byte)(alph + 32);
		InterNode inode = (InterNode) cache.get(iblock);
//		System.out.println(iblock+"\t"+inode);
		long child = inode.child(key);
		InterNode inode0 = new InterNode(key);
		if(statistic) inode0.setprefnumbers(prefnum);
		cache.put(inode0, inode);
		long seek = inode0.seek();
		inode.child(key, seek);		
		inode.dirty(true);
		return seek;
	}
	public boolean _insert(KeyAnalyzer keys, long iblock){  //by tk2510
		InterNode inode = (InterNode) cache.get(iblock);
		inode.setprefnum();
		inode.dirty(true);
		byte key = keys.key();
		long firstsubnode = inode.getFirstSubNode();
		if(firstsubnode==-1 && key!= KeyAnalyzer.ENDMARK){
			InterNode inode0 = new InterNode(key);
			cache.put(inode0, inode);
			long seek = inode0.seek();
			inode.setFirstSubNode(seek);
			inode.dirty(true);
			return _insert(keys,seek);
		}
		else if(firstsubnode ==-1 && key == KeyAnalyzer.ENDMARK){
			//leafnode
			if(!statistic){
				LeafNode lnode = new LeafNode(keys.getValue());
				cache.put(lnode, inode);
				long seek = lnode.seek();
				inode.setFirstSubNode(seek);
				inode.dirty(true);
			}	
			return true;
		}
		else if(key != KeyAnalyzer.ENDMARK){
			Pair result = inode.insertSubNode(cache,key);
			boolean inresult = (Boolean) result.getFirst();
			long sub = (Long)result.getSecond();
			if(!inresult)
			{
				inode.setFirstSubNode(sub);
				inode.dirty(true);
				return _insert(keys,sub);
			}
			else return _insert(keys,sub);
		}
		else{
//			if(inode.isLastNode(cache))
//				return false;
//			else{
//				//insert leafnode
//				if(!statistic){
//					LeafNode lnode = new LeafNode(keys.getValue());
//					cache.put(lnode, inode);
//					long seek = lnode.seek();
//					inode.setFirstSubNode(seek);
//					inode.dirty(true);
//				}	
//				return true;
//			}
			return true;
		}
	}
	private boolean insert(KeyAnalyzer keys, long iblock) throws IOException {
		InterNode inode = (InterNode) cache.get(iblock);
//		System.out.println((char)inode.key()+"\t"+inode.getprefnum());
		inode.setprefnum();
//		System.out.println((char)inode.key()+"\t"+inode.getprefnum());
		byte key = keys.key();
		long child = inode.child(key);
		inode.dirty(true);
		if (-1 == child && key != KeyAnalyzer.ENDMARK) {
			InterNode inode0 = new InterNode(key);
			cache.put(inode0, inode);
			long seek = inode0.seek();
//			printfile.writeBytes(Long.toString(inode.getprefnum())+"\t"+Long.toString(inode.seek()) +"\t"+ Long.toString(seek)+ "\t"+(char)inode0.key()+"\n");
			inode.child(key, seek);
			inode.dirty(true);
			return insert(keys, seek);
		} else if (-1 == child && key == KeyAnalyzer.ENDMARK) {
			if(!statistic){
				LeafNode lnode = new LeafNode(keys.getValue());
				cache.put(lnode, inode);
				long seek = lnode.seek();
				inode.child(key, seek);
				inode.dirty(true);
//				System.out.println("a");
			}	
//			printfile.writeBytes("$\n");
			return true;
		} else if (key == KeyAnalyzer.ENDMARK) {
			return false;
		} else {
//			printfile.writeBytes(Long.toString(inode.getprefnum())+"\t"+Long.toString(inode.seek()) + "\t"+(char)inode.key()+"\n");
			return insert(keys, child);
		}
	}

	public boolean searchprefix(KeyAnalyzers key) {
		return searchprefix(key, root);
	}

	private boolean extractprefix(KeyAnalyzers keys, long iblock) {
		InterNode inode = (InterNode) cache.get(iblock);
		LeafNode ret = null;
		byte[] childs = inode.allChildKeys();
		for (int i = 0; i < childs.length; i++) {
			long child = inode.child(childs[i]);
			if (cache.get(child).TYPE == 0) {
				ret = (LeafNode) cache.get(child);
				keys.addValue(ret.getValue());
			} else
				extractprefix(keys, child);
		}
		if (keys.getCount() > 0)
			return true;
		else
			return false;
	}

	private boolean searchprefix(KeyAnalyzers keys, long iblock) {
		InterNode inode = (InterNode) cache.get(iblock);
		byte key = keys.key();
		if (key == KeyAnalyzer.ENDMARK)
			return extractprefix(keys, iblock);
		// System.out.print((char)key);
		long child = inode.child(key);
		// if (child == 1283281)
		// System.out.println();
		if (-1 == child) {
			System.out.println(inode.toString());
			System.out.println("not contain the key!!!");
			System.out.println((char)key);
			return false;
		} else {
			if (key == KeyAnalyzer.ENDMARK) {
				LeafNode ret;
				try {
					ret = (LeafNode) cache.get(child);
					keys.addValue(ret.getValue());
					//					keys.setValue(ret.getValue());
				} catch (ClassCastException e) {
					//	System.out.println(keys.getKey());
					throw new ClassCastException(keys.getKey());
				}

				return true;
			} else {
				return searchprefix(keys, child);
			}

		}
	}
	
	public void addSubList(List<BPair> subNodeList,InterNode parent){
		 InterNode inode = (InterNode) cache.get(parent.getFirstSubNode());
		 subNodeList.add(new BPair(inode,trienum));
		 addBrotherList(subNodeList,inode);
	}
	
	private void addBrotherList(List<BPair> subNodeList,InterNode inode) {
		// TODO Auto-generated method stub
		 if(inode.getBrotherNode() !=-1){
			 InterNode inode0 = (InterNode)cache.get(inode.getBrotherNode());
			 subNodeList.add(new BPair(inode0,trienum));
			 addBrotherList(subNodeList,inode0);
		 }
	}
	public boolean search(KeyAnalyzer key) {
		return search(key, root);
	}

	private boolean search(KeyAnalyzer keys, long iblock) {
		InterNode inode = (InterNode) cache.get(iblock);
		byte key = keys.key();
		System.out.println(inode.seek()+"\t"+(char)inode.key()+"\n");
		// System.out.print((char)key);
		long child = inode.child(key);
		// if (child == 1283281)
		// System.out.println();
		if (-1 == child) 
		{
			System.out.println(inode.toString());
			System.out.println("not contain the key!!!");
			System.out.println((char)key);
			return false;
		} 
		else 
		{
			if (key == KeyAnalyzer.ENDMARK) 
			{
				LeafNode ret;
				try {
					ret = (LeafNode) cache.get(child);
					keys.setValue(ret.getValue());
				} catch (ClassCastException e) {
					//	System.out.println(keys.getKey());
					throw new ClassCastException(keys.getKey());
				}

				return true;
			} else {
				return search(keys, child);
			}
		}
	}
	public void close() {
		cache.close();
	}
	private void create() {
		try {
			file.setLength(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cache = new TrieCache(file, size, bsize);
		InterNode inode0 = new InterNode(KeyAnalyzer.ENDMARK);
		inode0.seek(root);
		cache.put(inode0);
	}

	private void search() {
		cache = new TrieCache(file, size, 0);
	}
	
	public InterNode getInode(long iblock)
	{
		return (InterNode) cache.get(iblock);
	}
	public long getblock()
	{
		return this.currentblock;
	}
	public boolean searchInode(int childnum,long iblock)
	{
		InterNode inode = (InterNode) cache.get(iblock);
		long child = inode.getchild(childnum);
		if(child == -1)
			return false;
		else
		{		
			currentblock = child;
			return true;
		}
		
	}
	private void append()
	{

	}

	public static interface Mode 
	{
		public static int CREATE = 0;
		public static int APPEND = 1;
		public static int SEARCH = 2;
	}

	
//	public long insertAll(InterNode parent,Trie totalpreftrie, long current, boolean first) 
//			throws CloneNotSupportedException 
//	{
//		// TODO Auto-generated method stub
//		InterNode inode = (InterNode) totalpreftrie.cache.get(current);
//		InterNode inode0 = (InterNode) parent.clone();
//		inode0.setBrotherNode(-1);
//		inode0.setFirstSubNode(-1);
//		totalpreftrie.cache.put(inode0,inode);		
//		long seek = inode0.seek();
//		if(first)
//		{
//			inode.setFirstSubNode(seek);
//			inode.dirty();
//		}
//		else
//		{
//			
//		}
//		if(parent.getFirstSubNode()!=-1){
//			InterNode subNode = (InterNode) cache.get(parent.getFirstSubNode());
//			insertSub(subNode,totalpreftrie,seek);
//		}
//		return seek;
//	}
	public void insertSub(InterNode subRoot, Trie totalpreftrie,long seek) throws CloneNotSupportedException
	{
		// TODO Auto-generated method stub
//		System.out.println("-1");
//		System.out.println(seek);
		
//		if(inode == null){
//			System.out.println("null");
//		}
		InterNode inode =  (InterNode) totalpreftrie.cache.get(seek);
		InterNode inode0 = new InterNode(subRoot.key());
		inode0.setprefnumbers(subRoot.getprefnum());
//		inode0.setBrotherNode(-1);
//		inode0.setFirstSubNode(-1);
//	    long retseek = seek;
		totalpreftrie.cache.put(inode0,inode);
		long _seek = inode0.seek();
		inode.setFirstSubNode(_seek);
		inode.dirty(true);
		if(subRoot.getFirstSubNode() != -1)
		{
			InterNode subNode = (InterNode) cache.get(subRoot.getFirstSubNode());
//			System.out.println("1");
//			System.out.println(_seek);
			insertSub(subNode,totalpreftrie,_seek);
		}
		
		InterNode previous = subRoot;
		InterNode insprevious = inode0;
		while(previous.getBrotherNode()!=-1)
		{
			InterNode bInode = (InterNode) cache.get(previous.getBrotherNode());
			InterNode _bInode = new InterNode(bInode.key());
			_bInode.setprefnumbers(bInode.getprefnum());
//			InterNode _bInode = (InterNode) bInode.clone();
//			_bInode.setFirstSubNode(-1);
//			_bInode.setBrotherNode(-1);
			totalpreftrie.cache.put(_bInode,insprevious);
			_seek = _bInode.seek();
			insprevious.setBrotherNode(_seek);
			insprevious.dirty(true);
			if(bInode.getFirstSubNode()!=-1)
			{
				InterNode subNode = (InterNode) cache.get(bInode.getFirstSubNode());
//				System.out.println("2");
//				System.out.println(_seek);
				insertSub(subNode,totalpreftrie,_seek);
			}
			insprevious = _bInode;
			previous = bInode;
		}

	}
	public boolean searchStr(KeyAnalyzer value){
		InterNode inode = (InterNode) cache.get(root);
		return searchStr(value,inode.getFirstSubNode());
	}
	public boolean searchStr(KeyAnalyzer value,long cur) {
		// TODO Auto-generated method stub
		System.out.println(cur);
		InterNode inode = (InterNode) cache.get(cur);
		byte key = value.key();
		System.out.println(inode.seek()+"\t"+(char)inode.key());
		// System.out.print((char)key);
		InterNode brother;
		if(key!= '$'){
			if(inode.key() > key){
				System.out.println("a");
				return false;
			}
			else if(inode.key() == key)
				return searchStr(value,inode.getFirstSubNode());
			else{
				boolean find = false;
				System.out.println("b");
				if(inode.getBrotherNode()!= -1){
					brother = (InterNode) cache.get(inode.getBrotherNode());
				}
				else return false;
				while(true){
					if(brother.key() > key){
						System.out.println("ba");
						return false;
					}
					else if(brother.key() == key){
						return searchStr(value,brother.getFirstSubNode());
					}
					else{
						System.out.println("bb"+(char)brother.key());
						if(brother.getBrotherNode()!= -1){
							brother = (InterNode) cache.get(brother.getBrotherNode());
						}
						else return false;					
					}
				}
			}
		}
		else{
			return true;
		}
	}
}
