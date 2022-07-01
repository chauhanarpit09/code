package abc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Space {

	class Node {
		String nodeName;
		int user;
		Node parent;
		int desCount;
		boolean isLocked;
		ArrayList<Node> children;
		
		public Node(String nodeName) {
			this.nodeName = nodeName;
			this.user = -1;
			this.parent = null;
			this.isLocked  = false;
			this.desCount = 0;
			this.children = new ArrayList<Node>();
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
		if(currNode.desCount>0) return false;
		
		for(Node p = currNode.parent;p!=null;p=p.parent) {
			if(p.isLocked) return false;
		}
		
		for(Node p = currNode.parent;p!=null;p=p.parent) {
			p.desCount++;
		}
		currNode.isLocked = true;
		currNode.user = userId;
		return true;
	}
	
	public boolean unlock(String node,int userId) {
		Node currNode = nameToNodeMap.get(node);
		if(!currNode.isLocked) return false;
		
		for(Node p = currNode.parent;p!=null;p=p.parent) {
			p.desCount--;
		}
		currNode.isLocked = false;
		currNode.user = -1;
		return true;
	}
	
	public boolean upgrade(String node,int userId) {
		Node currNode = nameToNodeMap.get(node);
		if(currNode.isLocked) return false;
		System.out.println(currNode.desCount);
		if(currNode.desCount==0) return false;
		System.out.println("2");
		if(!check(currNode,userId)) return false;	
		System.out.println(check(currNode,userId));
		
		unlockAllChild(currNode,userId);
		
		currNode.isLocked = true;
		currNode.user = userId;
		return true;
	}
	private void unlockAllChild(Node root,int userId) {
		if(root==null) return;
		for(Node temp: root.children) {
			unlockAllChild(temp,userId);
		}
		if(root.isLocked) {
			unlock(root.nodeName,userId);
		}
	}
	private boolean check(Node root,int userId) {
		if(root==null) return true;
		if(root.isLocked && root.user != userId) {
			return false;
		}
		for(Node temp: root.children) {
			if(check(temp,userId)==false) return false;
		}
		return true;
	}
	public void inorder(Node root) {
		if(root==null) return;
		System.out.println(root.nodeName);
		for(Node temp :  root.children) {
			inorder(temp);
		}
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Space ob = new Space();
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
		ob.inorder(root);
		
		
	}
}

/*
 
7 2 5
world asia africa china india sa egypt
1 china 9
1 india 9
3 asia 9
 
 * */
