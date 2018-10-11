package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.*;
import java.util.concurrent.atomic.AtomicInteger;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the java object for cSpell text.
* A text can be composed of sentences, phrases, words.
* A TextObj is composed of a list of TokenObjs.
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
public class TextObj 
{
    // public constructor
    /**
    * Public constructor to initiate the Text object.
    */
    public TextObj()
    {
    }
    /**
    * Public constructor to initiate the text Obj.
    *
    * @param    text    the text
    */
    public TextObj(String text)
    {
        Init(text);
    }
    /**
    * Public constructor to initiate the text Obj.
    *
    * @param    tokenList    the tokenList, cotains single word token
    * inlcuding pucntuation, space, linebreak, etc.
    */
    public TextObj(ArrayList<TokenObj> tokenList)
    {
        Init(tokenList);
    }
    // public methods
    public void SetText(String text)
    {
        text_ = text;
    }
    public void SetTokenList(ArrayList<TokenObj> tokenList)
    {
        // further make sure all token is a single word
        tokenList_ = GetAllTokens(tokenList);
    }
    public String GetText()
    {
        return text_;
    }
    public ArrayList<TokenObj> GetTokenList()
    {
        return tokenList_;
    }
    // convert token to string, put all token to a string by joining
    public String ToString()
    {
        // put all token + delimiters together
        String outText = tokenList_.stream()
            .map(token -> token.GetTokenStr())
            .collect(Collectors.joining());
        return outText;
    }
    // used after split,
    // after split operation, a token might include space(s),
    // use this to convert the token to string and then initiate a new TextObj
    public static String TokenListToText(ArrayList<TokenObj> tokenList)
    {
        // put all token + delimiters together
        String outText = tokenList.stream()
            .map(token -> token.GetTokenStr())
            .collect(Collectors.joining());
        return outText;
    }
    // gt the operation details for each token
    public static String TokenListToOperationDetailStr(
        ArrayList<TokenObj> tokenList)
    {
        /** To Be deleted, older implementation
        // for loop implementation for Java 7-
        String outStr = new String();
        int index = 0;
        for(TokenObj tokenObj:tokenList)
        {
            String dStr = index + GlobalVars.FS_STR
                + tokenObj.ToString();
            outStr += dStr + GlobalVars.LS_STR;        
            index++;
        }
        // java 8 implementation, not good for pararell processing
        int[] index = {0};    // use array for increasing index,
        String outStr = tokenList.stream()
            .map(token -> token.GetOpString(index[0]++))
            .collect(Collectors.joining());
        **/
        AtomicInteger aInt = new AtomicInteger(0);
        String outStr = tokenList.stream()
            .map(token -> token.GetOpString(aInt.getAndIncrement()))
            .collect(Collectors.joining(GlobalVars.LS_STR));
        return outStr;
    }
    // include empty space as a token
    public static ArrayList<TokenObj> TextToTokenList(String inText)
    {
        ArrayList<TokenObj> tokenList = new ArrayList<TokenObj>();
        if((inText != null) && (inText.length() > 0))
        {
            // keep token and delimiters
            String[] tokenArray = inText.split(patternStrSpace_);
            tokenList = new ArrayList<TokenObj>(Arrays.stream(tokenArray) 
                .map(token -> new TokenObj(token))
                .collect(Collectors.toList()));
        }
        // update index and posistion
        // this could added to above to speed up if needed
        UpdateIndexPos(tokenList);
        return tokenList;    
    }
    // update index and position for each TokenObj in the list
    // index: is the index of the tokenList
    // pos: is the index (ignoring empty string)
    public static void UpdateIndexPos(ArrayList<TokenObj> tokenList)
    {
        if((tokenList != null) && (tokenList.size() > 0))
        {
            int pos = 0;
            for(int index = 0; index < tokenList.size(); index++)
            {
                TokenObj tokenObj = tokenList.get(index);
                tokenObj.SetIndex(index);
                if(tokenObj.IsSpaceToken() == false)
                {
                    tokenObj.SetPos(pos);
                    pos++;
                }
            }
        }
    }
    // get the non-spaceToken list
    public static ArrayList<TokenObj> GetNonSpaceTokenObjList(
        ArrayList<TokenObj> inTokenList)
    {
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>();
        for(TokenObj tokenObj:inTokenList)
        {
            if(tokenObj.IsSpaceToken() == false) // skip space tokens
            {
                outTokenList.add(tokenObj);
            }
        }
        return outTokenList;
    }
    // init
    // further decompose tokenList to token without space
    // Similar to use flatmap for toeknObj has space in the toeknStr
    // The tokenObj should only have string without space,
    // This method is needed when split happen
    private static ArrayList<TokenObj> GetAllTokens(
        ArrayList<TokenObj> inTokenList) 
    {
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>();
        for(TokenObj tokenObj:inTokenList)
        {
            // further decompose token by string
            String tokenStr = tokenObj.GetTokenStr(); 
            String[] tokenArray = tokenStr.split(patternStrSpace_);
            if(tokenArray.length == 1)    // the original token
            {
                outTokenList.add(tokenObj);
            }
            else    // further decompose
            {
                ArrayList<TokenObj> tokenList2 
                    = new ArrayList<TokenObj>(Arrays.stream(tokenArray) 
                    .map(token -> new TokenObj(token))
                    .collect(Collectors.toList()));
                outTokenList.addAll(tokenList2);
            }
        }
        return outTokenList;
    }
    private void Init(ArrayList<TokenObj> tokenList)
    {
        // update tokenList_
        tokenList_ = GetAllTokens(tokenList);
        // update text
        if(tokenList_ != null)
        {
            text_ = ToString();
        }
    }
    // get the tokenList from the input text by tokenizer
    private void Init(String text)
    {
        // update text
        text_ = text;
        // update tokenList_ from the text
        // this tokenize by spaces, tabs, and line return
        // Use look-arounds to split on empty String just 
        // before or after the delimiters (spaces) to keep the delimiters
        // Need a throught test tested
        String[] tokenArray = text_.split(patternStrSpace_);
        tokenList_ = new ArrayList<TokenObj>(Arrays.stream(tokenArray) 
            .map(token -> new TokenObj(token))
            .collect(Collectors.toList()));
    }
    // to be deleted
    public static String[] GetTokenArray(String text)
    {
        String[] tokenArray = text.split(patternStrSpace_);
        return tokenArray;
    }
    // to be deleted
    public static ArrayList<String> GetTokenArrayList(String text)
    {
        ArrayList<String> tokenArrayList 
            = new ArrayList<String> (Arrays.asList(
                text.split(patternStrSpace_)));
        return tokenArrayList;
    }
    public static ArrayList<TokenObj> FlatTokenToArrayList(TokenObj inTokenObj)
    {
        String[] tokenArray 
            = inTokenObj.GetTokenStr().split(patternStrSpace_);
        ArrayList<TokenObj> tokenArrayList = new ArrayList<TokenObj>();
        for(String tokenStr:tokenArray)
        {
            TokenObj tokenObj = new TokenObj(inTokenObj);
            tokenObj.SetTokenStr(tokenStr);
            tokenArrayList.add(tokenObj);
        }
        return tokenArrayList;
    }
    private static void Test()
    {
        System.out.println("===== Unit Test of TextObj =====");
        // init: test double tabs and double spaces
        String inText = "Contraction:        We cant  theredve 123.45 hell.\nPlz u r good. ";
        // test case, go through each token
        TextObj textObj = new TextObj(inText);
        ArrayList<TokenObj> tokenList = textObj.GetTokenList();
        String outText = textObj.ToString();
        // print out
        System.out.println("--------- TextObj( ) -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("------ Detail -------------------");
        ArrayList<TokenObj> tokenObjList = textObj.GetTokenList();
        for(TokenObj tokenObj:tokenObjList)
        {
            System.out.println("[" + tokenObj.GetTokenStr() + "]");
        }
        System.out.println("===== End of Unit Test =====");
        // test more
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java TextObj");
            System.exit(0);
        }
        
        // Unit Test
        Test();
    }
    // data member
    // tokenize including unicode spaces, keep spcae as token , use look around
    //private static String patternStrSpace_ = "(?=\\s)|(?<=\\s)";
    //private static String patternStrSpace_ = "(?=[ \\t\\n ­])|(?<=[ \\t\\n ­])";
    public static String patternStrSpace_ = "(?=[\\s ­])|(?<=[\\s ­])";
    private String text_ = new String();    // the whole string for the text
    // the list of tokens for the text, including space, punctuation, etc.
    private ArrayList<TokenObj> tokenList_ = new ArrayList<TokenObj>();
}
