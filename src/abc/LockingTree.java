package abc;
import java.util.*;

import abc.Space.Node;
public class LockingTree {

	    int k,n,q;
		HashMap<String, Integer> indexMap = new HashMap<>();
		String[] nodes;
		HashMap<String,Integer> isLockedMap = new HashMap<>();
		HashMap<String,HashSet<String>> lockedChildListMap = new HashMap<>();
	    
	    
		private int getParent(int i) {
			if(i==0) return -1;
			return (i-1)/k;
		}
		

	    public boolean lock(String name, int userId) {
	    	//checking for locking
	    	if(isLockedMap.containsKey(name)) return false;
	    	if(lockedChildListMap.get(name).size()>0) return false;
	    	int i = indexMap.get(name);
	    	while(i!=-1) {
	    		if(isLockedMap.containsKey(nodes[i])) return false;
	    		i = getParent(i);
	    	}
	    	
	    	//locking starts
	    	i = getParent(indexMap.get(name));
	    	while(i!=-1) {
	    		lockedChildListMap.get(nodes[i]).add(name);
	    		i = getParent(i);
	    	}
	    	
	    	isLockedMap.put(name, userId);
	    	return true;
	    }
	    
	  
	    public boolean unlock(String name, int userId) {
	    	//checking
	    	if(!isLockedMap.containsKey(name)) return false;
	    	if(isLockedMap.get(name)!=userId) return false;
	    	
	    	//unlock starts
	    	isLockedMap.remove(name);
	    	int i = getParent(indexMap.get(name));
	    	while(i!=-1) {
	    		lockedChildListMap.get(nodes[i]).remove(name);
	    		i = getParent(i);
	    	}
	        return true;
	    }
	    
	    
	    private boolean unlockForUpgrade(String name, int userId,int currInd) {
	    	//checking
	    	if(!isLockedMap.containsKey(name)) return false;
	    	if(isLockedMap.get(name)!=userId) return false;
	    	
	    	//unlock starts
	    	isLockedMap.remove(name);
	    	int i = getParent(indexMap.get(name));
	    	while(i!=-1) {
	    		if(i!=currInd) {
	    			lockedChildListMap.get(nodes[i]).remove(name);
	    		}
	    		i = getParent(i);
	    	}
	        return true;
	    }

	    
	    public boolean upgrade(String name, int userId) {   
	    	if(isLockedMap.containsKey(name)) return false;
	    	if(lockedChildListMap.get(name).size()==0) return false;
	    	for(String s: lockedChildListMap.get(name)) {
	    		if(isLockedMap.get(s) != userId) return false;
	    	}
	    	
	    	for(String s: lockedChildListMap.get(name)) {
	    		unlockForUpgrade(s,userId,indexMap.get(name));
	    	}
	    	lockedChildListMap.get(name).clear();
	    	isLockedMap.put(name, userId);
	    	return true;
	    }
	        
	      
	    
	   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		LockingTree ob = new LockingTree();
		ob.n = sc.nextInt();
		ob.k = sc.nextInt();
		ob.q = sc.nextInt();
		ob.nodes = new String[ob.n];
		
		for(int i=0;i<ob.n;i++) {
			ob.nodes[i] = sc.next();
			ob.indexMap.put(ob.nodes[i],i);
			ob.lockedChildListMap.put(ob.nodes[i],new HashSet<String>());
		}
		
		for(int i=0;i<=ob.q;i++) {
			String c = sc.nextLine();
			if(i==0) continue;
			String[] query = c.split(" ");
			
			int op = Integer.parseInt(query[0]);
			String name = query[1];
			int userId = Integer.parseInt(query[2]);
			
			switch(op) {
			case 1: System.out.println(ob.lock(name, userId));
					break;
			case 2: System.out.println(ob.unlock(name, userId));
					break;
			case 3: System.out.println(ob.upgrade(name, userId));
					break;
			}
		}

	}

}


