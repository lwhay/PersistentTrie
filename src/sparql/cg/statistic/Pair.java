/**
 * 
 */
package sparql.cg.statistic;

import sparql.cg.trie.InterNode;

/**
 * @author Michael
 *
 */
/***
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */

public class Pair<F, S> {
	/*public*/ /*final*/ F first;
	/*public*/ /*final*/ S second;

	/***
	 * Constructor for a Pair. If either are null then equals() and hashCode() will throw
	 * a NullPointerException.
	 * @param first the first object in the Pair
	 * @param second the second object in the pair
	 */
	
	public Pair() {
//		this.first = new F();
//		this.second = new S();
	}
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public void setFirst(F first) {
		this.first = first;
	}
	
	public F getFirst() {
		return first;
	}
	
	public String toString(){
		return this.first.toString() +"\t"+this.second.toString();
	}
	public void setSecond(S second) {
		this.second = second;
	}
	
	public S getSecond() {
		return second;
	}
	
	public void setValue(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/***
	 * Checks the two objects for equality by delegating to their respective equals() methods.
	 * @param o the Pair to which this one is to be checked for equality
	 * @return true if the underlying objects of the Pair are both considered equals()
	 */
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Pair)) return false;
		final Pair<F, S> other;
		try {
			other = (Pair<F, S>) o;
		} catch (ClassCastException e) {
			return false;
		}
		return first.equals(other.first) && second.equals(other.second);
	}

	/***
	 * Compute a hash code using the hash codes of the underlying objects
	 * @return a hashcode of the Pair
	 */
	
	public int hashCode() {
		int result = 17;
		result = 31 * result + first.hashCode();
		result = 31 * result + second.hashCode();
		return result;

	}

	/***
	 * Convenience method for creating an appropriately typed pair.
	 * @param a the first object in the Pair
	 * @param b the second object in the pair
	 * @return a Pair that is templatized with the types of a and b
	 */
	public static <A, B> Pair <A, B> create(A a, B b) {
		return new Pair<A, B>(a, b);
	}
}
