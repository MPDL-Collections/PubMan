package de.mpg.mpdl.tools.csl_test;

import java.util.List;

import de.mpg.mpdl.tools.csl_test.CitationStyleLanguageManagerDefaultImpl;
import de.mpg.mpdl.tools.csl_test.CitationStyleLanguageManagerInterface;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;

/**
 * CitationStyleLanguageExecutor will simply process a sample item with a
 * citationstyle
 * 
 * @author walter
 * 
 */
public class CitationStyleLanguageExecutor {

    public static void main(String[] args) throws Exception {
        CitationStyleLanguageManagerInterface cslManager = new CitationStyleLanguageManagerDefaultImpl();
        cslManager.getOutput(createExportFormat("Name", "html") , "escidoc:1234");
    }
    
    private static ExportFormatVO createExportFormat(String name, String fileFormatName) 
    {
        ExportFormatVO exportFormat = new ExportFormatVO();
        exportFormat.setName("CSL");
        exportFormat.setFormatType(ExportFormatVO.FormatType.LAYOUT);
        FileFormatVO fileFormat = new FileFormatVO();
        fileFormat.setName(fileFormatName);
        fileFormat.setMimeType(FileFormatVO.getMimeTypeByName(fileFormatName));
        exportFormat.setSelectedFileFormat(fileFormat);
        // TODO set ID
        exportFormat.setId("xxx");;
        return exportFormat;
    }
}
