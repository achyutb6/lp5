/**
 * Project Description:
 *
 * Implement BinaryHeap and its nested class IndexedHeap, and the three versions of
 * Prim's algorithm discussed in class, and Kruskal's algorithm.  Starter code is provided.
 * Transfer your BinaryHeap code from SP3.  Some minor modifications may be required.
 *
 * 	@author Achyut Arun Bhandiwad - AAB180004
 *  @author Nirbhay Sibal - NXS180002
 *  @author Vineet Vats - VXV180008
 *
 */
package aab180004;

import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;
import rbk.Graph;

import aab180004.BinaryHeap.Index;
import aab180004.BinaryHeap.IndexedHeap;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

public class MST extends GraphAlgorithm<MST.MSTVertex> {
    String algorithm;
    public long wmst; //weight of the MST
    List<Edge> mst;   //List of edges included in MST
    
    MST(Graph g) {
	super(g, new MSTVertex((Vertex) null));
    }

    public static class MSTVertex implements Index, Comparable<MSTVertex>, Factory {
	boolean seen; //seen flag
	MSTVertex parent; //parent vertex
	int d; //distance
	Vertex vertex; //copy of vertex object
	int rank;
	int index;

	MSTVertex(Vertex vertex) {
		parent = this;
		rank = 0;
		this.vertex = vertex;
	}

	MSTVertex(MSTVertex u) {
		this.vertex = u.vertex;
		this.d = u.d;
	}

	public MSTVertex make(Vertex u) {
		return new MSTVertex(u);
	}

	/**
	 * find method of union_find
	 */
	public MSTVertex find(){
		if(this != parent){
			parent = parent.find();
		}
		return parent;
	}

	/**
	 * union method of union_find
	 * @param rv
	 */
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

	@Override
	public void putIndex(int index) {
		this.index = index;
	}

	@Override
	public int getIndex() { return this.index; }


	@Override
	public int compareTo(MSTVertex other) {
	    return Integer.compare(this.d,other.d);
	}
    }

	/**
	 * Kruskal algorithm implementation using the disjoint-set data structure with Union/Find operations
	 * @return wmst
	 */
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

	/**
	 * Implementation #3 using indexed priority queue of vertices
	 * Node v V S stores in v.d, the weight of a smallest edge that connects ∈ − v to some u∈S
	 * @param s source vertex
	 * @return wmst
	 */
	public long prim3(Vertex s) {
		algorithm = "indexed heaps";
		mst = new LinkedList<>();
		wmst = 0;
		IndexedHeap<MSTVertex> q = new IndexedHeap<>(g.size());

		//initialization
		for(Vertex u : g){
				get(u).seen = false;
				get(u).parent = null;
				get(u).d = Integer.MAX_VALUE;
			}
		get(s).d = 0;

		//adding all the vertex to the indexed min heap
		for(Vertex u : g){
			q.add(get(u));
		}

		while(!q.isEmpty()){
			MSTVertex u = q.remove();
			u.seen = true;
			wmst += u.d;
			for(Edge e : g.incident(u.vertex)){
				MSTVertex v = get(e.otherEnd(u.vertex));
				if(!v.seen && e.getWeight() < v.d){
					v.d = e.getWeight();
					v.parent = u;
					q.decreaseKey(v);
				}
			}
		}
		return wmst;
    }

	/**
	 * Implementation #2 using a priority queue of vertices, allowing duplicates
	 * Node v ∈ V−S stores in v.d, the weight of a smallest edge that connects v to some u∈S
	 * @param s source vertex
	 * @return wmst
	 */
	public long prim2(Vertex s) {
		algorithm = "PriorityQueue<Vertex>";
		mst = new LinkedList<>();
		wmst = 0;
		//initialization
		for(Vertex u : g){
			get(u).seen = false;
			get(u).parent = null;
			get(u).d = Integer.MAX_VALUE;
		}
		get(s).d = 0;
		PriorityQueue<MSTVertex> q = new PriorityQueue<>();
		q.add(get(s));

		while (!q.isEmpty()){
			Vertex u = q.remove().vertex;
			if(!get(u).seen){
				get(u).seen = true;
				wmst += get(u).d;
				for(Edge e : g.incident(u)){
					Vertex v = e.otherEnd(u);
					if(!get(v).seen && e.getWeight() < get(v).d){
						get(v).d = e.getWeight();
						get(v).parent = get(u);
						q.add(new MSTVertex(get(v)));
					}
				}
			}
		}
		return wmst;
    }

	/**
	 * Implementation #1 using a priority queue of edges
	 * @param s source vertex
	 * @return weight of MST
	 */
	public long prim1(Vertex s) {
		algorithm = "PriorityQueue<Edge>";
		mst = new LinkedList<>();
		wmst = 0;

		//initializing the vertex
		for(Vertex u : g){
			get(u).seen = false;
			get(u).parent = null;
		}

		get(s).seen = true;

		PriorityQueue<Edge> q = new PriorityQueue<>();
		//Adding all the edges incident to src to the min heap
		for(Edge e: g.incident(s)){
			q.add(e);
		}

		while(!q.isEmpty()){
			Edge e = q.remove();
			Vertex u = e.fromVertex();
			Vertex v = e.toVertex();
			if(get(u).seen && get(v).seen){
				continue; //skip if both visited
			}else if(!get(u).seen && get(v).seen){
				v = e.fromVertex();  //swap u and v
				u = e.toVertex();
			}
			get(v).seen = true;
			get(v).parent = get(u);
			wmst += e.getWeight();
			mst.add(e);
			for(Edge ev : g.incident(v)){
				if(!get(ev.otherEnd(v)).seen){
					q.add(ev);
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
	int choice = 0;  // Kruskal
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
