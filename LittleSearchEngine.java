package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/		
		HashMap<String, Occurrence> keywordMap = new HashMap<String, Occurrence>();
		Map<String, Integer> wordRepetitionMap = new HashMap<String, Integer>();
		
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			String currWord = getKeyword(sc.next());
			
			if(currWord != null) {
				if(wordRepetitionMap.containsKey(currWord)) {
					wordRepetitionMap.put(currWord, wordRepetitionMap.get(currWord)+1);
				}else {
					wordRepetitionMap.put(currWord, 1);
				}
			}
		}
		sc.close();
		for(Map.Entry<String, Integer> entry :wordRepetitionMap.entrySet()) {
			Occurrence occur = new Occurrence(docFile,entry.getValue().intValue());
			keywordMap.put(entry.getKey(), occur);
		}
		return keywordMap;
		
	}
	

	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for(Map.Entry<String, Occurrence> entry :kws.entrySet()) {
			String keyWord = entry.getKey();
			Occurrence occurrence = entry.getValue();
			//check if its already in the master table
			ArrayList<Occurrence> tableEntries = keywordsIndex.get(keyWord);
			
			//Does not have an entry in master table
			if(tableEntries == null || tableEntries.size() == 0) {
				tableEntries = new ArrayList<Occurrence>();
				tableEntries.add(occurrence);
				keywordsIndex.put(keyWord, tableEntries);
				continue;
			}
			
			/** 
			 * If the keyword already has entries in the master table, then these will be already
			 * in the descending order of frequencies in occurrence object. So find the right spot for
			 * the frequency in the current instance of occurrence.
			 */
			tableEntries.add(occurrence);
			ArrayList<Integer> indexList = insertLastOccurrence(tableEntries);
			System.out.println("=== Indexes are : ===" + indexList.toString());
			/*for(Occurrence occr :tableEntries) {
				System.out.println("Descending order of frequencies: ===" + occr.frequency);
			}*/
			keywordsIndex.put(keyWord, tableEntries);
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		String specialCharacters = "`~@#$%^&*()-_+=}]|\\{['<>/\"1234567890";
		String lowercase = word.toLowerCase();
		if (lowercase.contains(specialCharacters)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		boolean puncFound = false;
		for (int i = 0; i < lowercase.length(); i++) {
			char c = lowercase.charAt(i);
			if (Character.isAlphabetic(c)) {
				if (!puncFound) {
					sb = sb.append(Character.toString(c));
				}
				else {
					return null;
				}
			}
			else {
				if (!puncFound) {
					puncFound = true;
				}
			}
		}
		lowercase = sb.toString();
		if (!noiseWords.contains(lowercase)) {
			return lowercase;
		}
		return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		Occurrence target = occs.remove(occs.size()-1);
		//System.out.println("=== occs.size() start is : ===" + occs.size());
		//System.out.println("=== occs is : ===" + occs.toString());
		//System.out.println("=== target is : ===" + target.document + "--" + target.frequency);
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		int index = findIndexesAndInsert (occs, target, indexList);
		System.out.println("=== Index for inser is : ===" + index);
		occs.add(index, target);
		//occs.addAll(index+1,temp);
		//System.out.println("=== occs contents : ===" + occs.toString());
		return indexList;
	}
	
	private static int findIndexesAndInsert (ArrayList<Occurrence> occs, Occurrence key, ArrayList<Integer> indexList) {
		int low = 0;
	    int high = occs.size() - 1;
	    
		//System.out.println("=== left is : ===" + low);
		//System.out.println("=== right is : ===" + high);
		
	    while (high >= low) {
	        int middle = (low + high) / 2;
	        indexList.add(Integer.valueOf(middle));
	        if (occs.get(middle).frequency < key.frequency) {
	            high = middle - 1;
	        } else {
	            low = middle + 1;
	        }
	    }
	    return low;
		/*if (left > right) 
        {
          return -1;
        }

        int mid = (left + right) / 2;
    	System.out.println("=== middle is : ===" + mid);

        if (occs.get(mid).frequency == key.frequency) 
        {
          return mid;
        }
        if (occs.get(mid).frequency > key.frequency) 
        {
          return findIndexesAndInsert (occs, mid+1, right, key,indexList);
        }
        return findIndexesAndInsert (occs, left, mid-1, key, indexList);*/
	}
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<String> resultList = new ArrayList<String>();
		ArrayList<Occurrence> kwList1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> kwList2 = keywordsIndex.get(kw2);
		
		/*if(kwList1.size() > 5) {
			kwList1 = (ArrayList)kwList1.subList(0, 5);
		}
		
		if(kwList2.size() > 5) {
			kwList1 = (ArrayList)kwList2.subList(0, 5);
		}*/
		
		if(kwList2 == null || kwList2.size() == 0) {
			if(kwList1 != null && kwList1.size() > 0) {
					resultList = populateTop5FromList(kwList1);
					return resultList;
			}else {
				return null;
			}
		}
		
		if(kwList1 == null || kwList1.size() == 0) {
			if(kwList2 != null && kwList2.size() > 0) {
					resultList = populateTop5FromList(kwList2);
					return resultList;
			}else {
				return null;
			}
		}
		
		applyRulesToDuplicates(kwList1, kwList2);
		
		//System.out.println("== List1 after removal ===" + kwList1.toString());
		//System.out.println("== List2 after removal ===" + kwList2.toString());
		
		int i, j; 
		for (i = 0, j = 0; i < kwList1.size() && j < kwList2.size();) {
			if(resultList.size() == 5)break;
			//if(!(kwList1.get(i).document.equals(kwList2.get(j).document))) {
				if (kwList1.get(i).frequency >= kwList2.get(j).frequency) {
					resultList.add(kwList1.get(i).document);
					i++;
				} else{
					resultList.add(kwList2.get(j).document);
					j++;
				}
			//}
		}

		while(i < kwList1.size()) {
			if(resultList.size() == 5) break;
			resultList.add(kwList1.get(i++).document);
		}
		while(j < kwList2.size()) {
			if(resultList.size() == 5) break;
			resultList.add(kwList2.get(j++).document);
		}

		return resultList;
	}
	private static ArrayList<String> populateTop5FromList(ArrayList<Occurrence> occrList){
		ArrayList<String> finalList = new ArrayList<String>();
		int maxSize = 5;
		
		for(Occurrence occr :occrList) {
			if(finalList.size() == 5) {
				break;
			}
			finalList.add(occr.document);
		}
		return finalList;
	}
	
	private static void applyRulesToDuplicates(ArrayList<Occurrence> kwList1, ArrayList<Occurrence> kwList2){
		//Remove duplicates
		List<Occurrence> dupsToRemove = new ArrayList<Occurrence>();
		for(int i=0; i<kwList1.size(); i++) {
			for(int j=0; j<kwList2.size(); j++) {
				if(kwList1.get(i).document.equals(kwList2.get(j).document)) {
					if(kwList1.get(i).frequency < kwList2.get(j).frequency) {
						dupsToRemove.add(kwList1.get(i));
					}else {
						dupsToRemove.add(kwList2.get(j));
					}
				}
			}
		}
		
		//System.out.println("==Duplicates to remove ===" + dupsToRemove);
		
		for(Occurrence occr :dupsToRemove) {
			boolean foundInList1 = false;
			boolean foundInList2 = false;
			if(kwList1.contains(occr)) {
				foundInList1 = true;
				//kwList1.remove(occr);
			}
			if(kwList2.contains(occr)){
				foundInList2 = true;
				//kwList2.remove(occr);
			}
			if(foundInList1 && foundInList2) {
				kwList2.remove(occr);
			}else if(foundInList1) {
				kwList1.remove(occr);
			}else if(foundInList2) {
				kwList2.remove(occr);
			}
		}
	}

}



