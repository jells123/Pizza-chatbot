package pl.poznan.put.cs.si.puttalky;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.api.runtime.KieSession;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class BazaWiedzy {

    private OWLOntologyManager manager = null;
    private OWLOntology ontologia;

    //private Set<OWLClass> listaKlas;
    private Map<OWLClass, String[]> listaKlas;
    private Set<OWLClass> listaDodatkow;

    private Set<OWLClass> listaPizzow;

    private Set<String> listaNazwanychPizzow;
    private Set<String> listaTypowPizzow;

	OWLReasoner silnik;
    
    public void inicjalizuj(Parser p) {
		InputStream plik = this.getClass().getResourceAsStream("/pizza.owl");
		manager = OWLManager.createOWLOntologyManager();
		
		try {
			ontologia = manager.loadOntologyFromOntologyDocument(plik);
			silnik = new Reasoner.ReasonerFactory().createReasoner(ontologia);

			listaKlas = new HashMap<OWLClass, String[]>();
			for (OWLClass l : ontologia.getClassesInSignature()) {
			    listaKlas.put(l, getSlowaKluczoweKlasy(l, p));
            }

			listaDodatkow = new HashSet<OWLClass>();
			listaPizzow = new HashSet<OWLClass>();

            listaTypowPizzow = new HashSet<String>();
            listaNazwanychPizzow = new HashSet<String>();

            OWLClass dodatek  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#Dodatek"));
			for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(dodatek, false)) {
                if (klasa.getRepresentativeElement().toString().equals("owl:Nothing")) {
                    continue;
                }
				listaDodatkow.add(klasa.getRepresentativeElement());
			}
			OWLClass pizza  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#Pizza"));

			for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(pizza, false)) {
				if (klasa.getRepresentativeElement().toString().equals("owl:Nothing")) {
					continue;
				}
				OWLClass pizzaFound = klasa.getRepresentativeElement();
				listaPizzow.add(pizzaFound);

                OWLClass temp  = manager.getOWLDataFactory().getOWLClass(IRI.create(pizzaFound.toString().substring(1, pizzaFound.toString().length()-1)));
                int howManySubclasses = silnik.getSubClasses(temp, false).getNodes().size();

                if (howManySubclasses == 1) {
                    listaNazwanychPizzow.add(temp.getIRI().getFragment());
                }
                else {
                    listaTypowPizzow.add(temp.getIRI().getFragment());
                }

            }
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

	public Set<String> dopasujPizze(String s){ //meh ten parser... ??? jak to zrobić mądrzej
		Set<String> result = new HashSet<String>();
		if (s.equals("pizza"))
			return result;
		for (OWLClass klasa : listaPizzow){
			String[] pizzaParsed = listaKlas.get(klasa);
            if (String.join("", pizzaParsed).toLowerCase().contains(s.toLowerCase()) && s.length()>2){
                result.add(klasa.getIRI().getFragment());
            }
		}

		return result;
	}

	public boolean jestTypemPizzy(String pizza) {
        for (String p : listaTypowPizzow) {
            if (p.toString().toLowerCase().contains(pizza.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean jestNazwanaPizza(String pizza) {
        for (String p : listaNazwanychPizzow) {
            if (p.toString().toLowerCase().contains(pizza.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String[] getSlowaKluczoweKlasy(OWLClass klasa, Parser p) {
        int hashIdx = klasa.toString().indexOf('#');
        String classPartWords = String.join(" ", klasa.toString().substring(hashIdx + 1, klasa.toString().length() - 1).split("(?=\\p{Upper})"));
        String[] classParsed = p.parsuj(classPartWords.toLowerCase());

        return classParsed;
    }

    public Set<String> dopasujZakazanyDodatek(String s) {
        Set<String> result = new HashSet<String>();
        if (s.indexOf('-') == 0) {
            return dopasujDodatek(s.substring(1));
        }
        return result;
    }

    public Set<String> dopasujZakazanaPizze(String s) {
        Set<String> result = new HashSet<String>();
        if (s.indexOf('-') == 0) {
            return dopasujPizze(s.substring(1));
        }
        return result;
    }
    
    public Set<String> dopasujDodatek(String s){
    	Set<String> result = new HashSet<String>();
    	if (s.equals("pizza"))
    	    return result;
    	for (OWLClass klasa : listaDodatkow){
    	    String[] dodatekParsed = listaKlas.get(klasa);
    	    for (String d : dodatekParsed) {
                if (d.equals(s)) { // po obu stronach sparsowane?
                    result.add(klasa.getIRI().getFragment().toString());
                }
            }
    		/*if (klasa.toString().toLowerCase().contains(s.toLowerCase()) && s.length()>2){
                result.add(klasa.getIRI().getFragment().toString());
    		}*/
    	}
    	return result;
    }

    public Set<String> wyszukajPizzePoTypie(String iriPart) {
        Set<String> pizze = new HashSet<String>();
        OWLClass typ = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#" + iriPart));
        for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(typ, false)) {
            pizze.add(klasa.getEntities().iterator().next().asOWLClass().getIRI().getFragment());
        }
        return pizze;
    }

    public Set<String> wyszukajPizzePoWieluDodatkach(Set<String> dodatki){
        Set<String> pizze = new HashSet<String>();
        OWLObjectProperty maDodatek = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#maDodatek"));
        Set<OWLClassExpression> ograniczeniaEgzystencjalne = new HashSet<OWLClassExpression>();

        for (String d : dodatki) {
            OWLClass dodatek = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#" + d));
            OWLClassExpression wyrazenie = manager.getOWLDataFactory().getOWLObjectSomeValuesFrom(maDodatek, dodatek);
            ograniczeniaEgzystencjalne.add(wyrazenie);
        }

        OWLClassExpression pozadanaPizza = manager.getOWLDataFactory().getOWLObjectIntersectionOf(ograniczeniaEgzystencjalne);
        for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(pozadanaPizza, false)) {
            pizze.add(klasa.getEntities().iterator().next().asOWLClass().getIRI().getFragment());
        }

        return pizze;
    }
	
	public static void main(String[] args) {
		BazaWiedzy baza = new BazaWiedzy();
		//baza.inicjalizuj();
		
		OWLClass mieso = baza.manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#DodatekMięsny"));
		for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: baza.silnik.getSubClasses(mieso, true)) {
			System.out.println("klasa:"+klasa.toString());
		}
		for (OWLClass d:  baza.listaDodatkow){
			System.out.println("dodatek: "+d.toString());
		}

	}

}
