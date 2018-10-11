package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
/*****************************************************************************
* This class is a comparator to compare OrthographicScore records.
* Compare the total score, then edit distance, then phonetic, then overlap.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author NLM NLS Development Team
*
* @see        OrthographicScore
*
* @version    V-2018
****************************************************************************/
public class OrthographicScoreComparator<T> implements Comparator<T>
{
    /**
    * Compare two object o1 and o2.  Both objects o1 and o2 are 
    * OrthographicScore.  The compare algorithm: 
    *
    * @param  o1  first object to be compared
    * @param  o2  second object to be compared
    *
    * @return  a negative integer, 0, or positive integer to represent the
    *          object o1 is less, equals, or greater than object 02.
    */
    public int compare(T o1, T o2)
    {
        // 1. compare total score first
        double score1 = ((OrthographicScore) o1).GetScore();
        double score2 = ((OrthographicScore) o2).GetScore();
        int out = 0;
        if(score1 != score2)
        {
            // from high to low
            out = (int) (1000*(score2-score1));
        }
        else
        {
            // 2. compare edit score
            double edScore1 = ((OrthographicScore) o1).GetEdScore();
            double edScore2 = ((OrthographicScore) o2).GetEdScore();
            if(edScore1 != edScore2)
            {
                out = (int) (1000*(edScore2-edScore1));
            }
            else
            {
                // 3. compare phoneticScore
                double pScore1 = ((OrthographicScore) o1).GetPhoneticScore();
                double pScore2 = ((OrthographicScore) o2).GetPhoneticScore();
                if(pScore1 != pScore2)
                {
                    out = (int) (1000*(pScore2-pScore1));
                }
                else
                {
                    double oScore1 = ((OrthographicScore) o1).GetOverlapScore();
                    double oScore2 = ((OrthographicScore) o2).GetOverlapScore();
                    // 4. compare overlap Score
                    if(oScore1 != oScore2)
                    {
                        out = (int) (1000*(oScore2-oScore1));
                    }
                    else     // 5. Alphabetic order
                    {
                        String str1 = ((OrthographicScore) o1).GetTarStr();
                        String str2 = ((OrthographicScore) o2).GetTarStr();
                        out = str2.compareTo(str1);
                    }
                }
            }
        }
        return out;
    }
}
