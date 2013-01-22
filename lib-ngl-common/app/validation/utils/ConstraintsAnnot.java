package validation.utils;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;

import models.laboratory.run.instance.Run;
import play.data.validation.Constraints.Validator;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class ConstraintsAnnot {
	
	 /**
     * Defines a unique value for a code field.
     */
    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = UniqueCodeValidator.class)
    @play.data.Form.Display(name="constraint.uniquecode", attributes={"collection","type"})
    public static @interface UniqueCode {
        String message() default UniqueCodeValidator.message;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
        String collection();
        Class<?> type();             
    }
    
    /**
     * Validator for <code>@UniqueCode</code> fields.
     */
    public static class UniqueCodeValidator extends Validator<String> implements ConstraintValidator<UniqueCode, String> {
        
        final static public String message = "error.uniquecode";
        private UniqueCode constraintAnnotation;
        
        public UniqueCodeValidator() {}
        
        public void initialize(UniqueCode constraintAnnotation) {        	
            this.constraintAnnotation = constraintAnnotation;
        }
        
        public boolean isValid(String code) {
            if(code == null) {
                return true;
            }
            try{
            	MongoDBDAO.findByCode(constraintAnnotation.collection(), constraintAnnotation.type(), code);
            	return true;
            }catch(MongoException e){
            	return false;
            }
        }
        
      
    }
    
    /**
     * Defines a unique value for a code field.
     */
    @Target({TYPE})
    @Retention(RUNTIME)
    @Constraint(validatedBy = RunValidator.class)
    @play.data.Form.Display(name="constraint.uniquerun")
    public static @interface RunUnique {
        String message() default RunValidator.message;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};          
    }
    
    /**
     * Validator for <code>@UniqueCode</code> fields.
     */
    public static class RunValidator extends Validator<Run> implements ConstraintValidator<RunUnique, Run> {
        
        final static public String message = "error.uniquerun";
        private RunUnique constraintAnnotation;
        
        public RunValidator() {}
        
        public void initialize(RunUnique constraintAnnotation) {        	
            this.constraintAnnotation = constraintAnnotation;
        }
        
        public boolean isValid(Run run) {
            if(run == null) {
                return true;
            }
            try{
            	String s = run._id;
            	return true;
            }catch(MongoException e){
            	return false;
            }
        }
        
      
    }

}
