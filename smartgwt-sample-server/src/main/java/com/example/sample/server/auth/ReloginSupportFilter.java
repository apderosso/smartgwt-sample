package com.example.sample.server.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * A servlet filter that just sets a session attribute indicating whether the user is or will be using remember-me services in the current session. Ultimately
 * for use by a SmartGWT relogin implementation, which will look at the marker to decide whether or not to retry requests failing a security check.
 * <p/>
 * This kind of failure can be expected when using both remember-me services and CSRF protection. Irrelevant when one or both of these featured are disabled, or
 * when automatic retry is unnecessary.
 * 
 * See /index.jsp and com.example.sample.client.auth.AuthenticationManager.
 * 
 */
public class ReloginSupportFilter extends GenericFilterBean {

    public static final String ATTR_RM_REQUESTED = "rememberMeAuthenticationRequested";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpSession session = httpRequest.getSession();

        if (session.getAttribute(ATTR_RM_REQUESTED) == null) {
            final boolean requested = httpRequest.getParameter("remember-me") != null;
            final boolean remembered = isRememberMeAuthenticated();
            session.setAttribute(ATTR_RM_REQUESTED, remembered || requested);
        }

        chain.doFilter(request, response);
    }

    private boolean isRememberMeAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }

}
