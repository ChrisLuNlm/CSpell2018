package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is a comparator to compare NoisyChannelScores.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author NLM NLS Development Team
*
* @see       FrequencyScore
*
* @version    V-2018
****************************************************************************/
public class NoisyChannelScoreComparator<T> implements Comparator<T>
{
    /**
    * Compare two object o1 and o2.  Both objects o1 and o2 are 
    * NoisyChannelScore.  The compare algorithm: 
    *
    * @param  o1  first object to be compared
    * @param  o2  second object to be compared
    *
    * @return  a negative integer, 0, or positive integer to represent the
    *          object o1 is less, equals, or greater than object 02.
    */
    public int compare(T o1, T o2)
    {
        // 1. compare how many words for the candidates
        // for now, we assume less word is better,
        // i.e. whatever is better than "what ever"
        int out = 0;
        String cand1 = ((NoisyChannelScore) o1).GetCandStr();
        String cand2 = ((NoisyChannelScore) o2).GetCandStr();
        int wordNo1 = TermUtil.GetWordNo(cand1);
        int wordNo2 = TermUtil.GetWordNo(cand2);
        if(wordNo1 != wordNo2)
        {
            out = wordNo1 - wordNo2;    // less wordNo has higher rank
        }
        else
        {
            // 2. compare noisy Channel score 
            double score1 = ((NoisyChannelScore) o1).GetScore();
            double score2 = ((NoisyChannelScore) o2).GetScore();
            if(score1 != score2)
            {
                out = (int) (PRECISION_DIGIT*(score2-score1));
            }
            else
            {
                // 3. compare by orthographic score
                OrthographicScore oScore1 = ((NoisyChannelScore) o1).GetOScore();
                OrthographicScore oScore2 = ((NoisyChannelScore) o2).GetOScore();
                if(oScore1.GetScore() != oScore2.GetScore())
                {
                    OrthographicScoreComparator<OrthographicScore> osc 
                        = new OrthographicScoreComparator<OrthographicScore>();
                    out = osc.compare(oScore1, oScore2);
                }
                else // 4. hannelScore
                {
                    FrequencyScore fScore1 
                        = ((NoisyChannelScore) o1).GetFScore();
                    FrequencyScore fScore2 
                        = ((NoisyChannelScore) o2).GetFScore();
                    if(fScore1.GetScore() != fScore2.GetScore())
                    {
                        FrequencyScoreComparator<FrequencyScore> fsc
                            = new FrequencyScoreComparator<FrequencyScore>();
                        out = fsc.compare(fScore1, fScore2);
                    }
                    else    // 4. alphabetic order
                    {
                        out = cand2.compareTo(cand1);
                    }
                }
            }
        }
        return out;
    }
    // data member
    private static final int PRECISION_DIGIT = 100000000;
}
