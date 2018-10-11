package gov.nih.nlm.nls.cSpell.Ranker;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class ranks and finds the best ranked candidates by OrthographicScore.
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
public class RankByOrthographic
{
    // private constructor
    private RankByOrthographic()
    {
    }
    // return candidate str list sorted by orthographic score, higher first
    public static ArrayList<String> GetCandidateStrList(String inStr, 
        HashSet<String> candidates, double wf1, double wf2, double wf3)
    {
        ArrayList<OrthographicScore> candScoreList
            = GetCandidateScoreList(inStr, candidates, wf1, wf2, wf3);
        ArrayList<String> candStrList = new ArrayList<String>();    
        for(OrthographicScore os:candScoreList)
        {
            candStrList.add(os.GetTarStr());
        }
        return candStrList;
    }
    // specify the top no for the candidate list
    public static ArrayList<String> GetTopCandidateStrList(String inStr, 
        HashSet<String> candidates, double wf1, double wf2, double wf3,
        int topNo)
    {
        ArrayList<OrthographicScore> candScoreList
            = GetCandidateScoreList(inStr, candidates, wf1, wf2, wf3);
        ArrayList<String> candStrList = new ArrayList<String>();    
        int curNo = 0;
        //double lastScore = 0.0d;
        for(OrthographicScore os:candScoreList)
        {
            if(curNo < topNo)
            //|| (os.GetScore() == lastScore))    // add if same score, > topNo
            {
                candStrList.add(os.GetTarStr());
                curNo++;
                //lastScore = os.GetScore();
            }
        }
        return candStrList;
    }
    // return candidate scoreObj list sorted by score, higher first
    public static ArrayList<OrthographicScore> GetCandidateScoreList(
        String inStr, HashSet<String> candidates, double wf1, double wf2,
        double wf3)
    {
        HashSet<OrthographicScore> candScoreSet 
            = GetCandidateScoreSet(inStr, candidates, wf1, wf2, wf3);
        ArrayList<OrthographicScore> candScoreList 
            = new ArrayList<OrthographicScore>(candScoreSet);
        OrthographicScoreComparator<OrthographicScore> osc 
            = new OrthographicScoreComparator<OrthographicScore>();
        Collections.sort(candScoreList, osc);    
        return candScoreList;
    }
    // return candidate set with orthographic score
    // inStr is the srcTxt used to calculate the score between it and cand
    public static HashSet<OrthographicScore> GetCandidateScoreSet(String inStr, 
        HashSet<String> candidates, double wf1, double wf2, double wf3)
    {
        HashSet<OrthographicScore> candScoreSet 
            = new HashSet<OrthographicScore>();
        for(String cand:candidates)
        {
            OrthographicScore os 
                = new OrthographicScore(inStr, cand, wf1, wf2, wf3); 
            candScoreSet.add(os);
        }
        return candScoreSet;
    }
    // return the best ranked str from candidates using orthographic score
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        double wf1, double wf2, double wf3)
    {
        String topRankStr = inStr;
        // get the sorted list
        ArrayList<OrthographicScore> candScoreList
            = GetCandidateScoreList(inStr, candidates, wf1, wf2, wf3);
        if(candScoreList.size() > 0)
        {
            topRankStr = candScoreList.get(0).GetTarStr();
        }
        return topRankStr;
    }
    // private methods
    // not verified test
    private static int RunTest(boolean detailFlag, long limitNo)
    {
        // init dic
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        double wf1 = cSpellApi.GetOrthoScoreEdDistFac();
        double wf2 = cSpellApi.GetOrthoScorePhoneticFac();
        double wf3 = cSpellApi.GetOrthoScoreOverlapFac();
        // provide cmdLine interface
        int returnValue = 0;
        OrthographicScoreComparator<OrthographicScore> osc 
            = new OrthographicScoreComparator<OrthographicScore>();
        try
        {
            BufferedReader stdInput 
                = new BufferedReader(new InputStreamReader(System.in));
            try
            {
                String inText = null;
                System.out.println("- Please input a text (type \"Ctl-d\" to quit) > ");
                while((inText = stdInput.readLine()) != null)
                {
                    // ---------------------------------
                    // Get spell correction on the input
                    // ---------------------------------
                    // get all possible candidates
                    HashSet<String> candSet 
                        = NonWord1To1Candidates.GetCandidates(inText, 
                            cSpellApi);
                    System.out.println("-- canSet.size(): " + candSet.size());
                    // get final suggestion
                    String topRankStr = GetTopRankStr(inText, candSet, wf1,
                        wf2, wf3);
                    System.out.println("- top tank str: " + topRankStr); 
                    // print details
                    if(detailFlag == true)
                    {
                        HashSet<OrthographicScore> candScoreSet 
                            = GetCandidateScoreSet(inText, candSet, wf1, 
                            wf2, wf3);
                        System.out.println("------ Suggestion List ------");    
                        candScoreSet.stream()
                            .sorted(osc)    // sort it 
                            .limit(limitNo)    // limit the number for print out
                            .map(obj -> obj.ToString())
                            .forEach(str -> System.out.println(str));
                    }
                }
            }
            catch (Exception e2)
            {
                System.err.println(e2.getMessage());
                returnValue = -1;
            }
        }
        catch (Exception e)
        { 
            System.err.println(e.getMessage());
            returnValue = -1;
        }
        return returnValue;
    }
    
    // test Driver
    public static void main(String[] args)
    {
        boolean detailFlag = false;
        long limitNo = 10;
        if(args.length == 2)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
            limitNo = Long.parseLong(args[1]);
        }
        else if(args.length == 1)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java RankByOrthographic <-d> <limitNo>");
            System.exit(0);
        }
        // test
        int returnValue = RunTest(detailFlag, limitNo);
        System.exit(returnValue);
    }
    // data member
}
