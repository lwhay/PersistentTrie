/**
 * 
 */
package sparql.cg.hashtrie;

/**
 * @author c_g
 *
 */
public abstract class Hash<T> {
	/*
	 * @param key the input String
	 * @return Thread ID
	 */
	public abstract int hash(T key);
}
