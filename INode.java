import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class INode {
	public Set<Integer> targetOid;
	public Integer Ioid;
	public OneIndex I;
	public List<IEdge> outGoing;
	public List<IEdge> inComing;
	public String label;
	
	INode(OneIndex I, String label){
		this.I = I;
		this.outGoing  = new ArrayList<IEdge>();
		this.inComing  = new ArrayList<IEdge>();
		this.targetOid = new HashSet<Integer>();
		this.Ioid = I.nextOidNode;
		I.nextOidNode++;
		this.label = label;
	}
	
	INode(){
		this.I = null;
		this.outGoing  = new ArrayList<IEdge>();
		this.inComing  = new ArrayList<IEdge>();
		this.targetOid = new HashSet<Integer>();
		this.Ioid = 1;
	}
	
	INode(String label){
		this.I = null;
		this.outGoing  = new ArrayList<IEdge>();
		this.inComing  = new ArrayList<IEdge>();
		this.targetOid = new HashSet<Integer>();
		this.Ioid = 1;
		this.label = label;
	}
	
	INode(OneIndex I, Set<Integer> targetOid, List<IEdge> outGoing, List<IEdge> inComing, Integer Ioid){
		this.I = I;
		this.outGoing  = outGoing;
		this.inComing  = inComing;
		this.targetOid = targetOid;
		this.Ioid = Ioid;
	}
}
