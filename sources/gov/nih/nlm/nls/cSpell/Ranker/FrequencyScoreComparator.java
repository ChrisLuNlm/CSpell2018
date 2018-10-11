package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is a comparator to compare FrequencyScores.
* Compare the frequency score from WC, see WordCountScore.java
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
public class FrequencyScoreComparator<T> implements Comparator<T>
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
        // 1. compare how many words
        // for now, we assume less word is better,
        // i.e. whatever is better than "what ever"
        int out = 0;
        String word1 = ((FrequencyScore) o1).GetWord();
        String word2 = ((FrequencyScore) o2).GetWord();
        int wordNo1 = TermUtil.GetWordNo(word1);
        int wordNo2 = TermUtil.GetWordNo(word2);
        if(wordNo1 != wordNo2)
        {
            out = wordNo1 - wordNo2;    // less wordNo has higher rank
        }
        else    // same word no
        {
            // 2. compare total score first
            double score1 = ((FrequencyScore) o1).GetScore();
            double score2 = ((FrequencyScore) o2).GetScore();
            if(score1 != score2)
            {
                // from high to low
                out = (int) (PRECISION_DIGIT*(score2-score1));
            }
            else // 3. alphabetic order of word
            {
                out = word2.compareTo(word1);
            }
        }
        return out;
    }
    // data member
    private static final int PRECISION_DIGIT = 100000000;
}
