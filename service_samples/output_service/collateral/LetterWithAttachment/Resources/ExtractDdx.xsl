<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/input">
		<DDX xmlns="http://ns.adobe.com/DDX/1.0/">
			<PDF result="result">
				<xsl:for-each select="attachments/attachment">
					<FileAttachments>
						<xsl:attribute name="source">
							<xsl:value-of select="file"/>
						</xsl:attribute>
						<xsl:attribute name="nameKey">
							<xsl:value-of select="file"/>
						</xsl:attribute>
						<File mimetype="application/pdf">
							<xsl:attribute name="filename">
								<xsl:value-of select="file"/>
							</xsl:attribute>
						</File>
						<Description><xsl:value-of select="description"/></Description>
					</FileAttachments>
				</xsl:for-each>
				<PDF source="source"/>
			</PDF>
		</DDX>
	</xsl:template>
</xsl:stylesheet>