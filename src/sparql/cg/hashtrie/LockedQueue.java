/**
 * 
 */
package sparql.cg.hashtrie;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author c_g
 *
 */
public class LockedQueue extends LinkedBlockingQueue<List<String>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LockedQueue(int size) {
		super(size);
	}

}
