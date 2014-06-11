/**
 * 
 */
package sparql.cg.statistic;

import java.nio.ByteBuffer;


import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author Michael
 * 
 */
public class StringUtil {
	public static char[] bytesToChars(byte[] bytes) {
		String s = new String(bytes);
		char chars[] = s.toCharArray();
		/*
		 * String s1 = String.copyValueOf(chars); char ochars[] =
		 * s1.toCharArray(); return ochars;
		 */
		return chars;
	}

	public static byte[] getBytes(char[] chars) {
		// Charset cs = Charset.forName ("UTF-8");US-ASCII
		Charset cs = Charset.forName("US-ASCII");
		CharBuffer cb = CharBuffer.allocate(chars.length);
		cb.put(chars);
		cb.flip();
		ByteBuffer bb = cs.encode(cb);

		return bb.array();
	}

	public static char[] getChars(byte[] bytes) {
		// Charset cs = Charset.forName ("UTF-8");US-ASCII;
		Charset cs = Charset.forName("US-ASCII");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	public static byte[] charsToBytes(char[] chars) {
		String s = new String(chars);
		byte bytes[] = s.getBytes();
		return bytes;
	}

	public static String getSubString(char[] chars, int start, int end) {
		char[] ch = new char[end - start];
		for (int i = start; i < end; i++) {
			ch[i - start] = chars[i];
		}
		return ch.toString();
	}

	public static byte[] long2Bytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}

	public static long bytes2Long(byte[] byteNum,int strStart) {
		long num = 0;
		for (int ix = 0; ix < 8; ++ix) {
			num <<= 8;
			num |= (byteNum[strStart+ix] & 0xff);
		}
		return num;
	}

	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static int byteToInt(byte[] b,int strStart) {
		int s = 0;
		int s0 = b[strStart+0] & 0xff;// 最低位
		int s1 = b[strStart+1] & 0xff;
		int s2 = b[strStart+2] & 0xff;
		int s3 = b[strStart+3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}

	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();//
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static short byteToShort(byte[] b,int strStart) {
		short s = 0;
		short s0 = (short) (b[strStart] & 0xff);// 最低位
		short s1 = (short) (b[strStart+1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

}
