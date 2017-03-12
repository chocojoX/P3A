import java.util.ArrayList;
import java.util.List;


public class Node {
	public String value;
	private int oid;
	public ArrayList<Edge> outGoing;
	public ArrayList<Edge> inComing;
	public int nEdgeOut;
	public String label;
	
	Node(String name, int oid){
		this.value = name;
		this.oid = oid;
		this.outGoing = new ArrayList<Edge>();
		this.inComing = new ArrayList<Edge>();
		nEdgeOut= 0;
		this.label = "Unknown";
	}
	
	Node(String name, int oid, String label){
		this.value = name;
		this.oid = oid;
		this.outGoing = new ArrayList<Edge>();
		this.inComing = new ArrayList<Edge>();
		nEdgeOut= 0;
		this.label = label;
	}
	
	public void addEdgeFromNode(Edge e){
		this.outGoing.add(e);
		this.nEdgeOut +=1;
	}
	
	public void addEdgeToNode(Edge e){
		this.inComing.add(e);
	}
	
	
	public int getOid(){
		return(this.oid);
	}
	
	public int hashCode(){
		return this.oid % 15000;
	}
}
