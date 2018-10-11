package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is a comparator to compare CSpellScores for real-word 1To1.
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
public class CSpellScoreRw1To1Comparator<T> implements Comparator<T>
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
        OrthographicScoreComparator<OrthographicScore> osc 
            = new OrthographicScoreComparator<OrthographicScore>();
        OrthographicScore oScore1 = ((CSpellScore) o1).GetOScore();
        OrthographicScore oScore2 = ((CSpellScore) o2).GetOScore();
        int out = osc.compare(oScore1, oScore2);
        // the following code are simplfied by above
        // We still keep this java file in case we need better implementation
        // in the future
        /**
        int out = 0;
        // 1. compared by orthographic score, best
        OrthographicScore oScore1 = ((CSpellScore) o1).GetOScore();
        OrthographicScore oScore2 = ((CSpellScore) o2).GetOScore();
        if(oScore1.GetScore() != oScore2.GetScore())
        {
            OrthographicScoreComparator<OrthographicScore> osc 
                = new OrthographicScoreComparator<OrthographicScore>();
            out = osc.compare(oScore1, oScore2);
        }
        else
        {
            String cand1 = ((CSpellScore) o1).GetCandStr();
            String cand2 = ((CSpellScore) o2).GetCandStr();
            out = cand2.compareTo(cand1);
        }
        **/
        return out;
    }
    // data member
}
