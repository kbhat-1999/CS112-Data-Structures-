package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		
		TrieNode root = new TrieNode(null, null, null); //parent node
		TrieNode ptr = root;
		if (allWords.length == 0) {
			return root;
		}
		else {
			//ptr.firstChild = new TrieNode(new Indexes(0, 0, allWords.length[0]()-1);
			Indexes index = new Indexes(0, (short)0, (short)0);
			TrieNode child = new TrieNode(index, null, null); //holds data for first item in allWords list
		}
		for (int i = 0; i < allWords.length; i++) {
			while (ptr!=null) {
				int wordIndex = ptr.substr.wordIndex;
				short startIndex = ptr.substr.startIndex;
				short endIndex = ptr.substr.endIndex;
			}
			if (startIndex > word.length) {
				ptr = ptr.sibling;
				continue;
			}
			int samePrefix = () //index up to which strings are similar
					
			//no match
				
			
		}
		
		return null;
		
	}
//	private static TrieNode insert (String[] allWords, TrieNode parent, int i, String word) {
//		
//		for (int j = 0; j < allWords.length; i++) {
//			while (ptr!=null) {
//				int wordIndex = ptr.substr.wordIndex;
//				short startIndex = ptr.substr.startIndex;
//				short endIndex = ptr.substr.endIndex;
//			}
//		}
//	}
	
	private static boolean samePrefix (String[] allWords, short startIndex, short endIndex) { //does any word in tree have the given prefix
		for (int i = 0; i < allWords.length ; i++) {
			char startingChar = allWords[i].charAt(0);
			startIndex = (short) startingChar;
			endIndex = 
		}
	}
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		
		if (root == null) return null;
		ArrayList<TrieNode> matchingLeafNode = new ArrayList<>();
		TrieNode ptr = root;
		while (ptr != null) {
			//get substring
			String word = allWords[ptr.substr.wordIndex];
			if (word.startsWith(prefix)){ 
				if (ptr.firstChild != null) {
					matchingLeafNode.addAll(completionList(ptr.firstChild, allWords, prefix));
					ptr = ptr.sibling;
				}
				else {
					matchingLeafNode.add(ptr);
					ptr = ptr.sibling;
				}
			}
			else {
				ptr = ptr.sibling;
			}
		}
		return matchingLeafNode;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
