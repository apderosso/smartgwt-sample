<%@ taglib uri="http://www.smartclient.com/taglib" prefix="sc" %>
<%@ page language="java" pageEncoding="UTF-8"  isELIgnored="false"%>

<!--
  Adapted from the Bootstrap sign-in example at https://getbootstrap.com/docs/4.0/examples/sign-in/
  -->
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>sample Login</title>

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <!-- Custom styles for this template -->
    <style>
      html, body {
        height: 100%;
      }
      body {
        display: -ms-flexbox;
        display: -webkit-box;
        display: flex;
        -ms-flex-align: center;
        -ms-flex-pack: center;
        -webkit-box-align: center;
        align-items: center;
        -webkit-box-pack: center;
        justify-content: center;
        padding-top: 40px;
        padding-bottom: 40px;
        background-color: #f5f5f5;
      }

      .form-signin {
        width: 100%;
        max-width: 330px;
        padding: 15px;
        margin: 0 auto;
      }
      .form-signin .checkbox {
        font-weight: 400;
      }
      .form-signin .form-control {
        position: relative;
        box-sizing: border-box;
        height: auto;
        padding: 10px;
        font-size: 16px;
      }
      .form-signin .form-control:focus {
        z-index: 2;
      }
      .form-signin input[type="email"] {
        margin-bottom: -1px;
        border-bottom-right-radius: 0;
        border-bottom-left-radius: 0;
      }
      .form-signin input[type="password"] {
        margin-bottom: 10px;
        border-top-left-radius: 0;
        border-top-right-radius: 0;
      }
    </style>

    <%--
        Loading of framework resources is a one-liner when we use the SmartGWT tag library.  This has the added benefit of appending
        a build number to the query strings to help with browser caching, and also allows us to load the framework in the background
        before it's needed via the cacheOnly attribute.
     --%>
    <sc:loadISC modulesDir="modules" includeModules="Charts" skin="Tahoe" cacheOnly="true" />

  </head>

  <body class="text-center">

    <form class="form-signin" action="login" method='POST'>
      <h1 class="h3 mb-3 font-weight-normal">Please sign in</h1>
      <label for="inputEmail" class="sr-only">Email address</label>
      <input name="username" type="email" id="inputEmail" class="form-control" placeholder="Email address" required autofocus>
      <label for="inputPassword" class="sr-only">Password</label>
      <input name="password" type="password" id="inputPassword" class="form-control" placeholder="Password" required >
      <div class="checkbox mb-3">
        <label>
          <input type="checkbox" name="remember-me"> Remember me
        </label>
      </div>
      <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>

      <%-- 
        We add the CSRF token assigned to the request by Spring in 2 places:

        1.  On a form input, so that Spring is able to verify the authenticity of the login request.  See
            https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html

        2.  On a div where a SmartGWT 'relogin' pattern implementation will be looking for it.  We could have had the component
            inspect the DOM for the form element, but we must also include a CSRF token on the bootstrap page.  So that we could
            write code that only needed to find one element, we just use a div with html5 data attributes on both pages.  See
            
            https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes

            https://www.smartclient.com/smartgwt/javadoc/com/smartgwt/client/docs/Relogin.html

            com.smartgwt.quickstart.client.auth.AuthenticationManager.setAuthenticityToken()
            com.smartgwt.quickstart.client.auth.AuthenticationManager.setAuthenticityToken(RPCResponse) 
      --%>

      <div id='authenticity_token' data-name='${_csrf.parameterName}' data-value='${_csrf.token}'></div>
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>


      <%-- 
        Obviously meant for use during development / experimentation only, provide sample data credentials
      --%>
      <script>
        function copyCredentials(role) {
          var inputs = document.getElementsByTagName("input");
          if (role === "ADMIN") {
            inputs[0].value = 'john.smith@isomorphic.com';
            inputs[1].value = 'smith';
          } else {
            inputs[0].value = 'dmurphy@classicmodelcars.com';
            inputs[1].value = 'nosecret';
          }
        }
      </script>
      <p class="mt-5 mb-3 text-muted">
        [Click <a href="javascript:copyCredentials('ADMIN')">here</a> for 'admin' credentials.]
        <br/>
        [Click <a href="javascript:copyCredentials('USER')">here</a> for 'user' credentials.]
      </p>

    </form>

<%-- 
  And finally, we'll add the loginRequired marker that enables us to implement the SmartGWT relogin pattern.  See
  https://www.smartclient.com/smartgwt/javadoc/com/smartgwt/client/docs/Relogin.html
  --%>

<SCRIPT>//'"]]>>isc_loginRequired
//
// Embed this whole script block VERBATIM into your login page to enable
// SmartClient RPC relogin.
//=======    

if (!window.isc && document.domain && document.domain.indexOf(".") != -1 
    && !(new RegExp("^(\\d{1,3}\\.){3}\\d{1,3}$").test(document.domain))) 
{
    
    var set = false;
    while (document.domain.indexOf(".") != -1) {
        try {
            if (window.opener && window.opener.isc) break;
            if (window.top.isc) break;
            
            if (!set) { document.domain = document.domain; set = true; }
            else { document.domain = document.domain.replace(/.*?\./, ''); }
        } catch (e) {
            try {
                if (!set) { document.domain = document.domain; set = true }
                else { document.domain = document.domain.replace(/.*?\./, ''); }
            } catch (ee) {
                break;
            }
        }
    } 
}

var isc = top.isc ? top.isc : window.opener ? window.opener.isc : null;
if (isc && isc.RPCManager) isc.RPCManager.delayCall("handleLoginRequired", [window]);
</SCRIPT>

  </body>
</html>
