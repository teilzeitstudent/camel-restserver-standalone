<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
    
        <greetingResponse>
            <answer>Hello, <xsl:value-of select="//name"/></answer>
        </greetingResponse>
    
    </xsl:template>
</xsl:stylesheet>