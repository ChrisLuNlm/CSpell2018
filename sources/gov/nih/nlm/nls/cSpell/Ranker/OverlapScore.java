package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This class provides a java class to get the similarity score for overlap.
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
public class OverlapScore
{
    // private constructor
    private OverlapScore()
    {
    }
    public static double GetScore(String srcStr, String tarStr)
    {
        boolean caseFlag = false;
        return GetScore(srcStr, tarStr, caseFlag);
    }
    public static double GetScore(String srcStr, String tarStr, 
        boolean caseFlag)
    {
        double score = 1.0;
        // check case
        String src = srcStr;
        String tar = tarStr;
        if(caseFlag == false)    // not case sensitive
        {
            src = srcStr.toLowerCase();
            tar = tarStr.toLowerCase();
        }
        int srcLen = src.length();
        int tarLen = tar.length();
        // not the same String
        if(src.equals(tar) == false)
        {
            // get maxLength
            int maxLen = Math.max(srcLen, tarLen);
            // add split penalty
            maxLen += OrthographicUtil.GetSplitPenalty(src, tar);
            // cal leadOverlap
            int minLen = Math.min(srcLen, tarLen);
            int leadOverlap = 0;
            int ii = 0;
            while((ii < minLen)
            && (src.charAt(ii) == tar.charAt(ii)))
            {
                leadOverlap++;
                ii++;
            }
            // cal endOverlap
            int trailOverlap = 0;
            int jj = 0;
            while((jj < minLen)
            && (src.charAt(srcLen-1-jj) == tar.charAt(tarLen-1-jj)))
            {
                trailOverlap++;
                jj++;
            }
            // if match all charactrs to minLen
            // "123" and "123123" should be 0.55 not 1.0
            // spel should have higher score with spell than speil
            if(leadOverlap == minLen)
            {
                score = (1.0*leadOverlap + 0.1*trailOverlap)/(1.0*maxLen);
            }
            // spell should have higher score with sspell than nspell
            else if(trailOverlap == minLen)
            {
                score = (0.1*leadOverlap + 1.0*trailOverlap)/(1.0*maxLen);
            }
            else
            {
                score = (1.0*leadOverlap + 1.0*trailOverlap)/(1.0*maxLen);
            }
        }
        // make sure score is between 0.0 ~ 1.0
        score = ((score> 1.0)?1.0:score);
        return score;
    }
    // private methods
    private static void TestScores()
    {
        System.out.println("srcStr|tarStr|Overlap Score");
        /**
        TestScore("dianosed", "diagnosed");
        TestScore("diagnosed", "diagnosed");
        TestScore("abcdef", "123456");
        TestScore("abadef", "aba1def");
        TestScore("aaadef", "aaa123def");
        TestScore("bbbdef", "bbb123456def");
        TestScore("123456", "123 456");
        TestScore("123321", "12 33 21");
        TestScore("123 456", "123456");
        TestScore("lead", "leadends");    // leadOverLap - minLen
        TestScore("ends", "leadends");    // trailOverLap = minLen
        TestScore("123", "123123");
        TestScore("spel", "spell");    // should have higher score than below
        TestScore("spel", "speil");
        TestScore("spell", "sspell");    // should have higher score than below
        TestScore("spell", "nspell");
        TestScore("aaa", "aaaa");
        TestScore("baa", "abaa");
        TestScore("baa", "bbaa");
        TestScore("aaa", "aaa");
        TestScore("diction ary", "dictionary");
        TestScore("diction ary", "diction arry");
        **/
        TestScore("diagnost", "diagnosis");
        TestScore("diagnost", "diagnosed");
    }
    private static void TestScore(String srcStr, String tarStr)
    {
        double score = GetScore(srcStr, tarStr);
        System.out.println(srcStr + "|" + tarStr + "|" 
            + String.format("%1.4f", score));
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java OverlapScore");
            System.exit(0);
        }
        // test
        TestScores();
    }
    // data member
}
