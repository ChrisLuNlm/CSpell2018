package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the ending digit splitter, adding a space before the digit.
* Such as "from2007" to "from 2007". There are legit word ends with digits
* and they are consider as exceptionss. They are:
* - (1st|2nd|3rd|\dth)
* - (\d+)[a-zA-Z]]
* - (\d+)[a-zA-z]+-[a-zA-Z]+] to ["]
* - (\d+)[A-Z]+[A-Z0-9]+
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
public class EndingDigitSplitter 
{
    // private constructor
    private EndingDigitSplitter()
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
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_S_E_D);
            DebugPrint.PrintCorrect("ND", "EndingDigitSplitter", 
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
        // convert to coreterm, such as hereditary2)
        boolean splitFlag = false;
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC;
        CoreTermObj cto = new CoreTermObj(inWord, ctType);
        String inCoreTerm = cto.GetCoreTerm();
        // update core term: check if the token leads with digit
        Matcher matcherLd = patternED_.matcher(inCoreTerm);
        if((matcherLd.find() == true)
        && (DigitPuncTokenUtil.IsDigitPunc(inCoreTerm) == false))// can't be digit
        {
            // update core term: split if it is an exception
            if(IsException(inCoreTerm) == false)
            {
                String outCoreTerm = matcherLd.group(1) + matcherLd.group(2) 
                    + GlobalVars.SPACE_STR + matcherLd.group(3);
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
        boolean checkEmptyToken = false;
        boolean expFlag = false;
        if((TokenUtil.IsMatch(inWord, patternEDE1_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternEDE2_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternEDE3_, checkEmptyToken) == true)
        || (TokenUtil.IsMatch(inWord, patternEDE4_, checkEmptyToken) == true))
        {
            expFlag = true;
        }
        return expFlag;
    }
    // test driver
    private static void TestProcessWord()
    {
        System.out.println("----- Test Process Word: -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("100.1)");    // 26.txt
        inWordList.add("questions.1)");    // 26.txt
        inWordList.add("hereditary2)");    // 26.txt
        inWordList.add("disease3)");    // 26.txt
        inWordList.add("from2007");
        inWordList.add("Iam21year");
        inWordList.add("shuntfrom2007.");    // 14849.txt
        inWordList.add("jk5");    // 73.txt
        inWordList.add("-.1)");
        inWordList.add("+.1)");
        inWordList.add(".1)");
        inWordList.add("0.112)");
        // exception 1
        inWordList.add("A1");
        inWordList.add("A2780");
        inWordList.add("UPD14");    // 94.txt
        inWordList.add("CAD106");    // 14240.txt
        // exception 2
        inWordList.add("NCI-H460");
        inWordList.add("CCRF-HSB2");
        inWordList.add("Co-Q10");
        inWordList.add("saframycin-Yd2");
        // exception 3
        inWordList.add("alpha1");
        inWordList.add("beta2");
        inWordList.add("gamma2");
        inWordList.add("delta1");
        inWordList.add("epsilon4");
        // exception 4
        inWordList.add("c7");    // 18055.txt
        for(String inWord:inWordList)
        {
            System.out.println("- Process(" + inWord + "): "
                + Process(inWord));
        }
    }
    // test driver
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java EndingDigitSplitter");
            System.exit(0);
        }
        
        // init
        TestProcessWord();
    }
    // data member
    // pattern of leading digits
    private static final String patternStrED_ = "^(.*)([a-zA-Z\\.]+)(\\d+)$";
    private static final Pattern patternED_ = Pattern.compile(patternStrED_);
    // pattern of exception 1: [chars][digit]+
    private static final String patternStrEDE1_ = "^([A-Z]+)(\\d+)$";
    private static final Pattern patternEDE1_ 
        = Pattern.compile(patternStrEDE1_);
    // pattern of exception 2: [a-zA-Z]-[a-zA-z][digit]
    private static final String patternStrEDE2_ 
        = "^([a-zA-Z]+)-([a-zA-Z]+)(\\d+)$";
    private static final Pattern patternEDE2_ 
        = Pattern.compile(patternStrEDE2_);
    // pattern of exception 3: [.][alpha|beta|gamma][digit]
    private static final String patternStrEDE3_ 
        = "^(.*)(alpha|beta|gamma|delta|epsilon)(\\d)$";
    private static final Pattern patternEDE3_ 
        = Pattern.compile(patternStrEDE3_);
    // pattern of exception 4: [single chars][digit]+
    private static final String patternStrEDE4_ = "^([a-zA-Z])(\\d+)$";
    private static final Pattern patternEDE4_ 
        = Pattern.compile(patternStrEDE4_);
}
