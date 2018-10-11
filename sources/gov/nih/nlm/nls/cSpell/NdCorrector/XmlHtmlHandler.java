package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class converts Html/Xml entity to ASCII by handling escape characters.
* It includes:
* - [&amp;lt;] to [&lt;]
* - [&amp;gt;] to [&gt;]
* - [&amp;amp;] to [&amp;]
* - [&amp;quot;] to [&quot;]
* - [&amp;nbsp;] to [&nbsp;]
*
* - Does not handle &amp;#dd; to ASCII, might need it if they are in test data 
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
public class XmlHtmlHandler 
{
    // private constructor
    private XmlHtmlHandler()
    {
    }
    
    // public methods
    /**
    * This method converts Html/Xml entityto ASCII code. 
    * It is desgined to work on the input of a TokenObj (single word or text).
    *
    * @param    inTokenObj  the input TokenObj
    *
    * @return   the corrected word in the format TokenObj with process 
    *             information in the history if mapping is found. Otherwise,
    *           the original input token is returned.
    */
    public static TokenObj Process(TokenObj inTokenObj) 
    {
        boolean debugFlag = false;
        return Process(inTokenObj, debugFlag);
    }
    public static TokenObj Process(TokenObj inTokenObj, boolean debugFlag) 
    {
        // get string from tokenObj
        String inTokenStr = inTokenObj.GetTokenStr();
        String outTokenStr = ProcessWord(inTokenStr);
        //update info if there is a XMl/Html process
        TokenObj outTokenObj = new TokenObj(inTokenObj);
        if(inTokenStr.equals(outTokenStr) == false)
        {
            outTokenObj.SetTokenStr(outTokenStr);
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_XML_HTML);
            DebugPrint.PrintCorrect("ND", "XmlHtmlHandler", inTokenStr,
                outTokenStr, debugFlag);
        }
        return outTokenObj;
    }
    // private methods
    /**
    * This method converts Html/Xml entityto ASCII code. 
    * It is desgined to work on the input of single word.
    * However, it should also work on a text.
    *
    * @param    inWord  the input token (single word)
    *
    * @return   the corrected word, does not change the case,
    *           the original input token is returned if no mapping is found.
    */
    private static String ProcessWord(String inWord) 
    {
        String outWord = inWord;
        Set<String> keySet = escapeCharMap_.keySet();
        for(String key:keySet)
        {
            int pos = outWord.indexOf(key);
            while(pos >= 0)
            {
                int keyLength = key.length();
                String mapStr = escapeCharMap_.get(key);
                outWord = outWord.substring(0, pos) + mapStr
                    + outWord.substring(pos + keyLength);
                pos = outWord.indexOf(key);    //this resolves recursive mapping    
            }
        }
        
        return outWord;
    }
    // the element is Word (String)
    private static void TestProcessWord()
    {
        System.out.println("----- Test Process Word: -----");
        ArrayList<String> inWordList = new ArrayList<String>();
        inWordList.add(",do");
        inWordList.add("&lt;tag&gt;");
        for(String inWord:inWordList)
        {
            System.out.println("- Process(" + inWord + "): "
                + ProcessWord(inWord));
        }
    }
    // the element is TokenObj
    private static void TestProcess()
    {
        // init
        System.out.println("----- Test Process Text: -----");
        String inText = "Xml: head rolling &amp;amp; rock, (5'8&quot;).";
        // test process:  must use ArrayList<TextObj>
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
            inTokenList.stream()
            .map(token -> XmlHtmlHandler.Process(token))
            .collect(Collectors.toList()));
        // result
        String outText = TextObj.TokenListToText(outTokenList);
    
        // print out
        System.out.println("--------- XmlHtmlHandler( ) Test -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("----- Details -----------");
        int index = 0;
        for(TokenObj tokenObj:outTokenList)
        {
            System.out.println(index + "|" + tokenObj.ToHistString());
            index++;
        }
    }
    // test driver
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java XmlHtmlHandler");
            System.exit(0);
        }
        
        TestProcessWord();
        TestProcess();
    }
    // data members
    private static final HashMap<String, String> escapeCharMap_ 
        = new HashMap<String, String>();
    static
    {
        escapeCharMap_.put("&amp;", "&");
        escapeCharMap_.put("&lt;", "<");
        escapeCharMap_.put("&gt;", ">");
        escapeCharMap_.put("&quot;", "\"");
        escapeCharMap_.put("&nbsp;"," ");
        //escapeCharMap_.put("&nbsp;","\u00a0");
    }
}
