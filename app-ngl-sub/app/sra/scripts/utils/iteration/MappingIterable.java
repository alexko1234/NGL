package sra.scripts.utils.iteration;

import java.util.Iterator;
import java.util.function.Function;
/**
 * * Transforme un iterarable A en iterable B
 * @author sgas
 *
 * @param <A>
 * @param <B>
 */


import sra.scripts.utils.ZenIterable;

/**
 * Constructeur
 * @author sgas
 *
 * @param <A> Iterable A
 * @param <B> Iterable B
 */
public class MappingIterable <A,B> implements ZenIterable <B> {
	Iterable <A> i;
	Function <A, B> function;
	public MappingIterable(Iterable <A> i, Function <A, B> function) {
		this.i = i;
		this.function = function;
	}

	/**
	 * Fournit un iterator de B, transformé de l'iterator A
	 */
	@Override
	public Iterator<B> iterator() {
		Iterator <A> j = i.iterator();
		Iterator <B> k = new MappingIterator <>(j, function);
		return k;
	}
}
