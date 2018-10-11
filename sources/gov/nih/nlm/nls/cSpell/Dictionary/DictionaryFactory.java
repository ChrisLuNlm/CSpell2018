package gov.nih.nlm.nls.cSpell.Dictionary;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the factory of dictionary.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
*****************************************************************************/
public class DictionaryFactory
{
    // private constructor
    private DictionaryFactory()
    {
    }
    
    // public methods
    // caseFlag is true for case sensitive dictinoary 
    // or false for case-insensitive
    public static RootDictionary GetDictionary(int dicType)
    {
        boolean caseFlag = false;    // default
        return GetDictionary(dicType, caseFlag);
    }
    public static RootDictionary GetDictionary(int dicType, boolean caseFlag)
    {
        if(dicType == DIC_BASIC)
        {
            return new BasicDictionary(caseFlag);
        }
        else if(dicType == DIC_FULL)
        {
            return new FullDictionary(caseFlag);
        }
        return null;
    }
    // data member
    public final static int DIC_BASIC = 0;
    public final static int DIC_FULL = 1;
}
