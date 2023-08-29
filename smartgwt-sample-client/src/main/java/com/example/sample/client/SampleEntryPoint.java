package com.example.sample.client;

import java.util.logging.Level;

import com.example.sample.client.auth.AuthenticationManager;
import com.example.sample.client.auth.AuthenticationManager.Roles;
import com.example.sample.shared.Messages;
import com.example.sample.shared.constants.DS;
import com.example.sample.shared.constants.IDs;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
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

import lombok.extern.java.Log;

@Log
public final class SampleEntryPoint implements EntryPoint {

    private static final Messages MESSAGES = GWT.create(Messages.class);

    @Override
    public void onModuleLoad() {
        // Set Global Exception Handler
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

            @Override
            public void onUncaughtException(final Throwable e) {
                // Log client side uncaught exceptions through the logger
                log.log(Level.SEVERE, "Uncaught client exception", unwrap(e));
            }

            /**
             * Unwrap GWT's umbrella exception.
             *
             * @param e Exception
             * @return unwrapped exception
             */
            Throwable unwrap(final Throwable e) {
                if (e instanceof UmbrellaException) {
                    final UmbrellaException ue = (UmbrellaException) e;
                    if (ue.getCauses().size() == 1) {
                        return unwrap(ue.getCauses().iterator().next());
                    }
                }
                return e;
            }
        });

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
        grid.setDataSource(DS.Employee.DATASOURCE);
        grid.setAutoFetchData(true);
        grid.setSortField(DS.Employee.LAST_NAME);

        // Only an admin may add, edit, or remove an employee record. Show controls accordingly, but the server would enforce this rule regardless.
        grid.setCanEdit(AuthenticationManager.isUserInRole(Roles.ADMIN));
        grid.setListEndEditAction(RowEndEditAction.NEXT);
        grid.setCanRemoveRecords(AuthenticationManager.isUserInRole(Roles.ADMIN));

        final ToolStripButton refresh = new ToolStripButton(MESSAGES.refresh(), "[SKIN]/actions/refresh.png");
        refresh.setTooltip(MESSAGES.refreshTooltip());
        refresh.addClickHandler(event -> grid.refreshData());

        final MenuItem logout = new MenuItem(MESSAGES.logout());
        logout.addClickHandler(event -> AuthenticationManager.logout());

        // Minor personalization of a menu button using user data
        final Menu menu = new Menu();
        menu.addItem(logout);

        final ToolStrip toolstrip = new ToolStrip();
        toolstrip.addButton(refresh);
        toolstrip.addSpacer(new ToolStripSpacer());
        toolstrip.addMenuButton(new ToolStripMenuButton(AuthenticationManager.getCurrentUserName(), menu));

        final VLayout layout = new VLayout();
        layout.setID(IDs.MAIN_LAYOUT);
        layout.setWidth100();
        layout.setHeight100();
        layout.setPadding(20);

        layout.addMembers(toolstrip, grid);

        layout.draw();
    }

}