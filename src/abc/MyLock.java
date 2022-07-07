package abc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.*;
public class MyLock {
	
	private ConcurrentHashMap<String,HashSet<String>> threadLockedChildListMap;
	private ConcurrentHashMap<String,Integer> threadLockMap;
	private HashMap<String,String> parentMap;
	MyLock(ConcurrentHashMap<String,HashSet<String>> threadLockedChildListMap,ConcurrentHashMap<String,Integer> threadLockMap,HashMap<String,String> parentMap){
		this.threadLockMap = threadLockMap;
		this.threadLockedChildListMap = threadLockedChildListMap;
		this.parentMap = parentMap;
	}
	

	public synchronized void lockThread(String name) {
		while(threadLockMap.containsKey(name) || threadLockedChildListMap.get(name).size()>0) {
			try {
				wait();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String parent= new String(parentMap.get(name));
		while(parent.charAt(0)!='1') {
			if(threadLockMap.containsKey(parent)) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			parent= new String(parentMap.get(parent));
		}
		
		try {

			threadLockMap.put(name, 1);
			parent= parentMap.get(name);
			while(parent.charAt(0)!='1') {
				threadLockedChildListMap.get(parent).add(name);
				parent= parentMap.get(parent);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		/*
		System.out.println();
		System.out.println(this.threadLockedChildListMap);
		System.out.println(this.threadLockMap);
		System.out.println();
		*/
		
		
	}
	
	public synchronized void unlockThread(String name) {
		threadLockMap.remove(name);
		String parent= parentMap.get(name);
		while(parent.charAt(0)!='1') {
			threadLockedChildListMap.get(parent).remove(name);
			parent= parentMap.get(parent);
		}
		notify();
		/*
		System.out.println();
		System.out.println(this.threadLockedChildListMap);
		System.out.println(this.threadLockMap);
		System.out.println();
		*/
		
		
	}
}
