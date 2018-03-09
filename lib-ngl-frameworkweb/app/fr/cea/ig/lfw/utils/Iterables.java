package fr.cea.ig.lfw.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import fr.cea.ig.lfw.utils.iteration.AppendingIterable;
import fr.cea.ig.lfw.utils.iteration.FilteringIterable;
import fr.cea.ig.lfw.utils.iteration.FlatteningIterable;
import fr.cea.ig.lfw.utils.iteration.IntercalatingIterable;
import fr.cea.ig.lfw.utils.iteration.MappingIterable;
import fr.cea.ig.lfw.utils.iteration.PrependingIterable;
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
		if (i == null)
			return Optional.empty();			
		for (A a : i)
			return Optional.of(a);
		return Optional.empty();
	}
	
	public static <A> List<A> toList(Iterable<A> i) {
		List<A> l = new ArrayList<>();
		if (i != null)
			for (A a : i)
				l.add(a);
		return l;
	}
	
	public static int sum(List<Integer> l) {
		return foldr(l, 0, (sum,e) -> sum + e);
	}
	
	public static <A,B> B foldr(Iterable<A> i, B b, BiFunction<B,A,B> f) {
		if (i != null)
			for (A a : i)
				b = f.apply(b, a);
		return b;
	}
	
	public static String concat(Iterable<String> i) {
		return foldr(i, new StringBuilder(), (b,s) -> b.append(s)).toString();
	}
	
	public static <A> ZenIterable<A> intercalate(Iterable<A> i, A a) {
		return new IntercalatingIterable<>(i,a);
	}
	
	public static <A> ZenIterable<A> prepend(Iterable<A> i, A a) {
		return new PrependingIterable<>(i,a);
	}
	
	public static <A> ZenIterable<A> append(Iterable<A> i, A a) {
		return new AppendingIterable<>(i,a);
	}
	
	public static <A> ZenIterable<A> surround(Iterable<A> i, A before, A between, A after) {
		return intercalate(i,between).prepend(before).append(after);
	}
	
	public static <A, B extends Iterable<A>> ZenIterable<A> flatten(Iterable<B> ii) {
		return new FlatteningIterable<>(ii);
	}
	
}
