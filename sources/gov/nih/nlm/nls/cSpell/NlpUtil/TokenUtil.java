package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the java object for token utility.
* It includes lots of methods to validate the type of a token.
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
public class TokenUtil 
{
    // public constructor
    /**
    * Public constructor to initiate the HashMap table.
    */
    private TokenUtil()
    {
    }
    // public methods
    public static boolean IsSpaceToken(String inStr)
    {
        boolean spaceFlag = spaceStrSet_.contains(inStr);
        return spaceFlag;
    }
    public static boolean IsEmptyToken(String inStr)
    {
        boolean flag = ((inStr == null) || (inStr.length() == 0));
        return flag;
    }
    // to be deleted, no need
    // empty token is a token empty (no length) or null
    public static boolean IsNotEmptyToken(String inToken)
    {
        boolean flag = true;
        if((inToken == null) || (inToken.length() == 0))
        {
            flag = false;
        }
        return flag;
    }
    public static boolean IsMatch(String inToken, Pattern inPattern)    
    {
        boolean checkEmptyToken = true;
        return IsMatch(inToken, inPattern, checkEmptyToken);
    }
    public static boolean IsMatch(String inToken, Pattern inPattern,    
        boolean checkEmptyToken)
    {
        boolean flag = false;
        if(checkEmptyToken == true)
        {
            // check if empty token
            //if(TokenUtil.IsNotEmptyToken(inToken) == true)
            if(TokenUtil.IsEmptyToken(inToken) == false)
            {
                Matcher matcher = inPattern.matcher(inToken);
                flag = matcher.matches();
            }
        }
        else    // option: not to check empty token
        {
            Matcher matcher = inPattern.matcher(inToken);
            flag = matcher.matches();
        }
        return flag;
    }
    public static boolean IsName(String inToken)
    {
        // TBD
        return true;
    }
    // private methods
    private static void Test()
    {
        System.out.println("===== Unit Test of TokenUtil =====");
        System.out.println("-------");
        String str1 = "­";  // unicode space
        System.out.println("- IsSpaceToken: " + TokenUtil.IsSpaceToken(str1));
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java TokenUtil");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    // the unicode space str set must sync with TextObj.patternStrSpace_
    private static final HashSet<String> spaceStrSet_ = new HashSet<String>();
    static
    {
        spaceStrSet_.add(" ");    // U+0020, SPACE
        spaceStrSet_.add("\t");
        spaceStrSet_.add("\n");
        spaceStrSet_.add(" ");    // U+00A0, NO-BREAK SPACE
        spaceStrSet_.add("­");    // U+00AD, SOFT HYPHEN
    }
}
