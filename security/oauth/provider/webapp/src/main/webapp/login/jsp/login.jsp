<%@ page language="java" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>Login</title>
    <c:set var="contextPath" value="<%=request.getContextPath()%>" />
    <link rel="shortcut icon" type="image/x-icon"  href="${contextPath}/favicon.ico" />
    <link rel="stylesheet" type="text/css" href="${contextPath}/login/skin/Stylesheet.css" />
  </head>
  <body style="text-align: center; background: #f5f5f5; font-family: arial, tahoma, verdana">
    <div class="UILogin">
      <div class="LoginHeader">Sign in</div>
      <div class="LoginContent">
        <div class="WelcomeText">eXo OAuth Login</div>
        <div class="CenterLoginContent">
        <%-- authenticaion servlet will send name when authentication failed --%>
        	<c:if test="${not empty param.username}">
            <font color="red">Sign in failed. Wrong username or password.</font>
        	</c:if>
      		<form name="loginForm" action="${contextPath}/authorize" method="post" style="margin: 0px;">
            <input type="hidden" name="oauth_token" value="${param.oauth_token}" />      		
			      <input type="hidden" name="oauth_token_secret" value="${param.oauth_token_secret}" />      		
            <input type="hidden" name="oauth_consumer_key" value="${param.oauth_consumer_key}" />      		
            <input type="hidden" name="returnTo" value="${param.returnTo}" />      		
      		  <div class="FieldContainer" id="UIPortalLoginFormControl">
	       		  <label>User name</label><input class="UserName" name="username" value="${not empty param.username ? param.username : ""}"/>
		        </div>
            <div class="FieldContainer" id="UIPortalLoginFormControl">
              <label>Password</label><input class="Password" type="password" name="password" value=""/>
            </div>
		        <div class="LoginButton">
		          <div class="LoginButtonContainer">
		            <div class="Button">
		              <div class="LeftButton">
		                <div class="RightButton">
		                  <div class="MiddleButton">
		                  	<a href="javascript:login();" id="UIPortalLoginFormAction">Sign in</a>
		                  </div>
		                </div>
		              </div>
		            </div>
		          </div>
		        </div>
		        <div style="clear: left"><span></span></div>
		        <script type="text/javascript">			            
              function login() {
                document.loginForm.submit();                   
              }
            </script>
          </form>
        </div>
      </div>
    </div>
    <span style="margin: 10px 0px 0px 5px; font-size: 11px; color: #6f6f6f; text-align: center">
    Copyright &copy 2000-2008. All rights Reserved, eXo Platform SAS.</span>
  </body>
</html>
