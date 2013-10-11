<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<form1>
		<leaveRequest>
			<txtEmpName><xsl:value-of select="//leaveRequest/txtEmpName"/></txtEmpName>
			<txtEmpId><xsl:value-of select="//leaveRequest/txtEmpId"/></txtEmpId>
			<txtManagersName><xsl:value-of select="//leaveRequest/txtManagersName"/></txtManagersName>
			<txtDept><xsl:value-of select="//leaveRequest/txtDept"/></txtDept>
			<drpType><xsl:value-of select="//leaveRequest/drpType[1]"/></drpType>
			<txtComment><xsl:value-of select="//leaveRequest/txtComment[1]"/></txtComment>
			<dtmStartDate><xsl:value-of select="//leaveRequest/dtmStartDate[1]"/></dtmStartDate>
			<dtmEndDate><xsl:value-of select="//leaveRequest/dtmEndDate[1]"/></dtmEndDate>
			<numDays><xsl:value-of select="//leaveRequest/numDays[1]"/></numDays>
			<drpType><xsl:value-of select="//leaveRequest/drpType[2]"/></drpType>
			<txtComment><xsl:value-of select="//leaveRequest/txtComment[2]"/></txtComment>
			<dtmStartDate><xsl:value-of select="//leaveRequest/dtmStartDate[2]"/></dtmStartDate>
			<dtmEndDate><xsl:value-of select="//leaveRequest/dtmEndDate[2]"/></dtmEndDate>
			<numDays><xsl:value-of select="//leaveRequest/numDays[2]"/></numDays>
			<drpType><xsl:value-of select="//leaveRequest/drpType[3]"/></drpType>
			<txtComment><xsl:value-of select="//leaveRequest/txtComment[3]"/></txtComment>
			<dtmStartDate><xsl:value-of select="//leaveRequest/dtmStartDate[3]"/></dtmStartDate>
			<dtmEndDate><xsl:value-of select="//leaveRequest/dtmEndDate[3]"/></dtmEndDate>
			<numDays><xsl:value-of select="//leaveRequest/numDays[3]"/></numDays>
			<drpType><xsl:value-of select="//leaveRequest/drpType[4]"/></drpType>
			<txtComment><xsl:value-of select="//leaveRequest/txtComment[4]"/></txtComment>
			<dtmStartDate><xsl:value-of select="//leaveRequest/dtmStartDate[4]"/></dtmStartDate>
			<dtmEndDate><xsl:value-of select="//leaveRequest/dtmEndDate[4]"/></dtmEndDate>
			<numDays><xsl:value-of select="//leaveRequest/numDays[4]"/></numDays>
			<numTotalDays><xsl:value-of select="//leaveRequest/numTotalDays"/></numTotalDays>
		</leaveRequest>
	</form1>
</xsl:template>
</xsl:stylesheet>