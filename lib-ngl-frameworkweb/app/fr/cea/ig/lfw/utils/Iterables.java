package fr.cea.ig.lfw.utils;

import java.util.Optional;
import java.util.function.Function;

import fr.cea.ig.lfw.utils.iteration.FilteringIterable;
import fr.cea.ig.lfw.utils.iteration.MappingIterable;
import fr.cea.ig.lfw.utils.iteration.SkippingIterable;


//catalogue de raccourcis import static Iterables.* => on peut utiliser les methodes sans les prefixer.
public class Iterables {
	
	public static <A,B> ZenIterable<B> map(Iterable<A> i, Function<A,B> function) {
		return new MappingIterable<A,B>(i, function);
	}
	
	public static <A> ZenIterable <A> skip(Iterable<A> i, int cp) {
		return new SkippingIterable<A>(i, cp);
	}
	
	public static <A> ZenIterable <A> filter(Iterable<A> i, Function<A,Boolean> f) {
		return new FilteringIterable<A>(i, f);
	}
	
	/**
	 * Transforme un Iterable en ZenIterable
	 * @param <A> type des elemments de l'iterable
	 * @param i   iterable à transformer
	 * @return    iterable transformé
	 */
	public static <A> ZenIterable <A> zen(Iterable<A> i) {
		return skip(i,0);
	}
	
	public static <A> Optional<A> first(Iterable<A> i) {
		for (A a : i)
			return Optional.of(a);
		return Optional.empty();
	}
	
}
