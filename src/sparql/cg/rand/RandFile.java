package sparql.cg.rand;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sparql.cg.randfile.RandomFile;

public class RandFile extends Thread{
	
	public String file;
	BlockingQueue<String> strque;
	public RandFile(String file,BlockingQueue<String> que) {
		// TODO Auto-generated constructor stub
		this.file = file;
		this.strque = que;
	}
	public void run(){
		
		try {
			FileReader fread = new FileReader(file);
			BufferedReader infile = new BufferedReader(fread);
			String strline = null;
			while((strline = infile.readLine())!=null){
				String[] split = strline.split(" ");
				for(int i = 0; i< 3;i++){
					strque.put(split[i]);
				}								
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]) throws InterruptedException{
		String file = "University";
		String outfile = file+"_rand.nt";
		int writesize = 10000 ;
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(writesize);
		long start = System.currentTimeMillis();
		RandFile rand = new RandFile(file,queue);
		if(!rand.isAlive()) rand.start();
		WriteRand wrand = new WriteRand(outfile,queue,writesize);
		if(!wrand.isAlive())  wrand.start();
		
		rand.join();
		
		queue.put("exit");
		wrand.join();	
		long end = System.currentTimeMillis();
		System.out.println("rand-time:"+ (end-start));
	}
}
