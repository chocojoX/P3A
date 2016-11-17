
public class Edge {
	public String label;
	public int oid;
	public Node father;
	public Node son;
	
	Edge(String name, int oid, Node father, Node Son){
		this.label=name;
		this.oid=oid;
		this.father=father;
		this.son=son;
	}
	
	
}
