package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class contains the collection for all ending punctuations.
*
* Ending punctuation is an punctuation ends a word and followed by a space.
* It includes:
* - period [.]:  
* - Question mark [?]: 
* - Exclamation mark [!]: 
* - Comma [,]
* - Semicolon [;]
* - Colon [:]
* - Ampersand [&amp;]
* - Right Parenthesis [)]
* - Right square bracket []]
* - Right curlu Brace [}]
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
public class EndingPunc
{
    // private constructor
    private EndingPunc()
    {
    }
    
    public static boolean IsException(String inWord, String endingPunc)
    {
        Pattern pattern = null;
        switch(endingPunc)
        {
            case ENDING_P:
                pattern = patternP_;    
                break;
            case ENDING_QM:
                pattern = patternQM_;    
                break;
            case ENDING_EM:
                pattern = patternEM_;    
                break;
            case ENDING_CA:
                pattern = patternCA_;    
                break;
            case ENDING_SC:
                pattern = patternSC_;    
                break;
            case ENDING_CL:
                pattern = patternCL_;    
                break;
            case ENDING_A:
                pattern = patternA_;    
                break;
            case ENDING_RP:
                pattern = patternRP_;    
                break;
            case ENDING_RSB:
                pattern = patternRSB_;    
                break;
            case ENDING_RCB:
                pattern = patternRCB_;    
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
    public static String GetSplitStr(String inWord, String endingPunc)
    {
        String outStr = inWord;
        // split it if inWord is not an exception
        if(IsException(inWord, endingPunc) == false)
        {
            outStr = SplitObj.GetSplitStrAfterPunc(inWord, endingPunc);
        }
        return outStr;
    }
    private static void TestExceptionP()
    {
        System.out.println("----- Ending Punc Exception: period -----");
        ArrayList<String> inWordPList = new ArrayList<String>();
        // exception 1
        inWordPList.add("Dr.s");
        inWordPList.add("Mr.s");
        // exception 2
        inWordPList.add("16q22.1");
        inWordPList.add("123.2");
        inWordPList.add("123.234.4567");
        inWordPList.add("1c3.2d4.4e6");
        inWordPList.add("123.23a4.456");
        inWordPList.add("123a.234.456");
        // exception 3
        inWordPList.add("D.C.A.B.");
        inWordPList.add("D.C.A.B");
        inWordPList.add("d.c.a.");
        inWordPList.add("d.c.a");
        inWordPList.add("D.c");
        inWordPList.add("D.CC.A.B.");
        inWordPList.add("DD.C.A.B.");
        inWordPList.add("d.1.a.");
        inWordPList.add("D.123.A.B.");
        // exception 4
        inWordPList.add("St.-John");
        inWordPList.add("123.-John");
        inWordPList.add("#$.-John");
        inWordPList.add("St.$%^John");
        inWordPList.add("St.John");
        inWordPList.add("St.J.");
        inWordPList.add("Test...123");
        for(String inWordP:inWordPList)
        {
            System.out.println("- IsException(" + inWordP + "): "
                + EndingPunc.IsException(inWordP, ENDING_P));
        }
    }
    private static void TestExceptionQM()
    {
        System.out.println("----- Ending Punc Exception: Question Mark -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("ulcers?'");    // 12769.txt
        inWordList.add("ulcers?\"");
        inWordList.add("ulcers?]");
        inWordList.add("XX?'test");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord + "): "
                + EndingPunc.IsException(inWord, ENDING_QM));
        }
    }
    private static void TestExceptionEM()
    {
        System.out.println("----- Ending Punc Exception: Exclamation Mark -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("ulcers!'");
        inWordList.add("ulcers!\"");
        inWordList.add("ulcers!]");
        inWordList.add("XX!'test");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord + "): "
                + EndingPunc.IsException(inWord, ENDING_EM));
        }
    }
    private static void TestExceptionCA()
    {
        System.out.println("----- Ending Punc Exception: Comma -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("50,000");
        inWordList.add("1,234,567");
        inWordList.add("123");
        inWordList.add("12,34");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord + "): "
                + EndingPunc.IsException(inWord, ENDING_CA));
        }
    }
    private static void TestExceptionCL()
    {
        System.out.println("----- Ending Punc Exception: colon -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("12:34");
        inWordList.add("1a2:34");
        inWordList.add("12:3a4");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, ENDING_CL));
        }
    }
    private static void TestExceptionSC()
    {
        System.out.println("----- Ending Punc Exception: semicolon -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("XXX;\"");
        inWordList.add("fusion;syrinx");    // 22.txt
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, ENDING_SC));
        }
    }
    private static void TestExceptionA()
    {
        System.out.println("----- Ending Punc Exception: ampersand -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("AT&T");
        inWordList.add("R&D");
        inWordList.add("AT&");
        inWordList.add("1&2");
        inWordList.add("a&b");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, ENDING_A));
        }
    }
    private static void TestExceptionRP()
    {
        System.out.println("----- Ending Punc Exception: Right parenthesis -----");
        ArrayList<String> inWordRPList = new ArrayList<String>();
        // exception 1
        inWordRPList.add("homocyst(e)ine");
        inWordRPList.add("RS(3)PE");
        inWordRPList.add("NAD(P)H");
        inWordRPList.add("D(+)HUS");
        inWordRPList.add("XX(2)YY(3)");
        inWordRPList.add("XXX( )YYY");    // can't be empty in ()
        inWordRPList.add(" (A)Y");    // can't be empty before ()
        // exception 2
        inWordRPList.add("Ca(2+)-ATPase");
        inWordRPList.add("beta(2)-microglobulin");
        inWordRPList.add("G(i)-protein");
        inWordRPList.add("(Si)-synthase");
        inWordRPList.add("(2)-integrin");
        inWordRPList.add("Zn2(+)-binding");
        inWordRPList.add("I(131)-albumin");
        inWordRPList.add("poly(A)-binding");
        inWordRPList.add("(asparaginyl)-β-hydroxylase");
        inWordRPList.add("(ADP)-ribose");
        inWordRPList.add("xxx(yyy-xxx)-zzz");    //can't have - inside ()
        inWordRPList.add("xxx(yyy-xxx)-zzz-112");    //can't have - inside ()
        inWordRPList.add("xxx(yyy)zzz");    //must have - after ()
        inWordRPList.add("xxx(2)-a 123");    // can't have a space
        // exception 3
        inWordRPList.add("VO(2)max");
        inWordRPList.add("δ(18)O");
        inWordRPList.add("(123)I-mIBG");
        inWordRPList.add("(131)I");
        inWordRPList.add("xx(1)");
        inWordRPList.add("(1)yy");
        // exception 4
        inWordRPList.add("1");
        for(String inWordRP:inWordRPList)
        {
            System.out.println("- IsException(" + inWordRP 
                + "): " + IsException(inWordRP, ENDING_RP));
        }
    }
    private static void TestExceptionRSB()
    {
        System.out.println("----- Ending Punc Exception: right square brace -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("[11C]MeG");
        inWordList.add("[3H]-thymidine");
        inWordList.add("[3H]tyrosine");
        inWordList.add("benzo[a]pyrene");
        inWordList.add("B[e]P");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, ENDING_RSB));
        }
    }
    private static void TestExceptionRCB()
    {
        System.out.println("----- Ending Punc Exception: rigth curely braket -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("X");
        for(String inWord:inWordList)
        {
            System.out.println("- IsException(" + inWord 
                + "): " + IsException(inWord, ENDING_RCB));
        }
    }
    private static void TestException()
    {
        TestExceptionP();
        TestExceptionQM();
        TestExceptionEM();
        TestExceptionCA();
        TestExceptionCL();
        TestExceptionSC();
        TestExceptionA();
        TestExceptionRP();
        TestExceptionRSB();
        TestExceptionRCB();
    }
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java EndingPunc");
            System.exit(0);
        }
        
        // init
        // test case:  use CsText to test
        TestException();
    }
    // data member
    // exception pattern of ending punctuation of period
    private static final String patternStrP_
        = "^(.*\\.s)|((\\w*\\d\\.\\d\\w*)+)|((\\D\\.)+\\D?)|(\\w*\\.-\\w*)|(.*\\.['\"])$";
    private static final Pattern patternP_ = Pattern.compile(patternStrP_);
    // exception pattern of ending punctuation of question mark
    private static final String patternStrQM_ = "^(.*\\?['\"])$";
    private static final Pattern patternQM_ = Pattern.compile(patternStrQM_);
    // exception pattern of ending punctuation of exclamation mark
    private static final String patternStrEM_ = "^(.*!['\"])$";
    private static final Pattern patternEM_ = Pattern.compile(patternStrEM_);
    // exception pattern of ending punctuation of comma
    private static final String patternStrCA_ = "^(\\d+(,[\\d]{3})+)$";
    private static final Pattern patternCA_ = Pattern.compile(patternStrCA_);
    // exception pattern of ending punctuation of colon
    private static final String patternStrCL_ = "^(\\d+:\\d+)$";
    private static final Pattern patternCL_ = Pattern.compile(patternStrCL_);
    // exception pattern of ending punctuation of semicolon
    // no exception, always false, $ alwyas after ^, so it always false
    private static final String patternStrSC_ = "$^";
    private static final Pattern patternSC_ = Pattern.compile(patternStrSC_);
    // exception pattern of ending punctuation of ampersand
    private static final String patternStrA_ = "^[A-Z]+&[A-Z]+$";
    private static final Pattern patternA_ = Pattern.compile(patternStrA_);
    // exception pattern of ending punctuationof right parenthesis )
    private static final String patternStrRP_
        = "^((\\S)*\\([+\\w]\\)(\\S)*)|((\\S)*\\([+\\w]+\\)-(\\S)*)|((\\S)*\\(\\d+\\)(\\S)*)$";
    private static final Pattern patternRP_ = Pattern.compile(patternStrRP_);
    // exception pattern of ending punctuation of right square bracket
    private static final String patternStrRSB_ 
        = "^(\\S*\\[\\d+[A-Z]\\]\\S*)|(\\S*\\[[a-z]\\]\\S*)$";
    private static final Pattern patternRSB_ = Pattern.compile(patternStrRSB_);
    // exception pattern of ending punctuation of right curly brace
    private static final String patternStrRCB_ 
        = "$^";
    private static final Pattern patternRCB_ = Pattern.compile(patternStrRCB_);
    // exception pattern of ending punctuation of period
    public static final String ENDING_P = ".";  // p: period
    public static final String ENDING_QM = "?"; // qm: question mark
    public static final String ENDING_EM = "!"; // em: exclamation mark
    public static final String ENDING_CA = ","; // ca: comma
    public static final String ENDING_SC = ";"; // sc: semicolon
    public static final String ENDING_CL = ":"; // cl: colon
    public static final String ENDING_A = "&"; // a: ampersand
    public static final String ENDING_RP = ")"; // rp: right parenthesis
    public static final String ENDING_RSB = "]";// rsb: right square bracket
    public static final String ENDING_RCB = "}";// rcb: right curly brace
}
