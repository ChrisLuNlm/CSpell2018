package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import org.apache.commons.codec.language.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This class provides a java class to get the doubleMetaphone code of a string
* using org.apache.commons.codec.language.DoubleMetaphone.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class Metaphone2
{
    // private constructor
    private Metaphone2()
    {
    }
    // public method
    public static String GetCode(String inStr)
    {
        String outStr = dm_.doubleMetaphone(inStr);
        return outStr;
    }
    public static String GetCode(String inStr, int maxCodeLength)
    {
        dm_.setMaxCodeLen(maxCodeLength);
        String outStr = dm_.doubleMetaphone(inStr);
        return outStr;
    }
    public static int GetDistance(String str1, String str2)
    {
        int maxCodeLength = 10;
        return GetDistance(str1, str2, maxCodeLength);
    }
    public static int GetDistance(String str1, String str2, int maxCodeLength)
    {
        String str1Lc = str1.toLowerCase();
        String str2Lc = str2.toLowerCase();
        String str1Code = GetCode(str1Lc, maxCodeLength);
        String str2Code = GetCode(str2Lc, maxCodeLength);
        int dist = EditDistance.GetEditDistance(str1Code, str2Code);
        return dist;    
    }
    public static String GetDistanceDetailStr(String str1, String str2, 
        int maxCodeLength)
    {
        String str1Lc = str1.toLowerCase();
        String str2Lc = str2.toLowerCase();
        String str1Code = GetCode(str1Lc, maxCodeLength);
        String str2Code = GetCode(str2Lc, maxCodeLength);
        int dist = EditDistance.GetEditDistance(str1Code, str2Code);
        String detailStr = str1Code + GlobalVars.FS_STR + str2Code
            + GlobalVars.FS_STR + dist;
        return detailStr;    
    }
    // alternate: use alternate encode
    public static String GetCode(String inStr, int maxCodeLength,
        boolean alternate)
    {
        dm_.setMaxCodeLen(maxCodeLength);
        String outStr = dm_.doubleMetaphone(inStr, alternate);
        return outStr;
    }
    public static void SetMaxCodeLen(int maxCodeLength)
    {
        dm_.setMaxCodeLen(maxCodeLength);
    }
    public static boolean IsEqualCode(String str1, String str2)
    {
        boolean flag = dm_.isDoubleMetaphoneEqual(str1, str2);
        return flag;
    }
    public static boolean IsEqualCode(String str1, String str2,
        boolean alternate)
    {
        boolean flag = dm_.isDoubleMetaphoneEqual(str1, str2, alternate);
        return flag;
    }
    // private methods
    private static void Test(String str)
    {
        ArrayList<String> inStrList = new ArrayList<String>();
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
        inStrList.add("dianosed");
        inStrList.add("diagnosed");
        inStrList.add("diagnose");
        inStrList.add(str);
        int maxCodeLength = 10;
        // print out
        System.out.println("-- maxCodeLength: [" + maxCodeLength + "]");
        for(String inStr:inStrList) 
        {
            String metaphone = GetCode(inStr, maxCodeLength); 
            System.out.println("- [" + inStr + "] => [" + metaphone + "]");
        }
    }
    
    // test drive
    public static void main(String[] args) 
    {
        String inStr = new String();
        if(args.length == 1)
        {
            inStr = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("*** Usage: java Metaphone2 <inStr>");
            System.exit(1);
        }
        // test
        Test(inStr);
    }
    //private methods
    private static DoubleMetaphone dm_ = new DoubleMetaphone();
}
