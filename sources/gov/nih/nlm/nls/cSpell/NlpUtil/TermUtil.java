package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/*****************************************************************************
* This is the utility class for terms.
* This class contains all basic operations for a term (multiwords).
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
public class TermUtil
{
    // private constructor
    private TermUtil()
    {
    }
    // public methods
    // remove extra space from a term, such as [A   B] to [A B]
    public static String StringTrim(String in)
    {
        StringTokenizer buf = new StringTokenizer(in, " \t");
        String out = new String();
        while(buf.hasMoreTokens())
        {
            out += buf.nextToken() + " ";
        }
        return out.trim();
    }
    public static String RemovePuncSpace(String inTerm)
    {
        return RemoveChars(inTerm, puncSpace_);
    }
    public static String RemoveChars(String inTerm, String removeCharList)
    {
        if((inTerm == null)
        || (removeCharList == null))
        {
            return inTerm;
        }
        String outStr = new String();
        StringTokenizer buf = new StringTokenizer(inTerm, removeCharList);
        while(buf.hasMoreTokens() == true)
        {
            outStr += buf.nextToken();
        }
        return outStr;
    }
    // convert a term (multiwords) into lowercase single words 
    // (separated by space & punctuation)
    public static ArrayList<String> ToWordList(String inTerm, boolean normFlag)
    {
        ArrayList<String> wordList = new ArrayList<String>();
        // lowercase, tokenize words by all puntuations
        if(normFlag == true)
        {
            StringTokenizer buf = new StringTokenizer(inTerm.toLowerCase(), 
                puncSpace_);
            while(buf.hasMoreTokens() == true)
            {
                wordList.add(buf.nextToken());
            }
        }
        else    // lowercase, use space and tab as delim
        {
            // last space is U+00A0, NO-BREAK SPACE, LATIN_1_SUPPLEMENT
            StringTokenizer buf2 = new StringTokenizer(inTerm.toLowerCase(), 
                SPACE_STR);
                //" \t ");
            while(buf2.hasMoreTokens() == true)
            {
                wordList.add(buf2.nextToken());
            }
        }
        return wordList;
    }
    // split on all unicode space, tab, and new line
    public static int GetWordNo(String inTerm)
    {
        // include unicode space
        String[] wordArray = inTerm.split(patternStrSpace_);
        int wordNo = wordArray.length;
        return wordNo;
    }
    // convert a term into words (separated by SPACE_STR: space and tab only)
    // no include spaces
    // SPACE_STR defined at the end of this class, including Unidocde spaces
    public static ArrayList<String> ToWordList(String inTerm)
    {
        // tokenize words
        //StringTokenizer buf = new StringTokenizer(inTerm.trim(), " \t");
        StringTokenizer buf = new StringTokenizer(inTerm.trim(), SPACE_STR);
        ArrayList<String> wordList = new ArrayList<String>();
        while(buf.hasMoreTokens() == true)
        {
            wordList.add(buf.nextToken());
        }
        return wordList;
    }
    // convert a term into words (separated by ASCII space only)
    public static ArrayList<String> ToWordListBySpace(String inTerm)
    {
        // tokenize words
        StringTokenizer buf = new StringTokenizer(inTerm.trim(), " ");
        ArrayList<String> wordList = new ArrayList<String>();
        while(buf.hasMoreTokens() == true)
        {
            wordList.add(buf.nextToken());
        }
        return wordList;
    }
    public static ArrayList<String> ToWordListBySpaceHyphen(String inTerm)
    {
        // tokenize words
        StringTokenizer buf = new StringTokenizer(inTerm.trim(), " -");
        ArrayList<String> wordList = new ArrayList<String>();
        while(buf.hasMoreTokens() == true)
        {
            wordList.add(buf.nextToken());
        }
        return wordList;
    }
    // predicate to check if a multiword term
    public static boolean IsMultiword(String inTerm)
    {
        boolean multiwordFlag = false;
        String inTermTrim = inTerm.trim();
        if(inTermTrim.indexOf(" ") != -1)
        {
            multiwordFlag = true;
        }
        
        return multiwordFlag;
    }
    // strip punctuaction, then trim
    public static String StripPunctuation(String inTerm)
    {
        int length = inTerm.length();
        char[] temp = new char[length];
        int index = 0;
        for(int i = 0; i < length; i++)
        {
            char tempChar = inTerm.charAt(i);
            if(CharUtil.IsPunctuation(tempChar) == false)
            {
                temp[index] = tempChar;
                index++;
            }
        }
        String out = new String(temp);
        return out.trim();            // must be trimmed
    }
    // strip space
    public static String StripSpace(String inTerm)
    {
        int length = inTerm.length();
        char[] temp = new char[length];
        int index = 0;
        for(int i = 0; i < length; i++)
        {
            char tempChar = inTerm.charAt(i);
            if(tempChar != ' ')
            {
                temp[index] = tempChar;
                index++;
            }
        }
        String out = new String(temp);
        return out.trim();            // must be trimmed
    }
    // replace punctuation with space in a string
    public static String ReplacePuncWithSpaceThenTrim(String inTerm)
    {
        char[] temp = inTerm.toCharArray();
        for(int i = 0; i < temp.length; i++)
        {
            if(CharUtil.IsPunctuation(temp[i]) == true)
            {
                temp[i] = ' ';
            }
        }
        String out = Trim(new String(temp));
        return out;
    }
    // strip leading chars if it is a punctuation
    public static String StripLeadChars(String inTerm, String specChars)
    {
        String outStrLead = inTerm;
        int index = 0;
        while((index < outStrLead.length())
        && (CharUtil.IsSpecifiedChar(outStrLead.charAt(index), specChars) == true))
        {
            index++;
        }
        if((index > 0) && (index < outStrLead.length()))
        {
            outStrLead = outStrLead.substring(index);
        }
        return outStrLead;
    }
    // strip ending chars if it is a punctuation
    public static String StripEndChars(String inTerm, String specChars)
    {
        String outStrEnd = inTerm;
        int length = outStrEnd.length();
        int index = length-1;
        while((index > -1)
        && (CharUtil.IsSpecifiedChar(outStrEnd.charAt(index), specChars) == true))
        {
            index--;
        }
        if((index < length-1) && (index > -1))
        {
            outStrEnd = outStrEnd.substring(0, index+1);
        }
        return outStrEnd;
    }
    // recursively remove legal punctuatin at the end of a term
    // used in merge case
    public static String StripEndPunc(String inTerm)
    {
        String curTerm = inTerm;
        String outTerm = new String();
        // recursively strip .?!,s:;'"s)]}
        while(true)
        {
            outTerm = StripEndChars(inTerm, endPunc_);
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
        outTerm.trim();
        if(outTerm.length() == 0)
        {
            outTerm = inTerm;
        }
        return outTerm;
    }
    // strip leading chars if it is a punctuation
    public static String StripLeadPuncSpace(String inTerm)
    {
        String outStrLead = inTerm;
        int index = 0;
        while((index < outStrLead.length())
        && ((CharUtil.IsPunctuation(outStrLead.charAt(index)) == true)
            || (outStrLead.charAt(index) == ' ')))
        {
            index++;
        }
        if((index > 0) && (index < outStrLead.length()))
        {
            outStrLead = outStrLead.substring(index);
        }
        return outStrLead;
    }
    // strip ending chars if it is a punctuation
    public static String StripEndPuncSpace(String inTerm)
    {
        String outStrEnd = inTerm;
        int length = outStrEnd.length();
        int index = length-1;
        while((index > -1)
        && ((CharUtil.IsPunctuation(outStrEnd.charAt(index)) == true)
            || (outStrEnd.charAt(index) == ' ')))
        {
            index--;
        }
        if((index < length-1) && (index > -1))
        {
            outStrEnd = outStrEnd.substring(0, index+1);
        }
        return outStrEnd;
    }
    public static String GetLeadWordFromTerm(String inTerm)
    {
        String inTerm1 = inTerm.trim();
        String leadWord = inTerm1.split(" ")[0];
        return leadWord;
    }
    public static String GetEndWordFromTerm(String inTerm)
    {
        String inTerm1 = inTerm.trim();
        int index = inTerm1.lastIndexOf(" ");
        String endWord = inTerm1.substring(index+1);
        return endWord;
    }
    public static String GetNField(String inTerm, int nField)
    {
        String outStr = inTerm;
        try
        {
            outStr = inTerm.split("\\|")[nField];
        }
        //catch(PatternSyntaxException pse)
        catch(Exception e)
        {
        }
        return outStr;
    }
    // remove all extra space and tab
    // [space  tab] -> [space tab]
    public static String Trim(String inTerm)
    {
        StringTokenizer buf = new StringTokenizer(inTerm, " \t");
        String outTerm = new String();
        while(buf.hasMoreTokens())
        {
            outTerm += buf.nextToken() + " ";
        }
        return outTerm.trim();
    }
    // private methods
    private static void Test()
    {
        System.out.println("===== Unit Test of TermUtil =====");
        String inStr = ":(This is a A-1--2 test),;  test Unicode [ ] link: http:";
        System.out.println("--inStr: [" + inStr + "]");
        System.out.println("-- wordNo: [" + GetWordNo(inStr) + "]");
        String inStr2 = "Test­unicode space\t[ ­] end.\nmore";
        System.out.println("--unicode space Str: [" + inStr2 + "]");
        System.out.println("-- unicode space wordNo: [" + GetWordNo(inStr2) + "]");
        System.out.println("--StripPunctuation: [" + StripPunctuation(inStr) 
            + "]");
        System.out.println("--ReplacePuncWithSpaceThenTrim: [" 
            + ReplacePuncWithSpaceThenTrim(inStr) + "]");
        System.out.println("------- Strip Punc, leadning Punc, ending punc");
        System.out.println("-- SP: [" + StripPunctuation(inStr) + "]");
        System.out.println("-- SLPS: [" + StripLeadPuncSpace(inStr) + "]");
        System.out.println("-- SEPS: [" + StripEndPuncSpace(inStr) + "]");
        System.out.println("-------");
        String testStr = "  - of - ";
        System.out.println("-- testStr: [" + testStr + "]");
        System.out.println("--SP: [" + StripPunctuation(testStr) + "]");
        System.out.println("--SLPS: [" + StripLeadPuncSpace(testStr) + "]");
        System.out.println("--SEPS: [" + StripEndPuncSpace(testStr) + "]");
        System.out.println("===== End of Unit Test =====");
        System.out.println("----- StripEndPuncSpace -----");
        String str1 = "tests?.";
        System.out.println("-- StripEndPuncSpace(" + str1 + "): [" 
            + StripEndPuncSpace(str1) + "]");
        str1 = "test..";
        System.out.println("-- StripEndPuncSpace(" + str1 + "): [" 
            + StripEndPuncSpace(str1) + "]");
        str1 = "test..1";
        System.out.println("-- StripEndPuncSpace(" + str1 + "): [" 
            + StripEndPuncSpace(str1) + "]");
    }
    // Unit test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("** Usage: java TermUtil");
        }
        // test
        Test();
    }
    // data members
    // U+00A0, NO-BREAK SPACE, LATIN_1_SUPPLEMENT is icnlude as the last space
    private static final String puncSpace_ 
        = " \t-({[)}]_!@#%&*\\:;\"',.?/~+=|<>$`^ ";
    // also include unicode space:
    // \s includes: [ \t\n\x0B\f\r]
    // U+00A0, NO-BREAK SPACE
    // U+00AD, SOFT HYPHEN
    private static final String SPACE_STR = " \t ­";
    private static final String patternStrSpace_ = "[\\s ­]+";
    // legal punctuation at the end of a term
    private static final String endPunc_ = ".?!,:;'\")}]";
}
