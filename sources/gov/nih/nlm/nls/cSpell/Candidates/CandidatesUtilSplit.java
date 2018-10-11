package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import java.lang.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This utility class generates split candidates.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class CandidatesUtilSplit
{
    // private constructor
    private CandidatesUtilSplit()
    {
    }
    // protected method
    // get candidates 
    // Do not split hyphen
    protected static HashSet<String> GetSplitSet(String inWord, 
        int maxSplitNo)
    {
        boolean hyphenSplitFlag = false;
        return GetSplitSet(inWord, maxSplitNo, hyphenSplitFlag);
    }
    // - split by space, 1 or 2 splits
    // - split by replacing hyphens to spaces
    protected static HashSet<String> GetSplitSet(String inWord, 
        int maxSplitNo, boolean hyphenSplitFlag)
    {
        // get split by space
        HashSet<String> splitSet = GetSplitSetBySpaces(inWord, 
            maxSplitNo);
        // TBD: get split string by replacing hyphen by space
        // This feature is disable because the R+, P-, F-
        // Need look at the data to see hwo to improve!
        
        if(hyphenSplitFlag == true)
        {
            String splitStrByHyphen = GetSplitByPunc(inWord, '-');
            if(splitStrByHyphen.equals(inWord) == false)
            {
                splitSet.add(splitStrByHyphen);
            }
        }
        return splitSet;
      }
    // get possible split set by replacing hyphen with space
    protected static String GetSplitByPunc(String inWord, char puncChar)
    {
        char[] temp = inWord.toCharArray();
        for(int i = 0; i < temp.length; i++)
        {
            if(temp[i] == puncChar)
            {
                temp[i] = ' ';
            }
        }
        String splitStr = TermUtil.Trim(new String(temp));
        return splitStr;
    }
    // private methods
    // the inWord should be lowerCase()
    // return all possible split combo within edit distance of N
    // maxSplitNo must >= 1 to get come result
    protected static HashSet<String> GetSplitSetBySpaces(String inWord,
        int maxSplitNo)
    {
        HashSet<String> splitSet = new HashSet<String>();
        // check iuputs
        if((inWord == null) || (inWord.length() == 0)
        || (maxSplitNo < 1))
        {
            return splitSet;
        }
        HashSet<String> curSplitSet = GetSplitSetBy1Space(inWord);
        splitSet.addAll(curSplitSet);
        int spaceNo = 1;
        // recursively for more than 1 split by space
        while(spaceNo < maxSplitNo)
        {
            HashSet<String> nextSplitSet = new HashSet<String>();
            // generate next level of split based on current split
            for(String curSplit:curSplitSet)
            {
                HashSet<String> tempSplitSet = GetSplitSetBy1Space(curSplit);
                nextSplitSet.addAll(tempSplitSet);
            }
            // updates
            curSplitSet = new HashSet<String>(nextSplitSet);
            splitSet.addAll(nextSplitSet);
            spaceNo++;
        }
        return splitSet;
    }
    // get all possible split combination by 1 space
    // lowercase only
    // not include duplicates
    // This is the core split process by space
    protected static HashSet<String> GetSplitSetBy1Space(String inWord)
    {
        HashSet<String> splitSet = new HashSet<String>();
        String word = inWord.toLowerCase();
        // Insert space inside the word, not on either ends
        for(int i = 1; i < word.length(); i++)
        {
            // Insert space for split
            String insertWord = word.substring(0, i) + GlobalVars.SPACE_STR
                + word.substring(i);
            // remove multiple spaces    
            // needed when inserting a space to a space
            // Use this to convert "a  b" to "a b"
            splitSet.add(TermUtil.StringTrim(insertWord));
        }
        return splitSet;
      }
    // test all split 
    private static void TestSplitUtil(String inStr)
    {
        System.out.println("====== test split util (no Dic check) ======"); 
        System.out.println("----- inStr: [" + inStr + "]");
        // Test possible split combination with 1 split
        HashSet<String> splitSet1 = GetSplitSetBy1Space(inStr);
        boolean caseFlag = false;    // not case sensitive
        int dupNo = 0;
        // print out
        System.out.println("----- Check on candList1 -----"); 
        System.out.println("-- splitSet1.size(): " + splitSet1.size()); 
        System.out.println(splitSet1);
        // check if the candList1 correct by edit distance
        for(String split1:splitSet1)
        {
            int ed = EditDistance.GetEditDistance(inStr, split1, caseFlag);
            // these are errors: because 1 split should have ed = 1 
            if((ed != 1) && (inStr.equals(split1) == false))
            {
                System.out.println("**ERR: " + inStr + "|" + split1 + "|" + ed);
            }
            // candidate are same as inStr, produced by replace, not 0
            // check duplicate no.
            else if(inStr.equals(split1) == true)    
            {
                dupNo++;
            }
        }
        System.out.println("-- dupNo: " + dupNo);
        HashSet<String> candSet1a = GetSplitSetBySpaces(inStr, 1);
        System.out.println("-- candSet1a.size(): " + candSet1a.size()); 
        System.out.println(candSet1a);
        HashSet<String> candSet2a = GetSplitSetBySpaces(inStr, 2);
        System.out.println("-- candList2a.size(): " + candSet2a.size()); 
        System.out.println(candSet2a);
        // other test 5 for spliting candidates by hyphen
        String testStr = "-123--45-test-123-45.6-";
        System.out.println("- testStr: " + testStr);
        System.out.println("- splitByHyphen: ["
            + GetSplitByPunc(testStr, '-') + "]");
    }
    // test driver
    public static void main(String[] args) 
    {
        // example: knowabout, viseversa, hotflashes, testsplit,
        // Amlodipine5mgs
        String inStr = "Amlodipine5mgs";
        int maxSplitNo = 2;
        if(args.length == 1)
        {
            inStr = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("*** Usage: java SplitCandidatesUtil <inStr>");
            System.exit(1);
        }
        // 1. test
        TestSplitUtil(inStr);
    }
}
