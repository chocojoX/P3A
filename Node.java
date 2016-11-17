import java.util.ArrayList;
import java.util.List;


public class Node {
	public String value;
	private int oid;
	public ArrayList<Edge> outGoing;
	public ArrayList<Edge> inComing;
	public int nEdgeOut;
	
	Node(String name, int oid){
		this.value = name;
		this.oid = oid;
		this.outGoing = new ArrayList<Edge>();
		this.inComing = new ArrayList<Edge>();
		nEdgeOut= 0;
	}
	
	public int getOid(){
		return(this.oid);
	}
	
	public int hashCode(){
		return this.oid % 15000;
	}
}
