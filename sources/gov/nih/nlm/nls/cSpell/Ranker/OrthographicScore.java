package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This class provides a java object of orthographic score. It include scores
* of edit distance, phonetic and overlap.
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
public class OrthographicScore
{
    // public constructor
    /*
    public OrthographicScore(String srcStr, String tarStr)
    {
        double wf1 = 1.00;
        double wf2 = 0.70;
        double wf3 = 0.80;

        Init(srcStr, tarStr, wf1, wf2, wf3);
    }
    */
    // wf1, wf2, wf3 are the weighting factors for ed-Dist, phonetic, 
    // and overlaa, respectively
    // The best emperical value is 1.00, 0.70, and 0.80, set in config file
    public OrthographicScore(String srcStr, String tarStr, double wf1,
        double wf2, double wf3)
    {
        Init(srcStr, tarStr, wf1, wf2, wf3);
    }

    public String GetTarStr()
    {
        return tarStr_;
    }
    public double GetEdScore()
    {
        return edScore_;
    }
    public double GetPhoneticScore()
    {
        return phoneticScore_;
    }
    public double GetOverlapScore()
    {
        return overlapScore_;
    }
    public double GetScore()
    {
        return score_;
    }
    // Get Edit-Distance and Phonetic scores only
    public double GetEpScore()
    {
        return epScore_;
    }
    public String ToString()
    {
        String outStr = ToString(GlobalVars.FS_STR);
        return outStr;    
    }
    public String ToString(String fieldSepStr)
    {
        String outStr = srcStr_ + fieldSepStr + tarStr_ 
            + fieldSepStr + String.format("%1.8f", score_) 
            + fieldSepStr + String.format("%1.8f", edScore_) 
            + fieldSepStr + String.format("%1.8f", phoneticScore_)
            + fieldSepStr + String.format("%1.8f", overlapScore_);
        return outStr;    
    }
    // private method
    private void Init(String srcStr, String tarStr, double wf1, double wf2, 
        double wf3)
    {
        srcStr_ = srcStr;
        tarStr_ = tarStr;
        // calculate score
        edScore_ = EditDistanceScore.GetScore(srcStr, tarStr);
        phoneticScore_ = PhoneticScore.GetScore(srcStr, tarStr);
        overlapScore_ = OverlapScore.GetScore(srcStr, tarStr);

        // init value is 1.0, 1.0, 1.0 in Ensemble 
        //score_ = edScore_ + phoneticScore_ + overlapScore_;
        // use new best value: 1.0, 0.7, 0.8
        score_ = wf1*edScore_ + wf2*phoneticScore_ + wf3*overlapScore_;
        epScore_ = edScore_ + phoneticScore_;
    }
    private static void Test(String srcStr, String tarStr)
    {
        double wf1 = 1.00;
        double wf2 = 0.70;
        double wf3 = 0.80;
        OrthographicScore os 
            = new OrthographicScore(srcStr, tarStr, wf1, wf2, wf3);
        System.out.println(os.ToString());        
    }
    private static void Tests()
    {
        Test("spel", "spell");
        Test("spel", "speil");
        Test("spelld", "spell");
        Test("spelld", "spelled");
        // for merge
        Test("dicti onary", "dict unary");
        Test("dicti onary", "dictionary");
        Test("diction ary", "diction arry");
        Test("diction ary", "dictionary");
        // for real word
        Test("then", "than");
        Test("bowl", "bowel");
        Test("effect", "affect");
        Test("weather", "whether");
        Test("small", "smell");
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java OrthographicScore");
            System.exit(0);
        }
        // test
        Tests();
    }
    // data member
    // this is error model, the error is between srcStr and tarStr
    // use srcStr & tarStr to compare edDist, metaphone, and overlap
    private String srcStr_ = new String();
    private String tarStr_ = new String();
    private double edScore_ = 0.0;
    private double phoneticScore_ = 0.0;
    private double overlapScore_ = 0.0;
    private double score_ = 0.0;    // sum of ed, phonetic, overlap_
    private double epScore_ = 0.0;    // sum of edScore_  and phoneticScore_
}
