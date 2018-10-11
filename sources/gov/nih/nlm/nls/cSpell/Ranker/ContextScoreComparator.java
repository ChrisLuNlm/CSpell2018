package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is a comparator to compare ContextScores.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author NLM NLS Development Team
*
* @version    V-2018
****************************************************************************/
public class ContextScoreComparator<T> implements Comparator<T>
{
    /**
    * Compare two object o1 and o2.  Both objects o1 and o2 are 
    * FrequencyScore.  The compare algorithm: 
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
    
        // 1. compare total score first
        double score1 = ((ContextScore) o1).GetScore();
        double score2 = ((ContextScore) o2).GetScore();
        if(score1 != score2)
        {
            /** consider 0.0 is the worse than negative
            if(score1 == 0.0)
            {
                out = 1;
            }
            else if(score2 == 0.0)
            {
                out = -1;
            }
            else
            {
                // from high to low
                out = (int) (PRECISION_DIGIT*(score2-score1));
            }
            **/
            // from high to low
            out = (int) (PRECISION_DIGIT*(score2-score1));
        }
        else // 2. alphabetic order of word
        {
            String term1 = ((ContextScore) o1).GetTerm();
            String term2 = ((ContextScore) o2).GetTerm();
            out = term2.compareTo(term1);
        }
        return out;
    }
    // data member
    private static final int PRECISION_DIGIT = 100000000;
}
