/**
 * 
 */
package sparql.cg.hashtrie;

import static org.junit.Assert.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import sparql.cg.hashtrie.Hash;
import org.junit.Before;
import org.junit.Test;

/**
 * @author c_g
 *
 */
public class MultiHashTest {
	private Hash<String> hash;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		hash = MultiHash.instance("out.hash");
	}

	/**
	 * Test method for {@link org.cg.demo.MultiHash#hash(java.lang.String)}.
	 */
	@Test
	public void testHashString() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("out.hash"));
			String line = br.readLine();
			while (line != null) {
				String splits[] = line.split(" : ");
				int v = hash.hash(splits[0].trim()) - 1;
				assertEquals(v, Integer.parseInt(splits[2].trim()));
				assertEquals(hash.hash("ssssdfksjkdfjkdf"), 0);
				line = br.readLine();
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}

}
