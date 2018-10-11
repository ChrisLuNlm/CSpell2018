package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class provides a java object of CSpell score. It includes scores of
* orthographic, frequency, noisy channel and context.
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
public class CSpellScore
{
    // private constructor
    public CSpellScore(String wordStr, String candStr, WordWcMap wordWcMap,
        DoubleVec contextVec, Word2Vec word2Vec, double wf1, double wf2,
        double wf3)
    {
        wordStr_ = wordStr;
        candStr_ = candStr;
        // calculate score
        oScore_ = new OrthographicScore(wordStr_, candStr_, wf1, wf2, wf3);
        fScore_ = new FrequencyScore(candStr_, wordWcMap);
        nScore_ = new NoisyChannelScore(wordStr_, candStr_, wordWcMap, 
            wf1, wf2, wf3);
        cScore_ = new ContextScore(candStr_, contextVec, word2Vec);
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
    public NoisyChannelScore GetNScore()
    {
        return nScore_;
    }
    public ContextScore GetCScore()
    {
        return cScore_;
    }
    public static String GetScoreHeader()
    {
        String outStr = "NC_Score|wordStr|candStr|O_core|EditDis|Phonetic|overlap|candStr|F_Score|canStr|C_Score";
        return outStr;
    }
    // noisy channel + context
    // where noisyChannel = orthographic score + frequency
    public String ToString()
    {
        String outStr = ToString(GlobalVars.FS_STR);
        return outStr;    
    }
    public String ToString(String fieldSepStr)
    {
        String outStr = nScore_.ToString(fieldSepStr) + fieldSepStr
            + cScore_.ToString(fieldSepStr);
        return outStr;    
    }
    public static double GetMaxFScore(ArrayList<CSpellScore> cSpellScoreList)
    {
        double maxFScore = cSpellScoreList.stream()
            .map(c -> c.GetFScore().GetScore())
            .max(Comparator.comparingDouble(c -> new Double(c))).get();
        return maxFScore;    
    }
    // Edit distance score
    public static double GetMaxEScore(ArrayList<CSpellScore> cSpellScoreList)
    {
        double maxEScore = cSpellScoreList.stream()
            .map(c -> c.GetOScore().GetEdScore())
            .max(Comparator.comparingDouble(c -> new Double(c))).get();
        return maxEScore;    
    }
    // phonetic
    public static double GetMaxPScore(ArrayList<CSpellScore> cSpellScoreList)
    {
        double maxPScore = cSpellScoreList.stream()
            .map(c -> c.GetOScore().GetPhoneticScore())
            .max(Comparator.comparingDouble(c -> new Double(c))).get();
        return maxPScore;    
    }
    // overlap
    public static double GetMaxOScore(ArrayList<CSpellScore> cSpellScoreList)
    {
        double maxOScore = cSpellScoreList.stream()
            .map(c -> c.GetOScore().GetOverlapScore())
            .max(Comparator.comparingDouble(c -> new Double(c))).get();
        return maxOScore;    
    }
    // single word score, multiwords is 0.0
    // not completed with contextScore
    private static void Test(String wordStr, String candStr, 
        WordWcMap wordWcMap)
    {
        //CSpellScore cs = new CSpellScore(wordStr, candStr, wordWcMap);
        //System.out.println(cs.ToString());
    }
    // not completed with contextScore
    private static void Tests(WordWcMap wordWcMap, Word2Vec w2vOm)
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
            System.out.println("Usage: java CSpellScore");
            System.exit(0);
        }
        // test
        String inFile = "../data/Frequency/wcWord.data";
        boolean verboseFlag = true;
        WordWcMap wordWcMap = new WordWcMap(inFile, verboseFlag);
        String inOmFile = "../data/Context/syn1n.data";
        Word2Vec w2vOm = new Word2Vec(inOmFile, verboseFlag);
        Tests(wordWcMap, w2vOm);
    }
    // data member
    private String wordStr_ = new String();
    private String candStr_ = new String();
    private double score_ = 0.0;    // not used, reserved for future usage
    private OrthographicScore oScore_ = null;
    private FrequencyScore fScore_ = null;
    private NoisyChannelScore nScore_ = null;
    private ContextScore cScore_ = null;
}
