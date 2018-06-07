import java.util.*;
import java.util.Map.Entry;
import java.io.*;
public class TransInit {
	String encoding="utf-8";
	String source="eng";
	String target="fra";
	String method="lexicon";
	String dict_type="USEPAVLICK";
	String dict_path= "default";
	String in_file = null;
	String out_file = "out.conll";
	
	TransInit(){
		
	}
	
	//TODO: USE VEC && USE SIMILARITY
	public Map<String,String> readConfig (String fname) throws IOException {
		FileInputStream in_stream = new FileInputStream(fname);
		InputStreamReader reader = new InputStreamReader(in_stream,"utf-8");
		BufferedReader br = new BufferedReader(reader);
		
		String line = null;
		Map<String,String> configs = new HashMap<String,String>();
		while((line=br.readLine())!=null) {
			String[] strs = line.split("=");
			if (strs.length!=2) throw new IllegalArgumentException(); 
			else {
				configs.put(strs[0].trim(),strs[1].trim());
			}
		}
		return configs;
	}
	public void _init_(String fname) throws IOException {
		Map<String,String> configs = this.readConfig(fname);
		for (Entry<String,String> entry: configs.entrySet()) {
			switch(entry.getKey()) {
			case "Encoding":
				this.encoding = entry.getValue();
				break;
			case "Source":
				this.source = entry.getValue();
				break;
			case "Target":
				this.target = entry.getValue();
				break;
			case "Method":
				this.method = entry.getValue();
				break;
			case "DictType":
				this.dict_type = entry.getValue();
				break;
			case "DictPath":
				this.dict_path = entry.getValue();
				break;
			case "InFile":
				this.in_file = entry.getValue();
				break;
			case "OutFile":
				this.out_file = entry.getValue();
			default:
				break;
			}
		}
		System.out.println(configs.toString());
		if(this.dict_path.equals("default")) {
			Translate tl = new Translate(this.dict_type,this.method,this.source,this.target);
			if(this.in_file!=null) {
				tl.loadDictionary();
				tl.translateFile(this.in_file,this.out_file,"utf-8");
			}
			else throw new FileNotFoundException();
		}
		else if(this.dict_path!=null&&this.method.toLowerCase().equals("user")) {
			Translate tl = new Translate(this.dict_type,this.method,this.source,this.target,this.dict_path);
			if(this.in_file!=null) {
				tl.loadDictionary();
				tl.translateFile(this.in_file,this.out_file,this.encoding);
			}
			else throw new FileNotFoundException();
		}
	}
	
	public static void main(String[] args) throws IOException {
		TransInit trans =new TransInit();
		trans._init_(args[0]);
	}
	
}
