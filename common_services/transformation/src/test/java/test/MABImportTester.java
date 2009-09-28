package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.Pair;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.mab.MABImport;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class MABImportTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		MABImport imp = new MABImport();
		
		Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("MAB", "text/plain", "utf-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
    	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/metadateningest2009-08-13.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    	
    	String out = imp.transformMAB2XML(new String(baos.toByteArray(),"utf-8"));
    	
    	
    	
    	System.out.print(new String(result,"UTF-8"));
    	//System.out.print(new String(out.getBytes(),"utf-8"));
	}
	
	

}
