<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Book's</title>
        <style type="text/css">
          <xsl:comment>
            table {
            width:300px;
            border: 1px solid gray;
            padding:10px;
            margin-left: auto;
            margin-right: auto;
            margin-top: 20px;
            font-size: 12px;
            font-family: Verdana;
            }
          </xsl:comment>
        </style>
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="book">
    <table>
      <xsl:apply-templates select="//isdn"/>
      <xsl:apply-templates select="//title"/>
      <xsl:apply-templates select="//author"/>
      <xsl:apply-templates select="//pages"/>
      <xsl:apply-templates select="//price"/>
    </table>
  </xsl:template>
  <!-- ISDN -->
  <xsl:template match="isdn">
    <tr>
      <td>ISDN: </td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>
  <!-- Title -->
  <xsl:template match="title">
    <tr>
      <td>Title: </td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>
  <!-- Author -->
  <xsl:template match="author">
    <tr>
      <td>Author: </td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>
  <!-- Pages -->
  <xsl:template match="pages">
    <tr>
      <td>Pages: </td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>
  <!-- Price -->
  <xsl:template match="price">
    <tr>
      <td>Price: </td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>
  
</xsl:stylesheet>
