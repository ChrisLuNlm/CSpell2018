package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class checks if a token ends with the pattern of parenthetic
* plural forms, such as (s), (es), (ies). 
*
* <ul>
* <li>xxx(s): finger(s), hand(s), book(s) 
* <li>xxx(es)': mass(es), fetus(es), box(es), waltz(es), match(es), splash(es) 
* <li>xxx(ies): fly(ies), extremity(ies)
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
public class ParentheticPluralTokenUtil
{
    // private constructor
    private ParentheticPluralTokenUtil()
    {
    }
    // public methods
    public static String GetOrgWord(String inToken)
    {
        String outStr = inToken;
        if(IsParentheticPluralS(inToken) == true)
        {
            outStr = inToken.substring(0, inToken.length()-3);
        }
        else if(IsParentheticPluralEs(inToken) == true)
        {
            outStr = inToken.substring(0, inToken.length()-4);
        }
        else if(IsParentheticPluralIes(inToken) == true)
        {
            outStr = inToken.substring(0, inToken.length()-5);
        }
        return outStr;
    }
    public static boolean IsParentheticPlural(String inToken)
    {
        boolean flag = IsParentheticPluralS(inToken) 
            || IsParentheticPluralEs(inToken)
            || IsParentheticPluralIes(inToken);
        return flag;
    }
    // true: if meet possessive pattern 
    // private method
    private static boolean IsParentheticPluralS(String inToken)
    {
        boolean flag = false;
        if((TokenUtil.IsMatch(inToken, patternS_) == true)
        && (TokenUtil.IsMatch(inToken, patternS1_) == false)
        && (TokenUtil.IsMatch(inToken, patternS2_) == false))
        {
            flag = true;
        }
        return flag;
    }
    private static boolean IsParentheticPluralEs(String inToken)
    {
        boolean flag = false;
        if(TokenUtil.IsMatch(inToken, patternEs_) == true)
        {
            flag = true;
        }
        return flag;
    }
    private static boolean IsParentheticPluralIes(String inToken)
    {
        boolean flag = false;
        if(TokenUtil.IsMatch(inToken, patternIes_) == true)
        {
            flag = true;
        }
        return flag;
    }
    
    private static void Test()
    {
        // test case
        System.out.println("=== Unit Test of ParentheticPluralTokenUtil ===");
        ArrayList<String> inWordList = new ArrayList<String>();
        // (s)
        inWordList.add("");
        inWordList.add(" ");
        inWordList.add("finger(s)");
        inWordList.add("toe(s)");
        inWordList.add("rib(s)");
        inWordList.add("match(s)");
        inWordList.add("fly(s)");
        // (es)
        inWordList.add("Mass(es)");
        inWordList.add("exostosis(es)");
        inWordList.add("plexus(es)");
        inWordList.add("fetus(es)");
        inWordList.add("illness(es)");
        inWordList.add("waltz(es)");
        inWordList.add("box(es)");
        inWordList.add("match(es)");
        inWordList.add("splash(es)");
        inWordList.add("yyy(es)");
        inWordList.add("graph(es)");
        // (ies)
        inWordList.add("pneumonectomy(ies)");
        inWordList.add("extremity(ies)");
        inWordList.add("fly(ies)");
        inWordList.add("fay(ies)");
        inWordList.add("i9y(ies)");
        inWordList.add("xUy(ies)");
        for(String inWord:inWordList)
        {
            System.out.println("- IsParentheticPlural(" + inWord + "): [" 
                + IsParentheticPlural(inWord) + "], GetOrgWord: [" 
                + GetOrgWord(inWord) + "]");
        }
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java ParentheticPluralTokenUtil");
            System.exit(0);
        }
        // test case and print out
        Test();
    }
    // data member
    // pattern: (s)
    private static final String patternSStr_ = "^[a-zA-Z]+\\(s\\)$";
    // not regalar plural forms
    private static final String patternS1Str_    // not (s)pattern
        = "^[a-zA-Z]+([sxz]|(ch)|(sh))\\(s\\)$";
    private static final String patternS2Str_    // not (s)pattern
        = "^[a-zA-Z]+[^aeiouAEIOU0-9]y\\(s\\)$";
    // pattern: s|x|z|ch|sh (es)
    private static final String patternEsStr_ 
        = "^[a-zA-Z]+([sxz]|(ch)|(sh))\\(es\\)$";
    // pattern: Cy(s)
    // pattern: Cy(ies)
    // C: b-d,f-h,j-n,p-t,v-z,not Vowels: a,e,i,o,u
    private static final String patternIesStr_ 
        = "^[a-zA-Z]+[^aeiouAEIOU0-9]y\\(ies\\)$";
    
    private static final Pattern patternS_ = Pattern.compile(patternSStr_);
    // not regular plural forms
    private static final Pattern patternS1_ = Pattern.compile(patternS1Str_);
    private static final Pattern patternS2_ = Pattern.compile(patternS2Str_);
    private static final Pattern patternEs_ = Pattern.compile(patternEsStr_);
    private static final Pattern patternIes_ = Pattern.compile(patternIesStr_);
}
