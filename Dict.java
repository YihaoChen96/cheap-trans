import java.util.*;

public class Dict {
	public ArrayList<Scores> scores=new ArrayList<Scores>();
	public String k;
	public String toString() {
		String str=k+": {";
		for(Scores s : scores) {
			str+=s+";";
		}
		str+="}\n";	
		return str;
	}
	
	public Dict() {
		
	}
	public Dict( String k,ArrayList<Scores> scores) {
		this.scores = scores;
		this.k = k;
	}
	
}
