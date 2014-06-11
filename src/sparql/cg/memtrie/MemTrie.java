package sparql.cg.memtrie;


public class MemTrie {
	private MemNode root = null;
	public MemTrie() {
		// TODO Auto-generated constructor stub
		root = new MemNode();
	}
	public boolean insert(String prefix,int id){
		boolean ret = true;
		MemNode node = root;
		for(int i = 0; i<prefix.length();i++){
			char key = prefix.charAt(i);
			if(node.getFirst()!=null){
				MemNode previous = node;
				node = node.getFirst();
				if(node.getKey() > key){
					MemNode newNode = new MemNode();
					newNode.setKey(key);
					newNode.setBrother(node);
					previous.setFirst(newNode);
					node  = newNode;
				}
				else if(node.getKey() < key){
					node = insertBro(node,previous,key);
				}
				else{
				}
				if(i+1 == prefix.length()){
					node.setId(id);	
//					System.out.println(id);
					ret = true;
				}
				
			}
			else{
				MemNode newNode = new MemNode();
				newNode.setKey(key);
				if(i+1 == prefix.length()){
					newNode.setId(id);
					ret = true;
				}
				node.setFirst(newNode);
				node = newNode;
			}
		}
		return ret;
	}
	private MemNode insertBro(MemNode node,MemNode previous,char key) {
		// TODO Auto-generated method stub
		if(node.getBrother()!=null){
			previous  = node;
			node = node.getBrother();
			if(node.getKey() < key ){
				return insertBro(node,previous,key);			
			}
			else if(node.getKey() > key){
				MemNode newNode = new MemNode();
				newNode.setKey(key);
				newNode.setBrother(node);
				previous.setBrother(newNode);
				return newNode ;
			}
			else return node;
			
		}
		else{
			MemNode newNode = new MemNode();
			newNode.setKey(key);
			node.setBrother(newNode);
			return newNode;
		}		
	}
		
	public int search(String prefix){
		MemNode node = root;
		int pos = 0;
		while(node!=null){
			char key = prefix.charAt(pos);
			if(node.getFirst()!=null){
//				System.out.println(pos);
				node = node.getFirst();			
				if(node.getKey() > key){
					node = null;
//					System.out.println("<"+pos);
				}
				else if(node.getKey() < key){
					node = searchBro(node,key);	
//					System.out.println(">"+pos);
				}
				else{
				}
				if(node != null && node.getId() != -1){
//					System.out.println("end"+pos);
					return node.getId();
				}
			}
			else{
				node = null;
			}
			pos++;
		}
		return -1;
		
	}
	private MemNode searchBro(MemNode node, char key) {
		// TODO Auto-generated method stub
		if(node.getBrother()!=null){
			node = node.getBrother();
			char nkey = node.getKey();
			if(nkey < key){
				return searchBro(node, key);
			}
			else if(nkey > key){
				return null;
			}
			else{
				return node;
			}
		}
		else{
			return null;
		}
	}
}