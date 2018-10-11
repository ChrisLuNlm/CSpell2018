package gov.nih.nlm.nls.cSpell.Util;
/*****************************************************************************
* This class checks the type of platform.
*
* <p><b>History:</b>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class Platform
{
    // private constructor
    private Platform()
    {
    }
    // public methods
    /*
    * This method detects if the plaform is a windows bases OS or not.
    *
    * @return true if the platform is a window based OS.
    */
    public static final boolean IsWindow()
    {
        boolean flag = false;
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().indexOf("windows") > -1)
        {
            flag = true;
        }
        return flag;
    }
}
