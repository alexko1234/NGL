package controllers.admin.types;

import play.mvc.Result;

/**
 * Interface defines all the operations must implemented by type controller
 * @author ejacoby
 *
 */
public interface IGenericCreateTypes {

	public Result add();

	Result show(long idCommonInfoType);

	Result edit(long idCommonInfoType);
}
