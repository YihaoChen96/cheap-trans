import java.util.*;
import java.io.*;

public class Util {
	private Map<String,String> lang_mp = new HashMap<String,String> ();
	private String charset;
	public Util() {
		this.lang_mp.put("eng","en");
		this.lang_mp.put("ben","bn");
		this.lang_mp.put("hin","hi");
		this.lang_mp.put("mal","ml");
		this.lang_mp.put("nld","nl");
		this.lang_mp.put("rus","ru");
		this.lang_mp.put("spa","es");
		this.lang_mp.put("tam","ta");
		this.lang_mp.put("tgl","tl");
		this.lang_mp.put("tur","tr");
		this.lang_mp.put("uig","ug");
		this.lang_mp.put("uzb","uz");
		this.lang_mp.put("yor","yo");
		this.lang_mp.put("deu","de");
		this.lang_mp.put("fra","fr");
		
		this.charset = "UTF-8";
	}
	
	public String langMapping(String key_lang) {
		return this.lang_mp.get(key_lang);
	}
	
	public ArrayList<String> readFile(String fname) throws IOException {
		ArrayList<String> str_lst=new ArrayList<String>();
		String str;
		FileInputStream input_stream = new FileInputStream(fname);
		InputStreamReader reader = new InputStreamReader(input_stream,this.charset);
		BufferedReader br = new BufferedReader(reader);
		while ((str = br.readLine())!= null) {
			str_lst.add(str);
		}
		
		br.close();
		reader.close();
		return str_lst;
	}
	
	public void writeFile(String outfname, ArrayList<String> outlines) throws IOException {
		FileOutputStream output_stream = new FileOutputStream(outfname);
		OutputStreamWriter writer = new OutputStreamWriter(output_stream, this.charset);
		BufferedWriter bw = new BufferedWriter(writer);
		outlines.trimToSize();
		
		for (int i=0; i<outlines.size();i++) {
			bw.write(outlines.get(i));
		}
		
		bw.close();
		writer.close();
	}
	public String getWord(String line) {
		String[] line_arr = line.split("\t");
		if (line_arr.length>5) return line_arr[5];
		else return null;
	}
	
	public String setWord(String line,String word) {
		String[] line_arr = line.split("\t");
		if (line_arr.length>5) {
			line_arr[5]=word;
			return String.join("\t", line_arr)+"\n";
		}
		else return line;
	}
	
	public String getTag(String line) {
		String[] line_arr = line.split("\t");
		if (line_arr.length>5) return line_arr[0];
		else return null;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public String getCharset() {
		return this.charset;
	}
	
	public String toCapitalize(String str) {  
	    char[] charArray = str.toCharArray();  
	    charArray[0] -= 32;  
	    return String.valueOf(charArray);  
	}  
	
	public ArrayList<String> engExpansion(String str) {
		ArrayList<String> exp = new ArrayList<String>(); 
		if(str.endsWith("s")) {
			exp.add(str.substring(0,str.length()-1));
		}
		if(str.endsWith("ed")) {
			exp.add(str.substring(0,str.length()-2));
		}
		return exp;
	}
	public static void main (String[] args) throws IOException {
		Util u = new Util();
		ArrayList<String> lst = u.readFile(args[0]);
		System.out.println(lst);
	}
	
	
	
}
