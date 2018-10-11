package gov.nih.nlm.nls.cSpell.Lib;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class provides global variables used in CSpell.
*
* <p><b>History:</b>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class GlobalVars
{
    // private constructor, no one can create it
    private GlobalVars()
    {
    }
    // public method
    public synchronized static GlobalVars GetInstance()
    {
        if(instance_ == null)
        {
            instance_ = new GlobalVars();
        }
        return instance_;
    }
    public void SetFieldSeparator(String value)
    {
        fieldSeparator_ = value;
    }
    public String GetFieldSeparator()
    {
        return fieldSeparator_;
    }
    
    // data member
    /** CSPELL default separator: "|" */
    public static final String LS_STR 
        = System.getProperty("line.separator").toString();    // line sep string
    public static final String FS_STR = "|";    // field seperator string
    public static final String CT_STR = "#";    // Comment string
    public static final String SPACE_STR = " ";    // space string
    public static final String HYPHEN_STR = "-";    // hyphen string
    public static final char SPACE_CHAR = ' ';    // space char
    /** CSPELL version */
    public static final String YEAR = "2018";    // year of release
    /** CSPELL jar string */
    public static final String CSPELL = "cSpell" + YEAR + "dist.jar";
    private String fieldSeparator_ = FS_STR;
    // singleton instance
    private static GlobalVars instance_;
}
