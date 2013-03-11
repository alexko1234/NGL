package utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.container.instance.Basket;
import models.laboratory.container.instance.Volume;
import models.laboratory.experiment.instance.ContainerUsed;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;

public class BasketsMockHelper {
	public static JsonNode getJsonBasket(Basket basket) {
		return Json.toJson(basket);
	}
	
	public static JsonNode getJsonContainer(String code) {
		return Json.parse("{\"container\":\""+code+"\"}");
	}
	
	public static Basket newBasket(String code){
		Basket basket = new Basket();
		basket.code = code;
		basket.experimentTypeCode = "testexperimenttypeCode";
		
		return basket;
	}
	
}
