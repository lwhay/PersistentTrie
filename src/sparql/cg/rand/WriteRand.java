package sparql.cg.rand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class WriteRand extends Thread{
	
	public String outfile;
	private BlockingQueue<String> strque;
	private int writesize;
	public WriteRand(String outfile,BlockingQueue<String> que,int size){
		this.outfile = outfile;
		this.strque = que;
		writesize = size;
	}
	
	public void run(){
		try {
			FileWriter fwrite = new FileWriter(outfile);
			BufferedWriter outfile = new BufferedWriter(fwrite);
			ArrayList<String> writelist = new ArrayList<String>();
			String str=null;
			long line = 0;
			try {
				while((str = strque.take())!="exit"){
					writelist.add(str);
					line++;
					if( line % writesize == 0){
						writeString(outfile,writelist);
					}
				}
				if(!writelist.isEmpty()){
					writeString(outfile,writelist);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			outfile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeString(BufferedWriter writefile,ArrayList<String> writelist) throws IOException {
		// TODO Auto-generated method stub
		int line = 0;
		int index = 0;
		String str = null;
		Random  rand = new Random();
		while(!writelist.isEmpty()){
			index = rand.nextInt(writelist.size());
			str = writelist.get(index);
			writefile.write(str+" ");
			writelist.remove(index);
			line++;
			if(line%3 == 0){
				writefile.write("\n");
			}
		}
		writelist.clear();
	}
	
	
}
