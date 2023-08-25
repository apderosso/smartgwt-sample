package com.example.sample.server.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.isomorphic.criteria.DefaultOperators;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.util.DataTools;

/**
 * A trivial Spring Security UserDetailsService implementation that obtains its data from a fetch against SmartClient DataSources.
 */
public final class UserProfileService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        try {

            // get the user account
            final DSRequest userAccountRequest = new DSRequest("User", DataSource.OP_FETCH);
            userAccountRequest.addToCriteria("username", DefaultOperators.Equals, username);

            final DSResponse userAccountResponse = userAccountRequest.execute();
            final Map<String, Object> userData = userAccountResponse.getDataMap();

            if (userData == null) {
                throw new UsernameNotFoundException(String.format("No active user found with id '%s'", username));
            }

            // get roles
            final DSRequest userRolesRequest = new DSRequest("UserRole", DataSource.OP_FETCH);
            userRolesRequest.setCriteria("id", userData.get("id"));
            final DSResponse userRolesResponse = userRolesRequest.execute();

            final Set<String> authorities = new HashSet<>();
            final List<String> roles = DataTools.getProperty(userRolesResponse.getDataList(), "role");

            // prepend each role with the prefix expected by Spring
            for (final String role : roles) {
                authorities.add("ROLE_" + role.toUpperCase());
            }

            // return a user details per contract
            final UserProfile profile = new UserProfile(username, (String) userData.get("password"), authorities);
            return profile;

        } catch (final UsernameNotFoundException e) {
            throw e;
        } catch (final Exception e) {
            throw new UsernameNotFoundException("Unable to obtain user profile", e);
        }
    }

}
