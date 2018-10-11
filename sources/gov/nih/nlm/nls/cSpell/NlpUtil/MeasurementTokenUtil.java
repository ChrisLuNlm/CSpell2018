package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
/*****************************************************************************
* This NLP utility class checks if a token is a measurement, unit, or 
* simplified format of measurement. A siimplified format of measurement is
* a digit plus a unit. Such as 25miles, 10inches, -0.5mm, 3weeks, 5mg, etc..
* The space between number (digit) and unit is omited.
*
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
*****************************************************************************/
public class MeasurementTokenUtil
{
    // private constructor
    private MeasurementTokenUtil()
    {
    }
    // public methods
    // includ eunit, simplified, and combined
    public static boolean IsMeasurements(String inWord, RootDictionary unitDic)
    {
        HashSet<String> unitSet = unitDic.GetDictionarySet();
        return IsMeasurements(inWord, unitSet);
    }
    public static boolean IsMeasurements(String inWord, HashSet<String> unitSet)
    {
        boolean mFlag = (IsUnits(inWord, unitSet)
            || IsCombinedMeasurements(inWord, unitSet)
            || IsSimplifiedMeasurement(inWord, unitSet));
        return mFlag;    
    }
    // check if it is an unit
    public static boolean IsUnits(String inWord, HashSet<String> unitSet)
    {
        boolean uFlag = IsUnit(inWord, unitSet) || IsCombinedUnit(inWord, unitSet);
        return uFlag;
    }
    public static boolean IsUnit(String inWord, HashSet<String> unitSet)
    {
        boolean uFlag = unitSet.contains(inWord);
        return uFlag;
    }
    // such as mg/day, mcg/ml
    public static boolean IsCombinedUnit(String inWord, HashSet<String> unitSet)
    {
        boolean uFlag = false;
        if(inWord.indexOf("/") > -1)
        {
            String[] orWords = inWord.split("/");
            boolean orFlag = true;
            for(String orWord:orWords)
            {
                if(IsUnit(orWord, unitSet) == false)
                {
                    orFlag = false;
                    break;
                }
            }
            uFlag = orFlag;
        }
        return uFlag;
    }
    // 30mg/50mg
    public static boolean IsCombinedMeasurements(String inWord, 
        HashSet<String> unitSet)
    {
        boolean mFlag = false;
        if(inWord.indexOf("/") > -1)
        {
            String[] orWords = inWord.split("/");
            boolean orFlag = true;
            for(String orWord:orWords)
            {
                if(IsSimplifiedMeasurement(orWord, unitSet) == false)
                {
                    orFlag = false;
                    break;
                }
            }
            mFlag = orFlag;
        }
        
        return mFlag;
    }
    // simplified measure matches the patten of [digit] + [unit] without space
    // and lowerCased
    public static boolean IsSimplifiedMeasurement(String inWord, 
        HashSet<String> unitSet) 
    {
        boolean flag = false;
        Matcher matcherM = patternM_.matcher(inWord);
        // 1. check if match measurement pattern
        //if(TokenUtil.IsMatch(inToken, patternM_) == true)
        if(matcherM.find() == true)
        {
            // split digit and unit
            String digit = matcherM.group(1);
            String unit = matcherM.group(2).toLowerCase();
            // check unit
            if(unitSet.contains(unit) == true)
            {
                flag = true;
            }
        }
        
        return flag;
    }
    
    // private methods
    private static void Tests(HashSet<String> unitSet)
    {
        // test case
        System.out.println("===== Unit Test of MeasurementTokenUtil =====");
        System.out.println("inWord|Unit|Simple-M|Comb-M|Measurements");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("0.5mg");
        inWordList.add("-0.25mm");
        inWordList.add("70kg");
        inWordList.add("500MG");
        inWordList.add("3days");
        inWordList.add("45Chris");
        inWordList.add("30mg/50kg");
        inWordList.add("cm3");
        inWordList.add("Gbit");
        inWordList.add("degreesC");
        inWordList.add("mg/day");
        inWordList.add("mg/ml");
        inWordList.add("mg/l");
        inWordList.add("mcg/ml");
    
        for(String inWord:inWordList)
        {
            Test(inWord, unitSet);
        }
        System.out.println("===== End of Unit Test =====");
    }
    private static void Test(String inWord, HashSet<String> unitSet)
    {
        System.out.println(inWord + "|" 
            + IsUnits(inWord, unitSet) + "|"
            + IsSimplifiedMeasurement(inWord, unitSet) + "|"
            + IsCombinedMeasurements(inWord, unitSet) + "|"
            + IsMeasurements(inWord, unitSet));
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java MeasurementTokenUtil");
            System.exit(0);
        }
        // inFile
        String unitFile =
        "/nfsvol/lex/Lu/Development/Spelling/cSpell2017/data/Dictionary/cSpell/unit.data";
        boolean lowerCaseFlag = false;    // lowerCase to all input
        HashSet<String> unitSet 
            = FileInToSet.GetHashSetByLine(unitFile, lowerCaseFlag);
        // test case and print out
        Tests(unitSet);
    }
    // data member
    // [digit] + [unit]
    private static final String patternStrM_ 
        = "^([+-]?\\d*\\.?\\d+)([a-zA-Z]+)$";
    private static final Pattern patternM_ = Pattern.compile(patternStrM_);    
}
