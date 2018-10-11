package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the collection for all leading punctuations.
*
* Leading punctuation is a punctuation leads a word after a space.
* It includes:
* - Ampersand [&amp;]
* - Left Parenthesis [(]
* - Left square bracket [[]
* - Left curlu Brace [{]
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
public class LeadingPunc
{
    // private constructor
    private LeadingPunc()
    {
    }
    
    public static boolean IsException(String inWord, String leadingPunc)
    {
        Pattern pattern = null;
        switch(leadingPunc)
        {
            case LEADING_A:
                pattern = patternA_;    
                break;
            case LEADING_LP:
                pattern = patternLP_;    
                break;
            case LEADING_LSB:
                pattern = patternLSB_;    
                break;
            case LEADING_LCB:
                pattern = patternLCB_;    
                break;
        }
        
        boolean exceptionFlag = false; 
        if(pattern != null)
        {
            Matcher matcher = pattern.matcher(inWord);
            exceptionFlag = matcher.matches();
        }
        return exceptionFlag;
    }
    public static String GetSplitStr(String inWord, String leadingPunc)
    {
        String outStr = inWord;
        // split it if inWord is not an exception
        if(IsException(inWord, leadingPunc) == false)
        {
            outStr = SplitObj.GetSplitStrBeforePunc(inWord, leadingPunc);
        }
        return outStr;
    }
    private static void TestExceptionA()
    {
        System.out.println("----- Leading Punc Exception: ampersand -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("AT&T");
        inWordList.add("R&D");
        inWordList.add("AT&");
        inWordList.add("1&2");
        inWordList.add("a&b");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, LEADING_A));
        }
    }
    private static void TestExceptionLP()
    {
        System.out.println("----- Leading Punc Exception: Left parenthesis -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        // exception 1
        inWordList.add("RS(3)PE");
        inWordList.add("Ca(2+)");
        inWordList.add("Ca(2+)-ATPase");
        inWordList.add("P(450)");
        inWordList.add("B(12)");
        inWordList.add("δ(18)O");
        inWordList.add("XX(2)YY(3)");
        // exception 2
        inWordList.add("V(max)");
        inWordList.add("C(min)");
        // exception 3
        inWordList.add("D(+)HUS");
        inWordList.add("GABA(A)");
        inWordList.add("apolipoprotein(a)");
        inWordList.add("beta(1)");
        inWordList.add("homocyst(e)ine");
        inWordList.add("poly(A)-binding");
        // exception 4
        inWordList.add("finger(s)");
        inWordList.add("fetus(es)");
        inWordList.add("extremity(ies)");
        // exception 5
        inWordList.add("poly-(ethylene");
        inWordList.add("poly-(ADP-ribose)");
        inWordList.add("C-(17:0)");
        inWordList.add("I-(alpha)");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, LEADING_LP));
        }
    }
    private static void TestExceptionLSB()
    {
        System.out.println("----- Leading Punc Exception: right square brace -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("benzo[a]pyrene");
        inWordList.add("B[e]P");
        inWordList.add("-[NAME]");    // 18175.txt
        inWordList.add("~[NAME]");    // 4.txt
        inWordList.add("[11C]MeG");
        inWordList.add("[3H]-thymidine");
        inWordList.add("[3H]tyrosine");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, LEADING_LSB));
        }
    }
    private static void TestExceptionLCB()
    {
        System.out.println("----- Leading Punc Exception: rigth curely braket -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("X");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, LEADING_LCB));
        }
    }
    private static void TestException()
    {
        TestExceptionA();
        TestExceptionLP();
        TestExceptionLSB();
        TestExceptionLCB();
    }
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java LeadingPunc");
            System.exit(0);
        }
        
        // init
        // test case:  use CsText to test
        TestException();
    }
    // data member
    // exception pattern of leading punctuation of ampersand
    private static final String patternStrA_ = "^[A-Z]+&[A-Z]+$";
    private static final Pattern patternA_ = Pattern.compile(patternStrA_);
    // exception pattern of leading punctuation of right parenthesis )
    private static final String patternStrLP_
        ="^((\\S)*\\([\\d]+(\\+)?\\)(\\S)*)|((\\S)*\\((max|min)\\))|((\\S)*\\([+\\w]\\)(\\S)*)|([\\w]+((s\\(es\\))|(y\\(ies\\))))|((\\S)*-\\((\\S)*)$";
    private static final Pattern patternLP_ = Pattern.compile(patternStrLP_);
    // exception pattern of leading punctuation of right square bracket
    private static final String patternStrLSB_ 
        = "^(\\S*\\[[a-z]\\]\\S*)|([~\\-]\\[\\S*)$";
    private static final Pattern patternLSB_ = Pattern.compile(patternStrLSB_);
    // exception pattern of leading punctuation of right curly brace: None
    private static final String patternStrLCB_ = "$^";
    private static final Pattern patternLCB_ = Pattern.compile(patternStrLCB_);
    // exception pattern of leading punctuation of period
    public static final String LEADING_A = "&"; // a: ampersand
    public static final String LEADING_LP = "("; // lp: left parenthesis
    public static final String LEADING_LSB = "[";// lsb: left square bracket
    public static final String LEADING_LCB = "{";// lcb: left curly brace
}
