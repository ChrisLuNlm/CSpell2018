package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class provides a java object of noisy channel score.
* The language model is the frequency WC score, P(w).
* The error model is the orthographics score, P(x/w).
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
public class NoisyChannelScore
{
    // private constructor
    public NoisyChannelScore(String wordStr, String candStr, 
        WordWcMap wordWcMap, double wf1, double wf2, double wf3)
    {
        wordStr_ = wordStr;
        candStr_ = candStr;
        // calculate score
        oScore_ = new OrthographicScore(wordStr_, candStr_, wf1, wf2, wf3);
        fScore_ = new FrequencyScore(candStr_, wordWcMap);
        score_ = oScore_.GetScore() * fScore_.GetScore();
    }
    // public method
    public double GetScore()
    {
        return score_;
    }
    public String GetCandStr()
    {
        return candStr_;
    }
    public OrthographicScore GetOScore()
    {
        return oScore_;
    }
    public FrequencyScore GetFScore()
    {
        return fScore_;
    }
    public String ToString()
    {
        String outStr = ToString(GlobalVars.FS_STR);
        return outStr;    
    }
    public String ToString(String fieldSepStr)
    {
        String outStr = String.format("%1.8f", score_) + fieldSepStr
            + oScore_.ToString(fieldSepStr) + fieldSepStr
            + fScore_.ToString(fieldSepStr);
        return outStr;    
    }
    // single word score, multiwords is 0.0
    private static void Test(String wordStr, String candStr, 
        WordWcMap wordWcMap)
    {
        double wf1 = 1.00;
        double wf2 = 0.70;
        double wf3 = 0.80;
        NoisyChannelScore ncs = new NoisyChannelScore(wordStr, candStr, 
            wordWcMap, wf1, wf2, wf3);
        System.out.println(ncs.ToString());
    }
    private static void Tests(WordWcMap wordWcMap)
    {
        ArrayList<String> testStrList = new ArrayList<String>();
        Test("spel", "spell", wordWcMap);
        Test("spel", "speil", wordWcMap);
        Test("spelld", "spell", wordWcMap);
        Test("spelld", "spelled", wordWcMap);
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java NosiyChannelScore");
            System.exit(0);
        }
        // test
        String inFile = "../data/Frequency/wcWord.data";
        boolean verboseFlag = true;
        WordWcMap wordWcMap = new WordWcMap(inFile, verboseFlag);
        Tests(wordWcMap);
    }
    // data member
    private String wordStr_ = new String();
    private String candStr_ = new String();
    private double score_ = 0.0;
    private OrthographicScore oScore_ = null;
    private FrequencyScore fScore_ = null;
}
