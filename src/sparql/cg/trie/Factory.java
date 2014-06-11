/**
 * 
 */
package sparql.cg.trie;

import sparql.cg.cache.Block;
import sparql.cg.cache.BlockHeader;

/**
 * @author C_G
 *
 */
public class Factory {
	public static Block createNode(BlockHeader header) {
		if (header.headType() == 0/*LeafNode.TYPE*/)
			return new LeafNode(header);
		if (header.headType() == 1/*InterNode.TYPE*/)
			return new InterNode(header);
		return null;
	}
}
