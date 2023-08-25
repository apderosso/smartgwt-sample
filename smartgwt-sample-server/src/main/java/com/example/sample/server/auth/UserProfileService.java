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
 * A trivial Spring Security UserDetailsService implementation that obtains its data from a fetch against 
 * SmartClient DataSources.  
 */
public final class UserProfileService implements UserDetailsService {

	@SuppressWarnings("unchecked")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		try {

			// get the user account
			DSRequest userAccountRequest = new DSRequest("User", DataSource.OP_FETCH);
			userAccountRequest.addToCriteria("username", DefaultOperators.Equals, username);

			DSResponse userAccountResponse = userAccountRequest.execute(); 
			Map<String, Object> userData = userAccountResponse.getDataMap();

			if (userData == null) {
				throw new UsernameNotFoundException(String.format("No active user found with id '%s'", username));
			}

			// get roles
			DSRequest userRolesRequest = new DSRequest("UserRole", DataSource.OP_FETCH);
			userRolesRequest.setCriteria("id", userData.get("id"));
			DSResponse userRolesResponse = userRolesRequest.execute();

			Set<String> authorities = new HashSet<String>();
			List<String> roles = DataTools.getProperty(userRolesResponse.getDataList(), "role");
			
			// prepend each role with the prefix expected by Spring
			for (String role : roles) {
				authorities.add("ROLE_" + role.toUpperCase());
			}
			
			// return a user details per contract
			UserProfile profile = new UserProfile(username, (String) userData.get("password"), authorities);
			return profile;

		} catch (UsernameNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new UsernameNotFoundException("Unable to obtain user profile", e);
		}
	}
	
}