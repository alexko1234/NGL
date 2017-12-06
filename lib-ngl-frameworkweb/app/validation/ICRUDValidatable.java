package validation;

/**
 * CRUD operation validation. Validation errors are notified
 * in the validation context.
 * 
 * Mapping of the IValidatable to this class can be done using the
 * context mode. 
 * 
 * There is not notion of invariants, so the default
 * implementation should do nothing.
 * 
 * The validateCreation method implementation would be 
 * <pre>
 * {@code
 * ctx.setCreationMode();
 * object.validate(ctx);
 * }
 * </pre>
 * 
 * @author vrd
 *
 */
public interface ICRUDValidatable<T> {
	
	/**
	 * Invariants, must always hold.
	 * @param ctx
	 */
	void validateInvariants(ContextValidation ctx);
	
	/**
	 * Validate the object state for the create operation.
	 * @param ctx
	 */
	void validateCreation(ContextValidation ctx);
	
	/**
	 * Validate the object state for the update operation. 
	 * @param ctx
	 */
	void validateUpdate(ContextValidation ctx, T past);
	
	/**
	 * Validate the object state for the delete operation.
	 * @param ctx
	 */
	void validateDelete(ContextValidation ctx);
	
}
