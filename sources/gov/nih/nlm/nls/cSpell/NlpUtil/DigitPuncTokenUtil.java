package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class handles all operations of digits and punctuation.
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
public class DigitPuncTokenUtil
{
    // public constructor
    /**
    * Public constructor to initiate DigitPuncTokenUtil.
    */
    private DigitPuncTokenUtil()
    {
    }
    // public methods
    public static boolean IsDigitPunc(String inToken)
    {
        boolean checkEmptyTokenFlag = true;
        return TokenUtil.IsMatch(inToken, patternDP_, checkEmptyTokenFlag);
    }
    public static boolean IsPunc(String inToken)
    {
        boolean checkEmptyTokenFlag = true;
        return TokenUtil.IsMatch(inToken, patternP_, checkEmptyTokenFlag);
    }
    // check if an Arabic number or digit
    public static boolean IsDigit(String inToken)
    {
        boolean checkEmptyTokenFlag = true;
        // can not be pure puntuation, such as "-" or "+"
        boolean digitFlag = ((IsPunc(inToken) == false)
            && (TokenUtil.IsMatch(inToken, patternD_, checkEmptyTokenFlag)));
        return digitFlag;    
    }
    // private method
    private static void TestDigitPunc()
    {
        ArrayList<String> inWordList = new ArrayList<String>(); 
        inWordList.add("~!@#$%^&*()");
        inWordList.add("123.500");
        inWordList.add("12-35-00");
        inWordList.add("12.35.00!");
        inWordList.add("!@#123$%^");
        inWordList.add("a.456");
        inWordList.add("");
        inWordList.add("  ");    // space is not a punctuation
        for(String inWord:inWordList)
        {
            System.out.println("- IsDigitPunc(" + inWord + "): " 
                + IsDigitPunc(inWord));
        }
    }
    private static void TestPunc()
    {
        ArrayList<String> inWordList = new ArrayList<String>(); 
        inWordList.add("~!@#$%^&*()");
        inWordList.add("_");
        inWordList.add("+`-={}|[]\\:\"<>?;',./");
        inWordList.add("~!@#$%^&*()_+`-={}|[]\\:\"<>?;',./");
        inWordList.add("abc");
        inWordList.add("123.500");
        inWordList.add("!@#123$%^");
        inWordList.add("  ");    // space is not a punctuation
        for(String inWord:inWordList)
        {
            System.out.println("- IsPunc(" + inWord + "): " + IsPunc(inWord));
        }
    }
    private static void TestDigit()
    {
        ArrayList<String> inWordList = new ArrayList<String>(); 
        inWordList.add("+123.456");
        inWordList.add("-123.456");
        inWordList.add("+123.");
        inWordList.add("+123");
        inWordList.add("123.");
        inWordList.add("123");
        inWordList.add("3");
        inWordList.add("3.");
        inWordList.add("3.0");
        inWordList.add(".4");
        inWordList.add(".45");
        inWordList.add("+.456");
        inWordList.add(".456");
        inWordList.add("0.4");
        inWordList.add("a0.456");
        inWordList.add("a.456");
        inWordList.add("0.456b");
        inWordList.add("");
        inWordList.add("a");
        inWordList.add("-");    // shoudl be false
        for(String inWord:inWordList)
        {
            System.out.println("- IsDigit(" + inWord + "): " + IsDigit(inWord));
        }
    }
    private static void Test()
    {
        System.out.println("===== Unit Test of DigitPuncTokenUtil =====");
        //TestDigit();
        System.out.println("---- TestPunc() ---");
        TestPunc();
        System.out.println("---- TestDigit() ---");
        TestDigit();
        System.out.println("---- TestDigitPunc() ---");
        TestDigitPunc();
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java DigitPuncTokenUtil");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    private static final String patternStrDP_ = "^([\\W_\\d&&\\S]+)$";
    private static final Pattern patternDP_ = Pattern.compile(patternStrDP_);
    // non-word char|_|&& not non-space char
    //private static final String patternStrP_ = "^([\\W_&&\\S]+)$";
    private static final String patternStrP_ = "^(\\p{Punct}+)$";
    private static final Pattern patternP_ = Pattern.compile(patternStrP_);
    //private static final String patternStrD_ = "^([+-]?(\\d*\\.)?\\d*)$";
    private static final String patternStrD_ = "^([+-]?(\\d)*(\\.)?\\d*)$";
    private static final Pattern patternD_ = Pattern.compile(patternStrD_);
}
