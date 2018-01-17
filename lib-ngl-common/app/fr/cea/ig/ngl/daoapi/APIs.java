package fr.cea.ig.ngl.daoapi;

public class APIs {
	
	private final PermissionAPI permissionAPI;
	private final ProjectAPI projectAPI;
	private final UserAPI userAPI;
	
	public APIs(PermissionAPI permissionAPI,
				ProjectAPI projectAPI,
				UserAPI userAPI) {
		this.permissionAPI = permissionAPI;
		this.projectAPI = projectAPI;
		this.userAPI = userAPI;
	}

	
	public PermissionAPI permission() { return permissionAPI; }
	public ProjectAPI project() { return projectAPI; }
	public UserAPI user() { return userAPI; }
	
}
