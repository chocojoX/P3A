import java.util.LinkedList;


public class DataPath {
	public LinkedList<String> labelPath;
	public LinkedList<Integer> oidPath;
	
	DataPath(){
		this.labelPath = new LinkedList<String>();
		this.oidPath= new LinkedList<Integer>();
	}
	
	public boolean isInstanceOf(LabelPath l){
		return(this.labelPath.equals(l.labelPath));
	}
}
