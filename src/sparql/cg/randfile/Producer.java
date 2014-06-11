package sparql.cg.randfile;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Producer extends Thread {
	
	private ArrayList<BlockingQueue<String>> bStr;
	private long start;
	private long end;
	private String file;
	private int totalLines = 0;
	private int nbThreads;
	public Producer() {
		// TODO Auto-generated constructor stub
	}
	
	
	
}
