package de.mpg.escidoc.pubman.installer.panels;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.izforge.izpack.installer.InstallData;

/**
 * 
 * An implementation of the IConfigurationCreatorPanel interface just for JUnit testing
 *
 * @author sieders (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class JUnitConfigurationPanel implements IConfigurationCreatorPanel
{
    
    @Override
    public void processFinishedSuccessfully(String text, String threadName)
    {
        System.out.println("Success Success Success Success Success Success Success Success ");
    }

    @Override
    public void processFinishedWithError(String text, Exception e, String threadName)
    {
        System.out.println("Error Error Error");
    }

    @Override
    public JTextArea getTextArea()
    {
        return new JTextArea();
    }

    @Override
    public String getInstallPath()
    {
        return getInstallData().getInstallPath();
    }

    @Override
    public String getInstanceUrl()
    {
        return getInstallData().getVariable("InstanceUrl");
    }

    @Override
    public InstallData getInstallData() 
    {
        return JUnitInstallData.instance();
    }
}
