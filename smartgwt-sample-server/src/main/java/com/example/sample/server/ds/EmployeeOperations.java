package com.example.sample.server.ds;

import java.util.List;

import com.example.sample.server.util.Util;
import com.example.sample.shared.constants.DS;
import com.example.sample.shared.datasource.bean.User;
import com.isomorphic.criteria.DefaultOperators;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;

/**
 * A collection of methods to be called during Employee CRUD operations. See the DmiOverview documentation topic for background.
 * 
 * https://www.smartclient.com/smartgwtee-latest/javadoc/com/smartgwt/client/docs/DmiOverview.html
 */
public final class EmployeeOperations {

    /**
     * When an Employee is added, use their email address to create a user account with a temporary password and USER group enrollment.
     */
    public DSResponse add(final DSRequest dsRequest) throws Exception {
        final DSResponse dsResponse = dsRequest.execute();

        final DSRequest usersRequest = new DSRequest(DS.User.DATASOURCE, DataSource.OP_ADD, dsRequest.getRPCManager());
        usersRequest.setFieldValue(DS.User.USERNAME, dsResponse.getFieldValue(DS.Employee.EMAIL));
        usersRequest.setFieldValue(DS.User.PASSWORD, "nosecret");

        final DSResponse usersResponse = usersRequest.execute();
        dsResponse.addRelatedUpdate(usersResponse);

        final DSRequest rolesRequest = new DSRequest(DS.UserRole.DATASOURCE, DataSource.OP_ADD, dsRequest.getRPCManager());
        rolesRequest.setFieldValue(DS.UserRole.ID, usersResponse.getFieldValue(DS.User.ID));
        rolesRequest.setFieldValue(DS.UserRole.ROLE, "USER");

        final DSResponse rolesResponse = rolesRequest.execute();
        dsResponse.addRelatedUpdate(rolesResponse);

        return dsResponse;
    }

    /**
     * Cascade changes to an Employee's email address to the corresponding username.
     */
    public DSResponse update(final DSRequest dsRequest) throws Exception {
        final DSResponse dsResponse = dsRequest.execute();

        final Object email = dsRequest.getFieldValue(DS.Employee.EMAIL);
        if (email == null) {
            return dsResponse;
        }

        final DSRequest usersRequest = new DSRequest(DS.User.DATASOURCE, DataSource.OP_UPDATE, dsRequest.getRPCManager());
        usersRequest.setAllowMultiUpdate(true);
        usersRequest.setCriteriaValue(DS.User.USERNAME, dsRequest.getOldValues().get(DS.Employee.EMAIL));
        usersRequest.setFieldValue(DS.User.USERNAME, dsRequest.getFieldValue(DS.Employee.EMAIL));

        final DSResponse usersResponse = usersRequest.execute();
        dsResponse.addRelatedUpdate(usersResponse);

        return dsResponse;
    }

    /**
     * When an employee is removed, also delete their user profile and group enrollment.
     */
    public DSResponse remove(final DSRequest dsRequest) throws Exception {
        final DSRequest userIdRequest = new DSRequest(DS.User.DATASOURCE, DataSource.OP_FETCH, dsRequest.getRPCManager());
        userIdRequest.addToCriteria(DS.User.USERNAME, DefaultOperators.Equals, dsRequest.getOldValues().get(DS.Employee.EMAIL));
        final Object userId = Util.getFirst((List<User>) userIdRequest.execute().getDataList()).getId();

        final DSResponse dsResponse = dsRequest.execute();

        final DSRequest rolesRequest = new DSRequest(DS.UserRole.DATASOURCE, DataSource.OP_REMOVE, dsRequest.getRPCManager());
        rolesRequest.setFieldValue(DS.UserRole.ID, userId);
        rolesRequest.setAllowMultiUpdate(true);
        final DSResponse rolesResponse = rolesRequest.execute();
        dsResponse.addRelatedUpdate(rolesResponse);

        final DSRequest usersRequest = new DSRequest(DS.User.DATASOURCE, DataSource.OP_REMOVE, dsRequest.getRPCManager());
        usersRequest.setFieldValue(DS.User.ID, userId);
        final DSResponse usersResponse = usersRequest.execute();
        dsResponse.addRelatedUpdate(usersResponse);

        return dsResponse;
    }

}
