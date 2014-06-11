package sparql.cg.statistic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class SplitThread extends Thread {

	private ArrayList<BlockingQueue<String>> bStr;
	private long start;
	private long end;
	private String file;
	private int totalLines = 0;
	private int nbThreads;

	public SplitThread( ArrayList<BlockingQueue<String>> _bStr, String infile, long _start, long _end, int _nbThreads){
		this.bStr=_bStr;
		this.file = infile;
		this.start = _start;
		this.end = _end;
		this.nbThreads = _nbThreads;
	}

	@Override
	public void run() {
		//produce messages
		try {
			//			BufferedReader br = new BufferedReader(new FileReader(file));
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			reader.skip(((start - 1) > 0) ? (start - 1) : 0);
			String strLine = null;
			long chars = 0;

			if (start != 0) {
				String strLineBefore = reader.readLine();

				boolean foundFirst = false;

				if (strLineBefore.equals(""))
					foundFirst = true;

				//			if (foundFirst)
				//				totalLines++;

				chars = strLineBefore.length();
				chars++;
			}


			//			while ((strLine = reader.readLine()) != null && start + totalLines < end) {
			while ((strLine = reader.readLine()) != null && start + chars/* + strLine.length()*/ < end) {
				totalLines++;
				chars += strLine.length();
				chars++;
				//				strLine = reader.readLine();
				try {
					//					System.out.println((totalLines % nbThreads) + ": " + bStr.get(totalLines % nbThreads).remainingCapacity() + ": " + strLine);
					bStr.get(totalLines % nbThreads).put(strLine);
					//					Thread.sleep(1);
					//					if (totalLines == 500)
					//						System.out.print(bStr.get(totalLines % nbThreads).size());
				} catch (InterruptedException e) {
					System.out.println(totalLines);
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
