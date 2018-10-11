package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class checks if a token is a number, such as thirty-four.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
*****************************************************************************/
public class NumberTokenUtil 
{
    // public constructor
    /**
    * Public constructor to initiate the HashMap table.
    */
    private NumberTokenUtil()
    {
    }
    // public methods
    // some token contain "-", such as two-third, thirty-four
    // inToken can't contain spaces 
    public static boolean IsNumber(String inToken, HashSet<String> numberSet)
    {
        boolean flag = true;
        String[] inTokenArray = inToken.split("-");
        for(String inTokenItem:inTokenArray)
        {
            if(IsNumberToken(inTokenItem, numberSet) == false)
            {
                flag = false;
                break;
            }
        }
        return flag;
    }
    // use lower case
    // inToken can't have punctuation
    public static boolean IsNumberToken(String inToken, 
        HashSet<String> numberSet)
    {
        return numberSet.contains(inToken.toLowerCase());
    }
    // get number from LEXICON
    public static HashSet<String> GetNumberSetFromFile(String lexNumberFile)
    {
        int fieldNo = 1;
        boolean lowercaseFlag = true;
        HashSet<String> numberSet = FileInToSet.GetHashSetByField(
            lexNumberFile, fieldNo, lowercaseFlag);
        return numberSet;    
    }
    // private methods
    private static void Test(HashSet<String> lexNumberSet)
    {
        System.out.println("===== Unit Test of NumberTokenUtil =====");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("Fifty");
        inWordList.add("half");
        inWordList.add("two-thirds");
        inWordList.add("eighty");
        inWordList.add("first");
        inWordList.add("firsts");
        inWordList.add("fourth");
        inWordList.add("zeroth");
        inWordList.add("billion");
        inWordList.add("halve");
        inWordList.add("thirty-four");
        inWordList.add("thirty-and");
        inWordList.add("thirty-a");
        inWordList.add("half-and-half");
        inWordList.add("one on one");
        for(String inWord:inWordList)
        {
            System.out.println("- IsNumber(" + inWord + "): " 
                + IsNumber(inWord, lexNumberSet));
        }
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java NumberTokenUtil");
            System.exit(0);
        }
        
        // init
        String lexNumberFile = "../data/Dictionary/cSpell/NRVAR";
        HashSet<String> lexNumberSet 
            = NumberTokenUtil.GetNumberSetFromFile(lexNumberFile);
        // test case and print out 
        Test(lexNumberSet);
    }
    // data member
}
