import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class OneIndex {
	public INode root;
	public Hashtable<Integer, INode> targetHash;
	public Hashtable<Integer, INode> IoidTable;
	public ArrayList<INode> allINodes; 
	public int nextOidNode;
	public int nextOidEdge;
	public ArrayList<Node> allFathersClassified;
	public ArrayList<Integer> unclassifiedNodesOid;
	public ArrayList<Integer> classifiedNodesOid;
	
	
	OneIndex(INode root){
		this.root = root;
		this.targetHash = new Hashtable<Integer, INode>();
		this.IoidTable = new Hashtable<Integer, INode>();
		this.targetHash.put(0, root);
		this.IoidTable.put(1, root);
		this.allINodes = new ArrayList<INode>();
		boolean added = this.allINodes.add(root);
		this.nextOidEdge = 1;
		this.nextOidNode = 2;
		this.allFathersClassified = new ArrayList<Node>();
		this.unclassifiedNodesOid = new ArrayList<Integer>();
		this.classifiedNodesOid = new ArrayList<Integer>();
	}
	
	public static OneIndex initiateOneIndex(Tree t){
		Node TRoot = t.root;
		INode IRoot = new INode("root");
		IRoot.targetOid.add(TRoot.getOid());
		OneIndex I = new OneIndex(IRoot);
		I.classifiedNodesOid.add(1);
		IRoot.I = I;
		I.allINodes.add(IRoot);
		return(I);
	}
	
	public int getIoidFromNode(Node n){
		if(this.targetHash.get(n.getOid()) == null){
			return -1;
		}
		return this.targetHash.get(n.getOid()).Ioid;
	}
	
	public ArrayList<Integer> existsPartialEquivalenceClass(INode father, ArrayList<String> labels){
		//Returns the list of Ioids of the nodes linked from father with all edgesLabels.
		// returns -1 if such a INode doesn't exist
		
		HashSet<String> labelsSet = new HashSet<String>();
		for(String l : labels){
			labelsSet.add(l);
		}
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		HashSet<INode> Sons = new HashSet<INode>();
		System.out.println(father.label);
		for(IEdge e : father.outGoing){
			String label = e.label;
			if(labelsSet.contains(label)){
				Sons.add(e.son);
			}
		}

		for(INode n : Sons){
			boolean plausible = true; 
			ArrayList<String> inComingLabels = new ArrayList<String>();
			for(IEdge e : n.inComing){
				if(!labels.contains(e.label) && e.father.Ioid == father.Ioid){
					plausible = false;
				}
				//System.out.println(e.label +  labels);
				if(e.father.Ioid == father.Ioid){
					inComingLabels.add(e.label);
				}
				
			}
			
			if(labelsSet.size() != inComingLabels.size()){
				plausible=false;
			}
			if(plausible){
				result.add(n.Ioid);
			}
		}
		//System.out.println("result : " + result);
		return result;
	}
	
	public static OneIndex makeOneIndex(Tree t){
		OneIndex I = initiateOneIndex(t);
		Node TRoot = t.root;
		I.unclassifiedNodesOid = t.allNodesOid;
		ArrayList<Node> sons = new ArrayList<Node>();
		for(Edge e : TRoot.outGoing){
			sons.add(e.son);
		}
		for(Node n2 : sons){
			I.updateAllFathersClassified(n2);
		}
		int cmpt = 0;
		while(!I.allFathersClassified.isEmpty()){
			cmpt++;
			//System.out.println("Nombre d'itérations : " + cmpt);
			I.recursiveMakeOneIndex();
		}
		return I;
	}
	
	public void recursiveMakeOneIndex(){
		
		Node n = this.allFathersClassified.get(0);

		if(!this.unclassifiedNodesOid.contains(n.getOid())){
			this.allFathersClassified.remove(0);
			return;
		}
		//System.out.println("node oid : " + n.getOid());
		// mapIoidLabel contains the pairs (father.Ioid, labels of edges linking father and son in tree)
		Hashtable<Integer, ArrayList<String>> mapIoidLabel = new Hashtable<Integer, ArrayList<String>>();
		// IfathersOid contains the Ioid of the fathers of the node n
		ArrayList<Integer> IfathersOid = new ArrayList<Integer>();
		for(Edge e : n.inComing){
			Node father = e.father;
			System.out.println("Father ids : " + father.getOid());
			int Ioid = getIoidFromNode(father);
			ArrayList<String> edgesLabels = mapIoidLabel.get(Ioid);
			if(edgesLabels == null){
				ArrayList<String> l = new ArrayList<String>();
				l.add(e.label);
				mapIoidLabel.put(Ioid, l);
			}
			else{
				edgesLabels.add(e.label);
			}
			if(!IfathersOid.contains(Ioid)){
				IfathersOid.add(Ioid);
			}
		}
		// labels contains the labels of the edges linking the first father to n
		// labelsSet is the set of these labels
		ArrayList<String> labels = mapIoidLabel.get(IfathersOid.get(0));
		System.out.println(IfathersOid.get(0));
		System.out.println(n.label);
		ArrayList<Integer> candidates = 
				this.existsPartialEquivalenceClass(this.IoidTable.get(IfathersOid.get(0)), 
						labels);
		//System.out.println(this.nextOidNode + " candidates : " + candidates);
		for(int i = 1; i<IfathersOid.size(); i++){
			labels = mapIoidLabel.get(IfathersOid.get(i));
			
			ArrayList<Integer> plausibleCandidates = 
					this.existsPartialEquivalenceClass(this.IoidTable.get(IfathersOid.get(i)), 
					labels);
			
			for(int k = candidates.size()-1; k>-1; k--){
				int p = candidates.get(k);
				if(!plausibleCandidates.contains(p)){
					candidates.remove(candidates.indexOf(p));
				}
			}
		}
		// At this stage several candidates may still be available if the Ifathers of the node we consider are
		// INCLUDED in the Ifathers of the candidates
		
		HashSet<Integer> IfathersOfn = new HashSet<Integer>();
		for(Edge e : n.inComing){
			int Ioid = this.getIoidFromNode(e.father);
			IfathersOfn.add(Ioid);
		}
		for(int Ioid : candidates){
			INode IN = this.IoidTable.get(Ioid);
			System.out.println("pouet : " + IN.Ioid);
			HashSet<Integer> IfathersOfCandidate = new HashSet<Integer>();
			for(IEdge IE : IN.inComing){
				IfathersOfCandidate.add(IE.father.Ioid);
			}
			System.out.println("Nombre de candidats : " + candidates.size());
			System.out.println(IfathersOfCandidate);
			if(!IfathersOfCandidate.containsAll(IfathersOfn) || !IfathersOfn.containsAll(IfathersOfCandidate)){
				candidates.remove(candidates.indexOf(Ioid));
			}
			
			/*if(!IfathersOfCandidate.equals(IfathersOfn)){
				candidates.remove(candidates.indexOf(Ioid));
			}*/
		}

//		if(n.getOid() ==281){
//			System.out.println("candidates size : " + candidates.size());
//			try {
//				Thread.sleep((long) 100.);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
		

		if(candidates.size()==1 && candidates.get(0)!=-1){
			int Ioid = candidates.get(0);
			INode IN = this.IoidTable.get(Ioid);
			this.targetHash.put(n.getOid(), IN);
			IN.targetOid.add(n.getOid());	
		}
		else if(candidates.isEmpty() || candidates.get(0)==-1){
			INode IN = new INode(this, n.label);
			this.IoidTable.put(this.nextOidNode-1, IN);
			this.targetHash.put(n.getOid(), IN);
			for(Edge e : n.inComing){
				int oid = e.father.getOid();
				INode Ifather = this.targetHash.get(oid);
				
				boolean edgeAlreadyExists = false;
				for(IEdge IE : Ifather.outGoing){
					if(IE.label.equals(e.label) &&  IE.son.Ioid == IN.Ioid){
						edgeAlreadyExists = true;
						break;
					}
				}
				if(!edgeAlreadyExists){
					IEdge IE = new IEdge(this, this.nextOidEdge, Ifather, IN, e.label);
					Ifather.outGoing.add(IE);
					IN.inComing.add(IE);
				}	
			}
			this.allINodes.add(IN);
			IN.targetOid.add(n.getOid());
		}
		else{
			int Ioid = candidates.get(0);
			INode IN = this.IoidTable.get(Ioid);
			this.targetHash.put(n.getOid(), IN);
			IN.targetOid.add(n.getOid());
			System.out.println("Plusieurs classes d'équivalences pour le noeud numero" + n.getOid() +
					"c'est un gros problème qui ne devrait jamais être arrivé :(");
			try {
				Thread.sleep((long) 1000.);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//System.out.println("Nodes with all classsified fathers : " + this.allFathersClassified.size());
		//System.out.println("Nodes yet to be insertd in the index : " + this.unclassifiedNodesOid.size());
		int idx = this.unclassifiedNodesOid.indexOf(n.getOid());
		
		if(idx>=0){
			this.unclassifiedNodesOid.remove(idx);
		}
		this.allFathersClassified.remove(0);

		for(Edge e : n.outGoing){
			this.updateAllFathersClassified(e.son);
		}
		this.classifiedNodesOid.add(n.getOid());
	}
	
	public void updateAllFathersClassified(Node n){
		if(!this.unclassifiedNodesOid.contains(n.getOid()) ||  this.allFathersClassified.contains(n.getOid()) ){
			return;
		}
		for(Edge e : n.inComing){
			Node father = e.father;
			int fatherOid = father.getOid();
			if(this.unclassifiedNodesOid.contains(fatherOid)){
				return;
			}
		}
		this.allFathersClassified.add(n);
		return;
	}
	
	public double getIndexPerformance(Tree t){
		double sim = computeSim(t, this);
		
		int nDG = this.allINodes.size();
		int n = t.nextOidNode-1;
		float mDG = 0;
		for(INode In : this.allINodes){
			mDG+=In.outGoing.size();
		}
		float m = t.nextOidEdge;

		double summaryQuality = 1-mDG/(2*m)-nDG/(2*n);
		
		return Math.sqrt(summaryQuality*sim);	
	}
	
	
	public static double computeSim(Tree tree, OneIndex I){
		Hashtable<Integer,double[]> DataOid = new Hashtable<Integer,double[]>();
		ArrayList<INode> allSonsEvaluated = new ArrayList<INode>();	
		
		//Initialize allSonsEvaluated with leaves of the DataGuide
		for(INode n : I.allINodes){
			if(n.outGoing.isEmpty()){
				allSonsEvaluated.add(n);
				double[] t = new double[2];
				t[0] = 1; 
				t[1] = sim2(n,tree, I , DataOid);
				DataOid.put(n.Ioid, t);
				updateASE(n, allSonsEvaluated, DataOid);
			}
		}
		int cmpt = -1;
		while(!allSonsEvaluated.isEmpty()){
			cmpt++;
			INode n = allSonsEvaluated.get(0);
			int m = n.Ioid;
			double s1 =0;
			if(n.outGoing.size()==0){
				s1 = 1;
			}
			else{
				List<IEdge> out = n.outGoing;
				int k = out.size();
				for(IEdge e:out){
					INode son = e.son;
					int id = son.Ioid;
					s1 += DataOid.get(id)[0]*DataOid.get(id)[1]/k;
				}
			}
			
			double s2 = sim2(n,tree, I, DataOid);
			double[] t = new double[2]; t[0] = s1; t[1] = s2;
			DataOid.put(m,t);
			updateASE(n, allSonsEvaluated, DataOid);
		}
		double[] tRoot = DataOid.get(1);
		
		return tRoot[0]*tRoot[1];	
	}
			
	
	
	public static void updateASE(INode n, ArrayList<INode> allSonsEvaluated, Hashtable<Integer,double[]> DataOid){
		allSonsEvaluated.remove(n);
		List<IEdge> in = n.inComing;
		for(IEdge e:in){
			INode father = e.father;
			boolean b = true; 
			List<IEdge> out = father.outGoing;
			for(IEdge f:out){
				INode son = f.son;
				int IDSon = son.Ioid;
				if(DataOid.get(IDSon)==null){
					b=false;
				}
			}
			
			if(b){
				allSonsEvaluated.add(father);
			}
		}
	}
	
	
	
	public static double sim2(INode n, Tree tree, OneIndex i2, Hashtable<Integer,double[]> DataOid){
		
		if(n.Ioid ==1){
			double res = 0;
			int k = n.outGoing.size();
			for(int i =0; i<k; i++){
				double[] t = DataOid.get(n.outGoing.get(i).son.Ioid);
				res += t[0]*t[1]/k;
			}
			double[] tt = new double[2];
			tt[0] = 1;
			tt[1] = res;
			DataOid.put(0, tt);
			return res;
		}
		
		List<IEdge> out = n.outGoing;
		List<IEdge> in = n.inComing;
		Hashtable<String,Integer> decompteIn = new Hashtable<String,Integer>();
		Hashtable<String,Integer> decompteOut = new Hashtable<String,Integer>();
		
		for(IEdge e:in){
			if(decompteIn.get(e.label)==null){
				decompteIn.put(e.label,1);
			}
			else{
				int k = decompteIn.remove(e.label);
				decompteIn.put(e.label,k+1);
			}
		}
		
		for(IEdge e:out){
			if(decompteOut.get(e.label)==null){
				decompteOut.put(e.label,1);
			}
			else{
				int k = decompteOut.remove(e.label);
				decompteOut.put(e.label,k+1);
			}
		}
		
		//int ID = n.dgOid;
		Set<Integer> represented = n.targetOid;
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
			s2 += f(dataInT)*g(dataOutT)/nbNodesRepresented;
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

	//out
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


