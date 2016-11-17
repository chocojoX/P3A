import java.util.ArrayList;
import java.util.List;


public class DGNode {
	public ArrayList<Integer> oid;
	public DataGuide dg;
	public List<DGEdge> outGoing;
	public List<DGEdge> inComing;
	
	DGNode(DataGuide dg, List<DGEdge> inComing, ArrayList<Integer> oid){
		this.dg = dg;
		this.oid=oid;
		this.inComing=inComing;
		this.outGoing=new ArrayList<DGEdge>();
	}
	
	DGNode(DataGuide dg, ArrayList<Integer> oid){
		this.dg = dg;
		this.oid= oid;
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
	}
	
	DGNode(DataGuide dg){
		this.dg = dg;
		this.oid= new ArrayList<Integer>();
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
	}
	
	public void addDGEdgeTo(String label, DGNode son){
		DGEdge newEdge = new DGEdge(label, this.dg.nextOidEdge, this, son);
		this.outGoing.add(newEdge);
		son.inComing.add(newEdge);
		this.dg.nextOidEdge+=1;
	}

	public int getOid() {
		return this.oid;
	}
}
