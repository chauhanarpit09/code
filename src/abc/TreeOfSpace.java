package abc;
import java.util.*;

import abc.Space.Node;
public class TreeOfSpace {
	class Node {
		String nodeName;
		int user;
		Node parent;
		boolean isdescendantLocked;
		boolean isLocked;
		ArrayList<Node> children;
		HashMap<String,Integer> lockedNodesMap;
		
		public Node(String nodeName) {
			this.nodeName = nodeName;
			this.user = -1;
			this.parent = null;
			this.isLocked  = false;
			this.isdescendantLocked = false;
			this.children = new ArrayList<Node>();
			this.lockedNodesMap = new HashMap<>();
		}
	}
	HashMap<String,Node> nameToNodeMap = new HashMap<>();
	public Node buildTree(String[] nodes,int k,int n) {
		
		Queue<Node> q = new LinkedList<>();
		Node root = new Node(nodes[0]);
		q.add(root);
		nameToNodeMap.put(root.nodeName,root);
		int i=1;
		while(i<n) {
			Node temp = q.poll();
			int tk = k;
			while(tk-->0) {
				Node tempNode = new Node(nodes[i++]);
				tempNode.parent = temp;
				temp.children.add(tempNode);
				q.add(tempNode);
				nameToNodeMap.put(tempNode.nodeName,tempNode);
			}
		}
		return root;
	}
	public boolean lock(String node,int userId) {
		Node currNode = nameToNodeMap.get(node);
		if(currNode.isLocked) return false;
		if(currNode.isdescendantLocked) return false;
		
		for(Node p = currNode.parent;p!=null;p=p.parent) {
			if(p.isLocked) return false;
		}
		
		for(Node p = currNode.parent;p!=null;p=p.parent) {
			p.isdescendantLocked = true;
			p.lockedNodesMap.put(node,userId);
		}
		
		currNode.isLocked = true;
		currNode.user = userId;
		return true;
	}

	public boolean unlock(String node,int userId) {
		Node currNode = nameToNodeMap.get(node);
		if(!currNode.isLocked) return false;
		if(currNode.user != userId) return false;
		for(Node p = currNode.parent;p!=null;p=p.parent) {
			p.isdescendantLocked = false;
			p.lockedNodesMap.remove(node);
		}
		currNode.isLocked = false;
		currNode.user = -1;
		return true;
	}
	
	public boolean upgrade(String node,int userId) {
		Node currNode = nameToNodeMap.get(node);
		if(currNode.isLocked) return false;
		if(!currNode.isdescendantLocked) return false;
		if(!check(currNode,userId)) return false;	
		unlockAllChild(currNode);
		currNode.lockedNodesMap.clear();
		currNode.isLocked = true;
		currNode.user = userId;
		return true;
	}
	
	private boolean check(Node root,int userId) {
		for(String key : root.lockedNodesMap.keySet()) {
			if(root.lockedNodesMap.get(key)!=userId) return false;
		}
		return true;
	}
	
	private void unlockAllChild(Node root) {
		for(String key : root.lockedNodesMap.keySet()) {
			Node currNode = nameToNodeMap.get(key);
			currNode.isLocked = false;
		}
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		TreeOfSpace ob = new TreeOfSpace();
		int n = sc.nextInt();
		int k = sc.nextInt();
		int q = sc.nextInt();
		String[] nodes = new String[n];
		
		for(int i=0;i<n;i++) {
			nodes[i] = sc.next();
		}
		
		Node root = ob.buildTree(nodes,k,n);
		for(int i=0;i<=q;i++) {
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


/*
 
7
2
5
World
Asia
Africa
India
China
SA
Egypt
1 China 9
1 India 9
3 Asia 9
2 India 9
2 Asia 9
 
 * */
 