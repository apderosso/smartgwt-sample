package com.example.sample.shared.constants;

/**
 * DataSource Constant file.
 */
public final class DS {

    private static final String UTILITY_CLASS = "Utility class";

    private DS() {
        throw new IllegalStateException(UTILITY_CLASS);
    }

    /**
     * Employee.ds.xml.
     */
    public static final class Employee {

        public static final String DATASOURCE = "Employee";

        public static final String EMPLOYEE_NUMBER          = "employeeNumber";
        public static final String LAST_NAME                = "lastName";
        public static final String FIRST_NAME               = "firstName";
        public static final String EXTENSION                = "extension";
        public static final String EMAIL                    = "email";
        public static final String OFFICE_CODE              = "officeCode";
        public static final String OFFICE_LOCATION          = "officeLocation";
        public static final String REPORTS_TO               = "reportsTo";
        public static final String REPORTS_TO_EMPLOYEE_NAME = "reportsToEmployeeName";
        public static final String JOB_TITLE                = "jobTitle";

        private Employee() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * User.ds.xml.
     */
    public static final class User {

        public static final String DATASOURCE = "User";

        public static final String OPERATION_ID_FETCH_CURRENT_USER = "fetchCurrentUser";

        public static final String ID       = "id";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String PROFILE  = "profile";

        private User() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * UserRole.ds.xml.
     */
    public static final class UserRole {

        public static final String DATASOURCE = "UserRole";

        public static final String OPERATION_ID_FETCH_BY_CURRENT_USER = "fetchByCurrentUser";

        public static final String PK       = "pk";
        public static final String ID       = "id";
        public static final String ROLE     = "role";
        public static final String USERNAME = "username";

        private UserRole() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

}
