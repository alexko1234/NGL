package sra.scripts.utils;

import java.util.Iterator;
import java.util.function.Function;

import sra.scripts.utils.iteration.MappingIterator;

/**
 * Pas encore utilisé
 * @author sgas 
 *
 */
public class Iterators {
	
	public static <A, B> Iterator<B> map(Iterator<A> i, Function<A,B> function) {
		return new MappingIterator<A, B>(i, function);
	}
	
	public static <A> Iterator<A> skip (Iterator <A> i, int cp) {
		return new SkipIterator<A>(i, cp);
		
	}
}
