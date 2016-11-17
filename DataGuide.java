import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class DataGuide {
	public DGNode root;
	public Hashtable<ArrayList<Integer>, DGNode> targetHash;
	public ArrayList<DGNode> allDGNodes; 
	public int nextOidEdge;
	
	DataGuide(DGNode root){
		this.root= root;
		this.targetHash = new Hashtable<ArrayList<Integer>, DGNode>();
		ArrayList<Integer> list = root.oid;
		this.targetHash.put(list ,root);
		this.nextOidEdge = 1;
		this.allDGNodes = new ArrayList<DGNode>();
		this.allDGNodes.add(root);
	}
	
	
	
	public static DataGuide MakeDataGuide(Tree tree){
		Node o = tree.getRoot();
		ArrayList<Integer> t1 = new ArrayList<Integer>();
		t1.add(tree.root.getOid());
		DataGuide dg = new DataGuide(new DGNode(dg, t1));
		dg = recursiveMake(dg, t1, tree, dg.root, dg.targetHash);
		return dg;
	}
	public void addEdgeBetween(DGNode father, DGNode son, String label){
		DGEdge edge = new DGEdge(this, label, this.nextOidEdge, father, son);
		this.nextOidEdge+=1;
		father.outGoing.add(edge);
		son.outGoing.add(edge);
	}
	
	public DGNode addNodeFrom(DGNode father, String label, ArrayList<Integer> oid){
		ArrayList<DGEdge> inComing = new ArrayList<DGEdge>();
		DGNode newDGNode = new DGNode(this, oid);
		this.targetHash.put(oid, newDGNode);
		inComing.add(new DGEdge(this, label, this.nextOidEdge, father, newDGNode));
		father.addDGEdgeTo(label, newDGNode);
		this.allDGNodes.add(newDGNode);
		return newDGNode;
	}
	
	public static DataGuide recursiveMake(DataGuide dg, ArrayList<Integer> t1, Tree tree, DGNode d1, Hashtable<ArrayList<Integer>, DGNode> targetHash){
		List<Couple<String, Integer>> p = new ArrayList<Couple<String, Integer>>();
		List<String> alreadySeen = new ArrayList<String>();
		for(int i=0; i<t1.size(); i++){
			Node n = tree.hashTable.get(t1.get(i));
			ArrayList<Edge> edges = n.outGoing;
			for(int j=0; j<edges.size(); j++){
				String label = edges.get(j).label;
				int oid = edges.get(j).son.getOid();
				p.add(new Couple<String, Integer>(label, oid));
			}	
		}
		for(int idx=0; idx< p.size(); idx++){
			String label = p.get(idx).label;
			int oid = p.get(idx).oid;
			if(!alreadySeen.contains(label)){
				alreadySeen.add(label);
				ArrayList<Integer> t2 = new ArrayList<Integer>();
				t2.add(oid);
				
				//find all "label" in p and get the oid corresponding.
				for(int i=idx+1; i<p.size(); i++){
					if(p.get(i).label.equals(label)){
						int oid2 = p.get(i).oid;
						t2.add(oid2);
					}
				}
				
				DGNode d2 = dg.targetHash.get(t2);
				if(d2==null){
					DGNode newNode = dg.addNodeFrom(d1, label, t2);
					dg = recursiveMake(dg, t2, tree, d2, targetHash);
				}
				else{
					dg.addEdgeBetween(d1, d2, label);
				}	
			}		
		}
		return dg;
	}
	
	
	
	
}
