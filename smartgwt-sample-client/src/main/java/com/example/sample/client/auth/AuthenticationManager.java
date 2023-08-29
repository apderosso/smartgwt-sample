package com.example.sample.client.auth;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.sample.shared.constants.DS;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.LoginRequiredCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.util.SC;

/**
 * A collection of client-side data and utilities dealing with user identification and authorizations. As always, note that the server manages its own copy of
 * user data and will act accordingly to requests for data CRUD.
 */
public final class AuthenticationManager {

    public enum Roles {
        ADMIN, USER
    }

    private static Record     userProfile;
    private static RecordList userAuthorizations;

    private static boolean resubmitOnLoginRequired;
    private static int     retryCount;

    private static AuthenticityToken token;
    private static final LoginWindow window = new LoginWindow();

    private static final DataSource usersDataSource = DataSource.get(DS.User.DATASOURCE);
    private static final DataSource rolesDataSource = DataSource.get(DS.UserRole.DATASOURCE);

    static {
        /*
         * When the user's session expires, Spring (in our case) will issue a redirect to the login page, which we've setup to contain the special
         * loginRequiredMarker. That login page also contains a new CSRF token that we'll need to submit along with the credentials when we reauthenticate. Set
         * the RPCManager default here, and the LoginWindow will reach back to reset it again with a new token on loginSuccess.
         */
        final int retryAttemptThreshold = 2;
        RPCManager.setLoginRequiredCallback(new LoginRequiredCallback() {

            @Override
            public void loginRequired(final int transactionNum, final RPCRequest rpcRequest, final RPCResponse rpcResponse) {

                setAuthenticityToken(rpcResponse);

                /*
                 * If remember-me services have been requested, spring should normally allow resubmission once we have the right csrf token... But you could end
                 * up though with a case where the remembered creds have become invalid - the key changes at the server, for example - and you really do need to
                 * authenticate again. Retry only so many times.
                 */
                if (resubmitOnLoginRequired && retryCount < retryAttemptThreshold) {

                    // if and when this dummy request goes unsuspended and the callback fires, we can reset the count
                    final RPCRequest ping = new RPCRequest();
                    ping.setWillHandleError(true);
                    RPCManager.sendRequest(ping, (response, rawData, request) -> retryCount = 0);

                    retryCount++;
                    RPCManager.resendTransaction();

                } else {
                    window.show();
                }
            }
        });

        // Initialize the CSRF token found in the bootstrap HTML on page load
        setAuthenticityToken();

        RPCManager.setCredentialsURL(GWT.getHostPageBaseURL() + "login");
    }

    /**
     * Loads user/contact details of the currently authenticated user asynchronously. Note that {@link #getAuthenticatedUser()} returns null until the
     * asynchronous operation's callback is fired. Also note that the server will by design determine who the current user is without any input from the client.
     */
    public static void loadUserProfile() {
        final DSRequest usersRequest = new DSRequest(DSOperationType.FETCH);
        usersRequest.setOperationId(DS.User.OPERATION_ID_FETCH_CURRENT_USER);
        usersRequest.setCallback((dsResponse, data, dsRequest) -> userProfile = dsResponse.getData()[0]);
        usersDataSource.execute(usersRequest);

        final DSRequest authoritiesRequest = new DSRequest(DSOperationType.FETCH);
        authoritiesRequest.setOperationId(DS.UserRole.OPERATION_ID_FETCH_BY_CURRENT_USER);
        authoritiesRequest.setCallback((dsResponse, data, dsRequest) -> userAuthorizations = dsResponse.getDataAsRecordList());
        rolesDataSource.execute(authoritiesRequest);
    }

    public static String getCurrentUserName() {
        return userProfile.getAttribute(DS.User.USERNAME);
    }

    public static boolean isUserInRole(final Roles role) {
        return userAuthorizations.find("role", role) != null;
    }

    /**
     * The Spring configuration includes CSRF protection, and requires that a CSRF accompany the HTTP POST to /logout.
     */
    public static void logout() {
        final RPCRequest logout = new RPCRequest();
        logout.setActionURL(GWT.getHostPageBaseURL() + "logout");
        logout.setContainsCredentials(true);
        logout.setUseSimpleHttp(true);
        logout.setShowPrompt(false);

        if (token != null) {
            final HashMap<String, String> params = new HashMap<>();
            params.put(token.getName(), token.getValue());
            logout.setParams(params);
        }

        RPCManager.sendRequest(logout, (response, rawData, request) -> Window.Location.reload());
    }

    /**
     * Returns the current CSRF token.
     */
    static AuthenticityToken getAuthenticityToken() {
        return token;
    }

    /**
     * Set the CSRF token to be included with future RPC request parameters
     * 
     * @param name the name of the HTTP parameter (e.g., _csrf)
     * @param value the value of the HTTP parameter (e.g., da48122e-ad5b-48d8-a0ff-017568ccff18)
     */
    private static void setAuthenticityToken(final String name, final String value) {
        setRpcParameter(name, value);
        token = new AuthenticityToken(name, value);
    }

    /**
     * Inspects the given rpcResponse's HTML (as returned by RPCResponse.getHttpResponseText) for the div that should contain a CSRF token as assigned to the
     * request by Spring Security, and includes it with all future RPC requests by calling {@link #setAuthenticityToken(String, String)}.
     * 
     * @param rpcResponse
     */
    static void setAuthenticityToken(final RPCResponse rpcResponse) {
        final String html = rpcResponse.getHttpResponseText();
        final RegExp regex = RegExp.compile("<div id='authenticity_token' data-name='(.*)' data-value='(.*)'");
        final MatchResult mr = regex.exec(html);
        if (mr != null) {
            setAuthenticityToken(mr.getGroup(1), mr.getGroup(2));
        } else {
            SC.logWarn("Authenticity token not found in http response: \n" + html, "Authentication");
        }
    }

    /**
     * A JSNI method that inspects the bootstrap page for a div that should contain a CSRF token as assigned to the request by Spring Security, and includes it
     * with all future RPC requests by calling {@link #setAuthenticityToken(String, String)}.
     */
    private native static void setAuthenticityToken() /*-{
        var token = $wnd.document.getElementById('authenticity_token');
        var tokenName = token.getAttribute('data-name');
        var tokenValue = token.getAttribute('data-value');
        
        if (tokenName && tokenValue) {
            $entry(@com.example.sample.client.auth.AuthenticationManager::setAuthenticityToken(Ljava/lang/String;Ljava/lang/String;)(tokenName, tokenValue));
            
            var retry = $wnd.document.getElementById('rememberme_enabled').getAttribute('data-value');
            $entry(@com.example.sample.client.auth.AuthenticationManager::resubmitOnLoginRequired = retry);
        } else {
            $wnd.isc.logWarn("Authenticity token not found in bootstrap html: \n" + $wnd.document.documentElement.outerHTML, "Authentication");
        }
    }-*/;

    /**
     * Add, update, or remove a parameter from RPCManager.actionURL. Useful for managing CSRF tokens.
     * 
     * @param name the name of the query string parameter
     * @param value the value of the named parameter. Null value causes the named parameter to be removed if it exists.
     */
    private static void setRpcParameter(final String name, final String value) {
        // decompose the actionURL
        String url = RPCManager.getActionURL();
        final String[] split = url.split("\\?");
        final LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (split.length > 1) {
            final Map<String, String> existing = Splitter.on('&').trimResults().omitEmptyStrings().withKeyValueSeparator("=").split(split[1]);
            params.putAll(existing);
            url = split[0];
        }

        // add or update the named parameter with the given value
        params.put(name, value);

        // remove any parameter without a value
        final Map<String, String> filtered = new LinkedHashMap<>();
        params.forEach((k, v) -> {
            if (v != null) {
                filtered.put(k, v);
            }
        });

        // recompose
        if (filtered.isEmpty()) {
            RPCManager.setActionURL(url);
        } else {
            RPCManager.setActionURL(url + "?" + Joiner.on("&").withKeyValueSeparator("=").join(filtered));
        }

        SC.logDebug("RPCManager.actionURL updated to include changed parameter value.  New URL is " + RPCManager.getActionURL(), "Authentication");
    }

}