import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class DataGuide {
	public DGNode root;
	public Hashtable<Set<Integer>, DGNode> targetHash;
	public Hashtable<Integer, DGNode> isRepresentedBy;
	public ArrayList<DGNode> allDGNodes; 
	public int nextOidEdge;
	public int nextOidNode;
	
	DataGuide(DGNode root){
		this.root= root;
		this.targetHash = new Hashtable<Set<Integer>, DGNode>();
		Set<Integer> list = root.oid;
		this.targetHash.put(list ,root);
		this.nextOidEdge = 1;
		this.nextOidNode = 1;
		this.allDGNodes = new ArrayList<DGNode>();
		this.allDGNodes.add(root);
	}
	
	
	
	public static DataGuide MakeDataGuide(Tree tree){
		Node o = tree.getRoot();
		Set<Integer> t1 = new HashSet<Integer>();
		t1.add(tree.root.getOid());
		DGNode dgRoot = new DGNode(t1, "root", 0);
		DataGuide dg = new DataGuide(dgRoot);
		dgRoot.dg = dg;
		dg = recursiveMake(dg, t1, tree, dg.root, dg.targetHash);
		dg.updateIsRepresentedBy();
		return dg;
	}
	public void addEdgeBetween(DGNode father, DGNode son, String label){
		DGEdge edge = new DGEdge(this, label, this.nextOidEdge, father, son);
		this.nextOidEdge+=1;
		father.outGoing.add(edge);
		son.outGoing.add(edge);
	}
	
	public DGNode addNodeFrom(DGNode father, String edgeLabel, Set<Integer> oid, String nodeLabel){
		ArrayList<DGEdge> inComing = new ArrayList<DGEdge>();
		DGNode newDGNode = new DGNode(this, oid, nodeLabel, this.nextOidNode);
		this.nextOidNode +=1;
		this.targetHash.put(oid, newDGNode);
		inComing.add(new DGEdge(this, edgeLabel, this.nextOidEdge, father, newDGNode));
		father.addDGEdgeTo(edgeLabel, newDGNode);
		this.allDGNodes.add(newDGNode);
		return newDGNode;
	}
	
	public static DataGuide recursiveMake(DataGuide dg, Set<Integer> t1, Tree tree, DGNode d1, Hashtable<Set<Integer>, DGNode> targetHash){
		List<Couple<String, Integer>> p = new ArrayList<Couple<String, Integer>>();
		List<String> alreadySeen = new ArrayList<String>();
		Iterator<Integer> it = t1.iterator();
		for(int i=0; i<t1.size(); i++){
			Node n = tree.hashTableOid.get(it.next());
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
				Set<Integer> t2 = new HashSet<Integer>();
				t2.add(oid);
				
				//find all "label" in p and get the oid corresponding.
				for(int i=idx+1; i<p.size(); i++){
					if(p.get(i).label.equals(label)){
						int oid2 = p.get(i).oid;
						t2.add(oid2);
					}
				}
				Iterator<Integer> it2 = t2.iterator();
				String nodeLabel = tree.hashTableOid.get(it2.next()).label;
				DGNode d2 = dg.targetHash.get(t2);
				if(d2==null){
					d1 = d1.removeAllOutGoingEdgesOfLabel(label);
					DGNode newNode = dg.addNodeFrom(d1, label, t2, nodeLabel);
					dg = recursiveMake(dg, t2, tree, newNode, targetHash);
				}
				else{
					d1 = d1.removeAllOutGoingEdgesOfLabel(label);
					dg.addEdgeBetween(d1, d2, label);
				}	
			}	
			
		}
		return dg;
	}
	
	public void updateIsRepresentedBy(){
		this.isRepresentedBy = new Hashtable<Integer, DGNode>();
		for(DGNode dgn : this.allDGNodes){
			Set<Integer> oids = dgn.oid;
			for(Integer oid : oids){
				this.isRepresentedBy.put(oid,  dgn);
			}
		}
	}
	
	public double getDGPerformance(Tree t){
		double sim = computeSim(t, this);
		
		int nDG = this.allDGNodes.size();
		int n = t.nextOidNode+1;
		float mDG = 0;
		for(DGNode dgn : this.allDGNodes){
			mDG+=dgn.outGoing.size();
		}
		
		float m = t.nextOidEdge;
		double summaryQuality = 1-mDG/(2*m)-nDG/(2*n);
		
		return Math.sqrt(summaryQuality*sim);	
	}
	
	public static double computeSim(Tree tree, DataGuide dg){
		Hashtable<Integer,double[]> DataOid = new Hashtable<Integer,double[]>();
		ArrayList<DGNode> allSonsEvaluated = new ArrayList<DGNode>();	
		
		//Initialize allSonsEvaluated with leaves of the DataGuide
		for(DGNode n : dg.allDGNodes){
			if(n.outGoing.isEmpty()){
				allSonsEvaluated.add(n);
				double[] t = new double[2];
				t[0] = 1; 
				t[1] = sim2(n,tree,dg, DataOid);
				DataOid.put(n.dgOid, t);
				updateASE(n, allSonsEvaluated, DataOid);
			}
		}
		int cmpt = -1;
		while(!allSonsEvaluated.isEmpty()){
			cmpt++;
			DGNode n = allSonsEvaluated.get(0);
			int m = n.dgOid;
			double s1 =0;
			if(n.outGoing.size()==0){
				s1 = 1;
			}
			else{
				List<DGEdge> out = n.outGoing;
				int k = out.size();
				for(DGEdge e:out){
					DGNode son = e.son;
					int id = son.dgOid;
					s1 += DataOid.get(id)[0]*DataOid.get(id)[1]/k;
				}
			}
			
			double s2 = sim2(n,tree,dg, DataOid);
			double[] t = new double[2]; t[0] = s1; t[1] = s2;
			DataOid.put(m,t);
			updateASE(n, allSonsEvaluated, DataOid);
		}
		double[] tRoot = DataOid.get(0);
		return tRoot[0]*tRoot[1];	
	}
			
	
	
	public static void updateASE(DGNode n, ArrayList<DGNode> ASE, Hashtable<Integer,double[]> DataOid){
		ASE.remove(n);
		List<DGEdge> in = n.inComing;
		for(DGEdge e:in){
			DGNode father = e.father;
			boolean b = true; 
			List<DGEdge> out = father.outGoing;
			for(DGEdge f:out){
				DGNode son = f.son;
				int IDSon = son.dgOid;
				if(DataOid.get(IDSon)==null){
					b=false;
				}
			}
			
			if(b){
				ASE.add(father);
			}
		}
	}
	
	
	public static double sim2(DGNode n, Tree tree, DataGuide dg, Hashtable<Integer,double[]> DataOid){
		
		if(n.dgOid ==0){
			double res = 0;
			int k = n.outGoing.size();
			for(int i =0; i<k; i++){
				double[] t = DataOid.get(n.outGoing.get(i).son.dgOid);
				res += t[0]*t[1]/k;
			}
			double[] tt = new double[2];
			tt[0] = 1;
			tt[1] = res;
			DataOid.put(0, tt);
			return res;
		}
		
		List<DGEdge> out = n.outGoing;
		List<DGEdge> in = n.inComing;
		Hashtable<String,Integer> decompteIn = new Hashtable<String,Integer>();
		Hashtable<String,Integer> decompteOut = new Hashtable<String,Integer>();
		
		for(DGEdge e:in){
			if(decompteIn.get(e.label)==null){
				decompteIn.put(e.label,1);
			}
			else{
				int k = decompteIn.remove(e.label);
				decompteIn.put(e.label,k+1);
			}
		}
		
		for(DGEdge e:out){
			if(decompteOut.get(e.label)==null){
				decompteOut.put(e.label,1);
			}
			else{
				int k = decompteOut.remove(e.label);
				decompteOut.put(e.label,k+1);
			}
		}
		
		//int ID = n.dgOid;
		Set<Integer> represented = n.oid;
		double s2 = 0;
		int nbNodesRepresented = represented.size();
		
		for(int id : represented){
			Node no = tree.hashTableOid.get(id);
			//DGNode representant = n;
			
			Hashtable<String,Integer> decompteInT = new Hashtable<String,Integer>();
			Hashtable<String,Integer> decompteOutT = new Hashtable<String,Integer>();
			List<Edge> inT = no.inComing;
			List<Edge> outT = no.outGoing;
			for(Edge e:inT){
				if(decompteInT.get(e.label)==null){
					decompteInT.put(e.label,1);
				}
				else{
					int k = decompteInT.remove(e.label);
					decompteInT.put(e.label,k+1);
				}
			}
			
			for(Edge e:outT){
				if(decompteOutT.get(e.label)==null){
					decompteOutT.put(e.label,1);
				}
				else{
					int k = decompteOutT.remove(e.label);
					decompteOutT.put(e.label,k+1);
				}
			}
			
			Set<String> setInT = decompteInT.keySet();
			int tailleInT = setInT.size();
			double[][] dataInT = new double[tailleInT][2];
			int i = 0;
			for(String s:setInT){
				dataInT[i][0]=decompteInT.get(s);
				if(decompteIn.get(s) == null){
					dataInT[i][1] = 0;
				}
				else{
					dataInT[i][1]=decompteIn.get(s);
				}
				i++;
			}
			
			Set<String> setOutT = decompteOutT.keySet();
			int tailleOutT = setOutT.size();
			double[][] dataOutT = new double[tailleOutT][2];
			i = 0;
			for(String s:setOutT){
				dataOutT[i][0]=decompteOutT.get(s);
				dataOutT[i][1]=decompteOut.get(s);
				i++;
			}
			s2 += f2(dataInT)*g2(dataOutT)/nbNodesRepresented;
		}
		return s2;
	}
	
	public static double g(double[][] x){
		int n = x.length;
		if(n==0){
			return 1;
		}
		double sum = 0;
		for( int i=0; i<n; i++){
			if(x[i][0] != 0 && x[i][1] != 0){
				sum += Math.min(x[i][0]/x[i][1], x[i][1]/x[i][0])/n;
			}
			else if(x[i][0] == 0 && x[i][1] == 0){
				sum+=1/n;
			}
		}
		return sum;
	}
	
	public static double f(double[][] x){
		int n = x.length;
		double sum = 0;
		for( int i=0; i<n; i++){
			if(x[i][0] != 0 && x[i][1] != 0){
				sum += Math.min(x[i][0]/x[i][1], x[i][1]/x[i][0])/n;
			}
		}
		return sum;
	}
	
	public static double g1(double[][] x){
		int n = x.length;
		if(n==0){
			return 1;
		}
		double sum = 0;
		for( int i=0; i<n; i++){
			if(x[i][0] != 0 && x[i][1] != 0){
				sum += 1./n;
			}
			else if(x[i][0] == 0 && x[i][1] == 0){
				sum+=1./n;
			}
		}
		return sum;
	}
	
	//in
	public static double f1(double[][] x){
		int n = x.length;
		double sum = 0;
		for( int i=0; i<n; i++){
			if(x[i][0] != 0 && x[i][1] != 0){
				sum += 1./n;
			}
		}
		return sum;
	}
	
	public static double g2(double[][] x){
		int n = x.length;
		if(n==0){
			return 1;
		}
		double sum = 0;
		for( int i=0; i<n; i++){
			if(x[i][0] != 0 && x[i][1] != 0){
				sum += 1./(2*n);
				if(x[i][0]==x[i][1]){
					sum += 1./(2*n);
				}
			}
			else if(x[i][0] == 0 && x[i][1] == 0){
				sum+=1./n;
			}
		}
		return sum;
	}
	
	//in
	public static double f2(double[][] x){
		int n = x.length;
		double sum = 0;
		for( int i=0; i<n; i++){
			if(x[i][0] != 0 && x[i][1] != 0){
				sum += 1./(2*n);
				if(x[i][0]==x[i][1]){
					sum += 1./(2*n);
				}
			}
		}
		return sum;
	}
	
	
	
}
