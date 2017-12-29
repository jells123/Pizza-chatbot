package pl.poznan.put.cs.si.puttalky;

import java.util.HashSet;
import java.util.Set;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class Fakt {
	
	private String nazwa;
	private Set<String> wartosci;
	
	public Fakt(){
	    this.wartosci=new HashSet<String>();
    }
	
	public Fakt(String nazwa, String wartosc)
	{
		this.nazwa=nazwa;
		this.wartosci=new HashSet<String>();
		this.wartosci.add(wartosc);
	}

    public Fakt(String nazwa, Set<String> wartosci)
    {
        this.nazwa=nazwa;
        this.wartosci=new HashSet<String>();
        for (String s : wartosci) {
            this.wartosci.add(s);
        }
    }

	
	public String getNazwa() {
        return this.nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Set<String> getWartosci() {
        return this.wartosci;
    }

    public String getWartosciString() {
	    String result = new String("");
	    for (String s : this.wartosci) {
	        result += (s + " ");
        }
        return result;
    }

    public String getWartosc() {
	    if (this.wartosci.size() == 1) {
	        for (String s : this.wartosci)
	            return s;
        }
        else if (this.wartosci.size() == 0)
            return "brak wartości!";

	    return "więcej niż jedna wartość!";
    }

    public void setWartosci(Set<String> wartosci) {
        this.wartosci = wartosci;
    }

    public void setWartosc(String wartosc) {
        this.wartosci.add(wartosc);
    }
	

}
