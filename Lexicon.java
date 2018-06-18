package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.lang.Math;

public class Lexicon {
	public String lex_path;
	public String target;
	public String source;
	public String dictionary;
	public LinkedHashMap<String,ArrayList<String>> e2f = new LinkedHashMap<String,ArrayList<String>>();
	public LinkedHashMap<String,ArrayList<String>> f2e = new LinkedHashMap<String,ArrayList<String>>();
	public LinkedHashMap<Map<String,String>, Integer> pairs = new LinkedHashMap<Map<String,String>, Integer>();
	public ArrayList<Dict> dct = new ArrayList<Dict>();
	public Util ut = new Util();

	public Lexicon() {
		this.dictionary = "USEPAVLICK";
		this.source = "eng";
		this.target = "spa";

		File f = new File ("");
		String path = f.getAbsolutePath();
		this.lex_path =  path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + "es";
	}

	public Lexicon(String source,String target) {
		this.dictionary = "USEPAVLICK";
		this.source = source;
		this.target = target;

		File f = new File ("");
		String path = f.getAbsolutePath();
		if (target.equals("eng"))
		this.lex_path = path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + ut.langMapping(source);
		else this.lex_path = path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + ut.langMapping(target);
	}

	public Lexicon(String dictionary,String source,String target) {
		this.source = source;
		this.target = target;
		this.dictionary = dictionary;

		File f = new File ("");
		String path = f.getAbsolutePath();
		if (dictionary.equals ("USEMASTERLEX"))
			this.lex_path = path + "{0}-eng.masterlex.txt.gz";
		else if (dictionary.equals("USEPAVLICK")) {
			if (target.equals("eng"))
				this.lex_path = path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + ut.langMapping(source);
			else 
				this.lex_path = path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + ut.langMapping(target);
		}

	}

	public Lexicon(String dictionary, String path,String source,String target) {
		this.source = source;
		this.target = target;
		if (dictionary.equals ("USEMASTERLEX"))
			this.lex_path = path + "{0}-eng.masterlex.txt.gz";
		else if (dictionary.equals("USEPAVLICK")) {
			if (target.equals("eng"))
				this.lex_path = path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + ut.langMapping(source);
			else 
				this.lex_path = path + "/src/main/java/edu/illinois/cs/cogcomp/lorelei/cheaptrans/dictionaries/dict." + ut.langMapping(target);
		}
	}

	private ArrayList<String> splitLines(ArrayList<String> lines) {
		ArrayList<String> new_lines = new ArrayList<String>();
		for (String line:lines) {
			String[] split_line = line.split("\t");
			for(int i=1;i<split_line.length;i++) {
				new_lines.add(split_line[0]+"\t"+split_line[i]);
			}
		}
		return new_lines;
	}

	private ArrayList<String[]> product (String[] lst1, String[] lst2){
		ArrayList<String[]> product_lst = new ArrayList<String[]>();
		for(String i: lst1) {
			for (String j: lst2) {
				String[] temp = {i,j};
				product_lst.add(temp);
			}
		}
		return product_lst;
	}

	public void readLexicon() throws FileNotFoundException, IOException {
		ArrayList<String> raw_lines=null;

		switch (this.dictionary) {
		case "USEMASTERLEX":
			throw new IllegalArgumentException("USEMASTERLEX Unimplemented");
		case "USEPAVLICK":
			raw_lines = this.ut.readFile(this.lex_path);
			break;
		default:
			throw new IllegalArgumentException("Invalid dictionary");
		}

		ArrayList<String> new_lines = this.splitLines(raw_lines);

		for (String line: new_lines) {
			String[] split_line = line.split("\t");
			String eng = "";
			String foreign = "";

			switch (this.dictionary) {
			case "USEMASTERLEX":
				foreign = split_line[0];
				eng = split_line[5];
				break;
			case "USEPAVLICK":
				foreign = split_line[0];
				eng = split_line[1];
				break;
			default:
				throw new IllegalArgumentException("Invalid dictionary");
			}

			Map<String,String> temp_pairs = new LinkedHashMap<String,String>();
			temp_pairs.put(eng,foreign);

			Map<String,String> temp_lowercase = new LinkedHashMap<String,String>();
			temp_lowercase.put(eng.toLowerCase(), foreign.toLowerCase());

			if(this.pairs.containsKey(temp_pairs))
				this.pairs.put(temp_pairs, this.pairs.get(temp_pairs)+1);
			else this.pairs.put(temp_pairs, 1);

			if(this.pairs.containsKey(temp_lowercase))
				this.pairs.put(temp_lowercase, this.pairs.get(temp_lowercase)+1);
			else this.pairs.put(temp_lowercase, 1);

			String[] ewords = eng.split("\\s+");
			String[] fwords = foreign.split("\\s+");
			ArrayList<String[]> product_lst = this.product(ewords, fwords);

			for(String[] pair: product_lst) {
				Map<String,String> entry = new LinkedHashMap<String,String>();
				entry.put(pair[0],pair[1]);

				if(this.pairs.containsKey(entry)) {
					this.pairs.put(entry, this.pairs.get(entry)+1);
					}
				else this.pairs.put(entry, 1);
				Map<String,String> entry_lowercase = new LinkedHashMap<String,String>();
				entry_lowercase.put(pair[0].toLowerCase(), pair[1].toLowerCase());

				if(this.pairs.containsKey(entry_lowercase))
					this.pairs.put(entry_lowercase, this.pairs.get(entry_lowercase)+1);
				else this.pairs.put(entry_lowercase, 1);
			}

			if(this.f2e.containsKey(foreign)) {
				this.f2e.get(foreign).add(eng);
			}
			else {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(eng);
				this.f2e.put(foreign, temp);
			}

			if(this.f2e.containsKey(foreign.toLowerCase())) {
				this.f2e.get(foreign.toLowerCase()).add(eng);
			}
			else {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(eng);
				this.f2e.put(foreign.toLowerCase(), temp);
			}

			if(this.e2f.containsKey(eng)) {
				this.e2f.get(eng).add(foreign);
			}
			else {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(eng);
				this.e2f.put(eng, temp);
			}
			if(this.e2f.containsKey(eng.toLowerCase())) {
				this.e2f.get(eng.toLowerCase()).add(foreign);
			}
			else {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(eng);
				this.e2f.put(eng.toLowerCase(), temp);
			}
		}
	}


	public void getLexiconMapping(String source, String target) throws IOException {
		//eng to lang translation//

		if (source.equals( "eng")) {
			this.readLexicon();

			for (String k : this.e2f.keySet()) {
				Map<String, Integer> scores = new LinkedHashMap<String, Integer>();

				ArrayList<String>e2f_k = this.e2f.get(k);
				for (String w : e2f_k) {
					Map<String,String> temp = new LinkedHashMap<String,String>();
					temp.put(k, w);
					//System.out.println(temp.toString()+this.pairs.containsKey(temp));
					if(this.pairs.containsKey(temp)) {
						scores.put(w,this.pairs.get(temp));
					}
				}

				int sum = 0;
				for(Entry<String,Integer>entry: scores.entrySet()) {
					sum+=entry.getValue();
				}

				double t1 = Math.max(0.1,(double)sum);

				//ascending sorted//
				scores.entrySet();
				ArrayList<Scores> nscore = new ArrayList<Scores>();

				for(Entry<String,Integer>entry: scores.entrySet()) {
					//System.out.print(entry.getValue()+" "+(double)entry.getValue()/t1+"\n");
					nscore.add(new Scores(entry.getKey(),(double)entry.getValue()/t1));
					//System.out.println("value:"+entry.getValue());
				}

				Collections.sort(nscore,new ScoresRevOrder());
				this.dct.add(new Dict(k,nscore));
			}
			//String out = this.dct.toString();
			

		}


		//lang to eng translation//
		else if (target.equals("eng")) {
			this.readLexicon();
			
			for (String k : this.f2e.keySet()) {
				
				Map<String, Integer> scores = new LinkedHashMap<String, Integer>();

				ArrayList<String>f2e_k = this.f2e.get(k);
				
				for (String w : f2e_k) {
					//System.out.println(w);
					Map<String,String> temp = new LinkedHashMap<String,String>();
					temp.put(w, k);
					//System.out.println(temp.toString()+this.pairs.containsKey(temp));
					if(this.pairs.containsKey(temp)) {
						scores.put(w,this.pairs.get(temp));
					}
				}

				int sum = 0;
				for(Entry<String,Integer>entry: scores.entrySet()) {
					sum+=entry.getValue();
				}

				double t1 = Math.max(0.1,(double)sum);

				//descending sorted//

				ArrayList<Scores> nscore = new ArrayList<Scores>();

				for(Entry<String,Integer>entry: scores.entrySet()) {
					//System.out.print(entry.getValue()+" "+(double)entry.getValue()/t1+"\n");
					nscore.add(new Scores(entry.getKey(),(double)entry.getValue()/t1));
					//System.out.println("value:"+entry.getValue());
				}

				Collections.sort(nscore,new ScoresRevOrder());
				this.dct.add(new Dict(k,nscore));
			}
			String out = this.dct.toString();
			
		}

		//other language translation//
		else {
			Lexicon lex_src = new Lexicon("eng",source);
			Lexicon lex_tar = new Lexicon("eng",target);
			lex_src.readLexicon();
			lex_tar.readLexicon();

			Set<String> src_keys = lex_src.e2f.keySet();
			Set<String> tar_keys = lex_tar.e2f.keySet();

			Set<String> intersect = new HashSet<String>();
			intersect.clear();
			intersect.addAll(src_keys);
			intersect.retainAll(tar_keys);

			for (String s : intersect) {
				Map<String, Integer> scores_src = new LinkedHashMap<String, Integer>();
				Map<String, Integer> scores_tar = new LinkedHashMap<String, Integer>();
				ArrayList<String>src_e2f_k = lex_src.e2f.get(s);
				ArrayList<String>tar_e2f_k = lex_tar.e2f.get(s);

				for (String w : src_e2f_k) {
					Map<String,String> temp = new LinkedHashMap<String,String>();
					temp.put(s, w);
					//System.out.println(temp.toString()+this.pairs.containsKey(temp));
					if(this.pairs.containsKey(temp)) {
						scores_src.put(w,this.pairs.get(temp));
					}
				}

				for (String w : tar_e2f_k) {
					Map<String,String> temp = new LinkedHashMap<String,String>();
					temp.put(s, w);
					//System.out.println(temp.toString()+this.pairs.containsKey(temp));
					if(this.pairs.containsKey(temp)) {
						scores_tar.put(w,this.pairs.get(temp));
					}
				}

				int sum = 0;
				for(Entry<String,Integer>entry: scores_src.entrySet()) {
					sum+=entry.getValue();
				}
				double t1 = Math.max(0.1,(double)sum);

				sum=0;
				for(Entry<String,Integer>entry: scores_tar.entrySet()) {
					sum+=entry.getValue();
				}
				double t2 = Math.max(0.1,(double)sum);

				//descending sorted//

				ArrayList<Scores> nscore_src = new ArrayList<Scores>();
				ArrayList<Scores> nscore_tar = new ArrayList<Scores>();

				for(Entry<String,Integer>entry: scores_src.entrySet()) {
					//System.out.print(entry.getValue()+" "+(double)entry.getValue()/t1+"\n");
					nscore_src.add(new Scores(entry.getKey(),(double)entry.getValue()/t1));
					//System.out.println("value:"+entry.getValue());
				}

				for(Entry<String,Integer>entry: scores_tar.entrySet()) {
					//System.out.print(entry.getValue()+" "+(double)entry.getValue()/t1+"\n");
					nscore_tar.add(new Scores(entry.getKey(),(double)entry.getValue()/t1));
					//System.out.println("value:"+entry.getValue());
				}

				Collections.sort(nscore_src,new ScoresRevOrder());
				Collections.sort(nscore_tar,new ScoresRevOrder());

				//TODO: +=???
				for(Scores s_src : nscore_src) {
					ArrayList<Scores> new_scores = new ArrayList<Scores>();
					for (Scores s_tar:nscore_tar) {
						Scores temp = new Scores(s_tar.str,s_tar.score*s_src.score);
						new_scores.add(temp);
					}
					this.dct.add(new Dict(s_src.str,new_scores));
				}
			}
		}
	}

	public Dict lookUp (String word) {
		for(Dict i: this.dct) {
			//System.out.println(i.k+":"+word);
			if (i.k.equals(word)) return i;
		}
		return null;

	}

	public void getFAfile(String target) throws FileNotFoundException, IOException {

		Lexicon lex = new Lexicon("eng",target);
		lex.readLexicon();
		FileOutputStream output_stream = new FileOutputStream("text.eng-"+target);
		OutputStreamWriter writer = new OutputStreamWriter(output_stream,"UTF-8");
		BufferedWriter bw = new BufferedWriter(writer);

		for(Dict dict : lex.dct) {
			ArrayList<Scores> scores = dict.scores;
			for (Scores s:scores) {
				bw.write(dict.k.toLowerCase()+" ||| "+ s.str.toLowerCase()+"\n");
			}
		}

		bw.close();
	}

	public void readWriteTest () throws IOException {
		Lexicon lex = new Lexicon();
		lex.readLexicon();
		FileOutputStream output_stream = new FileOutputStream("e2f.txt");
		OutputStreamWriter writer = new OutputStreamWriter(output_stream,"UTF-8");
		BufferedWriter bw = new BufferedWriter(writer);

		for (Entry<String, ArrayList<String>> entry:lex.e2f.entrySet()) {
			bw.write(entry.getKey()+"\t"+entry.getValue());
			bw.newLine();
		}

		output_stream = new FileOutputStream("f2e.fr");
		writer = new OutputStreamWriter(output_stream,"utf-8");
		bw = new BufferedWriter(writer);

		for (Entry<String, ArrayList<String>> entry:lex.f2e.entrySet()) {
			bw.write(entry.getKey()+"\t"+entry.getValue());
			bw.newLine();
			//System.out.println(entry.getKey()+"\t"+entry.getValue());
		}

		output_stream = new FileOutputStream("pairs.txt");
		writer = new OutputStreamWriter(output_stream,"UTF-8");
		bw = new BufferedWriter(writer);

		for (Entry<Map<String,String>, Integer> entry:lex.pairs.entrySet()) {
			bw.write(entry.getKey()+"\t"+entry.getValue());
			bw.newLine();
		}
		bw.close();
		
		output_stream = new FileOutputStream("dict.txt");
		writer = new OutputStreamWriter(output_stream,"UTF-8");
		bw = new BufferedWriter(writer);

		for (Dict d: this.dct) {
			bw.write(d.toString());
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Lexicon lex = new Lexicon();
		//lex.readWriteTest();
		lex.getLexiconMapping("spa", "eng");
		lex.readWriteTest();
	}
}
