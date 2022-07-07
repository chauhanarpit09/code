package abc;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import abc.Space.Node;
public class LockingTree {

	    int k;
		HashMap<String, Integer> indexMap;
		String[] nodes;
		HashMap<String,Integer> isLockedMap;
		HashMap<String,HashSet<String>> lockedChildListMap;
		MyLock lock;
		
	    public LockingTree(
	    		HashMap<String, Integer> indexMap,
	    		HashMap<String,Integer> isLockedMap,
	    		HashMap<String,HashSet<String>> lockedChildListMap,
	    		String[] nodes,
	    		int k,
	    		ConcurrentHashMap<String,HashSet<String>> threadLockedChildListMap,
	    		ConcurrentHashMap<String,Integer> threadLockMap,
	    		HashMap<String,String> parentMap) {
	    	
	    	this.indexMap = indexMap;
	    	this.isLockedMap = isLockedMap;
	    	this.lockedChildListMap = lockedChildListMap;
	    	this.nodes = nodes;
	    	this.k = k;
	    	this.lock = new MyLock(threadLockedChildListMap,threadLockMap,parentMap);
	    }
	    
		private int getParent(int i) {
			if(i==0) return -1;
			return (i-1)/k;
		}
		

	    public boolean lock(String name, int userId) {
	    	//checking for locking
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  Trying  \n");
	    	lock.lockThread(name);
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  started  \n");
	    	try {
	    		if(isLockedMap.containsKey(name)) {
	    			//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    			lock.unlockThread(name);
	    			return false;
	    		}
		    	if(lockedChildListMap.get(name).size()>0) {
		    		//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
		    		lock.unlockThread(name);
		    		return false;
		    	}
		    	int i = indexMap.get(name);
		    	while(i!=-1) {
		    		if(isLockedMap.containsKey(nodes[i])) {
		    			//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
		    			lock.unlockThread(name);
		    			return false;
		    		}
		    		i = getParent(i);
		    	}
		    	
		    	//locking starts
		    	i = getParent(indexMap.get(name));
		    	while(i!=-1) {
		    		lockedChildListMap.get(nodes[i]).add(name);
		    		i = getParent(i);
		    	}
		    	
		    	isLockedMap.put(name, userId);
		    	lock.unlockThread(name);
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  stoped  \n");
	    	return true;
	    }
	    
	  
	    public boolean unlock(String name, int userId) {
	    	//checking
	    	////System.out.println("--------->"+Thread.currentThread().getName()+"  Trying  \n");
	    	lock.lockThread(name);
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  Started  \n");
	    	try {
	    		if(!isLockedMap.containsKey(name)) {
	    			//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    			lock.unlockThread(name);
	    			return false;
	    		}
		    	if(isLockedMap.get(name)!=userId) {
		    		//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
		    		lock.unlockThread(name);
		    		return false;
		    	}
		    	
		    	//unlock starts
		    	isLockedMap.remove(name);
		    	int i = getParent(indexMap.get(name));
		    	while(i!=-1) {
		    		lockedChildListMap.get(nodes[i]).remove(name);
		    		i = getParent(i);
		    	}
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    	lock.unlockThread(name);
	        return true;
	    }
	    
	    public boolean unlockForUpgrade(String name, int userId,int currIndForUpgrade) {
		    isLockedMap.remove(name);
		    int i = getParent(indexMap.get(name));
		    while(i!=-1) {
		    	if(i!=currIndForUpgrade) {
		    		lockedChildListMap.get(nodes[i]).remove(name);
		    	}
		    	i = getParent(i);
		    }
	        return true;
	    }

	    
	    public boolean upgrade(String name, int userId) { 
	    	lock.lockThread(name);
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  Started  \n");
	    	if(isLockedMap.containsKey(name)) {
	    		//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    		lock.unlockThread(name);
	    		return false;
	    	}
	    	if(lockedChildListMap.get(name).size()==0) {
	    		//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    		lock.unlockThread(name);
	    		return false;
	    	}
	    	for(String s: lockedChildListMap.get(name)) {
	    		if(isLockedMap.get(s) != userId) {
	    			//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    			lock.unlockThread(name);
	    			return false;
	    		}
	    	}
	    	
	    	for(String s: lockedChildListMap.get(name)) {
	    		unlockForUpgrade(s,userId,indexMap.get(name));
	    	}
	    	lockedChildListMap.get(name).clear();
	    	isLockedMap.put(name, userId);
	    	//System.out.println("--------->"+Thread.currentThread().getName()+"  Stoped  \n");
	    	lock.unlockThread(name);
	    	return true;
	    }
	   

}


