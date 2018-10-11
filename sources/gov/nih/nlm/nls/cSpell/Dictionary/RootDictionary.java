package gov.nih.nlm.nls.cSpell.Dictionary;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the interface for dictionary implementation.
* It provides most basic methods for a dictioanry.
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
public interface RootDictionary
{
    // public methods
    public void AddWord(String word);
    public void AddDictionary(String inFile);
    public void AddDictionaries(String inFile, String rootPath);
    public void AddDictionaries(String inFile, String rootPath, 
        boolean debugFlag);
    public void AddDictionaries(String inFilePath, boolean debugFlag);
    public boolean IsDicWord(String word);    // exact match
    public boolean IsValidWord(String word);    // check X', X/Y
    public int GetSize();
    public HashSet<String> GetDictionarySet();
    // data member
}
