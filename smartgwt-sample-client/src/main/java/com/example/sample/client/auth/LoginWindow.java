package com.example.sample.client.auth;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.events.SubmitValuesEvent;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * A simple Window providing an interface for relogin operations via Spring Security, including CSRF support, by way
 * of the given AuthenticationManager.
 * 
 * Refer to http://www.smartclient.com/smartgwtee-latest/javadoc/com/smartgwt/client/docs/Relogin.html.
 */
final class LoginWindow extends Window {

    private final DynamicForm form = new DynamicForm();

    LoginWindow() {

        setAutoCenter(true);
        setTitle("Session Expired");
        setShowCloseButton(false);
        setShowMinimizeButton(false);
        setShowMaximizeButton(false);
        setWidth(400);
        setHeight(260);
        setIsModal(true);
        setShowModalMask(true);
        setShowFooter(true);
        setFooterHeight(36);

        form.setTitleOrientation(TitleOrientation.TOP);
        form.setTitleSuffix(null);
        form.setRequiredTitleSuffix(null);
        form.setNumCols(1);

        ValuesHandler valuesHandler = new ValuesHandler();
        form.setSaveOnEnter(true);
        form.addSubmitValuesHandler(valuesHandler);

        IButton button = new IButton();
        button.setTitle("Login");
        button.setWidth(80);
        button.addClickHandler(valuesHandler);

        /*
         * Here we choose to force the username, so that we don't need to worry about reinitializing
         * the app if someone were to login using different but valid credentials.
         */
        StaticTextItem username = new StaticTextItem();
        username.setValueFormatter(new FormItemValueFormatter() {
        	@Override
			public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
	        		// mask all but the first character
	        		String val = String.valueOf(value);
				String fmt = val.substring(0,  1).concat(val.replaceAll(".", "*").substring(1));
				return fmt;
			}
		});
        username.setWidth(200);
        username.setTitle("Username");
        username.setName("username");
        username.setRequired(true);
        username.setCanEdit(false);

        PasswordItem password = new PasswordItem();
        password.setWidth(200);
        password.setTitle("Password");
        password.setName("password");
        password.setRequired(true);

        form.setFields(username, password);

        HLayout buttonLayout = new HLayout();
        buttonLayout.addMembers(new LayoutSpacer(120, 50), button, new LayoutSpacer());

        VLayout vLayout = new VLayout(20);
        vLayout.setLayoutTopMargin(40);
        vLayout.setLayoutLeftMargin(80);

        vLayout.addMember(form);
        vLayout.addMember(buttonLayout);

        addItem(vLayout);
    }

    @Override
    public void show() {
    
        /* 
         * This whole flow can be triggered by background RPC calls (e.g., polling for notification data) while the window
         * is already open - bail in that case 
         */
    		if (isVisible() && isDrawn()) {
    			return;
    		}

    		form.setValue("username", AuthenticationManager.getCurrentUserName());
        form.clearValue("password");
        form.focusInItem("password");

        setStatus(null);
        bringToFront();
        super.show();
    }

    /**
     * Provides a single handler for multiple interfaces, allowing the form to be submitted either by hitting the enter
     * key or clicking the submit button.
     */
    private class ValuesHandler implements SubmitValuesHandler, ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            handleValues();
        }

        @Override
        public void onSubmitValues(SubmitValuesEvent event) {
            handleValues();
        }

        private void resetHiddenFormValues() {
        	AuthenticityToken token = AuthenticationManager.getAuthenticityToken(); 
        	form.setValue(token.getName(), token.getValue());
        }
        
        /**
         * Resets the global RPCManager with proper authentication data, resubmits it to the configured credentialsURL,
         * and resends any suspended transaction. 
         */
        private void handleValues() {

            if (!form.validate()) {
                return;
            }

            resetHiddenFormValues();
            
            RPCRequest login = new RPCRequest();
            login.setActionURL(RPCManager.getCredentialsURL());
            login.setContainsCredentials(true);
            login.setUseSimpleHttp(true);
            login.setShowPrompt(false);
            login.setParams(form.getValues());            
            
            RPCManager.sendRequest(login, new RPCCallback() {
                public void execute(RPCResponse response, Object rawData, RPCRequest request) {

                	/*
                	 * We need to reset the token to whatever is in the response, regardless of status, so that subsequent
                	 * attempts will include the right thing.  Further, if the LoginWindow is presented to the user and 
                	 * the user doesn't resubmit their credentials before the CSRF token expires, you'll have gotten yet 
                	 * another token in the response.  In that case, you'll also need an updated form value for
                	 * subsequent attempts. 
                	 */
                	AuthenticationManager.setAuthenticityToken(response);
                	resetHiddenFormValues();
                	
                	if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {

                		close();
                		RPCManager.resendTransaction();
                		
                    } else {
                        setStatus("Login Failed.  Please try again, or close your browser to quit.");
                    }
                }
            });
        }
    }
}