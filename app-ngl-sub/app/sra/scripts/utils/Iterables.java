package sra.scripts.utils;

import java.util.Iterator;
import java.util.function.Function;

import sra.scripts.utils.iteration.MappingIterable;

class SkipIterable<A> implements ZenIterable <A> {

	Iterable <A> i;
	int cp;
	
	SkipIterable(Iterable<A> i, int cp) {
		this.i = i;
		this.cp = cp;
	}
	
	@Override
	public Iterator<A> iterator() {
		Iterator <A> j = i.iterator();
		Iterator <A> k = new SkipIterator<A>(j, cp);
		return k;
	}	
}

class SkipIterator<A> implements Iterator <A> {
	Iterator <A> i;
	
	SkipIterator(Iterator<A> i, int cp) {
		this.i = i;
		for (int j=0; j<cp; j++ ) {
			if (i.hasNext()) {
				i.next();
			}
		}
	}
	@Override
	public boolean hasNext() {
		return i.hasNext();
	}

	@Override
	public A next() {
		return i.next();
	}
}

class FilterIterable <A> implements ZenIterable <A> {
	
	Iterable <A> i;
	Function<A, Boolean> function;	
	FilterIterable(Iterable <A> i,Function<A, Boolean> function) {
		this.i = i;
		this.function = function;
	}
	@Override
	public Iterator<A> iterator() {
		return new FilterIterator<A>(i.iterator(), function);
	}	
}


class FilterIterator<A> implements Iterator<A> {
	
	Iterator <A> i;
	Function<A, Boolean> function;
	A tampon;
	boolean finished = false;
	
	FilterIterator(Iterator <A> i,Function<A, Boolean> function) {
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

//catalogue de raccourcis import static Iterables.* => on peut utiliser les methodes sans les prefixer.
public class Iterables {
	public static <A,B> ZenIterable<B> map(Iterable<A> i, Function<A,B> function){
		return new MappingIterable<A,B>(i, function);
	}
	public static <A> ZenIterable <A> skip(Iterable<A> i, int cp) {
		return new SkipIterable<A>(i, cp);
	}
	public static <A> ZenIterable <A> filter(Iterable<A> i, Function<A,Boolean> f) {
		return new FilterIterable<A>(i, f);
	}
	/**
	 * Transforme un Iterable en ZenIterable
	 * @param i   iterable à transformer
	 * @return    iterable transformé
	 */
	public static <A> ZenIterable <A> zen(Iterable<A> i) {
		return skip(i,0);
	}
	
}
