package de.mpg.escidoc.pubman.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import de.mpg.escidoc.services.citationmanager.utils.Utils;

public class HTMLSubSupConverter implements Converter{
    public static final String CONVERTER_ID = "HTMLSubSupConverter";
	public HTMLSubSupConverter()
	{
		
	}

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) 
	{
		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) 
	{
        String snippet = (String) object;
		snippet = Utils.replaceAllTotal(snippet, "\\&(?!amp;)", "&amp;");
		snippet = Utils.replaceAllTotal(snippet, "\\<(?!(\\/?style)|(\\/?su[bp]))", "&lt;");
//		snippet = Utils.replaceAllTotal(snippet, "\\<((\\/?su[bp]))\\>", "");
		return snippet;
		
//		
	}

}