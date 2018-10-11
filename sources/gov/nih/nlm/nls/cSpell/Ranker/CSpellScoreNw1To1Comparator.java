package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is a comparator to compare CSpellScores for Non-word 1To1.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author NLM NLS Development Team
*
* @see       CSpellScore
*
* @version    V-2018
****************************************************************************/
public class CSpellScoreNw1To1Comparator<T> implements Comparator<T>
{
    /**
    * Compare two object o1 and o2.  Both objects o1 and o2 are 
    * CSpellScore.  The compare algorithm: 
    *
    * @param  o1  first object to be compared
    * @param  o2  second object to be compared
    *
    * @return  a negative integer, 0, or positive integer to represent the
    *          object o1 is less, equals, or greater than object 02.
    */
    public int compare(T o1, T o2)
    {
        int out = 0;
        if(compareMode_ == COMPARE_BY_COMBO)
        {
            out = compareByCombo(o1, o2);
        }
        else if(compareMode_ == COMPARE_BY_ORTHOGRAPHICS)
        {
            out = compareByOrthographics(o1, o2);
        }
        else if(compareMode_ == COMPARE_BY_FREQUENCY)
        {
            out = compareByFrequency(o1, o2);
        }
        else if(compareMode_ == COMPARE_BY_CONTEXT)
        {
            out = compareByContext(o1, o2);
        }
        else if (compareMode_ == COMPARE_BY_ENSEMBLE)
        {
            out = compareByEnsemble(o1, o2);
        }
        return out;
    }
    public void SetCompareMode(int compareMode)
    {
        compareMode_ = compareMode;
    }
    private int compareByContext(T o1, T o2)
    {
        int out = 0;
        ContextScore cScore1 = ((CSpellScore) o1).GetCScore();
        ContextScore cScore2 = ((CSpellScore) o2).GetCScore();
        // 1. compared by context score, 4 last
        if(cScore1.GetScore() != cScore2.GetScore())
        {
            ContextScoreComparator<ContextScore> csc 
                = new ContextScoreComparator<ContextScore>();
            out = csc.compare(cScore1, cScore2);
        }
        // 2. alphabetic order
        else    
        {
            String cand1 = ((CSpellScore) o1).GetCandStr();
            String cand2 = ((CSpellScore) o2).GetCandStr();
            out = cand2.compareTo(cand1);
        }
        return out;
    }
    private int compareByFrequency(T o1, T o2)
    {
        int out = 0;
        FrequencyScore fScore1 = ((CSpellScore) o1).GetFScore();
        FrequencyScore fScore2 = ((CSpellScore) o2).GetFScore();
        // 1. compared by context score, 4 last
        if(fScore1.GetScore() != fScore2.GetScore())
        {
            FrequencyScoreComparator<FrequencyScore> fsc
                = new FrequencyScoreComparator<FrequencyScore>();
            out = fsc.compare(fScore1, fScore2);
        }
        // 2. alphabetic order
        else    
        {
            String cand1 = ((CSpellScore) o1).GetCandStr();
            String cand2 = ((CSpellScore) o2).GetCandStr();
            out = cand2.compareTo(cand1);
        }
        return out;
    }
    private int compareByEnsemble(T o1, T o2)
    {
        int out = 0;
        OrthographicScore oScore1 = ((CSpellScore) o1).GetOScore();
        OrthographicScore oScore2 = ((CSpellScore) o2).GetOScore();
        FrequencyScore fScore1 = ((CSpellScore) o1).GetFScore();
        FrequencyScore fScore2 = ((CSpellScore) o2).GetFScore();
        ContextScore cScore1 = ((CSpellScore) o1).GetCScore();
        ContextScore cScore2 = ((CSpellScore) o2).GetCScore();
        double score1 = 0.6*oScore1.GetScore() + 0.25*fScore1.GetScore()
            + 0.15*cScore1.GetScore();
        double score2 = 0.6*oScore2.GetScore() + 0.25*fScore2.GetScore()
            + 0.15*cScore2.GetScore();
        // 1. compared by orthographic score, best
        if(score1 != score2)
        {
            out = (int) (PRECISION_DIGIT*(score2-score1));
        }
        // 2. alphabetic order
        else    
        {
            String cand1 = ((CSpellScore) o1).GetCandStr();
            String cand2 = ((CSpellScore) o2).GetCandStr();
            out = cand2.compareTo(cand1);
        }
        return out;
    }
    private int compareByOrthographics(T o1, T o2)
    {
        int out = 0;
        OrthographicScore oScore1 = ((CSpellScore) o1).GetOScore();
        OrthographicScore oScore2 = ((CSpellScore) o2).GetOScore();
        // 1. compared by orthographic score, best
        if(oScore1.GetScore() != oScore2.GetScore())
        {
            OrthographicScoreComparator<OrthographicScore> osc 
                = new OrthographicScoreComparator<OrthographicScore>();
            out = osc.compare(oScore1, oScore2);
        }
        // 2. alphabetic order
        else    
        {
            String cand1 = ((CSpellScore) o1).GetCandStr();
            String cand2 = ((CSpellScore) o2).GetCandStr();
            out = cand2.compareTo(cand1);
        }
        return out;
    }
    // by combination, O, N, F, C
    private int compareByCombo(T o1, T o2)
    {
        int out = 0;
        OrthographicScore oScore1 = ((CSpellScore) o1).GetOScore();
        OrthographicScore oScore2 = ((CSpellScore) o2).GetOScore();
        NoisyChannelScore nScore1 = ((CSpellScore) o1).GetNScore();
        NoisyChannelScore nScore2 = ((CSpellScore) o2).GetNScore();
        FrequencyScore fScore1 = ((CSpellScore) o1).GetFScore();
        FrequencyScore fScore2 = ((CSpellScore) o2).GetFScore();
        ContextScore cScore1 = ((CSpellScore) o1).GetCScore();
        ContextScore cScore2 = ((CSpellScore) o2).GetCScore();
        // 1. compared by orthographic score, best
        if(oScore1.GetScore() != oScore2.GetScore())
        {
            OrthographicScoreComparator<OrthographicScore> osc 
                = new OrthographicScoreComparator<OrthographicScore>();
            out = osc.compare(oScore1, oScore2);
        }
        // 2. compared by noise channel score, 2nd best
        else if(nScore1.GetScore() != nScore2.GetScore())
        {
            NoisyChannelScoreComparator<NoisyChannelScore> nsc
                = new NoisyChannelScoreComparator<NoisyChannelScore>();
            out = nsc.compare(nScore1, nScore2);
        }
        // 3. compared by pure frequency score, 3rd best
        else if(fScore1.GetScore() != fScore2.GetScore())
        {
            FrequencyScoreComparator<FrequencyScore> fsc
                = new FrequencyScoreComparator<FrequencyScore>();
            out = fsc.compare(fScore1, fScore2);
        }
        // 4. compared by context score, 4 last
        else if(cScore1.GetScore() != cScore2.GetScore())
        {
            ContextScoreComparator<ContextScore> csc 
                = new ContextScoreComparator<ContextScore>();
            out = csc.compare(cScore1, cScore2);
        }
        // 5. alphabetic order
        else    
        {
            String cand1 = ((CSpellScore) o1).GetCandStr();
            String cand2 = ((CSpellScore) o2).GetCandStr();
            out = cand2.compareTo(cand1);
        }
        return out;
    }
    // data member
    public static final int COMPARE_BY_COMBO = 0;
    public static final int COMPARE_BY_ORTHOGRAPHICS = 1;
    public static final int COMPARE_BY_FREQUENCY = 2;
    public static final int COMPARE_BY_CONTEXT = 3;
    public static final int COMPARE_BY_ENSEMBLE = 4;
    private int compareMode_ = COMPARE_BY_COMBO;
    private static final int PRECISION_DIGIT = 100000000;
}
