package de.mpg.mpdl.migration.foxml;

/**
 * 
 * Constant values used for migration / transformation.
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
        
public interface MigrationConstants
{
    /**
     * the old namespave URI and the old schemaTypes.
     */
    public static final String OLD_NS_URI = "de.mpg.escidoc.metadataprofile.schema.x01";
    public static final String OLD_PUBLICATION = "de.mpg.escidoc.metadataprofile.schema.x01.PublicationDocument";
    public static final String OLD_FILE = "de.mpg.escidoc.metadataprofile.schema.x01.file.FileDocument";
    public static final String OLD_VIRRELEMENT = "de.mpg.escidoc.metadataprofile.schema.x01.virrelement.VirrelementDocument";
    public static final String OLD_ORGUNIT = "de.mpg.escidoc.metadataprofile.schema.x01.organization.OrganizationDetailsDocument";
    /**
     * the base URL of the solutions is used to register handles with the GWDG handle system.
     */
    public static final String PUBMAN_BASE_URL = "http://pubman.mpdl.mpg.de/pubman/item/escidoc:";
    public static final String FACEITEM_BASE_URL = "http://faces.mpib-berlin.mpg.de/details/escidoc:";
    public static final String FACESALBUM_BASE_URL = "http://faces.mpib-berlin.mpg.de/escidoc:";
    public static final String VIRR_BASE_URL = "http://virr.mpdl.mpg.de/escidoc:";
    public static final String TEST_BASE_URL = "http://testing.mpdl.mpg.de/escidoc:";
    /**
     * parameters used to access the GWDG handle system.
     */
    public static final String GWDG_PIDSERVICE_USER = "demo2";
    public static final String GWDG_PIDSERVICE_PASS = "Evaluierung";
    public static final String GWDG_PIDSERVICE_CREATE = "http://handle.gwdg.de:8080/pidservice/write/create";
    public static final String GWDG_PIDSERVICE_MODIFY = "http://handle.gwdg.de:8080/pidservice/write/modify";
    public static final String GWDG_PIDSERVICE_VIEW = "http://handle.gwdg.de:8080/pidservice/read/view";
    public static final String GWDG_PIDSERVICE_FIND = "http://handle.gwdg.de:8080/pidservice/read/search";
    /**
     * parameters for the URL encoding.
     */
    public static final String URL_ENCODING_FORMAT = "application/x-www-form-urlencoded";
    public static final String URL_ENCODING_SCHEME = "UTF-8";
    
}
