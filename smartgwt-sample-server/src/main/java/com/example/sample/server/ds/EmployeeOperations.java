package com.example.sample.server.ds;

import com.isomorphic.criteria.DefaultOperators;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;

/**
 * A collection of methods to be called during Employee CRUD operations.  See the DmiOverview documentation
 * topic for background.
 * 
 * https://www.smartclient.com/smartgwtee-latest/javadoc/com/smartgwt/client/docs/DmiOverview.html
 */
public final class EmployeeOperations {
	
	/**
	 * When an Employee is added, use their email address to create a user account with a temporary password
	 * and USER group enrollment.
	 */
	public DSResponse add(DSRequest dsRequest) throws Exception {

		DSResponse dsResponse = dsRequest.execute(); 
		
		DSRequest usersRequest = new DSRequest("User", DataSource.OP_ADD, dsRequest.getRPCManager());
		usersRequest.setFieldValue("username", dsResponse.getFieldValue("email"));
		usersRequest.setFieldValue("password", "nosecret");
		
		DSResponse usersResponse = usersRequest.execute();
		dsResponse.addRelatedUpdate(usersResponse);
		
		DSRequest rolesRequest = new DSRequest("UserRole", DataSource.OP_ADD, dsRequest.getRPCManager());
		rolesRequest.setFieldValue("id", usersResponse.getFieldValue("id"));
		rolesRequest.setFieldValue("role", "USER");
		
		DSResponse rolesResponse = rolesRequest.execute();
		dsResponse.addRelatedUpdate(rolesResponse);
		
		return dsResponse;
	}
	
	/**
	 * Cascade changes to an Employee's email address to the corresponding username.
	 */
	public DSResponse update(DSRequest dsRequest) throws Exception {
		
		DSResponse dsResponse = dsRequest.execute(); 

		Object email = dsRequest.getFieldValue("email");
		if (email == null) {
			return dsResponse;
		}

		DSRequest usersRequest = new DSRequest("User", DataSource.OP_UPDATE, dsRequest.getRPCManager());
		usersRequest.setAllowMultiUpdate(true);
		usersRequest.setCriteriaValue("username", dsRequest.getOldValues().get("email"));
		usersRequest.setFieldValue("username", dsRequest.getFieldValue("email"));
		
		DSResponse usersResponse = usersRequest.execute();
		dsResponse.addRelatedUpdate(usersResponse);
		
		return dsResponse;
	}

	/**
	 * When an employee is removed, also delete their user profile and group enrollment.
	 */
	public DSResponse remove(DSRequest dsRequest) throws Exception {

		DSRequest userIdRequest = new DSRequest("User", DataSource.OP_FETCH, dsRequest.getRPCManager());
		userIdRequest.addToCriteria("username", DefaultOperators.Equals, dsRequest.getOldValues().get("email"));
		Object userId = userIdRequest.execute().getDataMap().get("id");
		
		DSResponse dsResponse = dsRequest.execute(); 

		DSRequest rolesRequest = new DSRequest("UserRole", DataSource.OP_REMOVE, dsRequest.getRPCManager());
		rolesRequest.setFieldValue("id", userId);
		rolesRequest.setAllowMultiUpdate(true);
		DSResponse rolesResponse = rolesRequest.execute();
		dsResponse.addRelatedUpdate(rolesResponse);
		
		DSRequest usersRequest = new DSRequest("User", DataSource.OP_REMOVE, dsRequest.getRPCManager());
		usersRequest.setFieldValue("id", userId);				
		DSResponse usersResponse = usersRequest.execute();
		dsResponse.addRelatedUpdate(usersResponse);
		
		return dsResponse;
	}
	
}