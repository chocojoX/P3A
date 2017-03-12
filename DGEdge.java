
public class DGEdge {
	public String label;
	public int oid;
	public DGNode father;
	public DGNode son;
	public DataGuide dg;
	
	DGEdge(DataGuide dg, String label, int oid, DGNode father, DGNode Son){
		this.label= label;
		this.oid= oid;
		this.father= father;
		this.son= Son;
		this.dg = dg;
	}
	
	
	
}
