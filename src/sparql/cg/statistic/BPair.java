/**
 * 
 */
package sparql.cg.statistic;

import sparql.cg.trie.InterNode;

/**
 * @author tk2510
 *
 */
public class BPair {
	private InterNode first;
	private int  second;
	/**
	 * 
	 */
	public BPair() {
		// TODO Auto-generated constructor stub
	}
	public BPair(InterNode first,int second){
		this.first = first ; 
		this.second = second;
	}
	public InterNode getFirst(){
		return first;
	}
	public void setFirst(InterNode inode){
		this.first = inode;
	}
	
	public int getSecond(){
		return second;
	}
	public void setSecond(int second){
		this.second = second;
	}
	
	public Object clone() throws CloneNotSupportedException{ 
		BPair bp=new BPair(); 
		bp.setFirst((InterNode) first.clone());
		bp.setSecond(second);
		return bp;
	}
	

}
