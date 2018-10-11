package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This is a java class for orthographic utility to get 
* similarity and penalty for split.
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
public class OrthographicUtil
{
    // private constructor
    private OrthographicUtil()
    {
    }
    // the srcStr is the spelling error String
    // the tarStr is the suggestion String
    public static int GetSplitPenalty(String srcStr, String tarStr)
    {
        int splitCost = 1;
        return GetSplitPenalty(srcStr, tarStr, splitCost);
    }
    public static int GetSplitPenalty(String srcStr, String tarStr, 
        int splitCost)
    {
        int penalty = 0;
        // add penalty for split
        int srcSpaceNo = GetCharNo(srcStr, GlobalVars.SPACE_CHAR);
        int tarSpaceNo = GetCharNo(tarStr, GlobalVars.SPACE_CHAR);
        int splitNo = tarSpaceNo - srcSpaceNo;
        if(splitNo > 0)
        {
            penalty = splitNo * splitCost;
        }
        return penalty;
    }
    // norm score from 0.0 to 1.000
    public static double GetNormScore(int cost, double ceiling)
    {
        double score = 0.0;
        if(cost <= ceiling)
        {
            score = (ceiling - cost)/ceiling;
        }
        return score;
    }
    public static int GetCharNo(String inStr, char matchChar)
    {
        int charNo = 0;
        if(inStr != null)
        {
            for(int i = 0; i < inStr.length(); i++)
            {
                char curChar = inStr.charAt(i);
                if(curChar == matchChar)
                {
                    charNo++;
                }
            }
        }
        return charNo;
    }
    // private methods
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java OrthographicUtil");
            System.exit(0);
        }
        // test
    }
    // data member
}
