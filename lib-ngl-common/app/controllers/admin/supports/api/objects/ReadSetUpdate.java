package controllers.admin.supports.api.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import validation.ContextValidation;
import controllers.admin.supports.api.NGLObject;
import controllers.admin.supports.api.NGLObjectsSearchForm;
import controllers.readsets.api.ReadSets;
import fr.cea.ig.MongoDBDAO;

public class ReadSetUpdate extends AbstractUpdate<ReadSet>{

	public ReadSetUpdate() {
		super(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class);		
	}
	
	@Override
	public Query getQuery(NGLObjectsSearchForm form) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
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
			ReadSets.delete(input.code);
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
			readSet.sampleOnContainer.properties.get(input.contentPropertyNameUpdated).value = input.newValue;
			readSet.validate(cv);
			if(!cv.hasErrors()){
				updateObject(readSet);				
			}
		}
	}
	
	private void updateReadSetProperties(ReadSet readSet, String oldValue, String newValue)
	{
		readSet.code=readSet.code.replace(oldValue, newValue);
		readSet.sampleOnContainer.properties.put("tag", new PropertySingleValue(newValue));
		
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
