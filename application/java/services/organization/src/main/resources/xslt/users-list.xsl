<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:ns="http://exoplatform.org/organization/rest"
	exclude-result-prefixes="xlink ns">

	<xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
  <html>
  <body>
    <table border="1">
      <tr bgcolor="#9acd32">
        <th>userName</th>
        <th>firstName</th>
        <th>lastName</th>
        <th>email</th>
      </tr>
      <xsl:for-each select="users/user">
      <tr>
        <td><xsl:value-of select="userName"/></td>
        <td><xsl:value-of select="firstName"/></td>
        <td><xsl:value-of select="lastName"/></td>
        <td><xsl:value-of select="email"/></td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>
	
</xsl:stylesheet>
