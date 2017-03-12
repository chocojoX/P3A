import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;



public class mainClass {

	public static void printTree(Tree t) throws FileNotFoundException, UnsupportedEncodingException{
		Node root = t.root;
		
		PrintWriter writer = new PrintWriter("tree.txt", "UTF-8");		
		iterativePrintTree(root, writer);
		writer.close();
		return;
	}
	
	public static void iterativePrintTree(Node n, PrintWriter writer){
		for(Edge e : n.outGoing){
			writer.println(n.getOid() + "->" + e.son.getOid() + "[label = " + e.label + "]");
			System.out.println(n.getOid() + "->" + e.son.getOid() + "[label = " + e.label + "]");
			iterativePrintTree(e.son, writer);
		}
		return;
	}
	
	public static void main(String[] args) throws IOException {
		long startTree = System.currentTimeMillis();
		Tree t = new Tree("myTree");
		/*t = Tree.addRelationCsv(t, "products.csv", "product", "product","productName", "productID");
		t = Tree.addRelationCsv(t, "customers.csv", "customer","customer", "contactName", "customerID");
		t = Tree.addRelationCsv(t, "suppliers.csv", "supplier", "supplier", "contactName", "supplierID");
		t = Tree.addRelationshipCsv(t, "supplies.csv" , "supplies", "supplier",
				"product", "supplierID", "productID");*/
		
		t = Tree.addRelationCsv2(t, "movies100.csv", "movie", "movie", "title", "id");
		t = Tree.addRelationCsv2(t, "persons100.csv", "person", "person", "name", "id");
		t = Tree.addRelationshipCsv2(t, "role100.csv", "role", "person", "movie", "id", "movie_id");
		long endTree = System.currentTimeMillis();
		System.out.println("nombre de noeuds : " +   (t.nextOidNode-1));
		System.out.println("nombre d'arêtes: " + (t.nextOidEdge-1));
		long startDG = System.currentTimeMillis();
		DataGuide dg = DataGuide.MakeDataGuide(t);
		long endDG = System.currentTimeMillis();
		DGNode dgRoot = dg.root;
		//DGNode n1 = dgRoot.outGoing.get(2).son;
		
		//printTree(t);
		/*for(DGEdge edge : n1.outGoing){
			System.out.println(edge.label);
		}*/
		int i = 0; 
		int cmpt = 0;
		for(DGNode dgn : dg.allDGNodes){
			cmpt += dgn.oid.size();
//			System.out.println(dgn.label);
//			System.out.println(dgn.oid);
			for(DGEdge dge : dgn.outGoing){
				i++;
				List<DGEdge> inComing = dgn.inComing;
				int name1 = dgn.dgOid;
				int name2 = dge.son.dgOid;
				System.out.println(name1 + "->" + name2 + "[label = " + dge.label + "]");
			}
		}
		System.out.println("Nombre d'arête du DG : " +  i);
		//System.out.println(t.hashTableOid.get(1649).label);
//		Node n = t.hashTableOid.get(1);
//		System.out.println(t.allNodesOid.size());
//		
//		
		long start1I = System.currentTimeMillis();
		OneIndex I = OneIndex.makeOneIndex(t);
		long end1I = System.currentTimeMillis();
		/*for(INode IN : I.allINodes){
			int name1 = IN.Ioid;
			for(IEdge IE : IN.inComing){
				int name2 = IE.father.Ioid;
				
				System.out.println(name2 + "->" + name1 + "[label = " + IE.label + "]");
			}
			
		}*/
		System.out.println("temps de création de l'arbre : " + (endTree-startTree));
		System.out.println("nombre de noeuds dans l'arbre : "  + t.nextOidNode);
		System.out.println("temps de création du dataguide : " + (endDG-startDG));
		System.out.println("temps de création du 1Index : " + (end1I-start1I));
		System.out.println("noeuds, arêtes du DataGuide : " + dg.nextOidNode + ", " + dg.nextOidEdge);
		//System.out.println("noeuds, arêtes du 1-Index : " + I.nextOidNode + ", " + I.nextOidEdge);
		System.out.println("Similarité avec le dataguide : " + DataGuide.computeSim(t, dg));
		System.out.println("Performance du dataguide : " + dg.getDGPerformance(t));
		System.out.println("Similarité avec le 1-Index : " + OneIndex.computeSim(t, I));
		System.out.println("Performance du 1-Index : " + I.getIndexPerformance(t));

	}

}
