package friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		/** COMPLETE THIS METHOD **/
		
		Person[] allFriends = g.members;
		HashMap<Integer, ArrayList<Edge>> nodeToEdgeMap = new HashMap<Integer, ArrayList<Edge>>();
		String pName = "";
		for(int i=0; i<allFriends.length; i++) {
			Person p = allFriends[i];
			pName = p.name;
			Friend fr = p.first;
			int pNumber = g.map.get(pName).intValue();
			ArrayList<Edge> fList = createFriendsOfPerson(p, pNumber, fr);
			nodeToEdgeMap.put(Integer.valueOf(pNumber), fList);
		}
		Queue<Integer> personVisited = new Queue<Integer>();
		HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();
		ArrayList<String> mutualFriends = new ArrayList<String>();
		
		int start = g.map.get(p1);
		personVisited.enqueue(start);
		parents.put(start, -1);
		
		while(!personVisited.isEmpty()) {
			int personNumber = personVisited.dequeue();
			ArrayList<Edge> friends = nodeToEdgeMap.get(Integer.valueOf(personNumber));
			for(int i=0; i<friends.size(); i++) {
				String fName = "";
				Edge e = friends.get(i);
				int friendNumber = e.v2;
				if(parents.containsKey(Integer.valueOf(friendNumber))) {
					continue;
				}else {
					personVisited.enqueue(friendNumber);
					parents.put(friendNumber, personNumber);
					for(Map.Entry<String, Integer> entry :g.map.entrySet()) {
						if(entry.getValue().equals(Integer.valueOf(friendNumber))) {
							fName = entry.getKey();
							break;
						}
					}
					
					if(p2.equals(fName)) {
						//Processing the path from target -> source
						ArrayList<Integer> path = new ArrayList<Integer>();
					    while (friendNumber != -1) {
					    	for(Map.Entry<String, Integer> entry :g.map.entrySet()) {
								if(entry.getValue().equals(Integer.valueOf(friendNumber))) {
									fName = entry.getKey();
									mutualFriends.add(fName);
								}
							}
					      int parent = parents.get(friendNumber);
					      friendNumber = parent;
					    }
					    
					    // order from source -> target
					    ArrayList<String> shortDistance = new ArrayList<String>();
					    for (int j = mutualFriends.size() - 1; j >= 0; j--) { 
				            shortDistance.add(mutualFriends.get(j)); 
				        } 
					    return shortDistance;
					  }
					}
				}
			}
			return mutualFriends;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		/** COMPLETE THIS METHOD **/
		
		Person[] allFriends = g.members;
		HashMap<Integer, ArrayList<Edge>> nodeToEdgeMap = new HashMap<Integer, ArrayList<Edge>>();
		ArrayList<ArrayList<String>> finalCliques = new ArrayList<ArrayList<String>>();
		ArrayList<String> clique = new ArrayList<String>();
				int personIndex = -1;
				
		if(allFriends == null || allFriends.length == 0) {
					return finalCliques;
		}
		
		String pName = "";
		for(int i=0; i<allFriends.length; i++) {
			Person p = allFriends[i];
			pName = p.name;
			Friend fr = p.first;
			int pNumber = g.map.get(pName).intValue();
			ArrayList<Edge> fList = createFriendsOfPerson(p, pNumber, fr);
			nodeToEdgeMap.put(Integer.valueOf(pNumber), fList);
		}
		
		Queue<Integer> personVisited = new Queue<Integer>();
		//ArrayList<Integer> parents = new ArrayList<Integer>();
		HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();
		int currentParent = -1;
		
		for(Integer fNum: nodeToEdgeMap.keySet()) {
			if(!parents.containsKey(fNum)) {
				int start = fNum;
				personVisited.enqueue(start);
				parents.put(Integer.valueOf(start), -1);
				while(!personVisited.isEmpty()) {
					int personNumber = personVisited.dequeue();
					Person p = null;
					String perName = "";
					for(Map.Entry<String, Integer> entry :g.map.entrySet()) {
						if(entry.getValue().equals(Integer.valueOf(personNumber))) {
							perName = entry.getKey();
							break;
						}
					}
			
					for(int i=0; i<allFriends.length; i++){
						if(perName.equals(allFriends[i].name)) {
							p=allFriends[i];
							break;
						}
					}
					if(p.student && school.equals(p.school)){
						clique.add(perName);
					}else {
						if(parents.containsKey(personNumber) && parents.get(personNumber) != currentParent) {
							continue;
						}else {						
							if(clique.size() > 0) {
								finalCliques.add(clique);
								clique = new ArrayList<String>();
							}
							continue;
						}
					}
			
					ArrayList<Edge> friends = nodeToEdgeMap.get(Integer.valueOf(personNumber));
					currentParent = personNumber;
					for(int i=0; i<friends.size(); i++) {
						Edge e = friends.get(i);
						int friendNumber = e.v2;
						if(parents.containsKey(Integer.valueOf(friendNumber))) {
							continue;
						}else {
							personVisited.enqueue(friendNumber);
							parents.put(Integer.valueOf(friendNumber), personNumber);
						}
					}
				}
			}
		}
		if(clique.size() > 0) {
			finalCliques.add(clique);
		}
		return finalCliques;
		
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		/** COMPLETE THIS METHOD **/
		
		Person[] allFriends = g.members;
		HashMap<String, Integer> numNameMap = g.map;
		HashMap<Integer, ArrayList<Edge>> nodeToEdgeMap = new HashMap<Integer, ArrayList<Edge>>();
		ArrayList<Integer> connectors = new ArrayList<Integer>();
		ArrayList<String> connNames = new ArrayList<String>();

		String pName = "";
		int startingVertex = -1;
		
		for(int i=0; i<allFriends.length; i++) {
			Person p = allFriends[i];
			pName = p.name;
			Friend fr = p.first;
			int pNumber = numNameMap.get(pName).intValue();
			ArrayList<Edge> fList = createFriendsOfPerson(p, pNumber, fr);
			nodeToEdgeMap.put(Integer.valueOf(pNumber), fList);
		}
		
		HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		int counter = 0;
		HashMap<Integer, ArrayList<Integer>> vertexScoreMap = new HashMap<Integer, ArrayList<Integer>>();
		boolean first = true;

        for(Integer fNum: nodeToEdgeMap.keySet()){
        	if(!visited.contains(fNum)){
        		if(first) {
        			startingVertex = fNum;
        			parents.put(Integer.valueOf(fNum), -1);
        		}
        		first = false;
        		findConnectorsByDfs(fNum, nodeToEdgeMap, visited, counter, vertexScoreMap, parents, connectors);     
        	}
        }
        
        if(connectors.contains(Integer.valueOf(startingVertex)) && nodeToEdgeMap.get(startingVertex).size() == 1) {
        	connectors.remove(Integer.valueOf(startingVertex));
        }
        //System.out.println("==== connectors after: " + connectors);
        
        mapToConnNames(connectors, connNames, numNameMap);		
		return connNames;
		
	}
	
	
	private static void findConnectorsByDfs(Integer data, HashMap<Integer, ArrayList<Edge>> adjacencyMap, ArrayList<Integer> visited, int counter, HashMap<Integer, ArrayList<Integer>> vertexScoreMap, HashMap<Integer, Integer> parents, ArrayList<Integer> connectors){
		ArrayList<Integer> scores = new ArrayList<Integer>();
		if(!visited.contains(data)) {
			visited.add(data);
		}
		int dfsnum = ++counter;
		int back = dfsnum;
		scores.add(0, Integer.valueOf(dfsnum));
		scores.add(1, Integer.valueOf(back));
		if(vertexScoreMap.containsValue(scores)) {
			dfsnum = ++counter;
			back = dfsnum;
			scores.add(0, Integer.valueOf(dfsnum));
			scores.add(1, Integer.valueOf(back));
			vertexScoreMap.put(data, scores);
		}else {
			vertexScoreMap.put(data, scores);
		}

		ArrayList<Edge> neighbors = adjacencyMap.get(data);
    	for(int i=0;  i<neighbors.size(); i++){
    		Edge e = neighbors.get(i);
    		int neighbor = e.v2;
    		/*if(parents.containsKey(data)) {
    			int parentOfData = parents.get(data).intValue();
    			if(neighbor == parentOfData) {
    				 System.out.println("==== parent found continue: " + neighbor + "," + data);
    				continue;
    			}else {
    				parents.put(Integer.valueOf(neighbor), data);
    			}
    		}else {
    			parents.put(Integer.valueOf(neighbor), data);
    		}*/
    		if(!visited.contains(Integer.valueOf(neighbor))){
    			findConnectorsByDfs(Integer.valueOf(neighbor), adjacencyMap, visited, counter, vertexScoreMap, parents, connectors);
    			int dv = vertexScoreMap.get(e.v1).get(0);
    			int bv = vertexScoreMap.get(e.v1).get(1);
    			int dw = vertexScoreMap.get(neighbor).get(0);
    			int bw = vertexScoreMap.get(neighbor).get(1);
    		
    			if(dv > bw) {
    				bv = Math.min(bv, bw);
	    			vertexScoreMap.remove(Integer.valueOf(e.v1));
	    			scores = new ArrayList<Integer>();
	    			scores.add(0, Integer.valueOf(dv));
	    			scores.add(1, Integer.valueOf(bv));
	    			vertexScoreMap.put(e.v1, scores); 
    			}
    			if(dv <= bw) {
    				if(!connectors.contains(e.v1)) {
    					connectors.add(e.v1);
    				}
    			}
    			
    		}
    		else{
    	    			int dw = vertexScoreMap.get(neighbor).get(0);
    	    			int bw = vertexScoreMap.get(neighbor).get(1);
    	    			int dv = vertexScoreMap.get(e.v1).get(0);
    	    			int bv = vertexScoreMap.get(e.v1).get(1);
    	    			bv = Math.min(bv, dw);
    	    			vertexScoreMap.remove(Integer.valueOf(e.v1));
    	    			scores = new ArrayList<Integer>();
    	    			scores.add(0, Integer.valueOf(dv));
    	    			scores.add(1, Integer.valueOf(bv));
    	    			vertexScoreMap.put(e.v1, scores);   	    			
    		}
    	
        }
    	return;
}
	
private static ArrayList<Edge> createFriendsOfPerson(Person p, int pNum, Friend fr){
	ArrayList<Edge> edgeList = new ArrayList<Edge>();
	Queue<Friend> friends = new Queue<Friend>();
	friends.enqueue(fr);
	Edge edge;		
	while (!friends.isEmpty()) {
		Friend f = friends.dequeue();
		int fn = f.fnum;
		edge = new Edge(pNum, fn);
		edgeList.add(edge);
		if(f.next != null) {
			friends.enqueue(f.next);
		}
	}
	return edgeList;
}

private static void mapToConnNames(ArrayList<Integer> connectors, ArrayList<String> connNames, HashMap<String, Integer> numNameMap) {
	for(Integer i :connectors) {
		for(Map.Entry<String, Integer> entry :numNameMap.entrySet()) {
			if(i == entry.getValue()) {
				connNames.add(entry.getKey());
			}
		}
	}
}
}

