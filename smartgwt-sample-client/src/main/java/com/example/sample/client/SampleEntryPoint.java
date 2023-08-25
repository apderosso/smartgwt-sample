package com.example.sample.client;

import com.example.sample.client.auth.AuthenticationManager;
import com.example.sample.client.auth.AuthenticationManager.Roles;
import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.PageKeyHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripSpacer;

public final class SampleEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {
        final KeyIdentifier debugKey = new KeyIdentifier();
        debugKey.setCtrlKey(true);
        debugKey.setKeyName("D");

        Page.registerKey(debugKey, new PageKeyHandler() {

            @Override
            public void execute(final String keyName) {
                SC.showConsole();
            }
        });

        /*
         * Note that the user has already proven their identity or they'd be unable to load the bootstrap page containing this application, as it's protected by
         * our security configuration. Now, we can just load the authenticated user's profile data - useful for example when determining visibility of protected
         * controls, etc. Also note that loadUserProfile submits 2 asynchronous fetches, but queueing allows us to combine them intelligently. Refer to
         * RPCManager.startQueue javadoc for further discussion:
         * https://www.smartclient.com/smartgwt-latest/javadoc/com/smartgwt/client/rpc/RPCManager.html#startQueue--
         */
        RPCManager.startQueue();

        AuthenticationManager.loadUserProfile();

        RPCManager.sendQueue(response -> render());
    }

    /**
     * Prepare and render the application's main layout.
     */
    private void render() {
        final ListGrid grid = new ListGrid();
        grid.setDataSource("Employee");
        grid.setAutoFetchData(true);
        grid.setSortField("lastName");

        // Only an admin may add, edit, or remove an employee record. Show controls accordingly, but the server would enforce this rule regardless.
        grid.setCanEdit(AuthenticationManager.isUserInRole(Roles.ADMIN));
        grid.setListEndEditAction(RowEndEditAction.NEXT);
        grid.setCanRemoveRecords(AuthenticationManager.isUserInRole(Roles.ADMIN));

        final ToolStripButton refresh = new ToolStripButton("Refresh", "[SKIN]/actions/refresh.png");
        refresh.setTooltip("Allow the session to timeout and click the refresh button to trigger a relogin flow");
        refresh.addClickHandler(event -> grid.refreshData());

        final MenuItem logout = new MenuItem("Logout");
        logout.addClickHandler(event -> AuthenticationManager.logout());

        // Minor personalization of a menu button using user data
        final Menu menu = new Menu();
        menu.addItem(logout);

        final ToolStrip toolstrip = new ToolStrip();
        toolstrip.addButton(refresh);
        toolstrip.addSpacer(new ToolStripSpacer());
        toolstrip.addMenuButton(new ToolStripMenuButton(AuthenticationManager.getCurrentUserName(), menu));

        final VLayout layout = new VLayout();
        layout.setID("MainLayout");
        layout.setWidth100();
        layout.setHeight100();
        layout.setPadding(20);

        layout.addMembers(toolstrip, grid);

        layout.draw();
    }

}