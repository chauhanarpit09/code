package abc;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


class MyRunnable implements Runnable {
	LockingTree ob;
	int op;
	String name;
	int userId;
	public MyRunnable(LockingTree ob, int op,String name,int userId) {
    	this.ob = ob;
    	this.op = op;
    	this.name = name;
    	this.userId = userId;
    }
	
	public void run() {
		Thread.currentThread().setName(this.op+" "+this.name+" "+this.userId+" --> ");
		switch(op) {
		case 1: System.out.println(Thread.currentThread().getName()+ob.lock(name, userId)); break;
		case 2: System.out.println(Thread.currentThread().getName()+ob.unlock(name, userId)); break;
		case 3: System.out.println(Thread.currentThread().getName()+ob.upgrade(name, userId)); break;
		}	
	}
}
public class ThreadSafeLocking {

	private static int getParent(int i,int k) {
		if(i==0) return -1;
		return (i-1)/k;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		HashMap<String, Integer> indexMap = new HashMap<>();
		HashMap<String,Integer> isLockedMap = new HashMap<>();
		HashMap<String,HashSet<String>> lockedChildListMap = new HashMap<>();
		
		ConcurrentHashMap<String,HashSet<String>> threadLockedChildListMap = new ConcurrentHashMap<>();
		ConcurrentHashMap<String,Integer> threadLockMap = new ConcurrentHashMap<>();
		HashMap<String,String> parentMap = new HashMap<>();
		int n = sc.nextInt();
		int k = sc.nextInt();
		int q = sc.nextInt();
		String[] nodes = new String[n];
		for(int i=0;i<n;i++) {
			nodes[i] = sc.next();
			indexMap.put(nodes[i],i);
			lockedChildListMap.put(nodes[i],new HashSet<String>());
			threadLockedChildListMap.put(nodes[i],new HashSet<String>());
			parentMap.put(nodes[i],"1");
		}
		
		for(int i=1;i<n;i++) {
			if(i==-1) continue;
			parentMap.put(nodes[i], nodes[getParent(i,k)]);
		}

		LockingTree ob = new LockingTree(
				indexMap,
				isLockedMap,
				lockedChildListMap,
				nodes,
				k, 
				threadLockedChildListMap,
				threadLockMap,
				parentMap);
		MyRunnable[] jobs = new MyRunnable[q];
		
		for(int i=0;i<=q;i++) {
			String c = sc.nextLine();
			if(i==0) continue;
			String[] query = c.split(" ");
			
			int op = Integer.parseInt(query[0]);
			String name = query[1];
			int userId = Integer.parseInt(query[2]);
			
			MyRunnable runable = new MyRunnable(ob,op,name,userId);
			jobs[i-1] = runable;
		}
		ExecutorService service = Executors.newFixedThreadPool(5);
		for(MyRunnable job: jobs) {
			service.submit(job);
		}
		service.shutdown();
	}

}
