package validation;

// Default converter from IValidation to ICRUDValidatable
public interface ICRUDValidation<T> extends IValidation,ICRUDValidatable<T> {
	
	default void validateInvariants(ContextValidation ctx) {
	}
	
	default void validateCreation(ContextValidation ctx) {
		ctx.setCreationMode();
		validate(ctx);
	}

	default void validateUpdate(ContextValidation ctx, T past) {
		ctx.setUpdateMode();
		validate(ctx);		
	}
	
	default void validateDelete(ContextValidation ctx) {
		ctx.setDeleteMode();
		validate(ctx);				
	}

}
