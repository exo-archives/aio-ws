<%@ page language="java"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<script type="text/javascript" src="../../js/prototype.js"></script>
<style type="text/css">
.main {
	font-family: TimesNewRoman, Arial, Helvetica, serif;
	font-style: normal;
	font-size: 12pt;
}
.link {
  cursor: pointer;
  color: #0066cc;
}
</style>
<script type="text/javascript">
<!--
  function submit(url) {
    $('text').value = "";
    var request = new Ajax.Request(                                                                                                                       
      url,                                                                                                                                                
      {          
        method: "GET",
        requestHeaders:{ 'Accept':'text/plain' },
        onSuccess: update,                                                                                                                            
        onFailure: _error_
      }                                                                                                                                                   
    );                              
  }

  function update(req) {
	  $('text').value = req.responseText;
  }
  function _error_(req) {
	  alert(req.status + ": " + req.statusText);
  }
-->
</script>
</head>
<body>
	<div class="main">
		<p style="font-weight: bold">Resource is protected by oAuth.</p>
		<p>Principal name: <%=request.getUserPrincipal()%></p>
    <p>Authentication type: <%=request.getAuthType()%></p>
    <p>Is user in role 'exo': <%=request.isUserInRole("exo")%></p>
    <p>Is user in role 'admin': <%=request.isUserInRole("admin")%></p>
    <br />
    <p>Try access sample REST services</p>
    <textarea rows="6" cols="45" id="text">Response text will be placed here</textarea><br />
    <ul>
    <li><span class="link" onclick="submit('../../rest/samples/security/all');">Accessible to all users</span></li>
    <li><span class="link" onclick="submit('../../rest/samples/security/exo');">Accessible to users in 'exo' role only</span></li>
    <li><span class="link" onclick="submit('../../rest/samples/security/admin');">Accessible to users in 'admin' role only</span></li>
    </ul>
    <hr />
    <input type="button" value="Logout" onclick="document.location = 'index.jsp?logout=yes';"/> 
	</div>
</body>
</html>