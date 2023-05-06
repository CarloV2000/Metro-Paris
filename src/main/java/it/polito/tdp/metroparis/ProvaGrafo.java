package it.polito.tdp.metroparis;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class ProvaGrafo {

	public static void main(String[] args) {//crerazione di un grafo
		/*1 : creo il grafo scegliendo tra i 16 tipi di grafi*/
		Graph<String, DefaultEdge> grafo = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		/*2 : inserisco i vertici*/
		grafo.addVertex("r");
		grafo.addVertex("s");
		grafo.addVertex("t");
		grafo.addVertex("v");
		grafo.addVertex("w");
		grafo.addVertex("x");
		
		//...
		
		/*3 : inserisco gli archi*/
		grafo.addEdge("r", "s");//aggiungo arco che collega r e s
		grafo.addEdge("r", "w");
		grafo.addEdge("s", "w");
		grafo.addEdge("t", "x");
		grafo.addEdge("t", "w");
		grafo.addEdge("w", "x");
		//...
		
		System.out.println(grafo.toString());//parentesi graffe per grafici non direzionati e tonde per DirectedGraph
		
		System.out.println("Vertici : "+ grafo.vertexSet().size());
		System.out.println("Archi : "+ grafo.edgeSet().size());
		
		for(String v : grafo.vertexSet()) {
			System.out.println("Vertice "+ v +" ha grado " + grafo.degreeOf(v));
			for(DefaultEdge arco :grafo.edgesOf(v)) {
				//System.out.println(arco);
				/*if(v.equals(grafo.getEdgeSource(arco))) {//perche altrimenti: errori(un source puo essere target in certi casi)
					String verticeDiArrivo = grafo.getEdgeTarget(arco);
					System.out.println("\tè connesso a " + verticeDiArrivo);
				}else {
					String verticeDiArrivo = grafo.getEdgeSource(arco);
					System.out.println("\tè connesso a " + verticeDiArrivo);
				}*/
				String verticeDiArrivo = Graphs.getOppositeVertex(grafo, arco, v); //Graphs contiene gia di suo un metodo che controlla questa cosa
				System.out.println("\tè connesso a " + verticeDiArrivo);
			}
		}
		
		//posso addirittura satare cicli e usare Graphs.neighborListOf(grafo, v); per avere una lista di tutti i vicini di un vertice
		
	}

}
