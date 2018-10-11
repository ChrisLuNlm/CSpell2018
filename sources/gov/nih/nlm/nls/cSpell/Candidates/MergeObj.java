package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import static java.util.stream.Collectors.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
/*****************************************************************************
* This class is the merge collection object.
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
public class MergeObj 
{
    // public constructor
    /**
    * Public constructor for MergeObj. 
    *
    * @param tarWord target word for merge
    * @param mergeWord merged word
    * @param coreMergeWord core term of the merged word
    * @param mergeNo total no of merge tokens
    */
    public MergeObj(String tarWord, String mergeWord, String coreMergeWord,
        int mergeNo)
    {
        tarWord_ = tarWord;
        mergeWord_ = mergeWord;
        coreMergeWord_ = coreMergeWord;
        mergeNo_ = mergeNo;
    }
    /**
    * Public constructor for MergeObj. 
    *
    * @param tarWord target word for merge
    * @param orgMergeWord original word before the merge
    * @param mergeWord merged word
    * @param coreMergeWord core term of the merged word
    * @param mergeNo total no of merge tokens
    * @param startIndex index of the starting token of the merge
    * @param tarIndex index of the target token for merge
    * @param endIndex index of the ending token of the merge
    * @param startPos position (index in the no space token list) of the 
    *             starting token for the merge
    * @param tarPos positin of thetarget token for merge
    * @param endPos position of the ending token for the merge
    */
    public MergeObj(String tarWord, String orgMergeWord, String mergeWord,
        String coreMergeWord, int mergeNo, int startIndex, int tarIndex, 
        int endIndex, int startPos, int tarPos, int endPos)
    {
        tarWord_ = tarWord;
        orgMergeWord_ = orgMergeWord;
        mergeWord_ = mergeWord;
        coreMergeWord_ = coreMergeWord;
        mergeNo_ = mergeNo;
        startIndex_ = startIndex;
        tarIndex_ = tarIndex;
        endIndex_ = endIndex;
        startPos_ = startPos;
        tarPos_ = tarPos;
        endPos_ = endPos;
    }
    // set target term
    public void SetTarWord(String tarWord)
    {
        tarWord_ = tarWord;
    }
    public void SetOrgMergeWord(String orgMergeWord)
    {
        orgMergeWord_ = orgMergeWord;
    }
    public void SetMergeWord(String mergeWord)
    {
        mergeWord_ = mergeWord;
    }
    public void SetCoreMergeWord(String coreMergeWord)
    {
        coreMergeWord_ = coreMergeWord;
    }
    public void SetStartIndex(int startIndex)
    {
        startIndex_ = startIndex;
    }
    public void SetEndIndex(int endIndex)
    {
        endIndex_ = endIndex;
    }
    public void SetTarIndex(int tarIndex)
    {
        tarIndex_ = tarIndex;
    }
    public void SetStartPos(int startPos)
    {
        startPos_ = startPos;
    }
    public void SetEndPos(int endPos)
    {
        endPos_ = endPos;
    }
    public void SetTarPos(int tarPos)
    {
        tarPos_ = tarPos;
    }
    public String GetTarWord()
    {
        return tarWord_;
    }
    public String GetOrgMergeWord()
    {
        return orgMergeWord_;
    }
    public String GetMergeWord()
    {
        return mergeWord_;
    }
    public String GetCoreMergeWord()
    {
        return coreMergeWord_;
    }
    public int GetMergeNo()
    {
        return mergeNo_;
    }
    public int GetStartIndex()
    {
        return startIndex_;
    }
    public int GetEndIndex()
    {
        return endIndex_;
    }
    public int GetTarIndex()
    {
        return tarIndex_;
    }
    public int GetStartPos()
    {
        return startPos_;
    }
    public int GetEndPos()
    {
        return endPos_;
    }
    public int GetTarPos()
    {
        return tarPos_;
    }
    // get the simulated original term by add space tokens
    public static String GetNonMergeTerm(MergeObj mergeObj, 
        ArrayList<TokenObj> nonSpaceTextList)
    {
        String nonMergeTerm = new String();
        if((mergeObj != null)
        && (nonSpaceTextList != null))
        {
            int startPos = mergeObj.GetStartPos();
            int endPos = mergeObj.GetEndPos();
            nonMergeTerm = nonSpaceTextList.get(startPos).GetTokenStr();
            for(int i = startPos + 1; i <= endPos; i++)
            {
                if((i >= 0) && (i < nonSpaceTextList.size()))
                {
                    nonMergeTerm += GlobalVars.SPACE_STR 
                        + nonSpaceTextList.get(i).GetTokenStr();
                }
                else    // illegal index
                {
                    break;
                }
            }
        }
        return nonMergeTerm;
    }
    /**
    * This override method checks the objects sequentiqlly if hascode are the 
    * same.
    */
    public boolean equals(Object anObject)
    {
        boolean flag = false;
        if((anObject != null) && (anObject instanceof MergeObj))
        {
            if(this.ToString().equals(((MergeObj)anObject).ToString()))
            {
                flag = true;
            }
        }
        return flag;
    }
    /**
    * This override method is used in hashTable to store data as key.
    */
    public int hashCode()
    {
        int hashCode = this.ToString().hashCode();
        return hashCode;
    }
    // compose the object and converts backto String format
    // format: tarWord|orgMergeWord|mergeWord|coreMergeWord|mergeNo|startIndex|tarIndex|endIndex|startPos|tarPos|endPos
    public String ToString()
    {
        String ourStr = tarWord_ + GlobalVars.FS_STR + orgMergeWord_
            + GlobalVars.FS_STR + mergeWord_ 
            + GlobalVars.FS_STR + coreMergeWord_
            + GlobalVars.FS_STR + mergeNo_ + GlobalVars.FS_STR
            + startIndex_ + GlobalVars.FS_STR + tarIndex_
            + GlobalVars.FS_STR + endIndex_ + GlobalVars.FS_STR + startPos_ 
            + GlobalVars.FS_STR + tarPos_ + GlobalVars.FS_STR + endPos_;
        return ourStr;
    }
    // public methods
    // private methods
    private static void Test()
    {
        int tarIndex = 6;        // target index
        int startIndex = 4;    // start index of merge
        int endIndex = 6;        // end index of merge
        int tarPos = 3;        // target pos
        int startPos = 2;    // start pos of merge
        int endPos = 3;        // end pos of merge
        int mergeNo = 1;        // total no of merged tokens
        String tarWord = "gnosed";        // target term
        String mergeWord = "diagnosed.";    // suggested merged terms
        String coreMergeWord = "diagnosed";    // core suggested merged terms
        String orgMergeWord = "dia gnosed";    // org word b4 merge
        MergeObj mergeObj = new MergeObj(tarWord, orgMergeWord, mergeWord, 
            coreMergeWord, mergeNo, startIndex, tarIndex, endIndex, startPos, 
            tarPos, endPos);
        String inText = "He is dia gnosed last week.";    
        ArrayList<TokenObj> inTextList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> nonSpaceTextList
            = TextObj.GetNonSpaceTokenObjList(inTextList);
        System.out.println("------ Merge Obj -------");
        System.out.println(mergeObj.ToString());
        System.out.println("------ Non Merge Term -------");
        String nonMergeTerm = GetNonMergeTerm(mergeObj, nonSpaceTextList);
        System.out.println("- inText: [" + inText + "]");
        System.out.println("- nonMergeTerm: [" + nonMergeTerm + "]");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java MergeObj");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    // index is the index in the original text, inluding space token
    // this is needed during the merge operation when correct the original text
    private int tarIndex_ = -1;        // target index
    private int startIndex_ = -1;    // start index of merge
    private int endIndex_ = -1;        // end index of merge
    // position is the index in the non-space token list
    // this is needed for the context scoring bz it only uses nonSpaceTokens
    private int tarPos_ = -1;        // target pos
    private int startPos_ = -1;        // start pos of merge
    private int endPos_ = -1;        // end pos of merge
    private int mergeNo_ = 0;        // total no of merged tokens
    private String tarWord_ = new String();        // target word
    private String orgMergeWord_ = new String();    // original b4 merged word
    private String mergeWord_ = new String();    // suggested merged word
    private String coreMergeWord_ = new String();//coreTerm of sug merged word
}
