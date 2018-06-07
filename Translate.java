import java.io.IOException;
import java.util.ArrayList;

public class Translate {
	public Lexicon lex;
	public String dict_path=null;
	public String method;
	public boolean use_tag_list = false;
	public boolean use_vecs = false;
	public Util util = new Util();
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
			System.out.println("dict_path Read Unimplemented");
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
	
	public ArrayList<String> translate(ArrayList<String> lines) {
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
				String srcwords = words.get(jj);
				//System.out.println(srcwords);
				String srctags = tags.get(jj);
				Dict hit = this.lex.lookUp(srcwords);
				//System.out.println(hit);
				if (hit == null) {
					hit = this.lex.lookUp(srcwords.toLowerCase());
					if (hit!=null) srcwords = srcwords.toLowerCase();
				}
				
				if (hit == null&&this.lex.source.equals("eng")) {
					ArrayList<String> src_expand = this.util.engExpansion(srcwords);
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
				
				if (this.use_tag_list&& hit==null&&srctags.substring(0, 1).equals("B")) {
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
					trans_words.add(srcwords);
					missing+=1;
					missed_words.add(srcwords);
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
		ArrayList<String> results = this.translate(lines);
		this.lex.ut.writeFile(outname, results);
		
	}
	public static void main (String[] args) throws IOException {
		Translate tl = new Translate("USEPAVLICK","lexicon",args[1],args[3]);
		
		tl.loadDictionary();
		tl.translateFile(args[0],args[2],"utf-8");
	}
	
}
