package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class checks the cases of a token.
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
public class CaseTokenUtil 
{
    // Private constructor
    /**
    * Private constructor so no one can call
    */
    private CaseTokenUtil()
    {
    }
    // public methods
    public static boolean IsLowerCase(String inToken)
    {
        // can not be pure digit and Punc
        Matcher matcher = patternLC_.matcher(inToken);
        boolean flag = !DigitPuncTokenUtil.IsDigitPunc(inToken)
            && matcher.matches();
        return flag;
    }
    public static boolean IsUpperCase(String inToken)
    {
        Matcher matcher = patternUC_.matcher(inToken);
        boolean flag = !DigitPuncTokenUtil.IsDigitPunc(inToken)
            && matcher.matches();
        return flag;
    }
    public static boolean IsCapitalizedCase(String inToken)
    {
        Matcher matcher = patternCP_.matcher(inToken);
        boolean flag = !DigitPuncTokenUtil.IsDigitPunc(inToken)
            && matcher.matches();
        return flag;
    }
    public static boolean IsMixedCased(String inToken)
    {
        boolean flag = !DigitPuncTokenUtil.IsDigitPunc(inToken)
        && !IsLowerCase(inToken) && !IsUpperCase(inToken) 
        && !IsCapitalizedCase(inToken);
        return flag;
    }
    public static String ToCapitalizedCase(String inToken)
    {
        String outStr = inToken.substring(0,1).toUpperCase() 
            + inToken.substring(1).toLowerCase();
        return outStr;
    }
    // use lower case
    // private methods
    private static void Tests()
    {
        System.out.println("===== Unit Test of CaseTokenUtil =====");
        Test("lowercase");
        Test("UPPERCASE");
        Test("Capitalized");
        Test("MixedCased");
        Test("mixedCased");
        Test("mixeD123Cased");
        Test("123");
        Test("!@#");
        Test("123!@#");
    }
    private static void Test(String inStr)
    {
        System.out.println(inStr + "|" + IsLowerCase(inStr) 
            + "|" + IsUpperCase(inStr)
            + "|" + IsCapitalizedCase(inStr)
            + "|" + IsMixedCased(inStr)
            + "|" + ToCapitalizedCase(inStr));
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java CaseTokenUtil");
            System.exit(0);
        }
        
        // init
        // test case and print out 
        Tests();
    }
    // data member
    private static final String patternStrUC_ = "^[^a-z]*$";    // upperCased
    private static final String patternStrLC_ = "^[^A-Z]*$";    // lowerCase
    private static final String patternStrCP_ = "^[A-Z][^A-Z]*$";// Capitalized
    private static final Pattern patternUC_ = Pattern.compile(patternStrUC_);
    private static final Pattern patternLC_ = Pattern.compile(patternStrLC_);
    private static final Pattern patternCP_ = Pattern.compile(patternStrCP_);
}
