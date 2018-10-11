package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the ending punctuation splitter, by adding space before them.
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
public class EndingPuncSplitter
{
    // private constructor
    private EndingPuncSplitter()
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
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_S_E_P);
            DebugPrint.PrintCorrect("ND", "EndingPuncSplitter", 
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
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_S_E_P);
        }
        return outTokenObj;
    }
    // recursively process
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
            TextObj textObj = new TextObj(lastText);
            ArrayList<TokenObj> inTokenList = textObj.GetTokenList();
            ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
                inTokenList.stream()
                .map(tokenObj -> tokenObj.GetTokenStr())
                .map(tokenStr -> Process(tokenStr))
                .map(outStr -> new TokenObj(outStr))
                .collect(Collectors.toList()));
                
            outText = TextObj.TokenListToText(outTokenList);
            maxProcess--;
        }
        return outText;
    }
    /**
    * This method splits the input word by adding a space after ending 
    * punctuation.  The input must be single word (no space).
    * The process method splits the inWord by adding space(s) after endingPunc.
    * Current algorithm can only handle max. up to 3 endignPuncs.
    * One in each component of coreTermObj: coreTerm, prefix, and suffix.
    * - prefix: leading str with punc|spac|number
    * - coreterm: = the original str - prefix - suffix
    * - suffix: ending str with punc|space|number
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
        // eProcess: check if can skip
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC_DIGIT;
        if(IsQualified(inWord) == true)
        {
            // 0. convert to coreTerm object
            boolean splitFlag = false;
            CoreTermObj cto = new CoreTermObj(inWord, ctType);
            // 1. update coreTerm
            String inCoreTerm = cto.GetCoreTerm();
            String lastEndingPunc = FindLastEndingPunc(inCoreTerm);
            // add a space after the last endingPunc
            if(lastEndingPunc != null)
            {
                // get the splitObj and then the split string
                String outCoreTerm 
                    = EndingPunc.GetSplitStr(inCoreTerm, lastEndingPunc);
                cto.SetCoreTerm(outCoreTerm);    
                splitFlag = true;
            }
            // 2. update the prefix when it ends with a endingPunc
            // prefix contains punc and numbers
            String prefix = cto.GetPrefix();
            if((prefix.length() != 0)    // can't be empty
            && (EndsWithEndingPunc(prefix) == true))    // ends with endingPunc
            {
                prefix = prefix + GlobalVars.SPACE_STR;
                cto.SetPrefix(prefix);
                splitFlag = true;
            }
            // 3. update the suffix and add a space after the last endingPunc
            // suffix contians punctuation and numbers
            String suffix = cto.GetSuffix();
            if((suffix.length() != 0)    // can't be empty
            && (ContainsEndingPunc(suffix) == true)    // must have endingPunc
            && (IsPureEndingPunc(suffix) == false))    // can't be pure endingPuncs
            {
                // add space after the last endingPunc
                String lastEndingPunc2 = FindLastEndingPunc(suffix);
                if(lastEndingPunc2 != null)
                {
                    // get the splitObj and then the split string
                    String outSuffix 
                        = EndingPunc.GetSplitStr(suffix, lastEndingPunc2);
                    cto.SetSuffix(outSuffix);
                    splitFlag = true;
                }
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
    private static String FindFirstEndingPunc(String inWord)
    {
        int minFirstIndex = Integer.MAX_VALUE;
        String firstEndingPunc = null;
        for(String endingPunc:endingPuncList_)
        {
            int firstIndex = inWord.indexOf(endingPunc);
            if((firstIndex != -1)
            && (firstIndex < minFirstIndex))
            {
                firstEndingPunc = endingPunc;
                minFirstIndex = firstIndex;
            }
        }
        return firstEndingPunc;
    }
    private static String FindLastEndingPunc(String inWord)
    {
        int maxLastIndex = -1;
        String lastEndingPunc = null;
        for(String endingPunc:endingPuncList_)
        {
            int lastIndex = inWord.lastIndexOf(endingPunc);
            if(lastIndex > maxLastIndex)
            {
                lastEndingPunc = endingPunc;
                maxLastIndex = lastIndex;
            }
        }
        return lastEndingPunc;
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
        if((ContainsEndingPunc(inWord) == true)    // contains no endingPunc
        && (InternetTokenUtil.IsEmail(inCoreTerm) == false)     // skip if eMail
        && (InternetTokenUtil.IsUrl(inCoreTerm) == false)    // skip if URL
        && (DigitPuncTokenUtil.IsDigitPunc(inWord) == false))//skip if digitPunc
        {
            qFlag = true;
        }
        return qFlag;
    }
    private static boolean IsPureEndingPunc(String inWord)
    {
        Matcher matcher = patternEP_.matcher(inWord);
        return matcher.matches();
    }
    private static boolean LeadsWithEndingPunc(String inWord)
    {
        Matcher matcher = patternLEP_.matcher(inWord);
        return matcher.matches();
    }
    private static boolean EndsWithEndingPunc(String inWord)
    {
        Matcher matcher = patternEEP_.matcher(inWord);
        return matcher.matches();
    }
    private static boolean ContainsEndingPunc(String inWord)
    {
        Matcher matcher = patternCEP_.matcher(inWord);
        return matcher.matches();
    }
    // test driver
    private static void TestProcessWord()
    {
        System.out.println("----- Test Process Word: -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("ankle,before.The");    // 15737.txt
        inWordList.add(",before.the");
        inWordList.add("before.the");
        inWordList.add("Dr.[NAME]");
        inWordList.add("&quot;");
        inWordList.add("&lt;");
        inWordList.add("Dr.s");
        inWordList.add("i.e.,");
        inWordList.add("Help...?");
        inWordList.add("SS!.");
        inWordList.add("male.read");
        inWordList.add("operation.,");
        inWordList.add("operation.,he");
        inWordList.add("Test.?");
        inWordList.add("Test.\"");
        inWordList.add("clinicaltrials.gov");
        inWordList.add("trombolysis..?");
        inWordList.add("i.e.?");
        inWordList.add(").");
        inWordList.add(".)");
        inWordList.add(".?");
        inWordList.add("50,000");
        inWordList.add("(50,000");
        inWordList.add("<[CONTACT]>.");
        inWordList.add("<[CONTACT]>");
        inWordList.add("you,[NAME]");
        inWordList.add("doctors.Thanks,");
        inWordList.add("http://www.ncbi.nlm.nih.gov/sites/ga?disorder=androgen%20insensitivity%20syndrome");
        inWordList.add("-http://www.ncbi.nlm.nih.gov/sites/ga?disorder=androgen%20insensitivity%20syndrome");
        inWordList.add("1q21.1");    //13.txt
        inWordList.add("16P-13.11");    // 7.txt
        inWordList.add("ulcers?'");    // .txt
        inWordList.add("ulcers!]");    // .txt
        inWordList.add("R&D");    // .txt
        inWordList.add("Research&development");    // .txt
        /**
        inWordList.add("times).");
        inWordList.add("123.234.456");
        inWordList.add("xxx(2)-yyy");
        inWordList.add("12:34");
        inWordList.add("Test.I");
        inWordList.add("Test...123");
        **/
        int MaxRecursive = 5;
        for(String inWord:inWordList)
        {
            System.out.println("- Process(" + inWord + "): " 
                + Process(inWord, MaxRecursive));
        }
    }
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java EndingPuncSplitter");
            System.exit(0);
        }
        
        TestProcessWord();
    }
    // data member
    // pure ending punctuation: add > 
    private static final String patternStrEP_ = "^[\\.\\?!,;:&\\)\\]\\}>]*$";
    private static final Pattern patternEP_ = Pattern.compile(patternStrEP_);
    // contains ending punctuation 
    private static final String patternStrCEP_ = "^.*[\\.\\?!,;:&\\)\\]\\}].*$";
    private static final Pattern patternCEP_ = Pattern.compile(patternStrCEP_);
    // leads with ending punctuation, must have more than 1 chars 
    private static final String patternStrLEP_ = "^[\\.\\?!,;:&\\)\\]\\}].+$";
    private static final Pattern patternLEP_ = Pattern.compile(patternStrLEP_);
    // ends with ending punctuation 
    private static final String patternStrEEP_ = "^.*[\\.\\?!,;:&\\)\\]\\}]$";
    private static final Pattern patternEEP_ = Pattern.compile(patternStrEEP_);
    private static final ArrayList<String> endingPuncList_ 
        = new ArrayList<String>();
    static
    {
        endingPuncList_.add(EndingPunc.ENDING_P);
        endingPuncList_.add(EndingPunc.ENDING_QM);
        endingPuncList_.add(EndingPunc.ENDING_EM);
        endingPuncList_.add(EndingPunc.ENDING_CA);
        endingPuncList_.add(EndingPunc.ENDING_SC);
        endingPuncList_.add(EndingPunc.ENDING_CL);
        endingPuncList_.add(EndingPunc.ENDING_A);
        endingPuncList_.add(EndingPunc.ENDING_RP);
        endingPuncList_.add(EndingPunc.ENDING_RSB);
        endingPuncList_.add(EndingPunc.ENDING_RCB);
    }
}
