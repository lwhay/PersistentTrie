package sparql.cg.test;

import java.util.ArrayList;
import java.util.List;

public class TransferTest {
	
	public void search(){
		List<Long> trie = new ArrayList<Long>();
		change(trie);
		Long a = trie.get(0);
		String b = "absc.df";
		String[] c=b.split("\\.");
		for(int i = 0 ; i< c.length;i++)
			System.out.println(c[i]);
		
	}
	
	public void change(List<Long> trie ){
		trie.add(new Long(123));
		
	}
	
	public static void main(String[] args){
		new TransferTest().search();
	}
	
	
}
