
public class IEdge {
	public String label;
	public int oid;
	public INode father;
	public INode son;
	public OneIndex I;
	
	
	IEdge(OneIndex I, int oid, INode father, INode son,String label ){
		this.label = label;
		this.oid = oid;
		this.father= father;
		this.son = son;
		this.I = I;
	}
}
