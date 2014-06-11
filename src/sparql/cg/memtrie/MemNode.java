package sparql.cg.memtrie;

public class MemNode {

	private char key = '$';
	
	private MemNode first = null;
	
	private MemNode brother = null;
		
	private int id = -1;
	
	public MemNode() {}
	
	public MemNode(char key) {
		this.key = key;
	}
	public char getKey() {
		return key;
	}

	public void setKey(char key) {
		this.key = key;
	}

	public MemNode getFirst() {
		return first;
	}

	public void setFirst(MemNode first) {
		this.first = first;
	}

	public MemNode getBrother() {
		return brother;
	}

	public void setBrother(MemNode brother) {
		this.brother = brother;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
