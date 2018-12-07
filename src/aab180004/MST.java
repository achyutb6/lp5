// Starter code for LP5

package aab180004;

import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;
import rbk.Graph;

import aab180004.BinaryHeap.Index;
import aab180004.BinaryHeap.IndexedHeap;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.File;

public class MST extends GraphAlgorithm<MST.MSTVertex> {
    String algorithm;
    public long wmst;
    List<Edge> mst;
    
    MST(Graph g) {
	super(g, new MSTVertex((Vertex) null));
    }

    public static class MSTVertex implements Index, Comparable<MSTVertex>, Factory {
	boolean seen;
	MSTVertex parent;
	int d; //distance
	Vertex u;
	int rank;

	MSTVertex(Vertex u) {
		parent = this;
		rank = 0;
		this.u = u;
	}

	MSTVertex(MSTVertex u) {  // for prim2
	}

	public MSTVertex make(Vertex u) {
		return new MSTVertex(u);
	}

	public MSTVertex find(){
		if(this != parent){
			parent = parent.find();
		}
		return parent;
	}

	public void union(MSTVertex rv){
		if(this.rank > rv.rank){
			rv.parent = this;
		}else if(this.rank < rv.rank){
			this.parent = rv;
		}else{
			this.rank++;
			rv.parent = this;
		}
	}

	public void putIndex(int index) { }

	public int getIndex() { return 0; }

	public int compareTo(MSTVertex other) {
	    return Integer.compare(this.d,other.d);
	}
    }

    public long kruskal() {
		algorithm = "Kruskal";
		Edge[] edgeArray = g.getEdgeArray();
		Arrays.sort(edgeArray);
		mst = new LinkedList<>();
		wmst = 0;

		for(Edge e : edgeArray){
			MSTVertex ru = get(e.fromVertex()).find();
			MSTVertex rv = get(e.toVertex()).find();
			if(ru != rv){
				mst.add(e);
				wmst += e.getWeight();
				ru.union(rv);
			}
		}
        return wmst;
    }

    public long prim3(Vertex s) {
	algorithm = "indexed heaps";
        mst = new LinkedList<>();
	wmst = 0;
	IndexedHeap<MSTVertex> q = new IndexedHeap<>(g.size());
	for(Vertex u : g){
			get(u).seen = false;
			get(u).parent = null;
			get(u).d = Integer.MAX_VALUE;
		}
	get(s).d = 0;
	for(Vertex u : g){
		q.add(get(u));
	}
	while(!q.isEmpty()){
		MSTVertex u = q.remove();
		u.seen = true;
		wmst += u.d;
		for(Edge e : g.incident(u.u)){
			MSTVertex v = get(e.otherEnd(u.u));
			if(!v.seen && e.getWeight() < v.d){
				v.d = e.getWeight();
				v.parent = u;
				q.decreaseKey(v);
			}
		}
	}
	return wmst;
    }

    public long prim2(Vertex s) {
	algorithm = "PriorityQueue<Vertex>";
        mst = new LinkedList<>();
	wmst = 0;
 	for(Vertex u : g){
		get(u).seen = false;
		get(u).parent = null;
		get(u).d = Integer.MAX_VALUE;
	}
	get(s).d = 0;
	PriorityQueue<MSTVertex> q = new PriorityQueue<>();
 	q.add(get(s));

 	while (!q.isEmpty()){
 		MSTVertex u = q.remove();
 		if(!u.seen){
 			u.seen = true;
 			wmst += u.d;
			for(Edge e : g.incident(u.u)){
				MSTVertex v = get(e.otherEnd(u.u));
				if(!v.seen && e.getWeight() < v.d){
					v.d = e.getWeight();
					v.parent = u;
					q.add(v);
				}
			}
		}
	}
	return wmst;
    }

    public long prim1(Vertex s) {
	algorithm = "PriorityQueue<Edge>";
        mst = new LinkedList<>();
	wmst = 0;
	for(Vertex u : g){
		get(u).seen = false;
		get(u).parent = null;
	}
	get(s).seen = true;
	PriorityQueue<Edge> q = new PriorityQueue<>();
	for(Edge e: g.incident(s)){
		q.add(e);
	}

	while(!q.isEmpty()){
		Edge e = q.remove();
		Vertex v = e.toVertex();
		if(get(v).seen){
			continue;
		}
		get(v).seen = true;
		get(v).parent = get(e.otherEnd(v));
		wmst += e.getWeight();
		mst.add(e);
		for(Edge e2 : g.incident(v)){
			if(!get(e2.otherEnd(v)).seen){
				q.add(e2);
			}
		}
	}
	return wmst;
    }

    public static MST mst(Graph g, Vertex s, int choice) {
	MST m = new MST(g);
	switch(choice) {
	case 0:
	    m.kruskal();
	    break;
	case 1:
	    m.prim1(s);
	    break;
	case 2:
	    m.prim2(s);
	    break;
	default:
	    m.prim3(s);
	    break;
	}
	return m;
    }

    public static void main(String[] args) throws FileNotFoundException {
	Scanner in;
	int choice = 1;  // Kruskal
        if (args.length == 0 || args[0].equals("-")) {
            in = new Scanner(System.in);
        } else {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        }

	if (args.length > 1) { choice = Integer.parseInt(args[1]); }

	Graph g = Graph.readGraph(in);
        Vertex s = g.getVertex(1);

	Timer timer = new Timer();
	MST m = mst(g, s, choice);
	System.out.println(m.algorithm + "\n" + m.wmst);
	System.out.println(timer.end());
    }
}
