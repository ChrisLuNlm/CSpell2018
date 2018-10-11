package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the java object for coreTerm.
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
public class CoreTermObj 
{
    // public constructor
    /**
    * Public constructor 
    *
    * @param inStr input string
    * @param ctType core term type, defined in CoreTermUtil.java
    */
    public CoreTermObj(String inStr, int ctType)
    {
        Process(inStr, ctType);
    }
    public void SetPrefix(String prefix)
    {
        prefix_ = prefix;
    }
    public void SetCoreTerm(String coreTerm)
    {
        coreTerm_ = coreTerm;
    }
    public void SetSuffix(String suffix)
    {
        suffix_ = suffix;
    }
    public String GetCoreTerm()
    {
        return coreTerm_;
    }
    public String GetPrefix()
    {
        return prefix_;
    }
    public String GetSuffix()
    {
        return suffix_;
    }
    // compose the object and converts backto String format
    public String ToString()
    {
        String ourStr = prefix_ + coreTerm_ + suffix_;
        return ourStr;
    }
    public String ToDetailString()
    {
        String ourStr = prefix_ + GlobalVars.FS_STR
            + coreTerm_ + GlobalVars.FS_STR + suffix_;
        return ourStr;
    }
    // public methods
    public void Process(String inTerm, int ctType)
    {
        // 1. get coreterm from the input Term
        coreTerm_ = CoreTermUtil.GetCoreTerm(inTerm, ctType);
        // 2. get prefix and suffix
        int inLength = inTerm.length();
        int coreLength = coreTerm_.length();
        if((coreLength > 0)    // not empty string
        && (coreLength < inLength))    // coreTerm = strip punc 
        {
            int index = inTerm.indexOf(coreTerm_);
            int indexS = index + coreLength; 
            // Check error: should not happen
            if(index == -1)
            {
                System.err.println("** Err@CoreTermObj.Process(" + inTerm 
                    + "): prefix too small");
            }
            else if (indexS > inLength)
            {
                System.err.println("** Err@CoreTermObj.Process(" + inTerm 
                    + "): suffix too big");
            }
            // get prefix
            if(index > 0)
            {
                prefix_ = inTerm.substring(0, index);
            }
            // get suffix
            if(indexS < inLength)
            {
                suffix_ = inTerm.substring(indexS);
            }
        }
    }
    // private methods
    private static void Test()
    {
        System.out.println("===== Unit Test of TokenUtil =====");
        ArrayList<String> inTermList = new ArrayList<String>();
        inTermList.add("- in details");
        inTermList.add("#$%IN DETAILS:%^(");
        inTermList.add("");
        inTermList.add(" ");
        inTermList.add("()");
        inTermList.add("-http://www.nih.gov");
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC_DIGIT;
        for(String inTerm:inTermList)
        {
            CoreTermObj cto = new CoreTermObj(inTerm, ctType);
            String outStr = cto.ToString();
            System.out.println(inTerm + "|" + cto.GetPrefix() + "|"
                + cto.GetCoreTerm() + "|" + cto.GetSuffix() + "|"
                + cto.ToString() + "|" + inTerm.equals(outStr));
        }
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java CoreTermObj");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    private String coreTerm_ = new String();
    private String prefix_ = new String();        // leading punc chars
    private String suffix_ = new String();        // ending punc chars
}
