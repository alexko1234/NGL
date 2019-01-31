package controllers.containers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.NGLAPIController;
import controllers.QueryFieldsForm;
import controllers.StateController;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.containers.ContainerSupportsAPI;
import fr.cea.ig.ngl.dao.containers.ContainerSupportsDAO;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.CodeHelper;
import play.data.Form;
import play.mvc.Result;
import workflows.container.ContSupportWorkflows;
//import play.Logger;

@Historized
public class ContainerSupports extends NGLAPIController<ContainerSupportsAPI, ContainerSupportsDAO, ContainerSupport> implements StateController {
	
	private final Form<ContainerSupport>             containerSupportForm;
	
	@Inject
	public ContainerSupports(NGLApplication app, ContainerSupportsAPI api, ContSupportWorkflows workflows) {
		super(app, api, ContainerSupportsSearchForm.class);
		containerSupportForm       = app.formFactory().form(ContainerSupport.class);
	}
	
	@Override
	public Object updateStateImpl(String code, State state) throws APIValidationException, APIException {
//		Logger.debug("ContainerSupports.updateStateImpl() " + code);
		return api().updateState(code, state, getCurrentUser());
	}
	
	@Override
	public ContainerSupport saveImpl()throws APIValidationException, APIException {
		ContainerSupport input = getFilledForm(containerSupportForm, ContainerSupport.class).get();
		ContainerSupport cs = api().create(input, getCurrentUser());
		return cs;
	}

	@Override
	public ContainerSupport updateImpl(String code) throws Exception, APIException, APIValidationException {
		ContainerSupport input = getFilledForm(containerSupportForm, ContainerSupport.class).get();
		if(code.equals(input.code)) { 
			ContainerSupport containerSupportInDB = api().get(code);
			if (!containerSupportInDB.state.code.equals(input.state.code)) throw new Exception("You can not change the state code. Please use the state url ! ");
			
			QueryFieldsForm queryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class).get();
			ContainerSupport cs = null;
				if(queryFieldsForm.fields == null) { 
					cs = api().update(input, getCurrentUser());
				} else {
					cs = api().update(input, getCurrentUser(), queryFieldsForm.fields);
				}
				return cs;
		} else {
			throw new Exception("Container support codes are not the same");
		}
	}
	
	@Authenticated
	@Authorized.Write
	public Result saveCode(Integer numberOfCode) {
		try {
			List<String> codes = new ArrayList<>(numberOfCode);
			IntStream.range(0, numberOfCode).forEach(i -> {
				codes.add(CodeHelper.getInstance().generateContainerSupportCode());
			});
			return okAsJson(codes);
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}

	
//	/**
//	 * Construct the support query
//	 * @param supportsSearch
//	 * @return
//	 * @throws DAOException 
//	 * @see {@link ContainerSupportsSearchForm#getQuery()}
//	 */
//	@Deprecated 
//	private static DBQuery.Query getQuery(ContainerSupportsSearchForm supportsSearch) throws DAOException {
//		List<DBQuery.Query> queryElts = new ArrayList<>();
//		queryElts.add(DBQuery.exists("_id"));
//		if (StringUtils.isNotBlank(supportsSearch.categoryCode)) {
//			queryElts.add(DBQuery.is("categoryCode", supportsSearch.categoryCode));
//		}
//		if (StringUtils.isNotBlank(supportsSearch.containerSupportCategory)) {
//			queryElts.add(DBQuery.is("categoryCode", supportsSearch.containerSupportCategory));
//		}
//		if (CollectionUtils.isNotEmpty(supportsSearch.containerSupportCategories)) {
//			queryElts.add(DBQuery.in("categoryCode", supportsSearch.containerSupportCategories));
//		}
//		if (CollectionUtils.isNotEmpty(supportsSearch.fromTransformationTypeCodes)) {
//			if (supportsSearch.fromTransformationTypeCodes.contains("none")) {
//				queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")
//				,DBQuery.in("fromTransformationTypeCodes", supportsSearch.fromTransformationTypeCodes)));
//			} else {
//				queryElts.add(DBQuery.in("fromTransformationTypeCodes", supportsSearch.fromTransformationTypeCodes));
//			}			
//		}		
//		//These fields are not in the ContainerSupport collection then we use the Container collection
//		
//		//TODO GA allways used ?????
//		if (StringUtils.isNotBlank(supportsSearch.nextExperimentTypeCode) || StringUtils.isNotBlank(supportsSearch.processTypeCode)) {
//			logger.error("Allready used nextExperimentTypeCode in search container support. Please find where in java code");
//
//			/*Don't need anymore 09/01/2015
//			//If the categoryCode is null or empty, we use the ContainerSupportCategory data table to enhance the query
//			if(StringUtils.isNotEmpty(supportsSearch.experimentTypeCode) && StringUtils.isEmpty(supportsSearch.categoryCode)){
//				List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findByExperimentTypeCode(supportsSearch.experimentTypeCode);
//				List<String> ls = new ArrayList<String>();
//				for(ContainerSupportCategory c:containerSupportCategories){
//					ls.add(c.code);
//				}
//				if(ls.size() > 0){
//					queryElts.add(DBQuery.in("categoryCode", ls));
//				}
//			}
//			 */
//
//			//Using the Container collection for reaching container support
//			ContainersSearchForm cs = new ContainersSearchForm();
//			cs.nextExperimentTypeCode = supportsSearch.nextExperimentTypeCode;
//			cs.processTypeCode = supportsSearch.processTypeCode;		
//			cs.processProperties = supportsSearch.properties;	
//			BasicDBObject keys = new BasicDBObject();
//			keys.put("_id", 0);//Don't need the _id field
//			keys.put("support", 1);
//			Query queryContainer =ContainersOLD.getQuery(cs);
//			if (queryContainer != null) {
//				List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, queryContainer, keys).toList();
//				logger.debug("Containers " + containers.size());
//				List<String> supports  =new ArrayList<>();
//				for(Container c: containers){
//					supports.add(c.support.code);
//				}
//				if (StringUtils.isNotBlank(cs.nextExperimentTypeCode) || StringUtils.isNotBlank(cs.processTypeCode)) {
//					queryElts.add(DBQuery.in("code", supports));
//				}
//			} else {
//				return null;
//			}
//		}
//
//		/*23/05/2016  NGL-825 FDS : this criteria is meaningless for supports with multiple containers ( plates..)
//		if(CollectionUtils.isNotEmpty(supportsSearch.valuations)){
//			queryElts.add(DBQuery.or(DBQuery.in("valuation.valid", supportsSearch.valuations)));
//		}
//		*/
//		
//		/* 23/05/2016 FDS NGL-825: add search by storageCode */
//		if (StringUtils.isNotBlank(supportsSearch.storageCode)) {
//			queryElts.add(DBQuery.in("storageCode", supportsSearch.storageCode));
//		} else if(StringUtils.isNotBlank(supportsSearch.storageCodeRegex)) {
//			queryElts.add(DBQuery.regex("storageCode", Pattern.compile(supportsSearch.storageCodeRegex)));
//		}
//
//		if (StringUtils.isNotBlank(supportsSearch.stateCode)) {
//			queryElts.add(DBQuery.in("state.code", supportsSearch.stateCode));
//		}
//		
//		if (CollectionUtils.isNotEmpty(supportsSearch.stateCodes)) {
//			queryElts.add(DBQuery.in("state.code", supportsSearch.stateCodes));
//		}
//
//		if (CollectionUtils.isNotEmpty(supportsSearch.codes)) {
//			queryElts.add(DBQuery.in("code", supportsSearch.codes));
//		} else if(StringUtils.isNotBlank(supportsSearch.code)) {
//			queryElts.add(DBQuery.is("code", supportsSearch.code));
//		} else if(StringUtils.isNotBlank(supportsSearch.codeRegex)) {
//			queryElts.add(DBQuery.regex("code", Pattern.compile(supportsSearch.codeRegex)));
//		}
//		
//		if (CollectionUtils.isNotEmpty(supportsSearch.projectCodes)) {
//			queryElts.add(DBQuery.in("projectCodes", supportsSearch.projectCodes));
//		}
//
//		if (null != supportsSearch.fromDate) {
//			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", supportsSearch.fromDate));
//		}
//
//		if (null != supportsSearch.toDate) {
//			queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(supportsSearch.toDate, 1))));
//		}
//		
//		if(StringUtils.isNotBlank(supportsSearch.createUser)){   
//			queryElts.add(DBQuery.is("traceInformation.createUser", supportsSearch.createUser));
//		}
//
//		if(CollectionUtils.isNotEmpty(supportsSearch.users)){
//			queryElts.add(DBQuery.in("traceInformation.createUser", supportsSearch.users));
//		}
//
//		if(CollectionUtils.isNotEmpty(supportsSearch.sampleCodes)){
//			queryElts.add(DBQuery.in("sampleCodes", supportsSearch.sampleCodes));
//		}
//
//		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
//	}
}

