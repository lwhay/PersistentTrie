package sparql.cg.statistic;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class Distinguish {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String sread = "University";
		Map<String, Integer> dist = new HashMap<String, Integer>();
		LineNumberReader reader = new LineNumberReader(new FileReader(sread));
		String strLine = null;
		
		long starttime = System.currentTimeMillis();
		while ((strLine = /*reader.readLine()*/reader.readLine()) != null) {
			String splits[] = strLine.split(" ");
			for (int i = 0; i < 3; i++) {
				if (!dist.containsKey(splits[i]))
					dist.put(splits[i], new Integer(1));
			}
		}
		String output = "University.dist";
		BufferedWriter wr = null;
		try {
			wr = new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long count = 0;
		String outStr = "";
		try {
			for (String str : dist.keySet()) {
				//					System.out.println(str + " : " + shash.get(i).get(str));
				outStr += str;
				outStr += " ";
				if (++count % 3 == 0) {
					wr.write(outStr);
					wr.write("\n");
					outStr = "";
				}
			}
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endtime = System.currentTimeMillis();
		System.out.println("Elipse: " + (endtime - starttime));
	}
}
