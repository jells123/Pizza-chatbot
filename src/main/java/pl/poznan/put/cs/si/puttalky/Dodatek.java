package pl.poznan.put.cs.si.puttalky;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class Dodatek extends Formatka {

	boolean found = false;

	public void zadajPytanie() {
		System.out.println(getMonit()); 	
	}

	public boolean znalezionoDodatek() {
		return this.found;
	}
	public void setZnaleziono(boolean x) {
		this.found = x;
	}
}
