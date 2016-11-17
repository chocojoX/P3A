import java.util.Hashtable;


public class Tree {
	public String name;
	public Node root;
	public int nextOidNode;
	public int nextOidEdge;
	public Hashtable<Integer, Node> hashTable;
	
	Tree(String name){
		this.name=name;
		this.root = new Node("root", 0);
		this.hashTable = new Hashtable<Integer, Node>();
		this.hashTable.put(0, this.root);
		this.nextOidEdge = 1;
		this.nextOidNode =1;
	}
	
	public Node getRoot(){
		return(this.root);
	}
	
	public void addNode(String name){
		Node n = new Node(name, nextOidNode);
		this.hashTable.put(nextOidNode, n);
		this.nextOidNode+=1;
		
	}
}
