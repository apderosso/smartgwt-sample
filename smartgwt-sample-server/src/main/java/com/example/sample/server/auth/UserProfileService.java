package com.example.sample.server.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.sample.server.util.Util;
import com.example.sample.shared.constants.DS;
import com.example.sample.shared.datasource.bean.User;
import com.example.sample.shared.datasource.bean.UserRole;
import com.isomorphic.criteria.DefaultOperators;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;

/**
 * A trivial Spring Security UserDetailsService implementation that obtains its data from a fetch against SmartClient DataSources.
 */
public final class UserProfileService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            // get the user account
            final DSRequest userAccountRequest = new DSRequest(DS.User.DATASOURCE, DataSource.OP_FETCH);
            userAccountRequest.addToCriteria(DS.User.USERNAME, DefaultOperators.Equals, username);

            final DSResponse userAccountResponse = userAccountRequest.execute();
            final User userData = Util.getFirst((List<User>) userAccountResponse.getDataList());

            if (userData == null) {
                throw new UsernameNotFoundException(String.format("No active user found with id '%s'", username));
            }

            // get roles
            final DSRequest userRolesRequest = new DSRequest(DS.UserRole.DATASOURCE, DataSource.OP_FETCH);
            userRolesRequest.setCriteria(DS.UserRole.ID, userData.getId());
            final DSResponse userRolesResponse = userRolesRequest.execute();

            final Set<String> authorities = new HashSet<>();
            final List<String> roles = ((List<UserRole>) userRolesResponse.getDataList()).stream().map(UserRole::getRole).toList();

            // prepend each role with the prefix expected by Spring
            for (final String role : roles) {
                authorities.add("ROLE_" + role.toUpperCase());
            }

            // return a user details per contract
            return new UserProfile(username, userData.getPassword(), authorities);
        } catch (final UsernameNotFoundException e) {
            throw e;
        } catch (final Exception e) {
            throw new UsernameNotFoundException("Unable to obtain user profile", e);
        }
    }

}
