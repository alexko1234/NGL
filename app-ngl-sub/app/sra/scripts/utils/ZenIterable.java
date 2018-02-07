package sra.scripts.utils;

import java.util.function.Function;

/**
 * Interface qui permet d'utiliser un iterable de <A> avec les methodes skip, map et filter
 * en les chainant (fluent java)
 * 
 * @author sgas
 *
 * @param <A>  ZenIterable de <A>
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
