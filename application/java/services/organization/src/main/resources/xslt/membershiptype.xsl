<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:ns="http://exoplatform.org/organization/rest"
	exclude-result-prefixes="xlink ns">

	<xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="membershipType">
		<html>
			<body>
				<table border="1">
					<tr bgcolor="#9acd32">
						<th>Name</th>
						<th>Description</th>
						<th>parentId</th>
						<th>createdDate</th>
						<th>modifiedDate</th>
					</tr>
					<tr>
						<td>
							<xsl:value-of select="name" />
						</td>
						<td>
							<xsl:value-of select="description" />
						</td>
						<td>
							<xsl:value-of select="createdDate" />
						</td>
						<td>
							<xsl:value-of select="modifiedDate" />
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>