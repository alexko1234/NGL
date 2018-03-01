package fr.cea.ig.lfw.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/*
 * Interface qui permet d'utiliser un iterable de <A> avec les methodes skip, map et filter
 * en les chainant (fluent java)
 * 
 * @author sgas
 *
 */
public interface ZenIterable <A> extends Iterable <A> {
	
	/**
	 * Transforme un ZenIterable de A en ZenIterable de B, grace à la methode indiquée
	 * @param   f Fonction de transformation de B vers A
	 * @return  ZenIterable de B
	 */
	default <B> ZenIterable <B> map (Function <A,B> f) {
		return Iterables.map(this, f);
	}
	
	/**
	 * Ignore les cp premiers elements de l'iterable 
	 * @param  cp nombre de premiers élements à ignorer
	 * @return ZenIterable de B
	 */
	default ZenIterable <A> skip (int cp) {
		return Iterables.skip(this, cp);
	}
	
	/**
	 * Filtre les iterables 
	 * @param f fonction de filtre
	 * @return ZenIterable filtré
	 */
	default ZenIterable <A> filter (Function<A,Boolean> f) {
		return Iterables.filter(this, f);
	}
	//default void each(Function<A, Void> f) { => oblige à return null dans f.
	/**
	 * 
	 * @param  f    fonction appelé avec chaque element a
	 * @return this pour pouvoir chainer les appels
	 */
	default ZenIterable <A> each(Consumer<A> f) {
//		for (A a : this){
//			f.accept(a);
//		}
		forEach (f);
		return this;
	}
	
	default Optional<A> first() {
		return Iterables.first(this);
	}
	
}


// moins pratique que interface car 1! slot pour heritage alors que plusieurs pour interface.
abstract class ZenIterable2 <A> implements Iterable <A> {
	public <B> ZenIterable <B> map (Function <A,B> f) {
		return Iterables.map(this, f);
	}
	public ZenIterable <A> skip (int cp) {
		return Iterables.skip(this, cp);
	}
	public ZenIterable <A> filter (Function<A,Boolean> f) {
		return Iterables.filter(this, f);
	}
}
