<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xxsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:location-helper="java:de.mpg.escidoc.tools.util.xslt.LocationHelper" xmlns:nsCR="http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:origin="http://escidoc.de/core/01/structural-relations/origin/" xmlns:system="http://escidoc.de/core/01/system/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:foxml="info:fedora/fedora-system:def/foxml#" extension-element-prefixes="location-helper">
	
	<xsl:namespace-alias stylesheet-prefix="xxsl" result-prefix="xsl"/>

	<xsl:template match="node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|*|text()"/>
		</xsl:copy>
	</xsl:template>
  
	<xsl:template match="@*">
		<xsl:attribute name="{name()}"><xsl:value-of select="replace(., 'xalan:nodeset', '')"/></xsl:attribute>
	</xsl:template>

	<xsl:template match="xsl:include">
	
		<xsl:variable name="attributes-file-content" select="document(@href)"/>
	
		<xsl:apply-templates select="$attributes-file-content">
			<xsl:with-param name="include" select="true()"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="xsl:value-of[contains(@select, 'escidoc-core-accessor:getObjectAttribute') and contains(@select, '/oum/organizational-unit/')]">
		<xxsl:call-template name="get-parent-ous">
			<xxsl:with-param name="ou-id" select="$objectId"/>
		</xxsl:call-template>
	</xsl:template>
	
	<xsl:template match="xsl:variable[@name='sortfields']">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:attribute name="as">node()*</xsl:attribute>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="xsl:stylesheet" >
		<xsl:param name="include" select="false()"/>
		
		<xsl:choose>
			<xsl:when test="$include">
				<xsl:apply-templates select="*"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates select="*"/>
					
					<xxsl:template name="get-parent-ous">
						<xxsl:param name="ou-id"/>
						
						<xxsl:value-of select="$ou-id"/>
						<xxsl:text><xsl:text> </xsl:text></xxsl:text>
						<xxsl:variable name="ou-location" select="location-helper:getLocation($ou-id)"/>
						 
						<xxsl:variable name="ou-document" select="document($ou-location)"/>
						
						<xxsl:variable name="parent-ou-ids" select="$ou-document/foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/srel:parent"/>
						<xxsl:for-each select="$parent-ou-ids">
							<xxsl:call-template name="get-parent-ous">
								<xxsl:with-param name="ou-id" select="substring-after(@rdf:resource, '/')"/>
							</xxsl:call-template>
						</xxsl:for-each>
						
					</xxsl:template>
					
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>