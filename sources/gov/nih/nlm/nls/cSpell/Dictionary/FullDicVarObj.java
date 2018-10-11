package gov.nih.nlm.nls.cSpell.Dictionary;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the java object for a word (entry) in the full dictionary.
* Full dictioary is not used in 2018 release.
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
public class FullDicVarObj
{
    // private constructor
    public FullDicVarObj(String word) 
    {
        word_ = word;
    }
    public FullDicVarObj(String word, long pos, long infl, String src,
        boolean acrAbb, boolean properNoun)
    {
        word_ = word;
        pos_ = pos;
        infl_ = infl;
        src_ = src;
        acrAbb_ = acrAbb;
        properNoun_ = properNoun;
    }
    
    // public methods
    public String GetWord()
    {
        return word_;
    }
    public String GetSrc()
    {
        return src_;
    }
    public long GetPos()
    {
        return pos_;
    }
    public long GetInfl()
    {
        return infl_;
    }
    public boolean GetAcyAbb()
    {
        return acrAbb_;
    }
    public boolean GetProperNoun()
    {
        return properNoun_;
    } 
    /**
    * This method returns a string of all data members of the current LexItem. 
    * The format is:
    * word|POS|Infl|Src|AcrAbb|ProperNoun
    *
    * @return  a string representation of full dictionary variable object
    */
    public String ToString()
    {
        String fs = GlobalVars.FS_STR;
        String outStr = word_ + fs + pos_ + fs + infl_ + fs + src_ + fs 
            + acrAbb_ + fs + properNoun_ + fs;
        return outStr;
    }
    /**
    * This override method checks the objects sequentiqlly if hascode are the 
    * same. It is used to remove duplicate FullDicVarObj in a set. 
    * Two FullDicVarObj are considered as the same if the String format are 
    * the same:
    * word|pos|infl|src|acrAbb|properNoun
    */
    public boolean equals(Object anObject)
    {
        boolean flag = false;
        if((anObject != null) && (anObject instanceof FullDicVarObj))
        {
            if(this.ToString().equals(((FullDicVarObj)anObject).ToString()))
            {
                flag = true;
            }
        }
        return flag;
    }
    /**
    * This override method is used in hashTable to store data as key. It is
    * used to removed duplicate LexItems in a set. The hasdcode of String
    * format is used.
    *
    * @return  hash code of the detail string of LexItem
    */
    public int hashCode()
    {
        int hashCode = this.ToString().hashCode();
        return hashCode;
    }
    // data member
    public static final String SRC_NONE_STR = "E0000000";
    private String word_ = new String();    // word
    private String src_ = SRC_NONE_STR;        // EUI
    private long pos_ = 0;        // POS, category in long format
    private long infl_ = 0;        // inflection in long format
    private boolean acrAbb_ = false;
    private boolean properNoun_ = false;
}
