package fr.cea.ig.lfw.utils.iteration;

import java.util.Iterator;
import java.util.function.Function;

import fr.cea.ig.lfw.utils.ZenIterable;

public class FilteringIterable <A> implements ZenIterable <A> {
	
	Iterable <A> i;
	Function<A, Boolean> function;
	
	public FilteringIterable(Iterable <A> i,Function<A, Boolean> function) {
		this.i = i;
		this.function = function;
	}
	
	@Override
	public Iterator<A> iterator() {
		return new FilteringIterator<A>(i.iterator(), function);
	}
	
}