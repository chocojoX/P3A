import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DGNode {
	public Set<Integer> oid;
	public int dgOid;
	public DataGuide dg;
	public String label;
	public List<DGEdge> outGoing;
	public List<DGEdge> inComing;
	
	DGNode(){
		this.oid= new HashSet<Integer>();
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
		this.dg = null;
		this.label = "unknown";
	}
	
	DGNode(Set<Integer> oid){
		this.oid= oid;
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
		this.dg = null;
		this.label = "unknown";
	}
	
	DGNode(Set<Integer> oid, String label, int dgOid){
		this.oid= oid;
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
		this.dg = null;
		this.label = label;
		this.dgOid= dgOid;
	}
	
	DGNode(DataGuide dg, List<DGEdge> inComing, Set<Integer> oid, int dgOid){
		this.dg = dg;
		this.oid=oid;
		this.dgOid = dgOid;
		this.inComing=inComing;
		this.outGoing=new ArrayList<DGEdge>();
		this.label = "unknown";
	}
	
	DGNode(DataGuide dg, List<DGEdge> inComing, Set<Integer> oid, String nodeLabel, int dgOid){
		this.dg = dg;
		this.oid=oid;
		this.dgOid = dgOid;
		this.inComing=inComing;
		this.outGoing=new ArrayList<DGEdge>();
		this.label = nodeLabel;
	}
	
	DGNode(DataGuide dg, Set<Integer> oid){
		this.dg = dg;
		this.oid= oid;
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
		this.label = "unknown";
	}
	
	DGNode(DataGuide dg, Set<Integer> oid, String NodeLabel, int dgOid){
		this.dg = dg;
		this.oid= oid;
		this.dgOid = dgOid;
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
		this.label = NodeLabel;
	}
	
	DGNode(DataGuide dg){
		this.dg = dg;
		this.oid= new HashSet<Integer>();
		this.inComing= new ArrayList<DGEdge>();
		this.outGoing= new ArrayList<DGEdge>();
		this.label = "unknown";
	}
	
	public void addDGEdgeTo(String label, DGNode son){
		DGEdge newEdge = new DGEdge(this.dg, label, this.dg.nextOidEdge, this, son);
		this.outGoing.add(newEdge);
		son.inComing.add(newEdge);
		this.dg.nextOidEdge+=1;
	}

	public Set<Integer> getOid() {
		return this.oid;
	}

	public DGNode removeAllOutGoingEdgesOfLabel(String label2) {
		List<DGEdge> outGoing = this.outGoing;
		for(int i =0; i<outGoing.size(); i++){
			DGEdge e = outGoing.get(i);
			if(e.label.equals(label2)){
				this.outGoing.remove(i);
			}
		}
			
		return this;
	}
}
