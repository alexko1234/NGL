package services.description.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Value;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.parameter.IlluminaIndex;
import models.laboratory.parameter.NanoporeIndex;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

public abstract class AbstractExperimentService {

	
		public void main(Map<String, List<ValidationError>> errors)  throws DAOException{
			DAOHelpers.removeAll(ProcessType.class, ProcessType.find);
			DAOHelpers.removeAll(ExperimentTypeNode.class, ExperimentTypeNode.find);
	
			DAOHelpers.removeAll(ExperimentType.class, ExperimentType.find);
			DAOHelpers.removeAll(ExperimentCategory.class, ExperimentCategory.find);
	
			saveProtocolCategories(errors);
			saveExperimentCategories(errors);
			saveExperimentTypes(errors);
			saveExperimentTypeNodes(errors);
		}
	
		protected static List<InstrumentUsedType> getInstrumentUsedTypes(String...codes) throws DAOException {
			return DAOHelpers.getModelByCodes(InstrumentUsedType.class,InstrumentUsedType.find, codes);
		}
	
		protected static List<ExperimentType> getExperimentTypes(String...codes) throws DAOException {
			return DAOHelpers.getModelByCodes(ExperimentType.class,ExperimentType.find, codes);
		}
	
		protected static List<ExperimentTypeNode> getExperimentTypeNodes(String...codes) throws DAOException {
			return DAOHelpers.getModelByCodes(ExperimentTypeNode.class,ExperimentTypeNode.find, codes);
		}
	
		
		
		abstract void saveExperimentTypeNodes(Map<String, List<ValidationError>> errors) throws DAOException ;
	
		abstract void saveExperimentTypes(Map<String, List<ValidationError>> errors) throws DAOException;
	
		abstract void saveExperimentCategories(Map<String, List<ValidationError>> errors) throws DAOException;
	
		abstract void saveProtocolCategories(Map<String, List<ValidationError>> errors) throws DAOException;
	
	//GA 24/07/2015 ajout des TagCategories
		protected static List<Value> getTagCategories(){
			List<Value> values = new ArrayList<Value>();
			values.add(DescriptionFactory.newValue("SINGLE-INDEX", "SINGLE-INDEX"));
			values.add(DescriptionFactory.newValue("DUAL-INDEX", "DUAL-INDEX"));
			values.add(DescriptionFactory.newValue("MID", "MID"));
			return values;	
		}
		
		protected static List<Value> getTagIllumina() {
			
			List<IlluminaIndex> indexes = MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, IlluminaIndex.class, DBQuery.is("typeCode", "index-illumina-sequencing")).sort("name").toList();
			List<Value> values = new ArrayList<Value>();
			indexes.forEach(index -> {
				values.add(DescriptionFactory.newValue(index.code, index.name));	
			});
			
			return values;
		}		
}
