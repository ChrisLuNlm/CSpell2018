package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/*****************************************************************************
* This is the utility class for core term operation.
* This class is used to converts a token to core-term.
* A core-term is to remove all punctuation at the leading and ending of the 
* token except for closed brackets, such as (), [], {}, and &lt;&gt;
*
* Algorithm:
* <ul>
* <li>strip leading chars if they are punctuation, except for left closed brackets
* <li>strip ending chars if they are punctuation, except for right closed brackets
* <li>recursively strip closed brackets of (), [], {}, &lt;&gt; at both ends.
*     <ul>
*     <li>strip lead end bracket if netBracketNo = 0
*     <li>strip lead bracket if netBracketNo &gt; 0
*     <li>strip end bracket if netBracketNo &lt; 0
*     </ul>
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
public class CoreTermUtil
{
    // private constructor
    private CoreTermUtil()
    {
    }
    // public methods
    // the input term can be a word or a term
    public static String GetCoreTerm(String inTerm, int ctType)
    {
        boolean lcFlag = false;
        return GetCoreTerm(inTerm, ctType, lcFlag);
    }
    public static String GetCoreTerm(String inTerm, int ctType, boolean lcFlag)
    {
        String curTerm = inTerm;
        String outTerm = new String(); 
        // recursively strip (), [], {}, <>
        while(true)
        {
            outTerm = GetCoreTermBySteps(curTerm, ctType);
            if(outTerm.equals(curTerm) == true)
            {
                break;
            }
            else if(outTerm.length() < 1)
            {
                break;
            }
            curTerm = outTerm;
        }
        // reset it back to input term if no coreTerm, such as "()"
        outTerm.trim();
        if(outTerm.length() == 0)
        {
            outTerm = inTerm;
        }
        if(lcFlag == true)
        {
            outTerm = outTerm.toLowerCase();
        }
        return outTerm;
    }
    public static String GetCoreTermBySteps(String inTerm, int ctType)
    {
        String leadStripChars = leadSpacePunc_;
        String endStripChars = endSpacePunc_;
        if(ctType == CT_TYPE_SPACE_PUNC_DIGIT)
        {
            leadStripChars = leadSpacePuncDigit_;
            endStripChars = endSpacePuncDigit_;
        }
        // 1. Strip lead end chars that is a punct
        String outStrLead = TermUtil.StripLeadChars(inTerm, leadStripChars);
        String outStrEnd = TermUtil.StripEndChars(outStrLead, endStripChars);
        // 2. strip lead end brackets
        String outStrBracket = StripLeadEndBrackets(outStrEnd);
        String outStrLeadBracket = StripLeadBrackets(outStrBracket);
        String outStrEndBracket = StripEndBrackets(outStrLeadBracket);
        // 3. trim
        String outTerm = TermUtil.Trim(outStrEndBracket);
        return outTerm;
    }
    // private methods
    // inTerm can be a term or a word
    private static String StripLeadEndBrackets(String inTerm)
    {
        if(inTerm.length() == 0)
        {
            return inTerm;
        }
        String outTerm = inTerm;
        int lastIndex = inTerm.length()-1;
        char leadChar = inTerm.charAt(0);
        char endChar = inTerm.charAt(lastIndex);
        // even bracket
        if(((leadChar == '(') && (endChar == ')') && (GetNetCharNo(inTerm, '(', ')') == 0))
        || ((leadChar == '[') && (endChar == ']') && (GetNetCharNo(inTerm, '[', ']') == 0))
        || ((leadChar == '{') && (endChar == '}') && (GetNetCharNo(inTerm, '{', '}') == 0))
        || ((leadChar == '<') && (endChar == '>') && (GetNetCharNo(inTerm, '<', '>') == 0)))
        {
            outTerm = inTerm.substring(1, lastIndex);
        }
        return outTerm;
    }
    // Strip symmetric brackets at lead end chars
    private static String StripLeadBrackets(String inTerm)
    {
        if(inTerm.length() == 0)
        {
            return inTerm;
        }
        String outTerm = inTerm;
        int lastIndex = inTerm.length()-1;
        char leadChar = inTerm.charAt(0);
        char endChar = inTerm.charAt(lastIndex);
        // uneven brackets
        if(((leadChar == '(') && (GetNetCharNo(inTerm, '(', ')') > 0))
        || ((leadChar == '[') && (GetNetCharNo(inTerm, '[', ']') > 0))
        || ((leadChar == '{') && (GetNetCharNo(inTerm, '{', '}') > 0))
        || ((leadChar == '<') && (GetNetCharNo(inTerm, '<', '>') > 0)))
        {
            outTerm = inTerm.substring(1);
        }
        return outTerm;
    }
    private static String StripEndBrackets(String inTerm)
    {
        if(inTerm.length() == 0)
        {
            return inTerm;
        }
        String outTerm = inTerm;
        int lastIndex = inTerm.length()-1;
        char leadChar = inTerm.charAt(0);
        char endChar = inTerm.charAt(lastIndex);
        if(((endChar == ')') && (GetNetCharNo(inTerm, '(', ')') < 0))
        || ((endChar == ']') && (GetNetCharNo(inTerm, '[', ']') < 0))
        || ((endChar == '}') && (GetNetCharNo(inTerm, '{', '}') < 0))
        || ((endChar == '>') && (GetNetCharNo(inTerm, '<', '>') < 0)))
        {
            outTerm = inTerm.substring(0, lastIndex);
        }
        return outTerm;
    }
    // find the total number of a char (must be a single character string
    // netCharNo = leftCharNo - rightCharNo
    private static int GetNetCharNo(String inTerm, char leftChar, 
        char rightChar)
    {
        int netCharNo = 0;
        for(int i = 0; i < inTerm.length(); i++)
        {
            char curChar = inTerm.charAt(i);
            if(curChar == leftChar)
            {
                netCharNo++;
            }
            else if(curChar == rightChar)
            {
                netCharNo--;
            }
        }
        return netCharNo;
    }
    private static String StripLeadEndBracket(String inTerm)
    {
        String outTerm = inTerm;
        int lastIndex = inTerm.length()-1;
        char leadChar = inTerm.charAt(0);
        char endChar = inTerm.charAt(lastIndex);
        if(((leadChar == '(') && (endChar == ')'))
        || ((leadChar == '[') && (endChar == ']'))
        || ((leadChar == '{') && (endChar == '}'))
        || ((leadChar == '<') && (endChar == '>')))
        {
            outTerm = inTerm.substring(1, lastIndex);
        }
        return outTerm;
    }
    private static void Test(String inWord)
    {
        System.out.println("===== Unit Test of CoreTermUtil =====");
        ArrayList<String> inTermList = new ArrayList<String>();
        inTermList.add("- in details");
        inTermList.add("- In details:");
        inTermList.add("#$%IN DETAILS:%^(");
        inTermList.add("");
        inTermList.add(" ");
        inTermList.add("(");
        inTermList.add("()");
        inTermList.add("[()]");
        inTermList.add("$%5^&");
        inTermList.add("$%%^&");
        inTermList.add("{$%%^&}");
        inTermList.add("{in (5) details}");
        inTermList.add("{{in (5) details}");
        inTermList.add("{in (5) details}}");
        inTermList.add("{in (5)} details}}");
        inTermList.add("(in details:)");
        inTermList.add("(in details:))");
        inTermList.add("(-(in details)%^)");
        inTermList.add("{in (5) days},");
        inTermList.add("in (5 days),");
        inTermList.add("in ((5) days),");
        inTermList.add("((clean room(s)))");
        inTermList.add("((inch(es)))");
        inTermList.add("(%) decreased");
        inTermList.add(" space ");
        inTermList.add("-punc,");
        inTermList.add(" spacePunc: ");
        inTermList.add("-digit21:");
        inTermList.add("12digit21");
        inTermList.add(" spacePuncDigit: 12");
        // new data with unicode
        inTermList.add(" which");
        inTermList.add("“eye”");
        inTermList.add("•radical");
        // from input
        inTermList.add("i.e.,");
        inTermList.add("[ORGANIZATION][LOCATION].");
        inTermList.add("(dob-[DATE]),");
        inTermList.add("46XY,dup(16)(q13q23).");
        inTermList.add("girl(5'8\"),");
        inTermList.add("c8899A");
        inTermList.add("Hirayama's");
        inTermList.add(inWord);
        int ctType1 = CT_TYPE_SPACE_PUNC;    // include digit
        int ctType2 = CT_TYPE_SPACE_PUNC_DIGIT;    // no digit
        for(String inTerm:inTermList)
        {
            System.out.println("- GetCoreTerm(" + inTerm + "): [" 
                + GetCoreTerm(inTerm, ctType1) + "], ["
                + GetCoreTerm(inTerm, ctType2) + "]");
        }
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        String inWord = "i.e.,";
        if(args.length == 1)
        {
            inWord = args[0];
        }
        else if(args.length > 0)
        {
            System.out.println("** Usage: java CoreTermUtil <inWord>");
        }
        // test
        Test(inWord);
    }
    // data members
    public static final int CT_TYPE_SPACE_PUNC = 1;    // no spacePunc in ct
    public static final int CT_TYPE_SPACE_PUNC_DIGIT = 2;// no sapcePuncDigit
    private static final String leadSpacePunc_ 
        //= " \t-)}]_!@#%&*\\:;\"',.?/~+=|>$`^";
        = " \t-)}]_!@#%&*\\:;\"',.?/~+=|>$`^ “•”";
    private static final String endSpacePunc_ 
        //= " \t-({[_!@#%&*\\:;\"',.?/~+=|<$`^";
        = " \t-({[_!@#%&*\\:;\"',.?/~+=|<$`^ “•”";
    private static final String leadSpacePuncDigit_ 
        = " \t-)}]_!@#%&*\\:;\"',.?/~+=|>$`^ “•”0123456789";
    private static final String endSpacePuncDigit_ 
        = " \t-({[_!@#%&*\\:;\"',.?/~+=|<$`^ “•”0123456789";
}
