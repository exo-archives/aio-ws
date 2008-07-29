<%@ page language="java"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<style type="text/css">
.main {
	font-family: TimesNewRoman, Arial, Helvetica, serif;
	font-style: normal;
	font-size: 10pt;
}
</style>
</head>
<body>
	<div class="main">
		<p style="font-weight: bold">Resource is protected by oAuth.</p>
		<p>Principal name: <%=request.getUserPrincipal()%></p>
	</div>
</body>
</html>