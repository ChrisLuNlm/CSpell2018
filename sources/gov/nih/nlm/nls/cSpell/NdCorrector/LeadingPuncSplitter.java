package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the leading punctuation splitter, by adding space after them.
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
public class LeadingPuncSplitter
{
    // private constructor
    private LeadingPuncSplitter()
    {
    }
    
    // recursively process
    public static TokenObj Process(TokenObj inTokenObj, int maxProcess)
    {
        boolean debugFlag = false;
        return Process(inTokenObj, maxProcess, debugFlag);
    }
    public static TokenObj Process(TokenObj inTokenObj, int maxProcess,
        boolean debugFlag)
    {
        String inTokenStr = inTokenObj.GetTokenStr();
        String outTokenStr = Process(inTokenStr, maxProcess);
        TokenObj outTokenObj = new TokenObj(inTokenObj);
        //update info if there is a process
        if(inTokenStr.equals(outTokenStr) == false)
        {
            outTokenObj.SetTokenStr(outTokenStr);
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_S_L_P);
            DebugPrint.PrintCorrect("ND", "LeadingPuncSplitter", 
                inTokenStr, outTokenStr, debugFlag);
        }
        return outTokenObj;
    }
    public static TokenObj Process(TokenObj inTokenObj)
    {
        String inTokenStr = inTokenObj.GetTokenStr();
        String outTokenStr = Process(inTokenStr);
        TokenObj outTokenObj = new TokenObj(inTokenObj);
        //update info if there is a process
        if(inTokenStr.equals(outTokenStr) == false)
        {
            outTokenObj.SetTokenStr(outTokenStr);
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_S_L_P);
        }
        return outTokenObj;
    }
    public static String Process(String inWord, int maxProcess) 
    {
        String lastText = inWord;
        String outText = Process(inWord);
        while((maxProcess > 0)
        && (outText.equals(lastText) == false))
        {
            // recusively process
            lastText = outText;
            // converts to textObj for recursively process
            ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(lastText);
            ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
                inTokenList.stream()
                .map(token -> Process(token))
                .collect(Collectors.toList()));
                
            outText = TextObj.TokenListToText(outTokenList);
            maxProcess--;
        }
        return outText;
    }
    /**
    * This method splits the input word by adding a space after leading 
    * punctuation.  The input must be single word (no space).
    * The process method splits the inWord by adding space(s) after leadingPunc.
    * Current algorithm can only handle max. up to 3 endignPuncs.
    * One in each component of coreTermObj: coreTerm, prefix, and suffix.
    * - prefix: leading str with punc|spac|number
    * - coreterm: = the original str - prefix - suffix
    * - suffix: leading str with punc|space|number
    * This can be improved by using recursive algorithm in the coreTerm.
    * For example: "ankle,before.The" in 15737.txt will be split twice in 
    * recursive algorithm.
    *
    * @param    inWord  the input token (single word)
    *
    * @return   the splited word.
    */
    public static String Process(String inWord) 
    {
        String outWord = inWord;
        boolean debugFlag = false;
        // preProcess: check if can skip
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC_DIGIT;
        if(IsQualified(inWord) == true)
        {
            // 0. convert to coreTerm object
            boolean splitFlag = false;
            CoreTermObj cto = new CoreTermObj(inWord, ctType);
            // 1. update coreTerm
            String inCoreTerm = cto.GetCoreTerm();
            String firstLeadingPunc = FindFirstLeadingPunc(inCoreTerm);
            // add a space before the first leadingPunc
            if(firstLeadingPunc != null)
            {
                // get the splitObj and then the split string
                String outCoreTerm 
                    = LeadingPunc.GetSplitStr(inCoreTerm, firstLeadingPunc);
                cto.SetCoreTerm(outCoreTerm);    
                splitFlag = true;
            }
            // 2. update the prefix when it ends with a leadingPunc
            // prefix contains punc and numbers
            String prefix = cto.GetPrefix();
            if((prefix.length() != 0)    // can't be empty
            && (ContainsLeadingPunc(prefix) == true)//must have leadingPunc
            && (IsPureLeadingPunc(prefix) == false))//can't be pure leadingPuncs
            {
                // add space before the first leadingPunc
                String firstLeadingPunc2 = FindFirstLeadingPunc(prefix);
                if(firstLeadingPunc2 != null)
                {
                    // get the splitObj and then the split string
                    String outPrefix 
                        = LeadingPunc.GetSplitStr(prefix, firstLeadingPunc2);
                    cto.SetPrefix(outPrefix);
                    splitFlag = true;
                }
            }
            // 3. update the suffix and add a space after the last leadingPunc
            // suffix contians punctuation and numbers
            String suffix = cto.GetSuffix();
            if((suffix.length() != 0)    // can't be empty
            && (LeadsWithLeadingPunc(suffix) == true))    //leads with leadingPunc
            {
                suffix = GlobalVars.SPACE_STR + suffix;
                cto.SetSuffix(suffix);
                splitFlag = true;
            }
            // update outWord
            if(splitFlag == true)
            {
                outWord = cto.ToString();
            }
        }
        return outWord;
    }
    // check if the token can be skipped
    private static String FindFirstLeadingPunc(String inWord)
    {
        int minFirstIndex = Integer.MAX_VALUE;
        String firstLeadingPunc = null;
        for(String leadingPunc:leadingPuncList_)
        {
            int firstIndex = inWord.indexOf(leadingPunc);
            if((firstIndex != -1)
            && (firstIndex < minFirstIndex))
            {
                firstLeadingPunc = leadingPunc;
                minFirstIndex = firstIndex;
            }
        }
        return firstLeadingPunc;
    }
    private static String FindLastLeadingPunc(String inWord)
    {
        int maxLastIndex = -1;
        String lastLeadingPunc = null;
        for(String leadingPunc:leadingPuncList_)
        {
            int lastIndex = inWord.lastIndexOf(leadingPunc);
            if(lastIndex > maxLastIndex)
            {
                lastLeadingPunc = leadingPunc;
                maxLastIndex = lastIndex;
            }
        }
        return lastLeadingPunc;
    }
    // broader matcher
    private static boolean IsQualified(String inWord)
    {
        boolean qFlag = false;
        // use coreTerm for URL and eMail
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC_DIGIT;
        CoreTermObj cto = new CoreTermObj(inWord, ctType);
        String inCoreTerm = cto.GetCoreTerm();
        // check if pass the matcher to be qualified
        if(ContainsLeadingPunc(inWord) == true)    // contains leadingPunc
        {
            qFlag = true;
        }
        return qFlag;
    }
    private static boolean IsPureLeadingPunc(String inWord)
    {
        Matcher matcher = patternLP_.matcher(inWord);
        return matcher.matches();
    }
    private static boolean LeadsWithLeadingPunc(String inWord)
    {
        Matcher matcher = patternLLP_.matcher(inWord);
        return matcher.matches();
    }
    private static boolean EndsWithLeadingPunc(String inWord)
    {
        Matcher matcher = patternELP_.matcher(inWord);
        return matcher.matches();
    }
    private static boolean ContainsLeadingPunc(String inWord)
    {
        Matcher matcher = patternCLP_.matcher(inWord);
        return matcher.matches();
    }
    // test driver
    private static void TestProcessWord()
    {
        System.out.println("----- Test Process Word: -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("~[NAME]");    // 42.txt
        inWordList.add("-[NAME]");    // 18175.txt
        inWordList.add("1-plug&");    // 12271.txt
        inWordList.add("genes[transposons]");    // 78.txt
        inWordList.add("epilepsy(left");    // 12353.txt
        inWordList.add("test(HLA-B27)");    // 18186.txt
        inWordList.add("you,[NAME]");    // 50.txt
        inWordList.add("dr.[NAME]");
        inWordList.add("Dr.[NAME]");
        inWordList.add("<[CONTACT]>.");
        inWordList.add("you,[NAME]");
        inWordList.add("R&D");    // .txt
        inWordList.add("Research&development");    // .txt
        inWordList.add("xxx(2)-yyy");
        int MaxRecursive = 5;
        for(String inWord:inWordList)
        {
            System.out.println("- Process(" + inWord + "): " 
                + Process(inWord, MaxRecursive));
        }
    }
    private static void TestProcessText()
    {
        // init
        System.out.println("----- Test Process Text: -----");
        String inText = "Head rolling &amp;amp; rock(5'8&quot;).";
        int MaxRecursive = 5;
        // test process:  must use ArrayList<TextObj>
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
            inTokenList.stream()
            .map(token -> XmlHtmlHandler.Process(token))
            .map(token -> LeadingPuncSplitter.Process(token, MaxRecursive))
            .collect(Collectors.toList()));
        // result
        String outText = TextObj.TokenListToText(outTokenList);
        // print out
        System.out.println("--------- LeadingPuncSplitter( ) Test -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("----- Details -----------");
        int index = 0;
        for(TokenObj tokenObj:outTokenList)
        {
            System.out.println(index + "|" + tokenObj.ToHistString());
            index++;
        }
    }
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java LeadingPuncSplitter");
            System.exit(0);
        }
        
        //TestProcessWord();
        TestProcessText();
    }
    // data member
    
    // pure leading punctuation: add <
    private static final String patternStrLP_ = "^[&\\(\\[\\{<]*$";
    private static final Pattern patternLP_ = Pattern.compile(patternStrLP_);
    // contains leading punctuation 
    private static final String patternStrCLP_ = "^.*[&\\(\\[\\{].*$";
    private static final Pattern patternCLP_ = Pattern.compile(patternStrCLP_);
    // leads with leading punctuation, must have 1 char at least 
    private static final String patternStrLLP_ = "^[&\\(\\[\\{].*$";
    private static final Pattern patternLLP_ = Pattern.compile(patternStrLLP_);
    // ends with leading punctuation 
    private static final String patternStrELP_ = "^.*[&\\(\\[\\{]$";
    private static final Pattern patternELP_ = Pattern.compile(patternStrELP_);
    private static final ArrayList<String> leadingPuncList_ 
        = new ArrayList<String>();
    static
    {
        leadingPuncList_.add(LeadingPunc.LEADING_A);
        leadingPuncList_.add(LeadingPunc.LEADING_LP);
        leadingPuncList_.add(LeadingPunc.LEADING_LSB);
        leadingPuncList_.add(LeadingPunc.LEADING_LCB);
    }
}
