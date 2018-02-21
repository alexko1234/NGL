package fr.cea.ig.ngl.dao.api;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.ngl.dao.codelabels.CodeLabelAPI;
import fr.cea.ig.ngl.dao.permissions.PermissionAPI;
import fr.cea.ig.ngl.dao.projects.ProjectAPI;
import fr.cea.ig.ngl.dao.protocols.ProtocolAPI;
import fr.cea.ig.ngl.dao.users.UserAPI;

@Singleton
public class APIs {
	
	private final CodeLabelAPI         codeLabelAPI;
	private final PermissionAPI        permissionAPI;
	private final ProjectAPI           projectAPI;
	private final ProtocolAPI          protocolAPI;
	private final ReagentCatalogAPI    reagentCatalogAPI; 
	private final ResolutionConfigurationAPI resolutionConfigurationAPI;
	private final SraParameterAPI      sraParameterAPI;
	private final UserAPI              userAPI;
	private final ValuationCriteriaAPI valuationCriteriaAPI;
	
	@Inject
	public APIs(CodeLabelAPI         codeLabelAPI,
				PermissionAPI        permissionAPI,
				ProjectAPI           projectAPI,
				ProtocolAPI          protocolAPI,
				ReagentCatalogAPI    reagentCatalogAPI,
				ResolutionConfigurationAPI resolutionConfigurationAPI,
				SraParameterAPI      sraParameterAPI,
				UserAPI              userAPI,
				ValuationCriteriaAPI valuationCriteriaAPI) {
		this.codeLabelAPI         = codeLabelAPI;
		this.permissionAPI        = permissionAPI;
		this.projectAPI           = projectAPI;
		this.protocolAPI          = protocolAPI; 
		this.reagentCatalogAPI    = reagentCatalogAPI;
		this.resolutionConfigurationAPI = resolutionConfigurationAPI;
		this.userAPI              = userAPI;
		this.sraParameterAPI      = sraParameterAPI;
		this.valuationCriteriaAPI = valuationCriteriaAPI;
	}
	
	public CodeLabelAPI         codeLabel()         { return codeLabelAPI;         }
	public PermissionAPI        permission()        { return permissionAPI;        }
	public ProjectAPI           project()           { return projectAPI;           }
	public ProtocolAPI          protocol()          { return protocolAPI;          }
	public ReagentCatalogAPI    reagentCatalog()    { return reagentCatalogAPI;    }
	public ResolutionConfigurationAPI resolutionConfiguration() { return resolutionConfigurationAPI; }
	public SraParameterAPI      sraParameter()      { return sraParameterAPI;      }
	public UserAPI              user()              { return userAPI;              }
	public ValuationCriteriaAPI valuationCriteria() { return valuationCriteriaAPI; }
	
}
