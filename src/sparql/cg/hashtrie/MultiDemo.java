/**
 * 
 */
package sparql.cg.hashtrie;

import sparql.cg.trie.Config;


/**
 * @author c_g
 *
 */
public class MultiDemo {
	public MultiDemo(){
		
	}
//	public MultiDemo(int size,int bsize){
//		
//	}
	
	public void start(String file,String output){
		String path = "/home/hadoop/testjar";
		Config conf = new Config();
		conf.set("InputFile", path + "/University2.txt");
		conf.set("BufferSize", 10000);//Add Buffer
		conf.set("CacheSize", 10000);//LRU buffer
		conf.set("HashThread", 32);
		conf.set("TrieThread", 32);
		conf.set("IndexPath", path + "/index");//The directory of index
		conf.set("TrieMode", 0);// 0 is insert and 1 is search
		conf.set("JobName", "Job");
		conf.set("TrieQueueSize", 10000);
		conf.set("HashQueueSize", 10000);
		conf.set("TrieListSize", 100);
		conf.set("HashListSize", 100);
//		Job.startJob(conf, new Hash<String>() {
//			public int hash(String key) {
//				if (key.startsWith("a"))
//					return 0;
//				return 1;
//			}
//		});
		Job.startJob(conf,MultiHash.instance(path+"/"+output));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "/home/hadoop/testjar";
		Config conf = new Config();
		conf.set("InputFile", path + "/University.nt1");
		conf.set("BufferSize", 10000);//Add Buffer
		conf.set("CacheSize", 10000);//LRU buffer
		conf.set("HashThread", 32);
		conf.set("TrieThread", 32);
		conf.set("IndexPath", path + "/index");//The directory of index
		conf.set("TrieMode", 0);// 0 is insert and 1 is search
		conf.set("JobName", "Job");
		conf.set("TrieQueueSize", 10000);
		conf.set("HashQueueSize", 10000);
		conf.set("TrieListSize", 100);
		conf.set("HashListSize", 100);
//		Job.startJob(conf, new Hash<String>() {
//			public int hash(String key) {
//				if (key.startsWith("a"))
//					return 0;
//				return 1;
//			}
//		});
		Job.startJob(conf,MultiHash.instance(path+"/outhash-8-102400-8.log"));
	} 
	
	
}
