package pl.poznan.put.cs.si.puttalky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import morfologik.stemming.IStemmer;
import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class Parser {
	
	private String wypowiedz;
	private String[] slowaKluczowe;

	public static String[] slowaNiewazne = {
		"poprosić", "pizza", "prosić", "dodatek", "bez"
	};
	
	public Parser(){}
	
	public Parser(String wypowiedz)
	{
		this.wypowiedz=wypowiedz;
	}

	public String getWypowiedz() {
		return wypowiedz;
	}

	public void setWypowiedz(String wypowiedz) {
		this.wypowiedz = wypowiedz;
	}

	public String[] getSlowaKluczowe() {
		return slowaKluczowe;
	}

	public void setSlowaKluczowe(String[] slowaKluczowe) { this.slowaKluczowe = slowaKluczowe; }

	public void processSlowoBez() {
		String[] words = this.slowaKluczowe;
		int findBez = Arrays.asList(words).indexOf("bez");
		if ( findBez != -1 ) {
			for (int i = findBez + 1; i < words.length; i++) {
				words[i] = "-" + words[i];
			}
		}
	}

	public void przetworzOdpowiedz()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String buffer="";
		try {
			buffer = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSlowaKluczowe(parsuj(buffer.toString()));
	}

	public boolean jestDoPominiecia(String s) {
		if (s.length() <= 1)
			return true;
		s = s.toLowerCase();
		for (String slowo : Parser.slowaNiewazne) {
			if (s.equals(slowo))
				return true;
		}
		return false;
	}

	
	public String[] parsuj (String wypowiedz) {
		String[] slowa = wypowiedz.split(" ");
		ArrayList<String> tokeny = new ArrayList<String>();
		
		PolishStemmer s = new PolishStemmer();
		boolean bezFound = false;

		for (String slowo : slowa) {

			String token = new String("");
			String fat = new String("grube");

			if (stem(s, slowo).length>1)
				token = stem(s, slowo)[0];
			else
				token = slowo.toLowerCase();

			if (slowo.equals(fat))
				token = new String("gruby");

			if (slowo.equals("bez"))
				bezFound = true;

			if (jestDoPominiecia(token))
				continue;

			if (bezFound)
				token = "-" + token;

			tokeny.add(token);

		}
	    return tokeny.toArray(new String[tokeny.size()]);
	}
	
	public static String[] stem(IStemmer s, String slowo) {
	    ArrayList<String> result = new ArrayList<String>();
	    for (WordData wd : s.lookup(slowo)) {
	      result.add(wd.getStem().toString());
	      result.add(wd.getTag().toString());
	    }
	    return result.toArray(new String[result.size()]);
	  }
	
    public static final void main(String[] args) {
        try {
        	Parser p = new Parser();
        	String[] sparsowane = p.parsuj("grube gruby gruba grubi");
        	System.out.println(String.join(" ", sparsowane));
        	// problem, wegetariańska nie zmienia się na wegetariański...
        
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
	
}
