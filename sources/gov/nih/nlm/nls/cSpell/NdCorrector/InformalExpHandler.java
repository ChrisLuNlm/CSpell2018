package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the informal expression handler. It matches and maps informal 
* expression to a pre-specified term (formal expression).
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
public class InformalExpHandler 
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private InformalExpHandler()
    {
    }
    // public method
    /**
    * A method to process mapping frominformal expression to corrected word.
    * The lowercase of inWord is used as key for the mapping.
    *
    * @param     inTokenObj    the input tokenObj (single word)
    * @param    informalExpMap the map of informal expression
    * 
    * @return    the mapped corrected word (lowercase only) if mappnig found, 
    *             toherwise, the original input token is returned.
    */
    public static TokenObj Process(TokenObj inTokenObj, 
        HashMap<String, String> informalExpMap)
    {
        boolean debugFlag = false;
        return Process(inTokenObj, informalExpMap, debugFlag);
    }
    public static TokenObj Process(TokenObj inTokenObj, 
        HashMap<String, String> informalExpMap, boolean debugFlag)
    {
        String inTokenStr = inTokenObj.GetTokenStr();
        String outTokenStr = ProcessWord(inTokenStr, informalExpMap);
        TokenObj outTokenObj = new TokenObj(inTokenObj);
        //update info if there is a process
        if(inTokenStr.equals(outTokenStr) == false)
        {
            outTokenObj.SetTokenStr(outTokenStr);
            outTokenObj.AddProcToHist(TokenObj.HIST_ND_INFORMAL_EXP);
            DebugPrint.PrintCorrect("ND", "InformalExpHandler", 
                inTokenStr, outTokenStr, debugFlag);
        }
        return outTokenObj;
    }
    // private Methods
    /**
    * A method to process mapping frominformal expression to corrected word.
    * The lowercase of inWord is used as key for the mapping.
    *
    * @param     inWord    the input token (single word)
    * @param    informalExpMap the map of informal expression
    * 
    * @return    the mapped corrected word (lowercase only), 
    *             the original input token is returned if no mapping is found.
    */
    private static String ProcessWord(String inWord, 
        HashMap<String, String> informalExpMap)
    {
        String outWord = inWord;
        if(informalExpMap != null)
        {
            String mapWord = informalExpMap.get(inWord.toLowerCase());
            if(mapWord != null)
            {
                outWord = mapWord; 
            }
        }
        return outWord;
    }
    public static HashMap<String, String> GetInformalExpMapFromFile(
        String inDataFile)
    {
        boolean verboseFlag = false;
        return GetInformalExpMapFromFile(inDataFile, verboseFlag);
    }
    public static HashMap<String, String> GetInformalExpMapFromFile(
        String inDataFile, boolean verboseFlag)
    {
        // read in informal express map from a file
        HashMap<String, String> informalExpMap
            = FileInToMap.GetHashMapByFields(inDataFile, verboseFlag);
        return informalExpMap;
    }
    private static void TestProcessWord(HashMap<String, String> informalExpMap)
    {
        String inText = "? pls";
        String outText 
            = InformalExpHandler.ProcessWord(inText, informalExpMap);
        System.out.println("- in: [" + inText + "], out:[" + outText + "]");
    }
    private static void TestProcess(HashMap<String, String> informalExpMap)
    {
        // init
        String inText = "Contraction:        We  cant theredve hell. Plz u r good.";
        // test process:  must use ArrayList<TextObj>
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
            inTokenList.stream()
            .map(tokenObj -> InformalExpHandler.Process(tokenObj, informalExpMap))
            .collect(Collectors.toList()));
        // result    
        String outText = TextObj.TokenListToText(outTokenList);
        // print out
        System.out.println("--------- InformalExpHandler( ) -----------");
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
    public static void main(String[] args)
    {
        String inFile = "../data/Misc/informalExpression.data";
        if(args.length == 1)
        {
            inFile = args[0];
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java InformalExpHandler <inFile>");
            System.exit(0);
        }
        
        // init
        HashMap<String, String> informalExpMap
            = InformalExpHandler.GetInformalExpMapFromFile(inFile);
        
        // test
        TestProcessWord(informalExpMap);
        TestProcess(informalExpMap);
    }
    // data member
}
