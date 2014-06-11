/**
 * 
 */
package sparql.cg.trie;

import java.nio.ByteBuffer;

import sparql.cg.cache.Block;
import sparql.cg.cache.BlockHeader;

/**
 * @author C_G
 *
 */
public class LeafNode extends Block {
//	public static int TYPE = 0;
	private long value;
	public LeafNode() {
		this.TYPE = 0;
	}
	public LeafNode(BlockHeader header) {
		super(header);
	}
	public LeafNode(long value) {
		this.value = value;
	}
	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	@Override
	public void serialize(ByteBuffer buffer) {
		buffer.putLong(value);
	}

	@Override
	public void deserialize(ByteBuffer buffer) {
		value = buffer.getLong();
	}

	@Override
	public int length() {
		return BlockHeader.SIZE + Long.SIZE / 8;
	}

	@Override
	public int type() {
		return TYPE;
	}
	public String toString() {
		return "Value: " + value +";" +super.toString();
	}
}
