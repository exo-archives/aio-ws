<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:ns="http://exoplatform.org/organization/rest"
	exclude-result-prefixes="xlink ns">

  <xsl:output method="html" encoding="UTF-8" />
	<xsl:template match="groupInfo">
	 <html>
      <head>
        <link rel="stylesheet" type="text/css" href="/organization/css/style.css" />
        <script type="text/javascript" src="/organization/js/script.js"/>
      </head>
      <body>
        <div class="main">
          <div class="inner">
	          <xsl:apply-templates match="group"/>
						<!-- members table -->
						<table class="members-list" style="margin-top:20px;">
							<xsl:apply-templates select="members">
							 <!-- xsl:with-param name="groupId">
							   <xsl:value-of select="$groupId"/>
							 </xsl:with-param-->
							</xsl:apply-templates>
						</table>
            <input class="input" type="button" value="Back"
              style="float: left;" onclick="history.go(-1);" />
						<input class="input" type="button" value="Reload"
							style="float: left;" onclick="window.location.reload();" />
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	
	
	
	<xsl:template match="group">
	<xsl:variable name="groupId" select="id"/>
    <xsl:variable name="groupEditSuffix">
      <xsl:value-of select="concat('edit/?groupId=', $groupId)"/>
    </xsl:variable>
    <xsl:variable name="groupDeleteSuffix">
      <xsl:value-of select="concat('delete/?groupId=', $groupId)"/>
    </xsl:variable>
   
            <p class="table-title">
              <xsl:text>Group Info: </xsl:text>
              <xsl:value-of select="name" />
            </p>
            <table class="groups-list">
              <tr>
                <th>Name</th>
                <th>ID</th>
                <th>Label</th>
                <th>Description</th>
              </tr>
              <tr>
                <td>
                  <xsl:value-of select="name" />
                </td>
                <td>
                  <xsl:value-of select="$groupId" />
                </td>
                <td>
                  <xsl:value-of select="label" />
                </td>
                <td>
                  <xsl:value-of select="description" />
                </td>
                <td class="action">
                
                  <!-- Edit link -->
                  <!-- img src="/organization/img/Edit.gif" alt="Edit" title="Edit" style="cursor:pointer;">
                    <xsl:attribute name="onclick">
                      <xsl:text>top.location="</xsl:text>
                      <xsl:value-of select="concat(substring-before(./@xlink:href, '?'), $groupEditSuffix)" />
                      <xsl:text>"; return true;</xsl:text>
                    </xsl:attribute>
                  </img>
                  
                  <xsl:text>  </xsl:text-->
                  <!-- Delete link -->
                  
                  <!-- img src="/organization/img/Delete.gif"
                    alt="Delete" title="Delete" style="cursor:pointer;">
                    <xsl:attribute name="onclick">
                      <xsl:text>if(confirm("Do you want delete '</xsl:text>
                      <xsl:value-of select="$groupId"/>
                      <xsl:text>' group?")) remove("</xsl:text>
                      <xsl:value-of select="concat(substring-before(./@xlink:href, '?'), $groupDeleteSuffix)" />
                      <xsl:text>");</xsl:text>
                    </xsl:attribute>
                  </img-->
                </td>
              </tr>
            </table>
  </xsl:template>
	
	
	<!-- ************** create members table *************** -->
	<xsl:template match="members">
	 <xsl:param name="groupId"/>
		<tr>
			<th colspan="4">Members</th>
		</tr>
			<tr>
      <td>userName</td>
      <td>firstName</td>
      <td>lastName</td>
      <td>email</td>
		</tr>
		<xsl:choose>
			<xsl:when test="count(member)=0">
				<tr>
				    <td class="action">
						<i>No Items</i>
					</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="member">
          <!-- xsl:with-param name="groupId">
            <xsl:value-of select="$groupId"/>
          </xsl:with-param-->
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
  <!-- *************** feal members table *************** -->
	<xsl:template match="member">
   <xsl:param name="groupId"/>
		<tr>
			<td>
					<xsl:value-of select="userName" />
			</td>
			<td>
          <xsl:value-of select="firstName" />
      </td>
      <td>
          <xsl:value-of select="lastName" />
      </td>
      <td>
          <xsl:value-of select="email" />
      </td>
			<!-- 
			<td class="action">
        <img src="/organization/img/Delete.gif" alt="DeleteFromGroup"
        title="Delete from group"  style="cursor:pointer;">
          <xsl:attribute name="onclick">
            <xsl:text>if(confirm("Do you want delete user from group?")) top.location="</xsl:text>
              <xsl:value-of select="concat(substring-before(./@xlink:href, concat('user/', ., '/?')), $memberDeleteSuffix)" />
            <xsl:text>"; return true;</xsl:text>
          </xsl:attribute>
        </img>
			</td>
			-->
		</tr>
	</xsl:template>
</xsl:stylesheet>