package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.BasicTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;

public class Translate {
	public Lexicon lex;
	public String dict_path=null;
	public String method;
	public boolean use_wiki = false;
	public boolean use_embeddings = false;
	public Util util = new Util();
	//TODO:Implementing Embedding Initialization
	public Embedding embedding = null;
	public WikiMapping wiki = null;
	public NameListExtractor name_list=null;
	public Translate() {
		throw new IllegalArgumentException("Invalid Initiation");
	}

		//lex unspecified//
	public Translate(String dictionary,String method,String source,String target) {
		this.lex = new Lexicon(dictionary,source,target);
		this.method = method;
		this.dict_path = null;
	}


		//lex specified//
	public Translate(String dictionary,String method,String source,String target,String dict_path) {
		this.dict_path = dict_path;
		this.method = method;
		this.lex = new Lexicon(dictionary,dict_path,source,target);
	}


	public void loadDictionary () throws IOException {
		if (this.dict_path!=null) {
			this.lex.getLexiconMapping(this.lex.source, this.lex.target);
		}
		else if (this.method.equals("google")) {
			System.out.println("google Unimplemented");
		}
		else if (this.method.equals("lexicon")) {
			this.lex.getLexiconMapping(this.lex.source, this.lex.target);
		}
		else {
			System.out.println("other method Unimplemented");
		}
	}
	
	
	public String embeddingTranslate(String srcword) {
		if(srcword.matches("\\?|,|;|\\)|\\(|!|\\]|\\]|.|\"|\'|--|-|\\\\"))return srcword;	
		String oword = this.translateWord(srcword);
		if (!oword.equals(srcword)) return oword;
		else {
			String[] candidates = this.embedding.getCandidates(srcword);
			
			if(candidates==null){
				candidates = this.embedding.getCandidates(srcword.toLowerCase());
				if (candidates == null)return srcword;
			}
			
			for (String candidate: candidates) {
				if ((oword=this.translateWord(candidate)).equals(candidate))continue;
				else {return oword;}
			}
		}
		return srcword;
	}
	
	public String wikiTranslate(String srcword) {
		if(srcword.matches("\\?|,|;|\\)|\\(|!|\\]|\\]|.|\"|\'|--|-|\\\\"))return srcword;
		String oword = this.wiki.getWikiTitle(srcword);
		if (oword!=null)return oword;
		else return srcword;
	}
	
	public String translateWord (String srcword) {
		String oword = srcword;
		Dict hit = this.lex.lookUp(srcword);
		
		//Lower Case Lookup//
		if (hit == null) {
			hit = this.lex.lookUp(srcword.toLowerCase());
			if (hit!=null) srcword = srcword.toLowerCase();
		}

		if (hit == null&&this.lex.source.equals("eng")) {
			ArrayList<String> src_expand = this.util.engExpansion(srcword);
			//TODO: use vec, improve this algo
			for(String str : src_expand) {
				if((hit=this.lex.lookUp(str))!=null) {
					break;
				}
				else if((hit=this.lex.lookUp(str.toLowerCase()))!=null) {
					break;
				}
			}

		}

		if(hit !=null) {
			ArrayList<Scores> opts = hit.scores;
			Scores best = opts.get(0);
			double best_score = best.score;
			String best_word = best.str;
			for (Scores s : opts) {
				if(s.score==best_score&&s.str.equals(hit.k)==false) {
					best_word=s.str;
					break;
				}
				if(s.score<best_score) break;
			}

			return best_word;
		}
		else return oword;
		
	}
	
	
	public ArrayList<String> translateCONLL(ArrayList<String> lines) {
		ArrayList<String> outlines = new ArrayList<String>();
		int missing = 0;
		int total = 0;
		ArrayList<String> missed_words = new ArrayList<String>();

		int i = 0;
		double progress = 0;
		int window = 4;

		while(true) {

			double current_prog = i/(double)lines.size();
			if (current_prog > progress + 0.1){
				//System.out.println(current_prog);
				progress = current_prog;
			}

			if (i>=lines.size()) break;

			ArrayList<String> words = new ArrayList<String>();
			ArrayList<String> tags = new ArrayList<String>();

			for(String line: lines) {
				String word = this.util.getWord(line);
				String tag = this.util.getTag(line);
				if(word == null) break;

				words.add(word);
				tags.add(tag);
			}

			if (words.size()==0) {
				outlines.add("\n");
				i+=1;
				continue;
			}

			//TODO: check split//
			String[] sline = lines.get(i).split("\t");

			if (sline.length>5 && sline[5].equals("")) {
				sline[5] = "x";
			}

			ArrayList <String> trans_words= new ArrayList<String>();


			for(int jj = 0;jj<words.size();jj++) {
				//TODO: What does that mean?
				String srcword = words.get(jj);
				//System.out.println(srcword);
				String srctags = tags.get(jj);
				Dict hit = this.lex.lookUp(srcword);
				//System.out.println(hit);
				if (hit == null) {
					hit = this.lex.lookUp(srcword.toLowerCase());
					if (hit!=null) srcword = srcword.toLowerCase();
				}

				if (hit == null&&this.lex.source.equals("eng")) {
					ArrayList<String> src_expand = this.util.engExpansion(srcword);
					//TODO: use vec, improve this algo
					for(String str : src_expand) {
						if((hit=this.lex.lookUp(str))!=null) {
							break;
						}
						else if((hit=this.lex.lookUp(str.toLowerCase()))!=null) {
							break;
						}
					}

				}

				if (this.use_wiki&& hit==null&&srctags.substring(0, 1).equals("B")) {
					//System.out.println("Unimplemented");
				}

				if(hit !=null) {

					ArrayList<Scores> opts = hit.scores;

					int ngram = Math.min(3,outlines.size());
					ArrayList<String> context = new ArrayList<String>();
					for (int rev=outlines.size()-1;rev>0;rev--) {
						String c = this.util.getWord(outlines.get(rev));
						if (c==null) {
							context = null;
						}
						else {
							context.add(c);
						}
					}

					//TODO: LM
					//TODO: if we want to translate "two" to french word deux, our program
					//      will choose "two" instead of deux. However, deux should be the best
					//      translation. Hence, I decided to modify the algo to choose the word
					//      among those words with the highest score and the word must be different
					//      from the original word. If there is no words among highest score words
					//      that is different from the original word, we choose the hightest score word instead.

					Scores best = opts.get(0);
					double best_score = best.score;
					String best_word = best.str;
					for (Scores s : opts) {
						if(s.score==best_score&&s.str.equals(hit.k)==false) {
							best_word=s.str;
							break;
						}
						if(s.score<best_score) break;
					}

					if (outlines.size()>0 && this.util.getWord(outlines.get(outlines.size()-1))==null) {
						best_word = this.util.toCapitalize(best_word);
					}

					trans_words.add(best_word);


				}
				if(hit==null) {
					trans_words.add(srcword);
					missing+=1;
					missed_words.add(srcword);
				}
			}
			for(int x= 0 ;x<trans_words.size();x++) {
				outlines.add(this.util.setWord(lines.get(x), trans_words.get(x)));
				//System.out.println(outlines.get(x));
			}
			total=words.size();
			break;
		}
		return outlines;
	}

	public void translateFile (String fname, String outname,String format) throws IOException {
		ArrayList<String> lines = this.lex.ut.readFile(fname);
		ArrayList<String> results = this.translateCONLL(lines);
		this.lex.ut.writeFile(outname, results);

	}
	
	
	public List<TextAnnotation> processTAs(List<TextAnnotation> tas) throws FileNotFoundException, Exception {
		List<TextAnnotation> new_tas = new ArrayList<TextAnnotation>();
		int total_missed = 0;
		int total_translated = 0;
		int total_all = 0;
		for(TextAnnotation ta: tas) {
			
	    	View ner_view = ta.getView("NER_CONLL");
	    	List<Constituent>ner_cons = ner_view.getConstituents();
		    
	    	int missed = 0;
		    int translated = 0;
		    int total = 0;
		    System.out.println("Reading: "+ta.getId());
		    String[] tokens= ta.getTokens();
		    
		    View tk_view = ta.getView("TOKENS");
		       
		    List<TransMapping> mapping = new ArrayList<TransMapping>();
		    List<Constituent>cons = tk_view.getConstituents();
		    String result_word = "";
		    int offset = 0;
		    
		    //windowed translation
		    
		    for(int head = 0;head<tokens.length;) {
			//System.out.println(head);
			boolean trans_flag=false;
		    	for(int window=4;window>0;window--) {
		    		//tail not included,head included
		    		int tail = head+window;
		    		if (tail>tokens.length)continue;
		    		else {
		    			String [] sub = ta.getTokensInSpan(head, tail);
		    			String srcword = String.join(" ", sub);
		    			String origin_word = srcword;
		    			result_word = srcword;
					//System.out.println(srcword);
				    	//Translate the word according to method specification//
				    	if(this.use_wiki) result_word = this.wikiTranslate(srcword);
				    	if(this.use_embeddings && result_word.equals(srcword)) result_word = this.embeddingTranslate(srcword);
				    	if(!this.use_embeddings&&!this.use_wiki)result_word = this.translateWord(srcword);
				    	
				    	
				    	//If translated
				    	if (!result_word.equals(srcword)) {
				    		translated+=window;
				    		trans_flag=true;	
				    		int os = head;
							int oe = tail;
							String [] sword = result_word.split("\\s+");
							int ts = os + offset;
							int te = ts + sword.length;
							offset += sword.length-(oe-os);
							for(String s : srcword.split(" ")) {
								TransMapping tm = new TransMapping(s,result_word,os,os+1,ts,te,offset);
								mapping.add(tm);
								os++;
							}
							head+=window;
				    		break;
				    	}		
		    		}
		    	
		    	}
			if(trans_flag==false){
				//TODO:
				
				int os = head;
				int oe = head+1;
				String origin_word = ta.getToken(head);
				int ts = os + offset;
				int te = ts + 1;
				String trans_word = origin_word;
				for(Constituent con: ner_cons) {
					if (head>=con.getStartSpan()&&(head+1)<con.getEndSpan()&&con.getAttribute("label")=="PER") {
						trans_word = name_list.getRandomName();
						break;
					}
				}
				if(!origin_word.matches("\\?|,|;|\\)|\\(|!|\\]|\\]|.|\"|\'|--|-|\\\\"))missed+=1; 
				TransMapping tm = new TransMapping(origin_word,trans_word,os,oe,ts,te,offset);
				mapping.add(tm);
				head+=1;
			}	
		    }
		    
		    
		    
//		    for(Constituent con : cons ) {
//		    	int start = con.getStartSpan();
//		    	int end = con.getEndSpan();
//		    	String[] sub = new String[end-start];
//		    	
//		    	//String[] sub = ta.getTokensInSpan(start, end);
//		    	
//		    	for (int i = start; i<end; i++) {
//		    		sub[i-start] = tokens[i];
//		    	  	}
//		    	//String srcword = con.getTokenizedSurfaceForm();
//		    	String srcword = String.join(" ", sub);
//		    	String origin_word = srcword;
//		    	result_word = srcword;
//		    	//TODO: think carefully
//		    	if(this.use_wiki) result_word = this.wikiTranslate(srcword);
//		    	if(this.use_embeddings && result_word.equals(srcword)) result_word = this.embeddingTranslate(srcword);
//		    	if(!this.use_embeddings&&!this.use_wiki)result_word = this.translateWord(srcword);
//		    		
//			if(!srcword.matches("\\?|,|;|\\)|\\(|!|\\]|\\]|.|\"|\'|--|-|\\\\")){
//				total+=1;
//				if (!result_word.equals(srcword))translated+=1;
//				else missed+=1;
//			}	
//		    	int os = con.getStartSpan();
//				int oe = con.getEndSpan();
//				String [] sword = result_word.split("\\s+");
//				int ts = os + offset;
//				int te = ts + sword.length;
//				offset += sword.length-(oe-os);
//				TransMapping tm = new TransMapping(origin_word,result_word,os,oe,ts,te,offset);
//				mapping.add(tm);
//					
//		    }     
		    	List<String[]> tokenizedSentences = new ArrayList<String[]>();
		    	for(int i=0; i<ta.getNumberOfSentences();i++) {
		    		Sentence sen = ta.getSentence(i);
		    		int start = sen.getStartSpan();
		    		int end = sen.getEndSpan();
				int new_start = 0;
				int new_end = 0;
		    		for (TransMapping tm:mapping){
					if(tm.original_start==start)new_start = tm.translate_start;
					if(tm.original_end == end) new_end = tm.translate_end;
					if (tm.original_end>end)break;
				}
				//System.out.println("Origin: "+start+ " "+end + " New: "+new_start+ " "+new_end);
				List <String> tok_of_sen = new ArrayList<String>();
				int counter = 0;
				while(new_start<new_end){
					
		    		for(TransMapping tm : mapping) {
		    			if (tm.translate_start==new_start) {
		    				String[] tp = tm.translate_word.split("\\s+");
						for(String s:tp){
							tok_of_sen.add(s);
						}
						new_start = tm.translate_end;
		    			}
		    			
		    			if(tm.translate_end>new_end)break;
		    		}
				}
		    		String[] temp = new String[tok_of_sen.size()];
		    		for(int j = 0; j <tok_of_sen.size(); j++) {
		    			temp[j] = tok_of_sen.get(j);
		    		}
		    		tokenizedSentences.add(temp);
		    	}
		    	
		    	
//		        for (TextAnnotation ta :tas) {
//		        	TextAnnotation translated_ta = processTA(ta);
		        //}
		       	FileOutputStream output_stream = new FileOutputStream("/home/yihaoc/data/mappings.txt");
				OutputStreamWriter writer = new OutputStreamWriter(output_stream,"UTF-8");
				BufferedWriter bw = new BufferedWriter(writer);
		       	for(TransMapping tm :mapping) {
				bw.write(tm.toString());
					bw.newLine();
				}
		       	bw.close();
		    	BasicTextAnnotationBuilder btab = new BasicTextAnnotationBuilder();
		    	TextAnnotation new_ta = BasicTextAnnotationBuilder.createTextAnnotationFromTokens(ta.getCorpusId(), ta.getId(), tokenizedSentences);
		    	
		    	//System.out.println(new_ta.getTokens().length);
		    	View cheap_trans = new View(ViewNames.NER_CONLL,"CheapTrans",new_ta,1.0);
		    	//List<Constituent>new_ner_cons = new ArrayList<Constituent>();
		    	for (Constituent con: ner_cons) {
		    		int start = con.getStartSpan();
		    		int end = con.getEndSpan();
		    		String[] sub = new String[end-start];
		    		
		    		for (int i = start; i<end; i++) {
		    			sub[i-start] = tokens[i];
		         	}
		    		String srcword = String.join(" ", sub);
		       		int new_start = 0;
		    		int new_end = 0;
		       		for (TransMapping tm :mapping) {
		       			if (start==tm.original_start ) {
		       				new_start = tm.translate_start;
		       			}
		       			if (end == tm.original_end ) {
		       				new_end = tm.translate_end;
		       				break;
		       			}
		       		}
			//	System.out.println(new_start+" "+new_end);
		       		Constituent new_con = new Constituent(con.getLabel(),con.getConstituentScore(),con.getViewName(),new_ta,new_start,new_end);
		       		cheap_trans.addConstituent(new_con);
		       	}
		       	new_ta.addView(ViewNames.NER_CONLL, cheap_trans);
		       	new_tas.add(new_ta);
			System.out.println("Total: "+total+" Translated: "+translated+" Missed: "+missed );
			total_all+=total;
			total_translated+=translated;
			total_missed+=missed;
	}
		System.out.println("-----------------------------------------------------");
		System.out.println("Translation Statistics");
		System.out.println("-----------------------------------------------------");
		System.out.println("Total: "+total_all+" Translated: "+total_translated+" Missed: "+total_missed );
		return new_tas;
	}
	public void translateTAs(String inpath, String outpath) throws Exception {
		File tapath = new File(inpath);
        File[] filelist = tapath.listFiles();
        List<TextAnnotation> tas = new ArrayList<>();
        for (File f : filelist) {
            TextAnnotation ta = SerializationHelper.deserializeTextAnnotationFromFile(f.getAbsolutePath(), true);
            tas.add(ta);
            
        }

        List<TextAnnotation>new_tas = processTAs(tas);
        if(!(new File(outpath)).exists()) {
            IOUtils.mkdir(outpath);
        }


        for (TextAnnotation ta : new_tas) {
	    System.out.println("Writing:" +ta.getId());
            SerializationHelper.serializeTextAnnotationToFile(ta, outpath + "/" + ta.getId(), true, true);
        }
        System.out.println(String.format("Wrote %d textannotations to %s", filelist.length, outpath));
    
	}
	public static void main (String[] args) throws Exception {
		
		
		String inpath = args[0];
		String inlang = args[1];
		String outpath = args[2];
		String outlang = args[3];
		String format = args[4];
		String dictpath = args[5];
		String translate_method = args[6];
		String path1 = args[7];
		String path2 = args[8];
		String path3 = args[9];
		//TODO: method specification
		switch (format) {
		case "-c": 
			Translate tl_conll = new Translate ("USEPAVLICK","lexicon",inlang,outlang,dictpath);
			if (translate_method.contains("e")&&!translate_method.contains("w")) {
				tl_conll.use_embeddings=true;
				tl_conll.embedding=new Embedding(path1);
				//System.out.println(path1);
			}
			else if (!translate_method.contains("e")&&translate_method.contains("w")) {
				tl_conll.use_wiki=true;
				tl_conll.wiki=new WikiMapping(inlang,outlang,path1);
				//System.out.println(path1);
			}
			else if (translate_method.contains("e")&&translate_method.contains("w")) {
				tl_conll.use_wiki=true;
				tl_conll.use_embeddings=true;
				if(translate_method.indexOf('e')<translate_method.indexOf('w')) {
					tl_conll.embedding=new Embedding(path1);
					tl_conll.wiki=new WikiMapping(inlang,outlang,path2);
				}
				else {
					tl_conll.embedding=new Embedding(path2);
					tl_conll.wiki=new WikiMapping(inlang,outlang,path1);
				}
			}
			tl_conll.loadDictionary();
			tl_conll.translateFile(inpath,outpath,"UTF-8");
			break;
		case "-t":
			Translate tl_TA = new Translate ("USEPAVLICK","lexicon",inlang,outlang,dictpath);
			tl_TA.name_list=new NameListExtractor(args[9]);
			if (translate_method.contains("e")&&!translate_method.contains("w")) {
				tl_TA.use_embeddings=true;
				tl_TA.embedding=new Embedding(path1);
				//System.out.println(path1);
			}
			else if (!translate_method.contains("e")&&translate_method.contains("w")) {
				tl_TA.use_wiki=true;
				tl_TA.wiki=new WikiMapping(inlang,outlang,path1);
				//System.out.println(path1);
			}
			else if (translate_method.contains("e")&&translate_method.contains("w")) {
				tl_TA.use_wiki=true;
				tl_TA.use_embeddings=true;
				if(translate_method.indexOf('e')<translate_method.indexOf('w')) {
					tl_TA.embedding=new Embedding(path1);
					tl_TA.wiki=new WikiMapping(inlang,outlang,path2);
				}
				else {
					tl_TA.embedding=new Embedding(path2);
					tl_TA.wiki=new WikiMapping(inlang,outlang,path1);
				}
				
				//System.out.println(path1);
			}
			tl_TA.loadDictionary();
			tl_TA.translateTAs(inpath,outpath);
			break;
		default: System.out.println("Format Unsupported");
		}
	}

}
