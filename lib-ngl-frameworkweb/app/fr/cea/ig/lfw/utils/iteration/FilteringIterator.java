package fr.cea.ig.lfw.utils.iteration;

import java.util.Iterator;
import java.util.function.Function;

public class FilteringIterator<A> implements Iterator<A> {
	
	Iterator <A> i;
	Function<A, Boolean> function;
	A tampon;
	boolean finished = false;
	
	public FilteringIterator(Iterator <A> i, Function<A, Boolean> function) {
		this.i = i;
		this.function = function;
		computeNext();
	}
	
    private void computeNext() {
    	while (i.hasNext()) {
    		tampon = i.next();
    		if (function.apply(tampon)) {
    			return;
    		}
    	}
    	finished = true;
    }
    
	@Override
	public boolean hasNext() {
			return ! finished;
	}

	@Override
	public A next() {
		A tmp = tampon;
		computeNext();
		return tmp;
	}
	
}