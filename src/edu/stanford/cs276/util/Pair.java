package edu.stanford.cs276.util;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
    private A first;
    private B second;

    public Pair(A first, B second) {
    	super();
    	this.first = first;
    	this.second = second;
    }

    @Override
	public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;

    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof Pair) {
    		Pair otherPair = (Pair) obj;
    		return 
    	    		((  this.first == otherPair.first ||
    	    			( this.first != null && otherPair.first != null &&
    	    			  ((Integer)(this.first)).intValue()==((Integer)otherPair.first).intValue())) &&
    	    		 (	this.second == otherPair.second ||
    	    			( this.second != null && otherPair.second != null &&
    	    			((Integer)(this.second)).intValue()==((Integer)otherPair.second).intValue()))
    	    			   );
    	}
    	
    	return false;
    };

    @Override
	public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public A getFirst() {
    	return first;
    }

    public void setFirst(A first) {
    	this.first = first;
    }

    public B getSecond() {
    	return second;
    }

    public void setSecond(B second) {
    	this.second = second;
    }

//	@Override
//	public int compareTo(Pair o) {
//		try {
//			int a = (this.getFirst().toString()).compareTo((o.getFirst().toString()));
//			int b = (this.getSecond().toString()).compareTo((o.getSecond().toString()));
//			if (a==0 && b==0) {
//				return 0;
//			} else {
//				return a;
//			}
//		} catch (ClassCastException e) {
//			System.out
//					.println("CLASSCAST EXCEPTION WHILE COMPARING PAIRS");
//			return -1;
//		}
//	}
}