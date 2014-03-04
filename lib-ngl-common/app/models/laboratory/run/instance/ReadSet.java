package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.run.instance.FileValidationHelper;
import validation.run.instance.ReadSetValidationHelper;
import validation.run.instance.TreatmentValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;

public class ReadSet extends DBObject implements IValidation{

	public String typeCode;
	
	public State state;
	
	public String runCode;
	public String runTypeCode;
	public Date runSequencingStartDate;
	
	public Integer laneNumber;
	public Boolean dispatch = Boolean.FALSE;
	public String sampleCode; //nom de l'ind / ech //used for search
	public String projectCode;
	
	public Valuation productionValuation = new Valuation();
	public Valuation bioinformaticValuation = new Valuation();
	
    
	public String path;
	public String archiveId;
	public Date archiveDate;
	public TraceInformation traceInformation;
	public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
	public Map<String, PropertyValue> properties= new HashMap<String, PropertyValue>();
	
	public List<File> files;
	
	//insert after ngsrg
	public SampleOnContainer sampleOnContainer;
	
	
	/*
	 * for "archives" optimization purpose (query links)
	*/
	

	/*
	indexSequence 			tag lié au ls
	nbRead 					nombre de read de sequencage du ls
	???						ssid du ls (archivage)
	???						date d'archivage du ls
	nbClusterInternalFilter	nombre de clusters passant les filtres du ls
	nbBaseInternalFilter	nombre de bases correspondant au clusters passant les filtres du ls
	fraction				fraction de run du ls
	insertLength			id de la taille d'insert
	nbUsefulBase				nombre de bases utiles ls
	nbUsefulCluster			nombre de clusters utiles passant les filtres du ls
	q30 					q30 du ls
	score					score qualite moyen du ls
	 */

	@Override
	public void validate(ContextValidation contextValidation) {
		ReadSetValidationHelper.validateId(this, contextValidation);
		ReadSetValidationHelper.validateCode(this, InstanceConstants.READSET_ILLUMINA_COLL_NAME, contextValidation);
		ReadSetValidationHelper.validateReadSetType(this.typeCode, this.properties, contextValidation);
		ReadSetValidationHelper.validateState(this.typeCode, this.state, contextValidation);
		//TODO validation runTypeCode et runSequencingStartDate
		//TODO passage de la mauvaise cle dans le message d'erreur
		ReadSetValidationHelper.validateValuation(this.typeCode, this.bioinformaticValuation, contextValidation);
		ReadSetValidationHelper.validateValuation(this.typeCode, this.productionValuation, contextValidation);
		ReadSetValidationHelper.validateReadSetCodeInRunLane(this.code, this.runCode, this.laneNumber, contextValidation);
		ReadSetValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
		ReadSetValidationHelper.validateReadSetRunCode(this.runCode ,contextValidation);
		ReadSetValidationHelper.validateReadSetLaneNumber(this.runCode, this.laneNumber ,contextValidation);

		ReadSetValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		
		ReadSetValidationHelper.validateSampleCode(this.sampleCode, this.projectCode, contextValidation);
		
		ValidationHelper.required(contextValidation, this.path, "path");
		contextValidation.putObject("readSet", this);
		contextValidation.putObject("level", Level.CODE.ReadSet);
		TreatmentValidationHelper.validationTreatments(this.treatments, contextValidation);
		FileValidationHelper.validationFiles(this.files, contextValidation);
	}

	
}