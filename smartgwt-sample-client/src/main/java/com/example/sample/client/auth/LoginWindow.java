package com.example.sample.client.auth;

import com.example.sample.shared.Messages;
import com.example.sample.shared.constants.DS;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.SubmitValuesEvent;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * A simple Window providing an interface for relogin operations via Spring Security, including CSRF support, by way of the given AuthenticationManager.
 * 
 * Refer to https://smartclient.com/smartgwtee-latest/javadoc/com/smartgwt/client/docs/Relogin.html.
 */
final class LoginWindow extends Window {

    private static final Messages MESSAGES = GWT.create(Messages.class);

    private final DynamicForm form = new DynamicForm();

    LoginWindow() {
        setAutoCenter(true);
        setTitle(MESSAGES.sessionExpired());
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

        final ValuesHandler valuesHandler = new ValuesHandler();
        form.setSaveOnEnter(true);
        form.addSubmitValuesHandler(valuesHandler);

        final IButton button = new IButton();
        button.setTitle(MESSAGES.login());
        button.setWidth(80);
        button.addClickHandler(valuesHandler);

        /*
         * Here we choose to force the username, so that we don't need to worry about reinitializing the app if someone were to login using different but valid
         * credentials.
         */
        final StaticTextItem username = new StaticTextItem();
        username.setValueFormatter((value, r, f, item) -> {
            // mask all but the first character
            final String val = String.valueOf(value);
            return val.substring(0, 1).concat(val.replaceAll(".", "*").substring(1));
        });
        username.setWidth(200);
        username.setTitle(MESSAGES.username());
        username.setName(DS.User.USERNAME);
        username.setRequired(true);
        username.setCanEdit(false);

        final PasswordItem password = new PasswordItem();
        password.setWidth(200);
        password.setTitle(MESSAGES.password());
        password.setName(DS.User.PASSWORD);
        password.setRequired(true);

        form.setFields(username, password);

        final HLayout buttonLayout = new HLayout();
        buttonLayout.addMembers(new LayoutSpacer(120, 50), button, new LayoutSpacer());

        final VLayout vLayout = new VLayout(20);
        vLayout.setLayoutTopMargin(40);
        vLayout.setLayoutLeftMargin(80);

        vLayout.addMember(form);
        vLayout.addMember(buttonLayout);

        addItem(vLayout);
    }

    @Override
    public void show() {
        /*
         * This whole flow can be triggered by background RPC calls (e.g., polling for notification data) while the window is already open - bail in that case
         */
        if (isVisible() && Boolean.TRUE.equals(isDrawn())) {
            return;
        }

        form.setValue(DS.User.USERNAME, AuthenticationManager.getCurrentUserName());
        form.clearValue(DS.User.PASSWORD);
        form.focusInItem(DS.User.PASSWORD);

        setStatus(null);
        bringToFront();
        super.show();
    }

    /**
     * Provides a single handler for multiple interfaces, allowing the form to be submitted either by hitting the enter key or clicking the submit button.
     */
    private class ValuesHandler implements SubmitValuesHandler, ClickHandler {

        @Override
        public void onClick(final ClickEvent event) {
            handleValues();
        }

        @Override
        public void onSubmitValues(final SubmitValuesEvent event) {
            handleValues();
        }

        private void resetHiddenFormValues() {
            final AuthenticityToken token = AuthenticationManager.getAuthenticityToken();
            form.setValue(token.getName(), token.getValue());
        }

        /**
         * Resets the global RPCManager with proper authentication data, resubmits it to the configured credentialsURL, and resends any suspended transaction.
         */
        private void handleValues() {
            if (!form.validate()) {
                return;
            }

            resetHiddenFormValues();

            final RPCRequest login = new RPCRequest();
            login.setActionURL(RPCManager.getCredentialsURL());
            login.setContainsCredentials(true);
            login.setUseSimpleHttp(true);
            login.setShowPrompt(false);
            login.setParams(form.getValues());

            RPCManager.sendRequest(login, (response, rawData, request) -> {
                /*
                 * We need to reset the token to whatever is in the response, regardless of status, so that subsequent attempts will include the right thing.
                 * Further, if the LoginWindow is presented to the user and the user doesn't resubmit their credentials before the CSRF token expires, you'll
                 * have gotten yet another token in the response. In that case, you'll also need an updated form value for subsequent attempts.
                 */
                AuthenticationManager.setAuthenticityToken(response);
                resetHiddenFormValues();

                if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {
                    close();
                    RPCManager.resendTransaction();
                } else {
                    setStatus(MESSAGES.loginFailedMessages());
                }
            });
        }

    }

}