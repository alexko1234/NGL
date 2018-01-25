package fr.cea.ig.lfw.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunCollections {

	/*
	 * Return the first non null produced result. Exceptions are 
	 * handled as nulls.
	 * @param ps producers to check
	 * @return   
	 */
	@SafeVarargs
	public static <T> Optional<T> first(Supplier<Optional<T>>... suppliers) {
		for (Supplier<Optional<T>> s : suppliers) {
			Optional<T> value = s.get();
			if (value.isPresent())
				return value;
		}
		return Optional.empty();
	}
	
	public static <A,B> Function<A,B> fconst(final B value) {
		return x -> value;
	}
	
	public static <A,B> List<B> map(Collection<A> col, Function<A,B> map) {
		List<B> result = new ArrayList<>();
		for (A a : col)
			result.add(map.apply(a));
		return result;
	}
	
	public static <A,B,C> List<C> flatMap(Collection<A> col, Function<A,Collection<B>> flat, Function<B,C> map) {
		List<C> result = new ArrayList<>();
		for (A a : col)
			for (B b : flat.apply(a))
				result.add(map.apply(b));	
		return result;
	}
	
}
