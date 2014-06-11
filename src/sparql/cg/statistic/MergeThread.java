package sparql.cg.statistic;

import java.io.File;
import java.util.Iterator;

import sparql.cg.demo.CreateTrieDemo.ReplacerEntry;
import sparql.cg.trie.InterNode;
import sparql.cg.trie.KeyAnalyzer;
import sparql.cg.trie.Trie;

public class MergeThread extends Thread {
	private String trie_name;
	private int thread_num;
	private int nbThread;
	private Trie[] prefTrie;
	private Trie   totalpreftrie;
	
	public MergeThread(String triename,int threadnum,int nbThread,Trie totaltrie){
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
		rewriteroot(0,filesuffix);
		totalpreftrie.close();
//		search("Department");
		System.out.println("Thread " + thread_num +"is finished!");
	    for(int j = 0; j<nbThread; j++)
	    {
	    	prefTrie[j].close();
	    	File f = new File((trie_name + j) + filesuffix);
	    	if(f.exists())  
	    	{
	    		boolean delete = f.getAbsoluteFile().delete();
	    		System.out.println("delete"+j+delete);
	    	}
	    }
	
	}
	
	public void createpreftrie(long iblock[],int[] deepthread,long insertiblock){
//		deep++;
		
		for(int childnum=0 ; childnum < 95 ; childnum++)
		{
			long prefnum = 0;
			int totalnode = 0;
			int[] deepsearch  = new int[nbThread];
			System.arraycopy(deepthread,0,deepsearch,0,iblock.length) ;
			long[] currentblock = new long[nbThread];
			System.arraycopy(iblock,0,currentblock,0,iblock.length) ;
			long insertblock = insertiblock;
			for(int thread = 0 ; thread < nbThread ; thread++)
			{
//				System.out.println("searchblock"+iblock[thread]+"\t"+childnum);
				if(deepsearch[thread]==1)
				{
					
					if(prefTrie[thread].searchInode(childnum,currentblock[thread]))
					{						
						currentblock[thread] = prefTrie[thread].getblock();
						InterNode inode = (InterNode)prefTrie[thread].getInode(currentblock[thread]);
						prefnum += inode.getprefnum();
//						System.out.println(inode.seek()+"\t"+(char)inode.key());
						totalnode++;
					}
					else deepsearch[thread] = 0;
				}
			}
			if(totalnode > 0){
				insertblock = totalpreftrie.insertkey(childnum,insertblock,prefnum);
//				deep++;
				createpreftrie(currentblock,deepsearch,insertblock);
			}
		
		}
	}
	
	public void rewriteroot(long root,String filesuffix){
		int[] deepthread = new int[nbThread];
		long iblock[] = new long[nbThread];
		long insertiblock = 0;
		
		for(int i=0; i < nbThread ; i++)
		{
			File triefile = new File((trie_name + i) + filesuffix);
			if(triefile.exists()){
				prefTrie[i] = new Trie(triefile, 0, 5000,
						Trie.Mode.SEARCH,true);
				deepthread[i] = 1;
			}else 
					deepthread[i] = 0;
			iblock[i] = 0;
		}		
		long prefnum = 0;
		for(int thread = 0 ; thread < nbThread ; thread++)
		{
			if(deepthread[thread]==1)
			{			
				InterNode inode = (InterNode) prefTrie[thread].cache.get(root);
				prefnum += prefTrie[thread].getInode(root).getprefnum();
//						System.out.println(inode.seek()+"\t"+(char)inode.key());
			}
		}
		totalpreftrie.getInode(root).setprefnumbers(prefnum);
		createpreftrie(iblock,deepthread,insertiblock);
	}
	
	public void arrayCopy(int[][] src,int[][] dest){
		for(int i = 0;i<src.length;i++){
			 System.arraycopy(src[i], 0, dest[i], 0, src[i].length); 
		}
	}
	
	public void search(String key) {
		boolean find = false;
		KeyAnalyzer value = new KeyAnalyzer(key+"$", -1);
		prefTrie[0].search(value);
		if (value.getValue() == -1)
			System.out.println(value.toString());
		else System.out.println("search success!");
	}
}
