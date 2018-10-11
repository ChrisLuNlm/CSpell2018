package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class handles internet related operations. Such as check
* if a token is an eMail or URL.
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
public class InternetTokenUtil 
{
    // public constructor
    /**
    * private constructor 
    */
    private InternetTokenUtil()
    {
    }
    // public methods
    /**
    * A method to validate a token is an URL.
    *
    * @param     inToken    the input token (single word)
    * 
    * @return    true if the inToken is a valid URL, false if otherwise.
    */
    public static boolean IsUrl(String inToken) 
    {
        boolean checkEmptyTokenFlag = true;
        return TokenUtil.IsMatch(inToken, patternU_, checkEmptyTokenFlag);
    }
    /**
    * A method to validate a token is an eMail address
    *
    * @param     inToken    the input token (single word)
    * 
    * @return    true if the inToken is a valid eMail address, 
    *             false if otherwise.
    */
    public static boolean IsEmail(String inToken) 
    {
        boolean checkEmptyTokenFlag = true;
        return TokenUtil.IsMatch(inToken, patternE_, checkEmptyTokenFlag);
    }
    // private methods
    private static void TestEmail()
    {
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("ab#=$%c@mail.nih.gov");
        inWordList.add("abc@mail.nih.gov");
        inWordList.add("abc@gmail.com");
        inWordList.add("abc@com.gmail");
        inWordList.add("abc@mail.google");
        inWordList.add("abc@mail");
        inWordList.add("abc@com");
        inWordList.add("abc@123.net");
        inWordList.add("123@gmail.com");
        inWordList.add("12ab%^@gamil.com");
        inWordList.add("_+-@gamil.com");
        inWordList.add("!!@gamil.com");
        for(String inWord:inWordList)
        {
            System.out.println("- IsEmail(" + inWord + "): " + IsEmail(inWord));
        }
    }
    private static void TestUrl()
    {
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add("https://yahoo.com");
        inWordList.add("http://yahoo.com");
        inWordList.add("http://www.yahoo.com");
        inWordList.add("www.yahoo.com");
        inWordList.add("yahoo.com");
        inWordList.add("com");
        inWordList.add("http://www.yahoo.com?test=1%20try%20abc");
        inWordList.add("clinicaltrials.gov");
        inWordList.add("male.read");
        inWordList.add("co.uk");
        inWordList.add("co.ch");
        inWordList.add("https://ehlers-danlos.com/eds-types/");
        inWordList.add("http://emedicine.medscape.com/article/1143167-treatment");
        inWordList.add("http://emedicine.medscape.com/article/1143167-treatment.");
        inWordList.add("https://ehlers-danlos.com/eds-types/");
        inWordList.add("http://www.dupuytren-online.info/ledderhose_therapies.html");
        inWordList.add("http://www.newbornscreening.info/parents/organicaciddisorders/mma_hcu.html#4");
        inWordList.add("http://live-naa.pantheon.io/wp-content/uploads/2014/12/managing-ppa.pdf.");
        inWordList.add("http://goo.gl/c4rm4p");
        inWordList.add("good.bad");
        for(String inWord:inWordList)
        {
            System.out.println("- IsUrl(" + inWord + "): " + IsUrl(inWord));
        }
    }
    private static void Test()
    {
        System.out.println("===== Unit Test of InternetTokenUtil =====");
        TestEmail();
        System.out.println("-------");
        TestUrl();
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java InternetTokenUtil");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    // eMail
    private static final String patternStrE_ 
        = "^[\\w!#$%&'*+-/=?^_`{|}~]+@(\\w+(\\.\\w+)*(\\.(gov|com|org|edu|mil|net)))$";
    private static final Pattern patternE_ = Pattern.compile(patternStrE_);
    // URL
    private static final String patternStrU_ 
        // original
        //= "^((ftp|http|https|file)://)?(\\w+(\\.\\w+)*(\\.(gov|com|org|edu|mil|net|uk)).*)$";
        = "^((ftp|http|https|file)://)?((\\w|\\-)+(\\.(\\w|\\-)+)*(\\.(gov|com|org|edu|mil|net|uk|info|io|gl)).*)$";
    private static final Pattern patternU_ = Pattern.compile(patternStrU_);
}
