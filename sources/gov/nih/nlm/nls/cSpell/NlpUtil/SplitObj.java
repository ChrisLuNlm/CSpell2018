package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the java object for split.
* This class is used in non-dictionary splitter.
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
public class SplitObj 
{
    // public constructor
    /**
    * Public constructor 
    */
    public SplitObj()
    {
    }
    // no suffix is present
    public SplitObj(String inStr)
    {
        prefix_ = inStr;
    }
    public SplitObj(String prefix, String suffix)
    {
        prefix_ = prefix;
        suffix_ = suffix;
    }
    public void SetPrefix(String prefix)
    {
        prefix_ = prefix;
    }
    public void SetSuffix(String suffix)
    {
        suffix_ = suffix;
    }
    public String GetPrefix()
    {
        return prefix_;
    }
    public String GetSuffix()
    {
        return suffix_;
    }
    public String ToString()
    {
        // there is not split if the suffix is empty
        String outStr = prefix_;
        if(suffix_.length() != 0)
        {
            outStr = ToString(prefix_, suffix_);
        }
        return outStr;
    }
    public static String ToString(String prefix, String suffix)
    {
        String outStr = prefix + GlobalVars.SPACE_STR + suffix;
        return outStr;
    }
    // public methods
    // splitStr is the delimiter str where a space is added before it
    // split at the lastIndex of splitStr, after the splitStr
    public static String GetSplitStrAfterPunc(String inStr, String splitStr)
    {
        String outStr = inStr;
        int lastIndex = inStr.lastIndexOf(splitStr);
        int length = splitStr.length();
        if((lastIndex != -1)    // does not contains splitStr
        && (lastIndex != inStr.length()-length))    // not at the end
        {
            String prefix = inStr.substring(0, lastIndex+length);
            String suffix = inStr.substring(lastIndex+length);
            outStr = prefix + GlobalVars.SPACE_STR + suffix;
        }
        return outStr;
    }
    // split at the first index of splitStr, before the splitStr
    public static String GetSplitStrBeforePunc(String inStr, String splitStr)
    {
        String outStr = inStr;
        int index = inStr.indexOf(splitStr);
        int length = splitStr.length();
        if((index != -1)    // does not contains splitStr
        && (index != 0))    // not at the beginning
        {
            String prefix = inStr.substring(0, index);
            String suffix = inStr.substring(index);
            outStr = prefix + GlobalVars.SPACE_STR + suffix;
        }
        return outStr;
    }
    // splitStr is the delimiter str where a space is added before it
    // split at the lastIndex of splitStr
    public static SplitObj GetSplitObj(String inStr, String splitStr)
    {
        SplitObj splitObj = new SplitObj(inStr);
        int index = inStr.indexOf(splitStr);
        int lastIndex = inStr.lastIndexOf(splitStr);
        int length = splitStr.length();
        if((index != -1)    // does not contains splitStr
        && (lastIndex != inStr.length()-length))    // not at the end
        {
            String prefix = inStr.substring(0, lastIndex+1);
            String suffix = inStr.substring(lastIndex+1);
            splitObj = new SplitObj(prefix, suffix);
        }
        return splitObj;
    }
    // private methods
    private static void Test()
    {
        System.out.println("===== Unit Test of SplitObj =====");
        /*
        ArrayList<String> inTermList = new ArrayList<String>();
        inTermList.add("- in details");
        inTermList.add("#$%IN DETAILS:%^(");
        inTermList.add("");
        inTermList.add(" ");
        inTermList.add("()");
        inTermList.add("-http://www.nih.gov");
        for(String inTerm:inTermList)
        {
            CoreTermObj cto = new CoreTermObj(inTerm);
            String outStr = cto.ToString();
            System.out.println(inTerm + "|" + cto.GetPrefix() + "|"
                + cto.GetCoreTerm() + "|" + cto.GetSuffix() + "|"
                + cto.ToString() + "|" + inTerm.equals(outStr));
        }
        */
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java SplitObj");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    private String prefix_ = new String();        // leading Str before the split
    private String suffix_ = new String();        // ending str after the split
}
