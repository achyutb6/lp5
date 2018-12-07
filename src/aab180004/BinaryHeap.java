// Starter code for LP5

// Change to your netid
package aab180004;

import java.util.*;

public class BinaryHeap<T extends Comparable<? super T>> {
    Comparable[] pq;
    int size;

    // Constructor for building an empty priority queue using natural ordering of T
    public BinaryHeap(int maxCapacity) {
	pq = new Comparable[maxCapacity];
	size = 0;
    }

    // add method: resize pq if needed
    public boolean add(T x) {
        if(size+1>pq.length)
            return false;
        move(size, x);
        percolateUp(size);
        size++;
        return true;
    }

    public boolean offer(T x) {
	    return add(x);
    }

    // throw exception if pq is empty
    public T remove() throws NoSuchElementException {
	T result = poll();
	if(result == null) {
	    throw new NoSuchElementException("Priority queue is empty");
	} else {
	    return result;
	}
    }

    // return null if pq is empty
    public T poll() {
        if(isEmpty())
            return null;
        T min;
        min =(T) pq[0];
        pq[0]= pq[size-1];
        size--;
        percolateDown(0);
        return min;
    }
    
    public T min() { 
	return peek();
    }

    // return null if pq is empty
    public T peek() {
        if(isEmpty())
            return null;
        return (T)pq[0];
    }

    int parent(int i) {
	return (i-1)/2;
    }

    int leftChild(int i) {
	return 2*i + 1;
    }

    /** pq[index] may violate heap order with parent */
    void percolateUp(int index) {
        T x;
        x = (T)pq[index];
        while (index>0 && pq[parent(index)].compareTo(x)>0 ) {
            move(index,(T)pq[parent(index)]);
            index = parent(index);
        }
        move(index,x);
    }

    /** pq[index] may violate heap order with children */
    void percolateDown(int index) {
        T x;
        int c;
        x = (T)pq[index];
        c = leftChild(index);

        while(c<=(size-1)){
            if(c<size-1 && pq[c].compareTo(pq[c+1])>0)
                c++;
            if(x.compareTo((T)pq[c])>0)
                break;
            move(index,(T)pq[c]);
            index=c;
            c=leftChild(index);
        }
        move(index,x);
    }

    void move(int dest, T x) {
	pq[dest] = x;
    }

    int compare(Comparable a, Comparable b) {
	return ((T) a).compareTo((T) b);
    }
    
    /** Create a heap.  Precondition: none. */
    void buildHeap() {
	for(int i=parent(size-1); i>=0; i--) {
	    percolateDown(i);
	}
    }

    public boolean isEmpty() {
	return size() == 0;
    }

    public int size() {
	return size;
    }

    // Resize array to double the current size
    void resize() {
        Comparable[] oldPQ = pq;
        pq = new Comparable[size * 2];
        for(int i = 0 ; i < oldPQ.length ; i++){
            pq[i] = oldPQ[i];
        }
        buildHeap();
    }
    
    public interface Index {
        public void putIndex(int index);
        public int getIndex();
    }

    public static class IndexedHeap<T extends Index & Comparable<? super T>> extends BinaryHeap<T> {
        /** Build a priority queue with a given array */
        IndexedHeap(int capacity) {
            super(capacity);
	}

        /** restore heap order property after the priority of x has decreased */
        void decreaseKey(T x) {
            percolateUp(x.getIndex());
        }

	@Override
        void move(int i, T x) {
            super.move(i, x);
            x.putIndex(i);
        }
    }

    public static void main(String[] args) {
	Integer[] arr = {0,9,7,5,3,1,8,6,4,2};
	BinaryHeap<Integer> h = new BinaryHeap(arr.length);

	System.out.print("Before:");
	for(Integer x: arr) {
	    h.offer(x);
	    System.out.print(" " + x);
	}
	System.out.println();

	for(int i=0; i<arr.length; i++) {
	    arr[i] = h.poll();
	}

	System.out.print("After :");
	for(Integer x: arr) {
	    System.out.print(" " + x);
	}
	System.out.println();
    }
}
