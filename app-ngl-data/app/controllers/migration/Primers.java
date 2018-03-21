package controllers.migration;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import fr.cea.ig.play.NGLContext;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import services.instance.ImportDataUtil;

public class Primers extends Controller {
	NGLContext ctx;
	@Inject
	public Primers(NGLContext ctx) {
		this.ctx = ctx;
	}
	
	public Result migration(){
		new MigrationPrimers(ImportDataUtil.getDurationForNextSeconds(5),Duration.create(48,TimeUnit.HOURS), ctx);
		return ok();
	}

}
