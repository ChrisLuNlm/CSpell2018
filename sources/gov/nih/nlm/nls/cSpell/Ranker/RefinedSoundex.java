package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import org.apache.commons.codec.language.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This class provides a java class to get the refined soundex code form 
* of a string using org.apache.commons.codec.language.RefinedSoundex. 
* A refined soundex code is optimized for spelling checking words. 
* Soundex originally developed by Margaret Odell and Robert Russell.
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class RefinedSoundex
{
    // private constructor
    private RefinedSoundex()
    {
    }
    // Compute Edit (Levenshtein) distance
    public static boolean IsEqualCode(String str1, String str2)
    {
        boolean flag = GetCode(str1).equals(GetCode(str2));
        return flag;
    }
    public static boolean IsEqualSoundex(String str1, String str2)
    {
        boolean flag = GetSoundex(str1).equals(GetSoundex(str2));
        return flag;
    }
    public static int GetDistance(String str1, String str2)
    {
        String str1Lc = str1.toLowerCase();
        String str2Lc = str2.toLowerCase();
        String str1Code = GetCode(str1Lc);
        String str2Code = GetCode(str2Lc);
        int dist = EditDistance.GetEditDistance(str1Code, str2Code);
        return dist;
    }
    public static String GetDistanceDetailStr(String str1, String str2)
    {
        String str1Lc = str1.toLowerCase();
        String str2Lc = str2.toLowerCase();
        String str1Code = GetCode(str1Lc);
        String str2Code = GetCode(str2Lc);
        int dist = EditDistance.GetEditDistance(str1Code, str2Code);
        String detailStr = str1Code + GlobalVars.FS_STR + str2Code 
            + GlobalVars.FS_STR + dist;
        return detailStr;
    }
    public static String GetCode(String inStr)
    {
        String outStr = rs_.encode(inStr);
        return outStr;
    }
    public static String GetSoundex(String inStr)
    {
        String outStr = rs_.soundex(inStr);
        return outStr;
    }
    // public method
    public static void main(String[] args) 
    {
        ArrayList<String> inStrList = new ArrayList<String>();
        if(args.length == 1)
        {
            inStrList.add(args[0]);
        }
        else if (args.length == 0)
        {
            inStrList.add("zinc trisulphonatophthalocyanine");
            inStrList.add("anemia");
            inStrList.add("anaemia");
            inStrList.add("yuppie flu");
            inStrList.add("yuppy flu");
            inStrList.add("toxic edema");
            inStrList.add("toxic oedema");
            inStrList.add("careful");
            inStrList.add("carefully");
            inStrList.add("zyxorin");
            inStrList.add("zyxoryn");
            inStrList.add("zymographical");
            inStrList.add("zymographically");
            inStrList.add("absorption test");
            inStrList.add("absorption tests");
            inStrList.add("effect");
            inStrList.add("affect");
            inStrList.add("now");
            inStrList.add("know");
            inStrList.add("there");
            inStrList.add("their");
        }
        else if(args.length > 0)
        {
            System.err.println("*** Usage: java RefinedSoundex <inStr>");
            System.exit(1);
        }
        // print out
        for(String inStr:inStrList) 
        {
            String code = GetCode(inStr); 
            String soundex = GetSoundex(inStr); 
            System.out.println("- [" + inStr + "] => [" + code + "|"
                + soundex + "]");
        }
        System.out.println("-- effect|affect: " 
            + IsEqualCode("effect", "affect") + "|"
            + IsEqualSoundex("effect", "affect")); 
        System.out.println("-- now|know: " 
            + IsEqualCode("now", "know") + "|"
            + IsEqualSoundex("now", "know")); 
    }
    //private methods
    private static org.apache.commons.codec.language.RefinedSoundex rs_ 
        = new org.apache.commons.codec.language.RefinedSoundex();
}
