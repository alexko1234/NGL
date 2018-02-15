package fr.cea.ig.ngl.utils;

import play.libs.Json;

public interface DeepClonable<T> {

	@SuppressWarnings("unchecked")
	default T deepClone() {
		return Json.fromJson(Json.toJson(this),(Class<T>)getClass());
	}

}
