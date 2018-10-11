package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the leading digit splitter, by adding a space after the digit.
* Such as "20years" to "20 years". There are legit word starts with digits
* and they are consider as exceptionss. They are:
* - (1st|2nd|3rd|\dth)
* - (\d+)[a-zA-Z]]
* - (\d+)[a-zA-z]+-[a-zA-Z]+] to ["]
* - (\d+)[A-Z]+[A-Z0-9]+
* - (\d+)([a-zA-Z])(punct, digit)*
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
public class LeadingDigitSplitter 
{
    // private constructor
    private LeadingDigitSplitter()
    {
    }
    
    public static TokenObj Process(TokenObj inTokenObj)
    {
        boolean debugFlag = false;
        return Process(inTokenObj, debugFlag);
    }
    public static TokenObj Process(TokenObj inTokenObj, boolean debugFlag)
    {
        String inTokenStr = inTokenObj.GetTokenStr();
        String outTokenStr = Process(inTokenStr);
        TokenObj outTokenObj = new TokenObj(inTokenObj);
        //update info if there is a process
        if(inTokenStr.equals(outTokenStr) == false)
        {
            outTokenObj.SetTokenStr(outTokenStr);
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_S_L_D);
            DebugPrint.PrintCorrect("ND", "LeadingDigitSplitter", 
                inTokenStr, outTokenStr, debugFlag);
        }
        return outTokenObj;
    }
    /**
    * This method handle leading digit by adding a space after the digit
    * It is desgined to work on the input of single word.
    *
    * @param    inWord  the input token (single word)
    *
    * @return   the corrected word, does nto change the case,
    *           the original input token is returned if no mapping is found.
    */
    public static String Process(String inWord) 
    {
        String outWord = inWord;
        // convert to coreterm, such as 30th.
        boolean splitFlag = false;
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC;
        CoreTermObj cto = new CoreTermObj(inWord, ctType);
        String inCoreTerm = cto.GetCoreTerm();
        // update core term: check if the token leads with digit
        Matcher matcherLd = patternLD_.matcher(inCoreTerm);
        if(matcherLd.find() == true)
        {
            // update core term: split if it is an exception
            if(IsException(inCoreTerm) == false)
            {
                String outCoreTerm = matcherLd.group(1) + GlobalVars.SPACE_STR
                    + matcherLd.group(2) + matcherLd.group(3);
                cto.SetCoreTerm(outCoreTerm);
                splitFlag = true;
            }
        }
        // get outWord from coreTermObj if split happens
        if(splitFlag == true)
        {
            outWord = cto.ToString();
        }
        return outWord;
    }
    private static boolean IsException(String inWord)
    {
        boolean expFlag = false;
        boolean checkEmptyToken = false;
        if((TokenUtil.IsMatch(inWord, patternLDE1_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternLDE2_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternLDE3_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternLDE4_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternLDE5_, checkEmptyToken) == true))
        {
            expFlag = true;
        }
        return expFlag;
    }
    // test driver
    private static void TestProcessWord(HashMap<String, String> informalExpMap)
    {
        System.out.println("----- Test Process Word: -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        // split
        inWordList.add("21year");
        inWordList.add("5mg");
        inWordList.add("5and");
        inWordList.add("Iam21year");
        // exception 1
        inWordList.add("1st");
        inWordList.add("42nd");
        inWordList.add("3rd");
        inWordList.add("11th");
        inWordList.add("31st");
        inWordList.add("31th");
        inWordList.add("30th.");    // 73.txt
        inWordList.add("30th");        
        // exception 2
        inWordList.add("31D");
        inWordList.add("9L");
        inWordList.add("5q");
        // exception 3
        inWordList.add("67LR");
        inWordList.add("3Y1");
        inWordList.add("7PA2");
        inWordList.add("5FU");
        // exception 4
        inWordList.add("111In-Cl");
        inWordList.add("5q-syndrome");
        inWordList.add("38C-13");
        // exception 5
        inWordList.add("1q21.1.");    // 13.txt
        inWordList.add("1q21.1");    // 13.txt
        inWordList.add("1q21");
        inWordList.add("16P-13.11");    // 77.txt
        inWordList.add("16P-13");
        // others
        inWordList.add("15years");
        inWordList.add("1.5years");
        
        for(String inWord:inWordList)
        {
            System.out.println("- Process(" + inWord + "): "
                + Process(inWord));
        }
    }
    private static void TestProcess(HashMap<String, String> informalExpMap)
    {
        // init
        System.out.println("----- Test Process Text: -----");
        String inText = "u rolling &amp;amp; pls(12years).";
        // test process:  must use ArrayList<TextObj>
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
            inTokenList.stream()
            .map(token -> XmlHtmlHandler.Process(token))
            .map(token -> LeadingPuncSplitter.Process(token))
            // must have flatMap after split
            .flatMap(token -> TextObj.FlatTokenToArrayList(token).stream())
            .map(token -> LeadingDigitSplitter.Process(token))
            // must have flatMap after split
            .flatMap(token -> TextObj.FlatTokenToArrayList(token).stream())
            .map(token -> InformalExpHandler.Process(token, informalExpMap))
            .collect(Collectors.toList()));
        // result
        String outText = TextObj.TokenListToText(outTokenList);
        // print out
        System.out.println("--------- LeadingDigitSplitter( ) Test ----------");
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
    // test driver
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java LeadingDigitSplitter");
            System.exit(0);
        }
        
        // init
        String inFile = "../data/Misc/informalExpression.data";
        HashMap<String, String> informalExpMap
            = InformalExpHandler.GetInformalExpMapFromFile(inFile);
        // init
        TestProcessWord(informalExpMap);
        TestProcess(informalExpMap);
    }
    // data member
    // pattern of leading digits: must have t char after leading digits
    private static final String patternStrLD_ 
        = "^(\\d*\\.?\\d+)([a-zA-Z]{2,})(.*)$";
    private static final Pattern patternLD_ = Pattern.compile(patternStrLD_);
    // pattern of exception 1: ordinal number
    private static final String patternStrLDE1_ 
        = "^((\\d*)(1st|2nd|3rd))|((\\d+)(th))$";
    private static final Pattern patternLDE1_ 
        = Pattern.compile(patternStrLDE1_);
    // pattern of exception 2: [digit][single character]
    private static final String patternStrLDE2_ = "^(\\d+)([a-zA-Z])$";
    private static final Pattern patternLDE2_ 
        = Pattern.compile(patternStrLDE2_);
    // pattern of exception 3: [digit][Uppercase][uppercase, digit]
    private static final String patternStrLDE3_ = "^(\\d+)([A-Z]+)([A-Z0-9]*)$";
    private static final Pattern patternLDE3_ 
        = Pattern.compile(patternStrLDE3_);
    // pattern of exception 4: [digit][Uppercase][uppercase, digit]
    private static final String patternStrLDE4_ = "^(\\d+)([a-zA-Z]+)-(\\w*)$";
    private static final Pattern patternLDE4_ 
        = Pattern.compile(patternStrLDE4_);
    // pattern of exception 5: [digit][single character][digit, punc]
    private static final String patternStrLDE5_ 
        = "^(\\d+)([a-zA-Z])([\\p{Punct}\\d]*)$";
    private static final Pattern patternLDE5_ 
        = Pattern.compile(patternStrLDE5_);
}
