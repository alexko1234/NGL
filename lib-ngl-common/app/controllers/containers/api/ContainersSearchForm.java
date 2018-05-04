package controllers.containers.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.ListForm;
import controllers.NGLControllerHelper;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import play.Logger.ALogger;

public class ContainersSearchForm extends ListForm {
	
	private static ALogger logger = play.Logger.of(Containers.class);
	
	public String code; 
	public String codeRegex;
	public Set<String> codes;
	public String treeOfLifePathRegex;
	public String projectCode;
	public Set<String> projectCodes;
	public String stateCode;
	public Set<String> stateCodes;
	public String sampleCode;
	public Set<String> sampleCodes;
	public Set<String> sampleTypeCodes;
	public String ncbiScientificNameRegex;
	public String categoryCode;
	public String nextExperimentTypeCode;
	public String processTypeCode;
	public String processCategory;
	public String nextProcessTypeCode;
	public String supportCode;
	public Set<String> supportCodes;
	public String supportCodeRegex;
	public String supportStorageCodeRegex;
	public String containerSupportCategory;
	public Set<String> fromPurificationTypeCodes;
	public Set<String> fromTransfertTypeCodes;
	public Set<String> containerSupportCategories;
	public Set<String> fromTransformationTypeCodes;
	public Set<String> valuations;
	public Date fromDate;
	public Date toDate;
	public String column; //TODO rename in supportColumn
	public String line; //TODO rename in supportLine
	public String createUser; 
	public List<String> createUsers;
	public List<String> stateResolutionCodes;
	
	public String commentRegex;
	
	public Map<String, List<String>> properties = new HashMap<>();
	
	public Map<String, List<String>> processProperties = new HashMap<>();
	
	public Map<String, List<String>> contentsProperties = new HashMap<>();
	
	public Map<String, Boolean> existingFields;
	public Map<String, String> queryFields;
	
	public Boolean sampleCodesFromIWCProcess = Boolean.FALSE;
	
	@Override
	public String toString() {
		return "ContainersSearchForm [projectCode=" + projectCode
				+ ", projectCodes=" + projectCodes + ", stateCode=" + stateCode
				+ ", sampleCode=" + sampleCode + ", sampleCodes=" + sampleCodes
				+ ", categoryCode=" + categoryCode + ", nextExperimentTypeCode="
				+ nextExperimentTypeCode + ", processTypeCode=" + processTypeCode
				+ ", supportCode=" + supportCode
				+ ", containerSupportCategory=" + containerSupportCategory
				+ ", containerSupportCategories=" + containerSupportCategories
				+ ", fromTransformationTypeCodes=" + fromTransformationTypeCodes
				+ ", valuations=" + valuations + ", createUser=" + createUser 
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}

	@Override
	public Query getQuery() {
		List<DBQuery.Query> queryElts = new ArrayList<>();
		Query query = DBQuery.empty();

		
		if(this.processProperties.size() > 0){	
			List<String> processCodes = new ArrayList<>();
			List<DBQuery.Query> listProcessQuery = NGLControllerHelper.generateQueriesForProperties(this.processProperties, Level.CODE.Process, "properties");
			Query processQuery = DBQuery.and(listProcessQuery.toArray(new DBQuery.Query[queryElts.size()]));

			List<Process> processes = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, processQuery).toList();
			for(Process p : processes){
				processCodes.add(p.code);
			}
			queryElts.add(DBQuery.in("processCodes", processCodes));
		}
		
		if (CollectionUtils.isNotEmpty(this.sampleTypeCodes)) { //all
			queryElts.add(DBQuery.in("contents.sampleTypeCode", this.sampleTypeCodes));
		}

		if(StringUtils.isNotBlank(this.ncbiScientificNameRegex)){
			queryElts.add(DBQuery.regex("contents.ncbiScientificName", Pattern.compile(this.ncbiScientificNameRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(this.codes)){
			queryElts.add(DBQuery.in("code", this.codes));
		}else if(StringUtils.isNotBlank(this.code)){
			queryElts.add(DBQuery.is("code", this.code));
		}else if(StringUtils.isNotBlank(this.codeRegex)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(this.codeRegex)));
		}
		
		if(StringUtils.isNotBlank(this.treeOfLifePathRegex)){
			queryElts.add(DBQuery.regex("treeOfLife.paths", Pattern.compile(this.treeOfLifePathRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(this.stateCodes)){
			queryElts.add(DBQuery.in("state.code", this.stateCodes));
		}else if(StringUtils.isNotBlank(this.stateCode)){
			queryElts.add(DBQuery.is("state.code", this.stateCode));
		}

		if(StringUtils.isNotBlank(this.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", this.categoryCode));
		}
		
		if(this.sampleCodesFromIWCProcess){
		
			if(StringUtils.isBlank(this.nextProcessTypeCode))
				throw new RuntimeException("Missing nextProcessTypeCode to search container if sampleCodesFromIWCProcess");
			
			//1 extract all sampleCode from process in IW-C
			Set<String> sampleCodes = new TreeSet<>();
			List<Pattern> samplePathRegex = new ArrayList<>();
			MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.is("state.code", "IW-C").is("typeCode", this.nextProcessTypeCode))
			.cursor.forEach(p ->{
				sampleCodes.addAll(p.sampleCodes);
				String regexPath = ","+p.sampleCodes.iterator().next();
				samplePathRegex.add(Pattern.compile(regexPath));
			});
			
			if(CollectionUtils.isNotEmpty(sampleCodes)){
				//2 search all sample childs from previous sample
				List<Query> l = samplePathRegex.stream().map(r -> DBQuery.regex("life.path", r)).collect(Collectors.toList());
				MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, 
						DBQuery.or(l.toArray(new Query[0]))).cursor.forEach(s -> sampleCodes.add(s.code));
				
				if(CollectionUtils.isNotEmpty(this.sampleCodes)){
					this.sampleCodes.retainAll(sampleCodes);
					if(CollectionUtils.isNotEmpty(this.sampleCodes)){
						queryElts.add(DBQuery.in("sampleCodes", this.sampleCodes));
					}else{
						queryElts.add(DBQuery.in("sampleCodes", "-1")); // none results
					}
					
				}else if(CollectionUtils.isNotEmpty(sampleCodes)){
					queryElts.add(DBQuery.in("sampleCodes", sampleCodes));
				}else{
					queryElts.add(DBQuery.in("sampleCodes", "-1")); // none results
				}
			}else{
				queryElts.add(DBQuery.in("sampleCodes", "-1")); // none results
			}
		}else {
			if(CollectionUtils.isNotEmpty(this.projectCodes)){
				queryElts.add(DBQuery.in("projectCodes", this.projectCodes));
			}else if(StringUtils.isNotBlank(this.projectCode)){
				queryElts.add(DBQuery.in("projectCodes", this.projectCode));
			}
			
			if(CollectionUtils.isNotEmpty(this.sampleCodes)){
				queryElts.add(DBQuery.in("sampleCodes", this.sampleCodes));
			}else if(StringUtils.isNotBlank(this.sampleCode)){
				queryElts.add(DBQuery.in("sampleCodes", this.sampleCode));
			}
		}
		
		if(CollectionUtils.isNotEmpty(this.supportCodes)){
			queryElts.add(DBQuery.in("support.code", this.supportCodes));
		}else if(StringUtils.isNotBlank(this.supportCode)){
			queryElts.add(DBQuery.is("support.code", this.supportCode));
		}else if(StringUtils.isNotBlank(this.supportCodeRegex)){
			queryElts.add(DBQuery.regex("support.code", Pattern.compile(this.supportCodeRegex)));
		}

		if(StringUtils.isNotBlank(this.supportStorageCodeRegex)){
			queryElts.add(DBQuery.regex("support.storageCode", Pattern.compile(this.supportStorageCodeRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(this.fromPurificationTypeCodes)){
			if(this.fromPurificationTypeCodes.contains("none")){
				queryElts.add(DBQuery.or(DBQuery.size("fromPurificationTypeCode", 0),DBQuery.notExists("fromPurificationTypeCode")
						,DBQuery.in("fromPurificationTypeCode", this.fromPurificationTypeCodes)));
			}else{
				queryElts.add(DBQuery.in("fromPurificationTypeCode", this.fromPurificationTypeCodes));

			}
		}
		
		if(CollectionUtils.isNotEmpty(this.fromTransfertTypeCodes)){ 
				if(this.fromTransfertTypeCodes.contains("none")){
					queryElts.add(DBQuery.or(DBQuery.size("fromTransfertTypeCode", 0),DBQuery.notExists("fromTransfertTypeCode")
							,DBQuery.in("fromTransfertTypeCode", this.fromTransfertTypeCodes)));
				}else{
					queryElts.add(DBQuery.in("fromTransfertTypeCode", this.fromTransfertTypeCodes));
				}			
		}
				
		if(CollectionUtils.isNotEmpty(this.containerSupportCategories)){
			queryElts.add(DBQuery.in("support.categoryCode", this.containerSupportCategories));
		}else if(StringUtils.isNotBlank(this.containerSupportCategory)){
			queryElts.add(DBQuery.is("support.categoryCode", this.containerSupportCategory));
		}else if(StringUtils.isNotBlank(this.nextExperimentTypeCode)){
			List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findInputByExperimentTypeCode(this.nextExperimentTypeCode);
			List<String> cs = new ArrayList<>();
			for(ContainerSupportCategory c:containerSupportCategories){
				cs.add(c.code);
			}
			if(cs.size() > 0){
				queryElts.add(DBQuery.in("support.categoryCode", cs));
			}
		}



		List<String> listePrevious = new ArrayList<>();
		//used in processes creation
		if(StringUtils.isNotBlank(this.nextProcessTypeCode)){					
					
			ProcessType processType = ProcessType.find.findByCode(this.nextProcessTypeCode);
			if(processType != null){
				
				List<ExperimentType> experimentTypes = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(processType.firstExperimentType.code, processType.code);
				
				boolean onlyEx = true;
				for(ExperimentType e:experimentTypes){
					//Logger.info(e.code);
					if(!e.code.startsWith("ex")){
						onlyEx = false;
					}
					listePrevious.add(e.code);
				}			
				
				if(CollectionUtils.isNotEmpty(this.fromTransformationTypeCodes) && this.fromTransformationTypeCodes.contains("none")){
						queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")
						,DBQuery.in("fromTransformationTypeCodes", this.fromTransformationTypeCodes)));					
				}else if(CollectionUtils.isNotEmpty(this.fromTransformationTypeCodes)){
					queryElts.add(DBQuery.in("fromTransformationTypeCodes", this.fromTransformationTypeCodes));
				}else if(!onlyEx){
					queryElts.add(DBQuery.or(DBQuery.in("fromTransformationTypeCodes", listePrevious), DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")));
				}else{
					queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")));
				}
				
			
			}else{
				logger.error("NGL-SQ bad nextProcessTypeCode: "+this.nextProcessTypeCode);
				return null;
			}
		//used in experiment creation	
		}else if(StringUtils.isNotBlank(this.nextExperimentTypeCode)){
			
			List<DBQuery.Query> subQueryElts = new ArrayList<>();
			List<ProcessType> processTypes=ProcessType.find.findByExperimentTypeCode(this.nextExperimentTypeCode);
			if(CollectionUtils.isNotEmpty(processTypes)){
				for(ProcessType processType:processTypes){
					List<ExperimentType> previousExpType = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(this.nextExperimentTypeCode,processType.code);
					//Logger.debug("NB Previous exp : "+previousExpType.size());
					Set<String> previousExpTypeCodes = previousExpType.stream().map(et -> et.code).collect(Collectors.toSet());
					
					if(CollectionUtils.isNotEmpty(this.fromTransformationTypeCodes)){
						previousExpTypeCodes.retainAll(this.fromTransformationTypeCodes);
					}
					
					if(CollectionUtils.isNotEmpty(previousExpTypeCodes)){
						subQueryElts.add(DBQuery.in("processTypeCodes", processType.code).in("fromTransformationTypeCodes", previousExpTypeCodes));
					}else{
						subQueryElts.add(DBQuery.in("processTypeCodes", "-1")); //force to return zero result;
					}
					
					
				}
				if(subQueryElts.size() > 0){
					queryElts.add(DBQuery.or(subQueryElts.toArray(new DBQuery.Query[0])));
				}
				
			}else{
				//if not processType we not return any container
				queryElts.add(DBQuery.notExists("code"));
			}
			
			
		} else if(CollectionUtils.isNotEmpty(this.fromTransformationTypeCodes)){
			if(this.fromTransformationTypeCodes.contains("none")){
					queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")
					, DBQuery.regex("fromTransformationTypeCodes", Pattern.compile("^ext-to-.+$")),DBQuery.in("fromTransformationTypeCodes", this.fromTransformationTypeCodes)));
			} else {
				queryElts.add(DBQuery.in("fromTransformationTypeCodes", this.fromTransformationTypeCodes));
			}
		}
		
		
		if(null != this.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", this.fromDate));
		}

		if(null != this.toDate){
			queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(this.toDate, 1))));
		}

		if(CollectionUtils.isNotEmpty(this.valuations)){
			queryElts.add(DBQuery.or(DBQuery.in("valuation.valid", this.valuations)));
		}

		if(StringUtils.isNotBlank(this.column)){
			queryElts.add(DBQuery.is("support.column", this.column));
		}

		if(StringUtils.isNotBlank(this.line)){
			queryElts.add(DBQuery.is("support.line", this.line));
		}

		if(StringUtils.isNotBlank(this.processTypeCode)){   
			queryElts.add(DBQuery.in("processTypeCodes", this.processTypeCode));
		}

		
		if(CollectionUtils.isNotEmpty(this.createUsers)){
			queryElts.add(DBQuery.in("traceInformation.createUser", this.createUsers));
		}else if(StringUtils.isNotBlank(this.createUser)){
			queryElts.add(DBQuery.is("traceInformation.createUser", this.createUser));
		}
		
		
		if (CollectionUtils.isNotEmpty(this.stateResolutionCodes)) { //all
			queryElts.add(DBQuery.in("state.resolutionCodes", this.stateResolutionCodes));
		}
		
		if(StringUtils.isNotBlank(this.commentRegex)){
			queryElts.add(DBQuery.elemMatch("comments", DBQuery.regex("comment", Pattern.compile(this.commentRegex))));
		}
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(this.contentsProperties,Level.CODE.Content, "contents.properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(this.properties,Level.CODE.Container, "properties"));

		queryElts.addAll(NGLControllerHelper.generateExistsQueriesForFields(this.existingFields));
		queryElts.addAll(NGLControllerHelper.generateQueriesForFields(this.queryFields));
		
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}		
		
		return query;
	}
}
