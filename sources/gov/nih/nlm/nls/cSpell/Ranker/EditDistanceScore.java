package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
/*****************************************************************************
* This class calculates the similarity score for edit distance in orthographic
* score.
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
public class EditDistanceScore
{
    // private constructor
    private EditDistanceScore()
    {
    }
    // TBD: read the values from config file
    // get teh similarity score
    public static double GetScore(String srcStr, String tarStr)
    {
        /** Orignal value form Ensemlble
        int deleteCost = 95;    // = 0.095
        int insertCost = 95;    // = 0.095
        int replaceCost = 100;    // = 0.010 sub
        int swapCost = 90;        // TBD: should try 98 between delete and replace
        **/
        // new value for better perfroamcne
        int deleteCost = 96;    // = 0.095
        int insertCost = 90;    // = 0.090
        int replaceCost = 100;    // = 0.100
        int swapCost = 94;        // TBD: should try 98 between delete and replace
        int caseChangeCost = 10;
        boolean enhancedFlag = false;    // enhanced algorithm
        int splitCost = insertCost;
        return GetScore(srcStr, tarStr, deleteCost, insertCost, replaceCost,
            swapCost, caseChangeCost, enhancedFlag, splitCost);
    }
    public static double GetScore(String srcStr, String tarStr, int deleteCost,
        int insertCost, int replaceCost, int swapCost, int caseChangeCost,
        boolean enhancedFlag, int splitCost)
    {
        int cost = EditDistance.GetEditDistance(srcStr, tarStr, deleteCost,
            insertCost, replaceCost, swapCost, caseChangeCost, enhancedFlag);
        int penalty = OrthographicUtil.GetSplitPenalty(
            srcStr, tarStr, splitCost);
        double score = OrthographicUtil.GetNormScore(cost+penalty, 1000.0); 
        return score;
    }
    // private methods
    private static void TestEdSimScores()
    {
        TestEdSimScore("kitten", "sitting");    // insert
        TestEdSimScore("dianosed", "diagnosed");  // insert
        TestEdSimScore("dianosed", "deionized");  // d + s +...
        TestEdSimScore("Spell", "Spel");      // delete
        TestEdSimScore("Spell", "Speell");    // insert
        TestEdSimScore("Spell", "Spall");     // replace
        TestEdSimScore("Spell", "Sepll");     // swap
        TestEdSimScore("Spell", "Sp ell");    // insert
        TestEdSimScore("Spell", "spell");     // case
        TestEdSimScore("SPELL", "spell"); // case
        TestEdSimScore("SPELL", "spel");  // case + delete
        TestEdSimScore("SPELL", "speell");    // case + insert
        TestEdSimScore("SPELL", "spall"); // case + replace
        TestEdSimScore("SPELL", "SEPll"); // 5 case + trnaspose
        TestEdSimScore("SPELL", "sepll"); // 5 case + trnaspose
        TestEdSimScore("SPELL", "saall"); // 5 case + trnaspose
        TestEdSimScore("SPELL", "sEpll"); // 5 case + trnaspose
        TestEdSimScore("SPELL", "sEall"); // 5 case + trnaspose
        TestEdSimScore("SPELL", "sePll"); // 5 case + trnaspose
        TestEdSimScore("Spell", "sp ell");    // insert
    }
    private static void TestEdSimScore(String srcStr, String tarStr)
    {
        double score = GetScore(srcStr, tarStr);
        int cost = EditDistance.GetEditDistance(srcStr, tarStr, 
            95, 95, 100, 90, 10, false);
        System.out.println(srcStr + "|" + tarStr + "|" + cost + "|" + score);
    }
    // test Driver
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java EditDistanceScore");
            System.exit(0);
        }
        // test
        TestEdSimScores();
    }
    // data member
}
