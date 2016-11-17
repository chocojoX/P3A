
public class Couple<A, B> {
	B oid;
	A label;
	
	Couple(A label, B oid){
		this.oid = oid;
		this.label=label;
	}
	
	Couple(){
		this.oid= null;
		this.label =null;
	}
	
	public boolean equals (Couple c2){
		return(this.oid.equals(c2.oid) & this.label.equals(c2.label));
	}
}
