<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:ns="http://exoplatform.org/organization/rest"
  exclude-result-prefixes="xlink ns">

  <xsl:output method="html" encoding="UTF-8" />
  <xsl:template match="/">
  <html>
  <body>
    <table border="1">
      <tr bgcolor="#9acd32">
        <th>membershipType</th>
        <th>id</th>
        <th>groupId</th>
        <th>userName</th>
      </tr>
       <xsl:for-each select="memberships/membership">
      <tr>
        <td><xsl:value-of select="membershipType"/></td>
        <td><xsl:value-of select="id"/></td>
        <td><xsl:value-of select="groupId"/></td>
        <td><xsl:value-of select="userName"/></td>
      </tr>
      </xsl:for-each>
      </table>
      </body>
      </html>
      </xsl:template>
      </xsl:stylesheet>