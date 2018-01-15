package fr.cea.ig.ngl;

import play.mvc.Result;
import static play.mvc.Results.ok;

import java.util.Collection;
import java.util.function.Function;

import jsmessages.JsMessages;

public class Javascript {

	// Build a javascript  map from codes to names
	// replaces : jsCodes() & generateCodeLabel() of some controllers
	public static class Codes {
		
		private boolean first;
		
		private StringBuilder sb;
		
		public Codes() {
			sb = new StringBuilder();
			first = true;
		}
		
		public Codes dotColon(String key, String name, String value) {
			optComma();
			sb	.append('"')
				.append(key)
				.append('.')
				.append(name)
				.append("\":\"")
				.append(value)
				.append('"');
			return this;
		}
		
		public <S,T> Codes flatMapDotColon(Collection<S> c, Function<S,Collection<T>> flat, Function<T,String> key, Function<T,String> name, Function<T,String> value) {
			for (S s : c)
				for (T t : flat.apply(s)) 
					dotColon(key.apply(t),name.apply(t),value.apply(t));
			return this;
		}

		public <T> Codes mapDotColon(Collection<T> c, Function<T,String> key, Function<T,String> name, Function<T,String> value) {
			for (T t : c) 
				dotColon(key.apply(t),name.apply(t),value.apply(t));
			return this;
		}
		
		private void optComma() {
			if (first)
				first = false;
			else
				sb.append(',');
		}
		
		public Codes valuationCodes() {
			return dotColon("valuation", "TRUE",  "Oui")
				  .dotColon("valuation", "FALSE", "Non")
				  .dotColon("valuation", "UNSET", "---");
		}
		
		public Codes statusCodes() {
			return dotColon("status",    "TRUE",  "OK" )
				  .dotColon("status",    "FALSE", "KO" )
				  .dotColon("status",    "UNSET", "---");
		}

		public Result asCodeFunction() {
			StringBuilder r = 
					new StringBuilder()
						.append("Codes=(function(){var ms={")
						.append(sb)
						.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
			return ok(r.toString()).as("application/javascript");
		}
		
	}
	
	public static class Permissions {
		private StringBuilder sb;
		private boolean first;
		public Permissions() {
			sb    = new StringBuilder();
			first = true; 
		}
		public Permissions add(String s) {
			optComma();
			sb.append(s);
			return this;
		}
		private void optComma() {
			if (first)
				first = false;
			else
				sb.append(',');
		}
		
		public <T> Permissions map(Collection<T> c, Function<T,String> f) {
			for (T t : c) 
				add(f.apply(t));
			return this;
		}
		
		public Permissions addAll(Collection<String> c) {
			for (String s : c)
				add(s);
			return this;
		}
		
		public Result asCodeFunction() {
			StringBuilder r = 
					new StringBuilder()
					.append("Permissions={}; Permissions.check=(function(param){var listPermissions=[")
					.append(sb)
					.append("];return(listPermissions.indexOf(param) != -1);})");
			return ok(r.toString()).as("application/javascript");
		}
		
		public static Result jsPermissions(Collection<String> s) {
			return new Permissions()
					.addAll(s)
					.asCodeFunction();
		}
		public static <T> Result jsPermissions(Collection<T> s, Function<T,String> f) {
			return new Permissions()
					.map(s,f)
					.asCodeFunction();
		}
		
	}
	
	// public static Result jsMessages(JsMessages messages) {}
	
}
