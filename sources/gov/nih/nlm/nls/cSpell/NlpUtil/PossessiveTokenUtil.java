package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class checks if a token ends with the pattern of possessive. 
* The pattern includes (in both uppercase and lowercase):
*
* <ul>
* <li>xxx's: Alzheimer's 
* <li>xxxs': Alzheimers'
* <li>yyyx': Bazex', Elixs', 
* <li>xxxz': Duroziez', Schultz', Vaquez', Malassez'
* </ul>
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
public class PossessiveTokenUtil
{
    // private constructor
    private PossessiveTokenUtil()
    {
    }
    // public methods
    public static String GetOrgWord(String inToken)
    {
        String outStr = inToken;
        if(IsPossessivePattern1(inToken) == true)
        {
            outStr = inToken.substring(0, inToken.length()-1);
        }
        else if(IsPossessivePattern2(inToken) == true)
        {
            outStr = inToken.substring(0, inToken.length()-2);
        }
        return outStr;
    }
    public static boolean IsPossessive(String inToken)
    {
        boolean flag 
            = IsPossessivePattern1(inToken) || IsPossessivePattern2(inToken);
        return flag;
    }
    // true: if meet possessive pattern 
    // private method
    private static boolean IsPossessivePattern1(String inToken)
    {
        boolean flag = false;
        // check if ends with possessive
        // upperCase: 'S, S', X', Z'
        if((TokenUtil.IsMatch(inToken, pattern1a_) == true)
        || (TokenUtil.IsMatch(inToken, pattern1b_) == true)
        || (TokenUtil.IsMatch(inToken, pattern1c_) == true)
        || (TokenUtil.IsMatch(inToken, pattern1d_) == true)
        || (TokenUtil.IsMatch(inToken, pattern1e_) == true)
        || (TokenUtil.IsMatch(inToken, pattern1f_) == true))
        {
            flag = true;
        }
        return flag;
    }
    private static boolean IsPossessivePattern2(String inToken)
    {
        boolean flag = false;
        // check if ends with possessive
        // upperCase: 'S, S', X', Z'
        if((TokenUtil.IsMatch(inToken, pattern2a_) == true)
        || (TokenUtil.IsMatch(inToken, pattern2b_) == true))
        {
            flag = true;
        }
        return flag;
    }
    
    private static void Test()
    {
        // test case
        System.out.println("=== Unit Test of PossessiveTokenUtil ===");
        ArrayList<String> inWordList = new ArrayList<String>();
        // uppercase
        inWordList.add("");
        inWordList.add(" ");
        inWordList.add("Chris");
        inWordList.add("CHRIS'S");
        inWordList.add("Chris'S");
        inWordList.add("CHRIS'");
        inWordList.add("BAZEX'");
        inWordList.add("SCHULTZ'");
        // lowercase
        inWordList.add("Alzheimer's");
        inWordList.add("mediator's");
        inWordList.add("Alzheimers'");
        inWordList.add("mediators'");
        inWordList.add("Duroziez'");
        inWordList.add("tiz'");
        inWordList.add("Bazex'");
        inWordList.add("Elixs'");
        // more test
        inWordList.add("Chris's");
        inWordList.add("That is Chris's");
        inWordList.add("That is Chris's idea");
        for(String inWord:inWordList)
        {
            System.out.println("- IsPossessive(" + inWord + "): [" 
                + IsPossessive(inWord) + "], GetOrgWord: [" 
                + GetOrgWord(inWord) + "]");
        }
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java PossessiveTokenUtil");
            System.exit(0);
        }
        // test case and print out
        Test();
    }
    // data member
    // any not lowercase letters ([^a-z]) with S', X', Z'
    private static final String patternStr1a_ = "^[^a-z]+S'$";
    private static final String patternStr1b_ = "^[^a-z]+X'$";
    private static final String patternStr1c_ = "^[^a-z]+Z'$";
    
    // any uppercase, lowercase letter or space with s', x', z'
    private static final String patternStr1d_ = "^[a-zA-Z]+s'$";
    private static final String patternStr1e_ = "^[a-zA-Z]+x'$";
    private static final String patternStr1f_ = "^[a-zA-Z]+z'$";
    // any not lowercase letters ([^a-z]) with 'S
    private static final String patternStr2a_ = "^[^a-z]+'S$";
    // any uppercase, lowercase letter or space with 's
    private static final String patternStr2b_ = "^[a-zA-Z]+'s$";
    private static final Pattern pattern1a_ = Pattern.compile(patternStr1a_);
    private static final Pattern pattern1b_ = Pattern.compile(patternStr1b_);
    private static final Pattern pattern1c_ = Pattern.compile(patternStr1c_);
    private static final Pattern pattern1d_ = Pattern.compile(patternStr1d_);
    private static final Pattern pattern1e_ = Pattern.compile(patternStr1e_);
    private static final Pattern pattern1f_ = Pattern.compile(patternStr1f_);
    private static final Pattern pattern2a_ = Pattern.compile(patternStr2a_);
    private static final Pattern pattern2b_ = Pattern.compile(patternStr2b_);
}
