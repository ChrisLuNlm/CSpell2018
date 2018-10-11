package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NdCorrector.*;
/*****************************************************************************
* This class is the java object for cSpell token.
* A token is a single word in a text. 
* Spaces and tabs are used as word boundary.
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
public class TokenObj 
{
    // public constructor
    /**
    * Public constructor to initiate the Token Object.
    */
    public TokenObj()
    {
    }
    public TokenObj(String tokenStr)
    {
        orgTokenStr_ = tokenStr;
        tokenStr_ = tokenStr;
    }
    // used in merge
    public TokenObj(String orgTokenStr, String tokenStr)
    {
        orgTokenStr_ = orgTokenStr;
        tokenStr_ = tokenStr;
    }
    public TokenObj(TokenObj inTokenObj)
    {
        index_ = inTokenObj.GetIndex();// index of this token in the TextObj
        pos_ = inTokenObj.GetPos();        // position 
        tag_ = inTokenObj.GetTag();
        orgTokenStr_ = inTokenObj.GetOrgTokenStr();    // the org str, never change
        tokenStr_ = inTokenObj.GetTokenStr();    // the current str
        // history of processes
        procHist_ = new ArrayList<String>(inTokenObj.GetProcHist());
    }
    public TokenObj(TokenObj inTokenObj, String tokenStr)
    {
        index_ = inTokenObj.GetIndex();// index of this token in the TextObj
        pos_ = inTokenObj.GetPos();        // position 
        tag_ = inTokenObj.GetTag();
        orgTokenStr_ = inTokenObj.GetOrgTokenStr();    // the org str, never change
        // history of processes
        procHist_ = new ArrayList<String>(inTokenObj.GetProcHist());
        tokenStr_ = tokenStr;
    }
    // public methods
    public void SetIndex(int index)
    {
        index_ = index;
    }
    public void SetPos(int pos)
    {
        pos_ = pos;
    }
    public void SetTag(int tag)
    {
        tag_ = tag;
    }
    public void SetTokenStr(String tokenStr)
    {
        tokenStr_ = tokenStr;
    }
    public int GetIndex()
    {
        return index_;
    }
    public int GetPos()
    {
        return pos_;
    }
    public int GetTag()
    {
        return tag_;
    }
    public String GetOrgTokenStr()
    {
        return orgTokenStr_;
    }
    public String GetTokenStr()
    {
        return tokenStr_;
    }
    public ArrayList<String> GetProcHist()
    {
        return procHist_;
    }
    public void AddProcToHist(String proc)
    {
        procHist_.add(proc);
    }
    // to be improved to all unicode space
    // check if the token is one of space str (space token)
    // TBD, change to use data member
    public boolean IsSpaceToken()
    {
        boolean spaceFlag = TokenUtil.IsSpaceToken(tokenStr_); 
        return spaceFlag;
    }
    // all process history
    public String GetProcHistStr()
    {
        String outStr = PROC_START_STR + procHist_.stream()
            .collect(Collectors.joining(PROC_SP_STR)) + PROC_END_STR;
        return outStr;    
    }
    // orgToken|curToken|History
    public String ToHistString()
    {
        String outStr = orgTokenStr_ + GlobalVars.FS_STR + tokenStr_ 
            + GlobalVars.FS_STR + GetProcHistStr();
        return outStr;    
    }
    // all data members
    public String ToString()
    {
        String outStr = index_ + GlobalVars.FS_STR
            + pos_ + GlobalVars.FS_STR
            + tag_ + GlobalVars.FS_STR
            + orgTokenStr_ + GlobalVars.FS_STR
            + tokenStr_ + GlobalVars.FS_STR 
            + GlobalVars.FS_STR + GetProcHistStr();
        return outStr;    
    }
    // operation string with index
    public String GetOpString(int index)
    {
        String outStr = index + GlobalVars.FS_STR + ToHistString();
        return outStr;
    }
    // Test Driver
    private static void Test(HashMap<String, String> informalExpMap)
    {
        System.out.println("===== Unit Test of TokenObj =====");
        // init
        String inText = "Contraction: We cant theredve hell. Plz u r  good.";
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        // construct the outstr from tokens by joining
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
            inTokenList.stream()
            .map(tokenObj -> InformalExpHandler.Process(tokenObj, informalExpMap))
            .collect(Collectors.toList()));
        // result   
        String outText = TextObj.TokenListToText(outTokenList);
        
        // print out
        System.out.println("--------- ProcInformalExpression( ) -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("--------- detail -----------");
        for(TokenObj tokenObj:inTokenList)
        {
            System.out.println(tokenObj.ToString());
        }
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        String inFile = "../data/informalExpression.txt";
        if(args.length == 1)
        {
            inFile = args[0];
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java TokenObj <inFile>");
            System.exit(0);
        }
        
        // init
        HashMap<String, String> informalExpMap
            = InformalExpHandler.GetInformalExpMapFromFile(inFile);
        // Unit Test
        Test(informalExpMap);
    }
    // data member
    // for History: This should be file driven
    public static final String PROC_START_STR = "[";
    public static final String PROC_END_STR = "]";
    public static final String PROC_SP_STR = ",";
    public static final int TAG_NONE = 0;
    public static final int TAG_SEN_END = 1;// end of a sentence, such as . or ?
    public static final int NO_INDEX = -1;
    public static final int NO_POS = -1;
    public static final String HIST_ND_XML_HTML = "ND_XML_HTML";
    public static final String HIST_ND_INFORMAL_EXP = "ND_INFORMAL_EXP";
    public static final String HIST_ND_S_E_D = "ND_SPLIT_END_DIGIT";
    public static final String HIST_ND_S_E_P = "ND_SPLIT_END_PUNC";
    public static final String HIST_ND_S_L_D = "ND_SPLIT_LEAD_DIGIT";
    public static final String HIST_ND_S_L_P = "ND_SPLIT_LEAD_PUNC";
    public static final String HIST_NW_1 = "NW_1_To_1";
    public static final String HIST_NW_S = "NW_SPLIT";
    public static final String HIST_NW_M = "NW_MERGE";
    public static final String HIST_RW_1 = "RW_1_To_1";
    public static final String HIST_RW_S = "RW_SPLIT";
    public static final String HIST_RW_M = "RW_MERGE";
    public static final String MERGE_START_STR = "(";
    public static final String MERGE_END_STR = ")";
    // data member
    private int index_ = NO_INDEX;    // index of the token in the TextObj 
    private int pos_ = NO_POS;        // position, index, skip space tokens
    private int tag_ = TAG_NONE;    // not used yet
    private String orgTokenStr_ = new String();    // the org str, never change
    private String tokenStr_ = new String();    // the current str
    // history of processes
    private ArrayList<String> procHist_ = new ArrayList<String>();
}
