<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/input">
	<form1>
		<toName><xsl:value-of select="customer/name"/></toName>
		<toAddress1><xsl:value-of select="customer/address1"/></toAddress1>
		<toAddress2><xsl:value-of select="customer/address2"/></toAddress2>
		<toAddress3><xsl:value-of select="customer/address3"/></toAddress3>
		<xsl:for-each select="attachments/attachment">
			<attachment><xsl:value-of select="file"/></attachment>
		</xsl:for-each>
	</form1>
</xsl:template>
</xsl:stylesheet>