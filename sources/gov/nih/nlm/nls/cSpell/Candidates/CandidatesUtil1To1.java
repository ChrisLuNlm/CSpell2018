package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This utility class generates 1To1 candidates from an input string.
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
public class CandidatesUtil1To1
{
    // private constructor
    private CandidatesUtil1To1()
    {
    }
    // protected method
    // get candidates of edit distance 1 & 2
    // all candidates are operated in lowerCase 
    // inWord should be lowerCase
    // maxLength: do not proceed if length if too big to save time
    protected static HashSet<String> GetCandidatesByEd(String inWord, 
        int maxLength)
    {
        HashSet<String> candidates = new HashSet<String>();
        if(inWord.length() <= maxLength)
        {
            candidates = GetCandidatesByEd1(inWord);
            HashSet<String> candList2 = GetCandidatesByEd2(candidates);
            candidates.addAll(candList2);
            // remove the inWordLc
            candidates.remove(inWord.toLowerCase());
        }
        return candidates;
      }
    // private methods
    // the inWord must be lowerCase()
    // return all possible candidate within edit distance of 1
    private static HashSet<String> GetCandidatesByEd2(
        HashSet<String> candSet1)
    {
        // get all candidates with edit distance 2
        HashSet<String> candSet2 = new HashSet<String>();
        for(String cand1:candSet1)
        {
            HashSet<String> candSet12 = GetCandidatesByEd1(cand1);
            candSet2.addAll(candSet12);
        }
        // remvoe the original term.LC
        return candSet2;
    }
    // lowercase only
    // inWord must be lowercase
    // Done: change the return to HashSet might increase the performance speed?
    protected static HashSet<String> GetCandidatesByEd1(String inWord)
    {
        HashSet<String> candSet = new HashSet<String>();
        String inWordLc = inWord.toLowerCase();
        // Delete
        for(int i = 0; i < inWordLc.length(); i++)
        {
            String deleteWord = inWordLc.substring(0, i) 
                + inWordLc.substring(i+1);
            candSet.add(deleteWord);
        }
        // Insert
        for(int i = 0; i <= inWordLc.length(); i++)
        {
            // Insert: a - z
            for(char c = 'a'; c <= 'z'; c++)
            {
                String insertWord = inWordLc.substring(0, i) + String.valueOf(c)
                    + inWordLc.substring(i);
                candSet.add(insertWord);
            }
            // Insert space
            /*
            String insertWord = inWordLc.substring(0, i) + GlobalVars.SPACE_STR
                + inWordLc.substring(i);
            candSet.add(insertWord);
            */
        }
        // replace: include the origianl inWord
        for(int i = 0; i < inWordLc.length(); i++)
        {
            for(char c='a'; c <= 'z'; c++)
            {
                String alterWord = inWordLc.substring(0, i) + String.valueOf(c) 
                    + inWordLc.substring(i+1);
                candSet.add(alterWord);
            }
        }
        // transpose, ed: 1, 1.5,or 2.0?
        for(int i = 0; i < inWordLc.length()-1; i++)
        {
            String transWord = inWordLc.substring(0, i) 
                + inWordLc.substring(i+1, i+2)
                + inWordLc.substring(i, i+1) + inWordLc.substring(i+2);
            candSet.add(transWord);
        }
        // remvoe the original term.LC
        candSet.remove(inWordLc);
        return candSet;
      }
    // private
    private static void Test(String inStr)
    {
        // candidates with ED: 1
        HashSet<String> candSet1 = GetCandidatesByEd1(inStr);
        // print out
        System.out.println("-- inStr: [" + inStr + "]");
        System.out.println("-- candSet1.size(): " + candSet1.size()); 
        boolean caseFlag = false;    // not case sensitive
        int dupNo = 0;
        // check if the candList correct
        for(String cand1:candSet1)
        {
            int ed = EditDistance.GetEditDistance(inStr, cand1, caseFlag);
            // these are errors, should not have any
            if((ed != 1) && (inStr.equals(cand1) == false))
            {
                System.out.println(inStr + "|" + cand1 + "|" + ed);
            }
            // candidate are  same as inStr, produced by replace, not 0
            if(inStr.toLowerCase().equals(cand1) == true)    
            {
                dupNo++;
            }
        }
        System.out.println("-- dupNo: " + dupNo);
        //System.out.println(candSet1);
        // candidates with ED: 2
        /**
        HashSet<String> candSet2 = GetCandidatesByEd2(candList1);
        int exceedNo = 0;
        for(String cand2:candSet2)
        {
            int ed = EditDistance.GetEditDistance(inStr, cand2, caseFlag);
            if((ed > 2) && (inStr.equals(cand2) == false))
            {
                System.out.println(inStr + "|" + cand2 + "|" + ed);
                exceedNo++;
            }
        }
        System.out.println("-- candSet2.size(): " + candSet2.size()); 
        System.out.println("-- exceedNo: " + exceedNo); 
        **/
    }
    // test driver
    public static void main(String[] args) 
    {
        String inStr = "Regards";
        if(args.length == 1)
        {
            inStr = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("*** Usage: java OneToOneCandidatesUtil <inStr>");
            System.exit(1);
        }
        Test(inStr);
    }
}
