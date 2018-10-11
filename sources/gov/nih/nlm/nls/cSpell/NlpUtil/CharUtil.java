package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class handles all operations of characters.
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
public class CharUtil 
{
    // public constructor
    /**
    * Private constructor 
    */
    private CharUtil()
    {
    }
    // public methods
    // check if the inChar is in the specified chars (in a string format)
    public static boolean IsSpecifiedChar(char inChar, String specChars)
    {
        boolean isSpecCharFlag = false;
        if(specChars != null)
        {
            for(int i = 0; i < specChars.length(); i++)
            {
                if(inChar == specChars.charAt(i))
                {
                    isSpecCharFlag = true;
                    break;
                }
            }
        }
        return isSpecCharFlag;
    }
    //check if the inChar is a punctuation
    public static boolean IsPunctuation(char inChar)
    {
        boolean isPunctuation = false;
        int type = Character.getType(inChar);
        // check if the input is a punctuation
        if((type == Character.DASH_PUNCTUATION)         // -
        || (type == Character.START_PUNCTUATION)        // ( { [
        || (type == Character.END_PUNCTUATION)          // ) } ]
        || (type == Character.CONNECTOR_PUNCTUATION)    // _
        || (type == Character.OTHER_PUNCTUATION)        // !@#%&*\:;"',.?/ 
        || (type == Character.MATH_SYMBOL)              // ~ + = | < >
        || (type == Character.CURRENCY_SYMBOL)          // $
        || (type == Character.MODIFIER_SYMBOL))         // ` ^
        {
            isPunctuation = true;
        }
        return isPunctuation;
    }
    // privage methods
    private static void Test()
    {
        System.out.println("===== Unit Test of CharUtil =====");
        char inChar = '.';
        char inChar2 = 'A';
        String inStr = "12.ab%^";
        for(int i = 0; i < inStr.length(); i++)
        {
            char curChar = inStr.charAt(i);
            System.out.println("- IsPunctuation(" + curChar + "): [" 
                + IsPunctuation(curChar) + "]");
        }
        System.out.println("-------");
        System.out.println("- IsSpecifiedChar(" + inChar + ", " + inStr 
            + "): [" + IsSpecifiedChar(inChar, inStr) + "]");
        System.out.println("- IsSpecifiedChar(" + inChar2 + ", " + inStr 
            + "): [" + IsSpecifiedChar(inChar2, inStr) + "]");
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java CharUtil");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
}
