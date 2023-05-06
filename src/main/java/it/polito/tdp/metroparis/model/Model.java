package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo; //per Fermata deve esistere hashcode and equals
	List<Fermata>fermate;
	private Map<Integer, Fermata>fermateIdMAp;
	
	/**
	 * crea grafo e legge da db le info da inserirci
	 */
	public void creaGrafo() {
		//crea grafo
		this.grafo = new SimpleGraph<Fermata, DefaultEdge>(DefaultEdge.class);
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
			this.grafo.addEdge(c.getPartenza(), c.getArrivo());
		}
		long toc = System.currentTimeMillis();

	}
	
	/**
	 * determina il percorso minimo tra le due fermate
	 * @param partenza indica la fermata di partenza
	 * @param arrivo indica la fermata di arrivo
	 * @return lista di fermate rappresentanti il percorso piu breve
	 */
	public List<Fermata>percorso(Fermata partenza, Fermata arrivo){
		//visita grafo partendo da partenza
		BreadthFirstIterator<Fermata, DefaultEdge>visita = new BreadthFirstIterator<>(this.grafo, partenza);
		List<Fermata>raggiungibili = new ArrayList<Fermata>();
		
		while(visita.hasNext()) {//finche ci sono vertici nuovi
			Fermata f = visita.next();//prendo il vertice aggiungibile
			raggiungibili.add(f);//lo aggiungo alla lista di vertici raggiungibili (il primo è quello di partenza)
		}
		//trova il percorso sull'albero di visita(usando metodo getSpanningTreeEdge (solo per BreadthFirstIterator))
		List<Fermata>percorso = new ArrayList<Fermata>();//creo lista di vertici
		Fermata corrente = arrivo;
		percorso.add(arrivo);//ci metto il vertice di arrivo
		DefaultEdge e = visita.getSpanningTreeEdge(corrente);//dato corrente(vertice finale) dammi l'arco con cui ci sei arrivato
		
		while( e != null) {//finche non arrivo al primo vertice: aggiungo tutti i vertici per completare il cammino
			Fermata precedente = Graphs.getOppositeVertex(this.grafo, e, corrente);//questo è il vertice precedente
			percorso.add(0, precedente);
			corrente = precedente;//aumento il "livello"
			e = visita.getSpanningTreeEdge(corrente);
		}
		//il percorso è calcolato partendo dall'arrivo: occorre mettere le fermate nella lista specificando la posizione(0)
		return percorso;
	}
	
	
	public List<Fermata>getAllFermate(){
		MetroDAO dao = new MetroDAO();
		return dao.readFermate();
	}
	
	public boolean isGrafoLoaded() {//controlla solo se il grafo ha almeno 1 vertice
		return this.grafo.vertexSet().size() > 0;
	}
	
	
}
