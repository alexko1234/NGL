package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;
import controllers.readsets.api.ReadSets;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

public class ReadSetUpdate extends AbstractUpdate<ReadSet>{

	private ReadSets readSets;
	@Inject
	public ReadSetUpdate(ReadSets readSets) {
		super(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class);	
		this.readSets = readSets;
	}
	
	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<>();
		queryElts.add(getProjectCodeQuery(form, ""));
		queryElts.add(getSampleCodeQuery(form, ""));
		queryElts.addAll(getContentPropertiesQuery(form, "sampleOnContainer."));
		query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));	
		if(CollectionUtils.isNotEmpty(form.codes)){
			query.and(DBQuery.in("code", form.codes));
		}else if(StringUtils.isNotBlank(form.codeRegex)){
			query.and(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
		return query;
	}
	
	@Override
	public void update(NGLObject input, ContextValidation cv) {
		if(NGLObject.Action.delete.equals(NGLObject.Action.valueOf(input.action))){
			this.readSets.delete(input.code);
			//ReadSets.delete(input.code);
		}else if(NGLObject.Action.exchange.equals(NGLObject.Action.valueOf(input.action))){
			//Update readset and switch readSet
			
			//Get 2 readsets to switch
			ReadSet readSetOrigin = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, input.code);
			ReadSet readSetToSwitch = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, input.readSetToSwitchCode);
			
			//Modify readSet
			updateReadSetProperties(readSetOrigin, input.currentValue, input.newValue);
			updateReadSetProperties(readSetToSwitch, input.newValue, input.currentValue);
			
			
			//Get treament rg and global
			Treatment trtNgsrgOrigin = readSetOrigin.treatments.get("ngsrg");
			Treatment trtGlobalOrigin = readSetOrigin.treatments.get("global");
			//Replace value
			updateTreatment(trtNgsrgOrigin, trtGlobalOrigin);
			
			Treatment trtNgsrgSwitch = readSetToSwitch.treatments.get("ngsrg");
			Treatment trtGlobalSwitch = readSetToSwitch.treatments.get("global");
			//Replace value
			updateTreatment(trtNgsrgSwitch, trtGlobalSwitch);
			
			readSetOrigin.treatments.clear();
			readSetOrigin.treatments.put("ngsrg", trtNgsrgSwitch);
			readSetOrigin.treatments.put("global", trtGlobalSwitch);
			readSetToSwitch.treatments.clear();
			readSetToSwitch.treatments.put("ngsrg", trtNgsrgOrigin);
			readSetToSwitch.treatments.put("global", trtGlobalOrigin);
			
			//throw new RuntimeException(input.action+" not implemented");
			
			readSetOrigin.validate(cv);
			readSetToSwitch.validate(cv);
			if(!cv.hasErrors()){
				updateObject(readSetOrigin);
				updateObject(readSetToSwitch);
			}
		}else if(NGLObject.Action.replace.equals(NGLObject.Action.valueOf(input.action))){
			ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, input.code);
			PropertyDefinition pd = PropertyDefinition.find.findUnique(input.contentPropertyNameUpdated, Level.CODE.Content);
			Object newValue = ValidationHelper.convertStringToType(pd.valueType, input.newValue);
			
			// readSet.sampleOnContainer.properties.get(input.contentPropertyNameUpdated).value = newValue;
			readSet.sampleOnContainer.properties.get(input.contentPropertyNameUpdated).assignValue(newValue);
			readSet.validate(cv);
			if(!cv.hasErrors()){
				updateObject(readSet);				
			}
		}
	}
	
	private void updateReadSetProperties(ReadSet readSet, String oldValue, String newValue)
	{
		readSet.code=readSet.code.replace(oldValue, newValue);
		readSet.sampleOnContainer.properties.put(InstanceConstants.TAG_PROPERTY_NAME, new PropertySingleValue(newValue));
		
		readSet.files = readSet.files.stream().filter(file->!file.typeCode.equals("CLEAN")).collect(Collectors.toList());
		readSet.files.stream().forEach(file->{
			file.fullname = file.fullname.replace(oldValue, newValue);
			//file.usable=true;
		});
	}
	
	private void updateTreatment(Treatment trtNgsrg, Treatment trtGlobal)
	{
		//Replace value
		trtGlobal.results().get("default").put("usefulSequences", trtNgsrg.results.get("default").get("nbCluster"));
		trtGlobal.results().get("default").put("usefulBases", trtNgsrg.results.get("default").get("nbBases"));
	}
	
	@Override
	public Long getNbOccurrence(NGLObject input) {
		return 1L;
	}
}
