package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metroparis.db.MetroDAO;


public class Model {
	
	private Graph<Fermata, DefaultWeightedEdge> grafo; //per Fermata deve esistere hashcode and equals
	List<Fermata>fermate;
	private Map<Integer, Fermata>fermateIdMAp;
	
	/**
	 * crea grafo e legge da db le info da inserirci
	 */
	public void creaGrafo() {//peso archi = distanza tra le stazioni
		//crea grafo
		this.grafo = new SimpleWeightedGraph<Fermata, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.fermateIdMAp = new HashMap<Integer, Fermata>();
		
		//aggiungi vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.readFermate();
		Graphs.addAllVertices(this.grafo, this.fermate);
		//creo idMap
		for(Fermata f : this.fermate) {
			this.fermateIdMAp.put(f.getIdFermata(), f);
		}
		
		//aggiungi archi
		//metodo 1 : considero tutti i potenziali archi(coppie di vertici)
		/*for(Fermata partenza : this.grafo.vertexSet()){
			for(Fermata arrivo : this.grafo.vertexSet()) {
				if(dao.isFermateConnesse(partenza, arrivo)) {
					this.grafo.addEdge(partenza, arrivo);//in caso di stesso arco ma inverso(in caso di multigrafo(orientato) no problem, e neanche nel caso di grafo non orientato
				}
			}
		}*/
		//metodo 2 : prendo una fermata per volta e trovo lista di quelle adiacenti
	/*	for(Fermata partenza : this.grafo.vertexSet()){
			List<Fermata>collegate = dao.trovaCollegate(partenza, this.fermateIdMAp);
			for(Fermata arrivo : collegate) {
				this.grafo.addEdge(partenza, arrivo);//nessun problema in caso di fermate duplicate(anche con arrivo,partenza inversi)
			}
		}
		System.out.println("Grafo creato con "+this.grafo.vertexSet().size()+" vertici e "+ this.grafo.edgeSet().size() +" archi");
	*/
	/*	String s = "";
		long tic = System.currentTimeMillis();
		for(Fermata partenza : this.grafo.vertexSet()){
			List<Fermata>collegate = dao.trovaCollegateconIdMap(partenza, this.fermateIdMAp);
			for(Fermata arrivo : collegate) {
				this.grafo.addEdge(partenza, arrivo);//nessun problema in caso di fermate duplicate(anche con arrivo,partenza inversi)
				s += arrivo.getIdFermata() +'\n';
			}
		}
		long toc = System.currentTimeMillis();
		System.out.println(toc-tic +"Id delle fermate collegate : "+ s);
	
		
		*/
		//metodo 3 ancora meglio(ho creato la classe coppiaF e mi serve la idMap)
		long tic = System.currentTimeMillis();
		List<CoppiaF>coppie = dao.getAllCoppie(fermateIdMAp);
		for(CoppiaF c : coppie) {
			
			double distanza = LatLngTool.distance(c.getPartenza().getCoords(), c.getArrivo().getCoords(), LengthUnit.METER);
			Graphs.addEdge(this.grafo, c.getPartenza(), c.getArrivo(), distanza);
		}
		long toc = System.currentTimeMillis();

	}
	
	/**
	 * determina il percorso minimo tra le due fermate con grafo pesato
	 * @param partenza indica la fermata di partenza
	 * @param arrivo indica la fermata di arrivo
	 * @return lista di fermate rappresentanti il percorso piu breve
	 */
	public List<Fermata>percorso(Fermata partenza, Fermata arrivo){
		
		DijkstraShortestPath<Fermata, DefaultWeightedEdge> sp = new DijkstraShortestPath<>(this.grafo);
		GraphPath<Fermata, DefaultWeightedEdge> gp = sp.getPath(partenza, arrivo);
		List<Fermata>percorso = new ArrayList<>(gp.getVertexList());
		
		return percorso;
	}
	
	
	public List<Fermata>getAllFermate(){
		MetroDAO dao = new MetroDAO();
		return dao.readFermate();
	}
	
	/**
	 * metodo che crea grafo e in caso di archi ripetuti incrementa il peso all'arco gia esistente
	 * @param nMinCompagnieAeree
	 */
		/*public void creaGrafo(int nMinCompagnieAeree) {
			//grafo(gia creato nel costruttore)
			//idMap
			for(Airport x : this.allAeroporti) {
				this.aeroportiIdMap.put(x.getId(), x);
			}
			
			//vertici (gia compresi i vincoli sul numero di compagnie aeree)
			Graphs.addAllVertices(grafo, this.dao.getVertici(nMinCompagnieAeree, aeroportiIdMap));
			
			//archi
			List<CoppiaA>edges = dao.getArchi(aeroportiIdMap);
			for(CoppiaA x : edges) {
				Airport origin = x.getPartenza();
				Airport destination = x.getArrivo();
				int peso = x.getN();
			//metto controllo del tipo: se esistono i vertici: se l'arco esiste gia ci incremento il peso, altrimenti lo creo nuovo
				if(grafo.vertexSet().contains(origin) && grafo.vertexSet().contains(destination)) {
					DefaultWeightedEdge edge = this.grafo.getEdge(origin, destination);
					if(edge!=null) {
						double weight = this.grafo.getEdgeWeight(edge);
						weight += peso;
						this.grafo.setEdgeWeight(origin, destination, weight);
					} else {
						this.grafo.addEdge(origin, destination);
						this.grafo.setEdgeWeight(origin, destination, peso);
					}
				}
				
				
			}*/

	
	
	/**
	 * posto bilancio = (somma pesi archi entranti) - (somma peso archi uscenti)
	 * @param a è il vertice di cui calcolare il bilancio
	 * @return un int rappresentante il bilancio
	 */
	public int getBilancio(Fermata a){
		int bilancio = 0;
		List<DefaultWeightedEdge>entranti = new ArrayList<>(this.grafo.incomingEdgesOf(a));
		List<DefaultWeightedEdge>uscenti = new ArrayList<>(this.grafo.outgoingEdgesOf(a));
		
		for(DefaultWeightedEdge x : entranti) {
			bilancio += this.grafo.getEdgeWeight(x);
		}
		for(DefaultWeightedEdge x : uscenti) {
			bilancio -= this.grafo.getEdgeWeight(x);
		}
		
		return bilancio;
	}
	
	
	/**
	 * metodo per trovare tutti i vertici successori di un vertice in un grafo orientato ed ordinarli in base al bilancio(calcolato sopra)
	 * @param x è la fermata di cui calcolare i successori
	 * @return una lista di oggetti BilancioFermata(classe appositamente creata per avere due parametri(Fermata e int bilancio per ordinarli facilmente))
	 */
	public List<BilancioFermata> successoriDiFermata(Fermata x){
		List<Fermata> successori = new ArrayList<>(Graphs.successorListOf(this.grafo, x));
		List<BilancioFermata>bilancioSuccessori = new ArrayList<>();
		for(Fermata a: successori) {
			BilancioFermata bil = new BilancioFermata(a, getBilancio(a));
			bilancioSuccessori.add(bil);
		}
		Collections.sort(bilancioSuccessori);
		return bilancioSuccessori;
	}
	
	
	/**
	 * metodo che conta i (Country) confinanti dato un (Country)
	 * @param c è la fermata di cui calcolare il numero di confinanti
	 * @param anno è l'anno inserito dall'utente (entro il quale i cambiamenti di confini storici sono validi)
	 * @return un int rappresentante il numero di stati confinanti
	 */
	/*public int contaConfinantiDatoPaese(Fermata c) {
		int n = 0;
		DAO dao = new BordersDAO();
		for(Border b : dao.getCountryPairs(anno, countryIdMap)) {
			if(b.getPaese1().equals(c)) {
				n++;
			}
		}
		return n;
	}*/
	
	public int getNumberOfConnectedComponents(){
		int nComponentiConnesse = 0;
		
		ConnectivityInspector<Fermata, DefaultWeightedEdge> inspector = new ConnectivityInspector<>(this.grafo);
                  List<Set<Fermata>> connectedComponents = inspector.connectedSets();
                  for (Set<Fermata> component : connectedComponents) {
                 	    nComponentiConnesse++;
	         }
        
        return nComponentiConnesse;
    }
	
	
	/**
	 * viene visualizzata la lista di tutti i vertici raggiungibili nel grafo non orientato
	a partire da un vertice selezionato, che coincide con la componente connessa del grafo relativa allo stato
	scelto (ho usato il metodo Graphs.neighborListOf(grafo, vertice) per trovare tutti i confinanti).
	 * @param c è la (Fermata) di cui voglio sapere le confinanti
	 * @return una list di (Country):  adiacenti a quello inserito
	 */
	public List<Fermata> trovaConfinanti(Fermata c, int anno) {
		List<Fermata>confinanti = new ArrayList<Fermata>();
		confinanti = Graphs.neighborListOf(grafo, c);		
		return confinanti;
	}
	
	public boolean isGrafoLoaded() {//controlla solo se il grafo ha almeno 1 vertice
		return this.grafo.vertexSet().size() > 0;
	}

	public Graph<Fermata, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}
	
	
	
}
