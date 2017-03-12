import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


public class Tree {
	public String name;
	public Node root;
	public ArrayList<Integer> allNodesOid;
	public int nextOidNode;
	public int nextOidEdge;
	public Hashtable<String, Node> hashTable;
	public Hashtable<Integer, Node> hashTableOid;
	
	Tree(String name){
		this.name=name;
		this.root = new Node("root", 0);
		this.hashTable = new Hashtable<String, Node>();
		this.allNodesOid = new ArrayList<Integer>();
		this.hashTableOid = new Hashtable<Integer, Node>();
		this.hashTable.put("0", this.root);
		this.hashTableOid.put(0, this.root);
		this.nextOidEdge = 1;
		this.nextOidNode =1;
	}
	
	public Node getRoot(){
		return(this.root);
	}
	
	public Node addNode(String name, Node father, String nodeType, String edgeName, String inRelationID){
		Node n = new Node(name, this.nextOidNode, nodeType);
		this.hashTableOid.put(this.nextOidNode, n);
		System.out.println(edgeName.concat(inRelationID));
		this.hashTable.put(edgeName.concat(inRelationID), n);
		this.allNodesOid.add(this.nextOidNode);
		this.nextOidNode+=1;
		//System.out.println(this.allNodesOid.size());

		Edge e= new Edge(edgeName, nextOidEdge, father, n);
		father.addEdgeFromNode(e);
		n.addEdgeToNode(e);
		this.nextOidEdge +=1;
		return n;
	}
	

	public static Tree addRelationCsv(Tree t, String fileName, String edgeName, String nodeType, String mainAttribute, String nameOfIDAttribute) throws IOException{
		BufferedReader f;
		f = new BufferedReader(new FileReader(fileName));
		String suiv = null;
		try {
			suiv = f.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			suiv = null;
			try {
				suiv = f.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(suiv==null) break;
			String[] tmp = suiv.split(",");

			String[] attributes = new String[tmp.length];
			String[] attributesNames = new String[tmp.length];
			for(int i=0; i<tmp.length; i++){
				attributes[i] = tmp[i].split(":")[1];
				attributesNames[i] = tmp[i].split(":")[0];
			}
			
			String nodeName= "";
			String inRelationID = "";
			for(int j=0; j<attributes.length; j++){
				attributesNames[j] = attributesNames[j].replace("\"", "");
				if(attributesNames[j].equals(mainAttribute)){
					nodeName=attributes[j];
				}
				if(attributesNames[j].equals(nameOfIDAttribute)){
					inRelationID = attributes[j];
				}
			}
			
			if(t.hashTable)
			Node n = t.addNode(nodeName, t.root, nodeType, edgeName, inRelationID);
			t.nextOidNode++;
			for(int k=0; k<attributes.length; k++){
				attributes[k] = attributes[k].replaceAll("\"", "");
				attributesNames[k] = attributesNames[k].replaceAll("\"", "");
				attributes[k] = attributes[k].replaceAll("\\}", "");
				attributesNames[k] = attributesNames[k].replaceAll("\\{", "");
				
				Node n2 = t.addNode(attributes[k], n, attributesNames[k], attributesNames[k], attributes[k]);
//				Node n2 = new Node(attributes[k], t.nextOidNode);
//				t.nextOidNode++;
//				Edge e = new Edge(attributesNames[k], t.nextOidEdge, n, n2);
//				n.addEdgeFromNode(e);
//				n2.addEdgeToNode(e);
//				t.nextOidEdge++;
			}
			
		}
		f.close();
		return t;
	}

	public static Tree addRelationshipCsv(Tree t, String fileName, String edgeType, String nodeType1,
			String nodeType2, String ID1name, String ID2name) throws IOException{
		BufferedReader f;
		f = new BufferedReader(new FileReader(fileName));
		String suiv = null;
		try {
			suiv = f.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int cmpt =0;
		while(true){
			cmpt+=1;
			suiv = null;
			try {
				suiv = f.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(suiv==null) break;
			String[] tmpAllRelationship = suiv.split("\\},\\{");
			
			String[] tmp1 = tmpAllRelationship[0].split(",");
			String[] tmp2 = tmpAllRelationship[2].split(",");
			String[] attributes = new String[tmp1.length];
			String[] attributesNames = new String[tmp1.length];
			for(int i=0; i<tmp1.length; i++){
				attributes[i] = tmp1[i].split(":")[1];
				attributesNames[i] = tmp1[i].split(":")[0];
			}
			String inRelationID = "";
			for(int j=0; j<attributes.length; j++){
				attributesNames[j] = attributesNames[j].replace("\"", "");
				if(attributesNames[j].equals(ID1name)){
					inRelationID = attributes[j];
				}
			}
			//We now have the ID in the relation 1
			Node n1 = t.hashTable.get(nodeType1.concat(inRelationID));
			
			inRelationID = "";
			attributes = new String[tmp2.length];
			attributesNames = new String[tmp2.length];
			for(int i=0; i<tmp2.length; i++){
				attributes[i] = tmp2[i].split(":")[1];
				attributesNames[i] = tmp2[i].split(":")[0];
			}
			for(int j=0; j<attributes.length; j++){
				attributesNames[j] = attributesNames[j].replace("\"", "");
				if(attributesNames[j].equals(ID2name)){
					inRelationID = attributes[j];
				}
			}
			Node n2 = t.hashTable.get(nodeType2.concat(inRelationID));
			
			if(n1 == null || n2 == null){
				try {
					throw new Exception("One of the two nodes of the relationship does not exist (relationship number " + cmpt +")");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			Edge e = new Edge(edgeType, t.nextOidEdge, n1, n2);
			t.nextOidEdge +=1;
			n1.addEdgeFromNode(e);
			n2.addEdgeToNode(e);
			
		}
		f.close();
		return t;
	}

	public static Tree addRelationCsv2(Tree t, String fileName, String edgeName, String nodeType, String mainAttribute, String nameOfIDAttribute) throws IOException{
		BufferedReader f;
		f = new BufferedReader(new FileReader(fileName));
		String suiv = null;
		String[] attributesNames = new String[1];
		try {
			suiv = f.readLine();
			attributesNames = suiv.split(";");
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			suiv = null;
			try {
				suiv = f.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(suiv==null) break;
			String[] attributes = suiv.split(";");
			
			String nodeName= "";
			String inRelationID = "";
			for(int j=0; j<attributes.length; j++){
				attributesNames[j] = attributesNames[j].replace("\"", "");
				if(attributesNames[j].equals(mainAttribute)){
					nodeName=attributes[j];
				}
				if(attributesNames[j].equals(nameOfIDAttribute)){
					inRelationID = attributes[j];
				}
			}
			System.out.println(edgeName.concat(inRelationID));
			if(!t.hashTable.containsKey(edgeName.concat(inRelationID))){
				Node n = t.addNode(nodeName, t.root, nodeType, edgeName, inRelationID);
				t.nextOidNode++;
				for(int k=0; k<attributes.length; k++){
					attributes[k] = attributes[k].replaceAll("\"", "");
					attributesNames[k] = attributesNames[k].replaceAll("\"", "");
					attributes[k] = attributes[k].replaceAll("\\}", "");
					attributesNames[k] = attributesNames[k].replaceAll("\\{", "");
					
					Node n2 = t.addNode(attributes[k], n, attributesNames[k], attributesNames[k], attributes[k]);
				}
			}
		}
		f.close();
		return t;
	}	

	
	public static Tree addRelationshipCsv2(Tree t, String fileName, String edgeTypeName, String nodeType1,
			String nodeType2, String ID1name, String ID2name) throws IOException{
		BufferedReader f;
		f = new BufferedReader(new FileReader(fileName));
		String suiv = null;
		String[] attributesNames = new String[1];
		try {
			suiv = f.readLine();
			attributesNames = suiv.split(";");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int edgeType = -1;
		for(int j = 0; j<attributesNames.length; j++){
			if(attributesNames[j].replace("\"", "").equals(edgeTypeName)){
				edgeType=j;
				break;
			}
		}
		int cmpt =0;
		while(true){
			cmpt+=1;
			suiv = null;
			try {
				suiv = f.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(suiv==null) break;
			String[] attributes = suiv.split(";");

			String inRelationID = "";
			for(int j=0; j<attributes.length; j++){
				System.out.println(attributesNames[j]);
				if(attributesNames[j].replace("\"", "").equals(ID1name)){
					inRelationID = attributes[j];
				}
			}
			//We now have the ID in the relation 1
			Node n1 = t.hashTable.get(nodeType1.concat(inRelationID));
			System.out.println("Node 1 : " + n1.getOid()+ ", " + n1.label);
			
			inRelationID = "";
			for(int j=0; j<attributes.length; j++){
				attributesNames[j] = attributesNames[j].replace("\"", "");
				if(attributesNames[j].equals(ID2name)){
					inRelationID = attributes[j];
				}
			}
			Node n2 = t.hashTable.get(nodeType2.concat(inRelationID));
			System.out.println("Node 2 : " + n2.getOid() + ", " + n2.label);
			
			if(n1 == null || n2 == null){
				try {
					throw new Exception("One of the two nodes of the relationship does not exist (relationship number " + cmpt +")");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			Edge e = new Edge(attributes[edgeType], t.nextOidEdge, n2, n1);
			t.nextOidEdge +=1;
			n1.addEdgeToNode(e);
			n2.addEdgeFromNode(e);
			
		}
		f.close();
		return t;
	}
}


