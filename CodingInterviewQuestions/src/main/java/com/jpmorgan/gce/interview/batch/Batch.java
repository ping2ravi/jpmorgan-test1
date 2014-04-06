package com.jpmorgan.gce.interview.batch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Batch <T> implements Iterable<T> {

    List<T> batchList;
    
    public Batch(){
    	batchList = new ArrayList<T>();
    }

	@Override
	public Iterator<T> iterator() {
		return batchList.iterator();
	}
	
	public void add(T item){
		batchList.add(item);
	}
	
	public T remove(int index){
		T itemToRemove = batchList.get(index);
		batchList.remove(index);
		return itemToRemove;
	}
	public int size(){
		return batchList.size();
	}

}
