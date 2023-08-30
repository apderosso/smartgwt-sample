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
     * Customer.ds.xml.
     */
    public static final class Customer {

        public static final String DATASOURCE = "Customer";

        public static final String CUSTOMER_NUMBER           = "customerNumber";
        public static final String CUSTOMER_NAME             = "customerName";
        public static final String CONTACT_LAST_NAME         = "contactLastName";
        public static final String CONTACT_FIRST_NAME        = "contactFirstName";
        public static final String PHONE                     = "phone";
        public static final String ADDRESS_LINE_1            = "addressLine1";
        public static final String ADDRESS_LINE_2            = "addressLine2";
        public static final String STATE                     = "state";
        public static final String POSTAL_CODE               = "postalCode";
        public static final String COUNTRY                   = "country";
        public static final String SALES_REP_EMPLOYEE_NUMBER = "salesRepEmployeeNumber";
        public static final String SALES_REP_EMPLOYEE_NAME   = "salesRepEmployeeName";
        public static final String CREDIT_LIMIT              = "creditLimit";
        public static final String TERRITORY                 = "territory";

        private Customer() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

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
     * Office.ds.xml.
     */
    public static final class Office {

        public static final String DATASOURCE = "Office";

        public static final String OFFICE_CODE    = "officeCode";
        public static final String CITY           = "city";
        public static final String PHONE          = "phone";
        public static final String ADDRESS_LINE_1 = "addressLine1";
        public static final String ADDRESS_LINE_2 = "addressLine2";
        public static final String STATE          = "state";
        public static final String COUNTRY        = "country";
        public static final String POSTAL_CODE    = "postalCode";
        public static final String TERRITORY      = "territory";

        private Office() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * Order.ds.xml.
     */
    public static final class Order {

        public static final String DATASOURCE = "Order";

        public static final String ORDER_NUMBER    = "orderNumber";
        public static final String ORDER_DATE      = "orderDate";
        public static final String REQUIRED_DATE   = "requiredDate";
        public static final String SHIPPED_DATE    = "shippedDate";
        public static final String STATUS          = "status";
        public static final String COMMENTS        = "comments";
        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String CUSTOMER_NAME   = "customerName";

        private Order() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * OrderDetail.ds.xml.
     */
    public static final class OrderDetail {

        public static final String DATASOURCE = "OrderDetail";

        public static final String ORDER_NUMBER      = "orderNumber";
        public static final String PRODUCT_CODE      = "productCode";
        public static final String PRODUCT_NAME      = "productName";
        public static final String ORDER_LINE_NUMBER = "orderLineNumber";
        public static final String QUANTITY_ORDERED  = "quantityOrdered";
        public static final String PRICE_EACH        = "priceEach";

        private OrderDetail() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * Payment.ds.xml.
     */
    public static final class Payment {

        public static final String DATASOURCE = "Payment";

        public static final String CUSTOMER_NUMBER = "customerNumber";
        public static final String CUSTOMER_NAME   = "customerName";
        public static final String CHECK_NUMBER    = "checkNumber";
        public static final String PAYMENT_DATE    = "paymentDate";
        public static final String AMOUNT          = "amount";

        private Payment() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * Product.ds.xml.
     */
    public static final class Product {

        public static final String DATASOURCE = "Product";

        public static final String PRODUCT_CODE        = "productCode";
        public static final String PRODUCT_NAME        = "productName";
        public static final String PRODUCT_LINE        = "productLine";
        public static final String PRODUCT_SCALE       = "productScale";
        public static final String PRODUCT_VENDOR      = "productVendor";
        public static final String PRODUCT_DESCRIPTION = "productDescription";
        public static final String QUANTITY_IN_STOCK   = "quantityInStock";
        public static final String BUY_PRICE           = "buyPrice";
        public static final String MSRP                = "msrp";

        private Product() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * ProductLine.ds.xml.
     */
    public static final class ProductLine {

        public static final String DATASOURCE = "ProductLine";

        public static final String PRODUCT_LINE     = "productLine";
        public static final String TEXT_DESCRIPTION = "textDescription";
        public static final String HTML_DESCRIPTION = "htmlDescription";
        public static final String IMAGE            = "image";

        private ProductLine() {
            throw new IllegalStateException(UTILITY_CLASS);
        }

    }

    /**
     * Role.ds.xml.
     */
    public static final class Role {

        public static final String DATASOURCE = "Role";

        public static final String ROLE = "role";

        private Role() {
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
