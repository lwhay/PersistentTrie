/**
 * 
 */
package sparql.cg.statistic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import sparql.cg.trie.InterNode;
import sparql.cg.trie.KeyAnalyzer;
import sparql.cg.trie.Trie;

/**
 * @author tk2510
 *
 */
public class MergeTrie  extends Thread{
	private String trie_name;
	private int thread_num;
	private int nbThread;
	private Trie[] prefTrie;
	private Trie   totalpreftrie;
	private int root = 0;
	private boolean isRoot = true;
	/**
	 * 
	 */
	public MergeTrie(String triename,int threadnum,int nbThread,Trie totaltrie) {
		// TODO Auto-generated constructor stub
		this.trie_name=triename;
		this.thread_num = threadnum;
		this.nbThread = nbThread;
		prefTrie = new Trie[nbThread];
		totalpreftrie = totaltrie;
	}
	public void run(){
		String filesuffix=null;
		if(thread_num == 0 ){
			filesuffix = "urltrie.idx"; 
		}
		else filesuffix ="Nourltrie.idx";
		List<BPair> rootList = new ArrayList<BPair>();
		for(int i=0; i < nbThread ; i++)
		{
			File triefile = new File((trie_name + i) + filesuffix);
			if(triefile.exists()){
				prefTrie[i] = new Trie(triefile, 0, 0,
						Trie.Mode.SEARCH,true,i);
				rootList.add(new BPair((InterNode)prefTrie[i].cache.get(root),i));
			}	
		}
		try {
			merge(rootList,root,true);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		search("Department");
		totalpreftrie.close();
		
		System.out.println("Thread " + thread_num +"is finished!");
	    for(int j = 0; j<nbThread; j++)
	    {
	    	prefTrie[j].close();
	    	File f = new File((trie_name + j) + filesuffix);
	    	if(f.exists())  
	    	{
//	    		boolean delete = f.getAbsoluteFile().delete();
//	    		System.out.println("delete"+j+delete);
	    	}
	    }
	}
	
	public long merge(List<BPair> mergeList,long cur,boolean first) throws CloneNotSupportedException{
//		System.out.println(mergeList.size());
		List<BPair> subNodeList = new ArrayList<BPair>();
		InterNode parent = (InterNode)totalpreftrie.cache.get(cur);
		long pref = 0;
		long retseek = 0 ;
		boolean _first = true;
		for(int i = 0;i < mergeList.size() ; i++ ){
			InterNode inode = mergeList.get(i).getFirst();
			byte key = inode.key();
			pref +=inode.getprefnum();
			if(inode.getFirstSubNode() != -1)
				prefTrie[mergeList.get(i).getSecond()].addSubList(subNodeList,inode);
		}
		if(isRoot){
			parent.setprefnumbers(pref);
			parent.dirty(true);
			isRoot  = false;
//			System.out.println(pref);
		}
		else{
			InterNode inode0 = new InterNode(mergeList.get(0).getFirst().key());
			inode0.setprefnumbers(pref);
			totalpreftrie.cache.put(inode0, parent);
			long seek = inode0.seek();
			if(first){
				parent.setFirstSubNode(seek);
			}
			else{
				parent.setBrotherNode(seek);
			}
			parent.dirty(true);
			retseek = seek;
		}
		
		if(subNodeList.size()==0){
			return retseek;
		}
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(subNodeList, new BlockComparator());
		List<BPair> mergedeepList = new ArrayList<BPair>();
		int size;
		long current = retseek;                  //current is the totalprefTrie's current 
		while(subNodeList.size() > 0)
		{
			size = mergedeepList.size();
			 
			if(size > 0)
			{
				BPair deep = (BPair) subNodeList.get(0).clone();
				if(subNodeList.get(0).getFirst().key() == mergedeepList.get(size-1).getFirst().key())
				{
					mergedeepList.add(deep);
					subNodeList.remove(0);
				}
				else{
					if(mergedeepList.size() > 1){
						current = merge(mergedeepList,current,_first);
					}
					else{
						current = inSubTrie(mergedeepList.get(0),current,_first);
					}
					mergedeepList.clear();
					_first = false;
//					mergedeepList.add(deep);
//					subNodeList.remove(0);
				}					
			}
			else {
				BPair blockPair = (BPair) subNodeList.get(0).clone();
				mergedeepList.add(blockPair);
				subNodeList.remove(0);
			}
		}	
		
		if(mergedeepList.size() > 1){
			current = merge(mergedeepList,current,_first);
		}
		else{
			current = inSubTrie(mergedeepList.get(0),current,_first);
		}
		mergedeepList.clear();
		return retseek;
	}
	private long inSubTrie(BPair bPair,long current,boolean first) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		int trienum = bPair.getSecond();
		InterNode parent = bPair.getFirst();
		InterNode inode = (InterNode) totalpreftrie.cache.get(current);
		InterNode inode0 = new InterNode(parent.key());
		inode0.setprefnumbers(parent.getprefnum());
//		inode0.setBrotherNode(-1);
//		inode0.setFirstSubNode(-1);
		totalpreftrie.cache.put(inode0,inode);		
		long seek = inode0.seek();
		if(first)
			inode.setFirstSubNode(seek);
		else
			inode.setBrotherNode(seek);
		inode.dirty(true);
		if(parent.getFirstSubNode()!=-1){
			InterNode subNode = (InterNode) prefTrie[trienum].cache.get(parent.getFirstSubNode());
			prefTrie[trienum].insertSub(subNode,totalpreftrie,seek);
		}
		return seek;
	}
	
	public void search(String key) {
		boolean find = false;
		KeyAnalyzer value = new KeyAnalyzer(key+"$", -1);
		find = totalpreftrie.searchStr(value);
		if (!find)
			System.out.println("not success");
		else System.out.println("search success!");
	}
}

 class BlockComparator implements Comparator {
	@Override
	public int compare(Object t1, Object t2) {
		// TODO Auto-generated method stub
		BPair block1 = (BPair)t1;
		BPair block2 = (BPair)t2;
		return (block1.getFirst().key() - block2.getFirst().key());
	}
}
