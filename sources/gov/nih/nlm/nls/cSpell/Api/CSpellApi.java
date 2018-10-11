package gov.nih.nlm.nls.cSpell.Api;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.NdCorrector.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
/*****************************************************************************
* This class is API of CSpell. It is the only class needed for end users.
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
public class CSpellApi
{
    // public constructor
    /**
    * Public constructor for CSpellApi.
    */
    public CSpellApi()
    {
        Init();
    }
    /**
    * CSpellApi constructor, initiate related data using a specified
    * configuration file.
    *
    * @param   configFile   the absolute path of the configuration file
    */
    public CSpellApi(String configFile)
    {
        configFile_ = configFile;
        Init();
    }
    /**
    * CSpellApi constructor, initiate related data using a specified
    * configuration file.
    *
    * @param   configFile  the absolute path of the configuration file
    * @param   debugFlag   boolean flag for debug print
    */
    public CSpellApi(String configFile, boolean debugFlag)
    {
        configFile_ = configFile;
        Init(debugFlag);
    }
    /**
    * CSpellApi constructor, initiate related data with properties
    * needs to be overwritten.
    *
    * @param   properties   properties to be overwritten in config
    */
    public CSpellApi(Hashtable<String, String> properties)
    {
        properties_ = properties;
        Init();
    }
    /**
    * CSpellApi constructor, initiate related data with properties
    * needs to be overwritten.
    *
    * @param   configFile   the absolute path of the configuration file
    * @param   properties   properties to be overwritten in config
    */
    public CSpellApi(String configFile, Hashtable<String, String> properties)
    {
        configFile_ = configFile;
        properties_ = properties;
        Init();
    }
    /**
    * Get the configuration object.
    *
    * @return  Configuration object
    */
    public Configuration GetConfiguration()
    {
        return conf_;
    }
    // cSpell main process: input str, output Str
    /**
    * cSpell correction process, output to a string. Use funcmode and rankmode
    * specified in the configuration file.
    *
    * @param   inText   input text to be corrected
    * @return  corrected text
    */
    public String ProcessToStr(String inText)
    {
        // use modes from configuration
        String outStr = CorrectionApi.ProcessToStr(inText, this);
        return outStr;
    }
    /**
    * cSpell correction process, output to a string by 
    * specifying funcMode and rankMode.
    *
    * @param   inText   input text to be corrected
    * @param   funcMode funcMode for correction: NW/RW-Merge/Split/1To1
    * @param   rankMode rankMode for select correction from the candidate for NW
    *                     Split/1To1
    * @return  corrected text
    */
    public String ProcessToStr(String inText, int funcMode, int rankMode)
    {
        // update modes
        this.funcMode_ = funcMode;
        this.rankMode_ = rankMode;
        String outStr = CorrectionApi.ProcessToStr(inText, this);
        return outStr;
    }
    /**
    * cSpell correction process, output to an ArrayList of TokenObj by using 
    * funcMode and rankMode from configuratin file.
    *
    * @param   inText   input text to be corrected
    * @return  an ArrayList of TokenObj
    */
    public ArrayList<TokenObj> ProcessToTokenObj(String inText) 
    {
        boolean debugFlag = false;
        return ProcessToTokenObj(inText, debugFlag);
    }
    /**
    * cSpell correction process, output to an ArrayList of TokenObj by using 
    * funcMode and rankMode from configuratin files, with debug print option.
    *
    * @param   inText   input text to be corrected
    * @param   debugFlag boolean flag for debug print
    * @return  an ArrayList of TokenObj
    */
    public ArrayList<TokenObj> ProcessToTokenObj(String inText, 
        boolean debugFlag)
    {
        DebugPrint.Println("====== SpellApi.Process( ), funcMode: " 
            + funcMode_ + ", rankMode: " + rankMode_ + " ======", debugFlag);
        // non-dictionary and dictionary base correction    
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = CorrectionApi.ProcessByTokenObj(
            inTokenList, this, debugFlag);
        return outTokenList;
    }
    /**
    * cSpell correction process, output to an ArrayList of TokenObj by 
    * specifying funcMode and rankMode, with debug print option.
    *
    * @param   inText   input text to be corrected
    * @param   funcMode funcMode for correction: NW/RW-Merge/Split/1To1
    * @param   rankMode rankMode for select correction from the candidate for NW
    *                     Split/1To1
    * @param   debugFlag boolean flag for debug print
    * @return  an ArrayList of TokenObj
    */
    public ArrayList<TokenObj> ProcessToTokenObj(String inText, int funcMode, 
        int rankMode, boolean debugFlag)
    {
        // update modes
        this.funcMode_ = funcMode;
        this.rankMode_ = rankMode;
        return ProcessToTokenObj(inText, debugFlag);    
    }
    // non-word, real-word, 1-to-1, split, merge
    public void SetFuncMode(int funcMode)
    {
        funcMode_ = funcMode;
    }
    // orthographic, frequency, context, combine, cSpell
    public void SetRankMode(int rankMode)
    {
        rankMode_ = rankMode;
    }
    public void SetCanMaxCandNo(int maxCandNo)
    {
        cMaxCandNo_ = maxCandNo;
    }
    public int GetFuncMode()
    {
        return funcMode_;
    }
    public int GetRankMode()
    {
        return rankMode_;
    }
    // files
    public HashMap<String, String> GetInformalExpressionMap() 
    {
        return infExpMap_;
    }
    // dictionary
    public RootDictionary GetCheckDic()
    {
        return checkDic_;
    }
    public RootDictionary GetSuggestDic()
    {
        return suggestDic_;
    }
    public RootDictionary GetSplitWordDic()
    {
        return splitWordDic_;
    }
    public RootDictionary GetMwDic()
    {
        return mwDic_;
    }
    public RootDictionary GetPnDic()
    {
        return pnDic_;
    }
    public RootDictionary GetAaDic()
    {
        return aaDic_;
    }
    public RootDictionary GetSvDic()
    {
        return svDic_;
    }
    public RootDictionary GetUnitDic()
    {
        return unitDic_;
    }
    public WordWcMap GetWordWcMap()
    {
        return wordWcMap_;
    }
    public Word2Vec GetWord2VecIm()
    {
        return word2VecIm_;
    }
    public Word2Vec GetWord2VecOm()
    {
        return word2VecOm_;
    }
    // getter - Detector
    public int GetDetectorRwSplitWordMinLength()
    {
        return dRwSplitWordMinLength_;
    }
    public int GetDetectorRwSplitWordMinWc()
    {
        return dRwSplitWordMinWc_;
    }
    public int GetDetectorRw1To1WordMinLength()
    {
        return dRw1To1WordMinLength_;
    }
    public int GetDetectorRw1To1WordMinWc()
    {
        return dRw1To1WordMinWc_;
    }
    // getter - Candidates
    public int GetCanMaxCandNo()
    {
        return cMaxCandNo_;
    }
    public int GetCanNdMaxSplitNo()
    {
        return cNdMaxSplitNo_;
    }
    public int GetCanNw1To1WordMaxLength()
    {
        return cNw1To1WordMaxLength_;
    }
    public int GetCanNwMaxSplitNo()
    {
        return cNwMaxSplitNo_;
    }
    public int GetCanNwMaxMergeNo()
    {
        return cNwMaxMergeNo_;
    }
    public boolean GetCanNwMergeWithHyphen()
    {
        return cNwMergeWithHyphen_;
    }
    public int GetCanRw1To1WordMaxLength()
    {
        return cRw1To1WordMaxLength_;
    }
    public int GetCanRwMaxSplitNo()
    {
        return cRwMaxSplitNo_;
    }
    public int GetCanRwMaxMergeNo()
    {
        return cRwMaxMergeNo_;
    }
    public boolean GetCanRwMergeWithHyphen()
    {
        return cRwMergeWithHyphen_;
    }
    public int GetCanRwShortSplitWordLength()
    {
        return cRwShortSplitWordLength_;
    }
    public int GetCanRwMaxShortSplitWordNo()
    {
        return cRwMaxShortSplitWordNo_;
    }
    public int GetCanRwMergeCandMinWc()
    {
        return cRwMergeCandMinWc_;
    }
    public int GetCanRwSplitCandMinWc()
    {
        return cRwSplitCandMinWc_;
    }
    public int GetCanRw1To1CandMinLength()
    {
        return cRw1To1CandMinLength_;
    }
    public int GetCanRw1To1CandMinWc()
    {
        return cRw1To1CandMinWc_;
    }
    public int GetCanRw1To1CandMaxKeySize()
    {
        return cRw1To1CandMaxKeySize_;
    }
    // Getter - Rankers
    // used in non-word split and 1-to-1 to find candidtes within range
    public double GetRankNwS1RankRangeFac()
    {
        return rNwS1RankRangeFac_;
    }
    // used in non-word split and 1-to-1 to find candidtes using Oscore
    public double GetRankNwS1MinOScore()
    {
        return rNwS1MinOScore_;
    }
    // used in the real-word 1-to-1 score rules: RankRealWordByContext.java
    public double GetRankRw1To1CFac()
    {
        return rRw1To1CFac_;
    }
    // used in the real-word split score rules: RankRealWordSplitByContext.java
    public double GetRankRwSplitCFac()
    {
        return rRwSplitCFac_;
    }
    // used in the real-word merge score rules: RankRealWordMergeByContext.java
    public double GetRankRwMergeCFac()
    {
        return rRwMergeCFac_;
    }
    public double GetRankRw1To1WordMinCs()
    {
        return rRw1To1WordMinCs_;
    }
    public double GetRankRw1To1CandCsFac()
    {
        return rRw1To1CandCsFac_;
    }
    public double GetRankRw1To1CandMinCs()
    {
        return rRw1To1CandMinCs_;
    }
    public double GetRankRw1To1CandCsDist()
    {
        return rRw1To1CandCsDist_;
    }
    public double GetRankRw1To1CandFsFac()
    {
        return rRw1To1CandFsFac_;
    }
    public double GetRankRw1To1CandMinFs()
    {
        return rRw1To1CandMinFs_;
    }
    public double GetRankRw1To1CandFsDist()
    {
        return rRw1To1CandFsDist_;
    }
    // Scores
    public double GetOrthoScoreEdDistFac()
    {
        return orthoScoreEdDistFac_;
    }
    public double GetOrthoScorePhoneticFac()
    {
        return orthoScorePhoneticFac_;
    }
    public double GetOrthoScoreOverlapFac()
    {
        return orthoScoreOverlapFac_;
    }
    // Rankers - context
    public boolean GetWord2VecSkipWord()
    {
        return word2VecSkipWord_;
    }
    public int GetNw1To1ContextRadius()
    {
        return nw1To1ContextRadius_;
    }
    public int GetNwSplitContextRadius()
    {
        return nwSplitContextRadius_;
    }
    public int GetNwMergeContextRadius()
    {
        return nwMergeContextRadius_;
    }
    public int GetRw1To1ContextRadius()
    {
        return rw1To1ContextRadius_;
    }
    public int GetRwSplitContextRadius()
    {
        return rwSplitContextRadius_;
    }
    public int GetRwMergeContextRadius()
    {
        return rwMergeContextRadius_;
    }

    public void SetDetectNo(int detectNo)
    {
        detectNo_ = detectNo;
    }
    public int UpdateDetectNo()
    {
        return detectNo_++;
    }
    public int GetDetectNo()
    {
        return detectNo_;
    }
    public void SetCorrectNo(int correctNo)
    {
        correctNo_ = correctNo;
    }
    public int UpdateCorrectNo()
    {
        return correctNo_++;
    }
    public int GetCorrectNo()
    {
        return correctNo_;
    }
    // Close persistent files, files, and database connection
    // TBD: to be used when switch to DB
    public void Close()
    {
    }
    // privat methods
    // init
    private void Init()
    {
        boolean debugFlag = false;
        Init(debugFlag);
    }
    // update parameter from the config file to cSpellApi
    private void Init(boolean debugFlag)
    {
        // get config file from environment variable
        boolean useClassPath = false;
        if(configFile_ == null)
        {
            useClassPath = true;
            configFile_ = "data.Config.cSpell";
        }
        // read in configuration file
        conf_ = new Configuration(configFile_, useClassPath);
        if(properties_ != null)
        {
            conf_.OverwriteProperties(properties_);
        }
        String cSpellDir = conf_.GetProperty(Configuration.CS_DIR);
        // files: pre-correction
        String infExpFile = cSpellDir
            + conf_.GetProperty(Configuration.CS_INFORMAL_EXP_FILE);
        infExpMap_ = InformalExpHandler.GetInformalExpMapFromFile(infExpFile);
        // get dictionary for spell checker
        String checkDicFileStrs 
            = conf_.GetProperty(Configuration.CS_CHECK_DIC_FILES);
        checkDic_.AddDictionaries(checkDicFileStrs, cSpellDir, debugFlag);
        // get dictionary for spell suggestion - candidate
        String suggestDicFileStrs 
            = conf_.GetProperty(Configuration.CS_SUGGEST_DIC_FILES);
        suggestDic_.AddDictionaries(suggestDicFileStrs, cSpellDir, debugFlag);
        // no acr/abb dictionary: en + pn, used for split check
        String splitWordDicFileStrs 
            = conf_.GetProperty(Configuration.CS_SPLIT_WORD_DIC_FILES);
        splitWordDic_.AddDictionaries(splitWordDicFileStrs, cSpellDir, 
            debugFlag);
        // mw dictionary
        String mwDicFile = cSpellDir
            + conf_.GetProperty(Configuration.CS_MW_DIC_FILE);
        mwDic_.AddDictionary(mwDicFile);
        // properNoun dictionary
        String pnDicFile = cSpellDir
            + conf_.GetProperty(Configuration.CS_PN_DIC_FILE);
        pnDic_.AddDictionary(pnDicFile);
        // abb/acr dictionary
        String aaDicFile = cSpellDir
            + conf_.GetProperty(Configuration.CS_AA_DIC_FILE);
        aaDic_.AddDictionary(aaDicFile);
        // spVar dictionary
        String svDicFile = cSpellDir
            + conf_.GetProperty(Configuration.CS_SV_DIC_FILE);
        svDic_.AddDictionary(svDicFile);
        // unit file
        String unitDicFile = cSpellDir 
            + conf_.GetProperty(Configuration.CS_UNIT_DIC_FILE);
        unitDic_.AddDictionary(unitDicFile);
        // frequency file
        String frequencyFile = cSpellDir 
            + conf_.GetProperty(Configuration.CS_FREQUENCY_FILE);
        wordWcMap_ = new WordWcMap(frequencyFile);
        // word2Vec file
        String word2VecImFile = cSpellDir 
            + conf_.GetProperty(Configuration.CS_W2V_IM_FILE);
        word2VecIm_ = new Word2Vec(word2VecImFile);
        String word2VecOmFile = cSpellDir 
            + conf_.GetProperty(Configuration.CS_W2V_OM_FILE);
        word2VecOm_ = new Word2Vec(word2VecOmFile);
        // mode
        funcMode_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_FUNC_MODE));
        rankMode_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_RANK_MODE));
        // detectors
        dRwSplitWordMinLength_ = Integer.parseInt(conf_.GetProperty(
            Configuration.CS_DETECTOR_RW_SPLIT_WORD_MIN_LENGTH));
        dRwSplitWordMinWc_ = Integer.parseInt(conf_.GetProperty(
            Configuration.CS_DETECTOR_RW_SPLIT_WORD_MIN_WC));
        dRw1To1WordMinLength_ = Integer.parseInt(conf_.GetProperty(
            Configuration.CS_DETECTOR_RW_1TO1_WORD_MIN_LENGTH));
        dRw1To1WordMinWc_ = Integer.parseInt(conf_.GetProperty(
            Configuration.CS_DETECTOR_RW_1TO1_WORD_MIN_WC));
        // candidates
        cMaxCandNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_MAX_CANDIDATE_NO));
        cNdMaxSplitNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_ND_MAX_SPLIT_NO));
        cNwMaxSplitNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_NW_MAX_SPLIT_NO));
        cNwMaxMergeNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_NW_MAX_MERGE_NO));
        cNwMergeWithHyphen_ = Boolean.parseBoolean(
            conf_.GetProperty(Configuration.CS_CAN_NW_MERGE_WITH_HYPHEN));
        cRwMaxSplitNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_MAX_SPLIT_NO));
        cRwMaxMergeNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_MAX_MERGE_NO));
        cRwMergeWithHyphen_ = Boolean.parseBoolean(
            conf_.GetProperty(Configuration.CS_CAN_RW_MERGE_WITH_HYPHEN));
            
        cRwShortSplitWordLength_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_SHORT_SPLIT_WORD_LENGTH));
        cRwMaxShortSplitWordNo_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_MAX_SHORT_SPLIT_WORD_NO));
        cRwMergeCandMinWc_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_MERGE_CAND_MIN_WC));
        cRwSplitCandMinWc_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_SPLIT_CAND_MIN_WC));
        cRw1To1CandMinLength_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_1TO1_CAND_MIN_LENGTH));
        cRw1To1CandMinWc_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_1TO1_CAND_MIN_WC));
        cRw1To1CandMaxKeySize_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_CAN_RW_1TO1_CAND_MAX_KEY_SIZE));

        // rankers
        rNwS1RankRangeFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_NW_S1_RANK_RANGE_FAC));
        rNwS1MinOScore_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_NW_S1_MIN_OSCORE));
        rRw1To1CFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_C_FAC));
        rRwSplitCFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_SPLIT_C_FAC));
        rRwMergeCFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_MERGE_C_FAC));
        rRw1To1WordMinCs_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_WORD_MIN_CS));
        rRw1To1CandCsFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_CAND_CS_FAC));
        rRw1To1CandMinCs_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_CAND_MIN_CS));
        rRw1To1CandCsDist_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_CAND_CS_DIST));
        rRw1To1CandFsFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_CAND_FS_FAC));
        rRw1To1CandMinFs_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_CAND_MIN_FS));
        rRw1To1CandFsDist_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_RANKER_RW_1TO1_CAND_FS_DIST));

        // Score    
        orthoScoreEdDistFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_ORTHO_SCORE_ED_DIST_FAC));
        orthoScorePhoneticFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_ORTHO_SCORE_PHONETIC_FAC));
        orthoScoreOverlapFac_ = Double.parseDouble(
            conf_.GetProperty(Configuration.CS_ORTHO_SCORE_OVERLAP_FAC));

        // context
        word2VecSkipWord_ = Boolean.parseBoolean(
            conf_.GetProperty(Configuration.CS_W2V_SKIP_WORD));
        nw1To1ContextRadius_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_NW_1TO1_CONTEXT_RADIUS));
        nwSplitContextRadius_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_NW_SPLIT_CONTEXT_RADIUS));
        nwMergeContextRadius_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_NW_MERGE_CONTEXT_RADIUS));
        rw1To1ContextRadius_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_RW_1TO1_CONTEXT_RADIUS));
        rwSplitContextRadius_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_RW_SPLIT_CONTEXT_RADIUS));
        rwMergeContextRadius_ = Integer.parseInt(
            conf_.GetProperty(Configuration.CS_RW_MERGE_CONTEXT_RADIUS));
    }
    private static void TestProcess(String configFile)
    {
        // init
        System.out.println("----- Test Process Text: -----");
        String inText = "We  cant theredve spel and 987Pfimbria dianosed.Plz u r good123. ";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        String outText = cSpellApi.ProcessToStr(inText);
        // print out
        System.out.println("--------- CSpellApi( ) -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
    }
    // test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length == 1)
        {
            configFile = args[0];
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java CSpellApi <configFile>");
            System.exit(0);
        }
        TestProcess(configFile);
    }
    // data member
    private String configFile_ = null;
    private Configuration conf_ = null;
    private Hashtable<String, String> properties_ = null;  // overwrite properties
    // cSpell process ranking mode
    public static final int RANK_MODE_ORTHOGRAPHIC = 0;// pre-corr + orthographic
    public static final int RANK_MODE_FREQUENCY = 1;   // pre-corr + frequency
    public static final int RANK_MODE_CONTEXT = 2;    // pre-corr + context
    public static final int RANK_MODE_NOISY_CHANNEL = 3;// pre-corr + noisy channel
    public static final int RANK_MODE_ENSEMBLE = 4;            // ensemble
    public static final int RANK_MODE_CSPELL = 5;            // cSpell
    private int rankMode_ = RANK_MODE_CSPELL;    // ranking mode
    // cSpell process function mode
    public static final int FUNC_MODE_ND = 0;    // not dictionary base correct
    public static final int FUNC_MODE_NW_1 = 1; // ND + NW_1To1
    public static final int FUNC_MODE_NW_S = 2;    // ND + NW_Split
    public static final int FUNC_MODE_NW_M = 3;    // ND + NW_Merge
    public static final int FUNC_MODE_NW_S_1 = 4;    // ND + NW_Split_1To1
    public static final int FUNC_MODE_NW_A = 5;    // ND + NW All, 1, S, M
    public static final int FUNC_MODE_RW_1 = 6;    // NW + RW_1_to_1
    public static final int FUNC_MODE_RW_S = 7;    // NW + RW_Split
    public static final int FUNC_MODE_RW_M = 8;    // NW + RW_Merge
    public static final int FUNC_MODE_RW_M_S = 9;    // NW + RW_Merge_Split
    public static final int FUNC_MODE_RW_A = 10;    // NW + RW_All
    private int funcMode_ = FUNC_MODE_RW_A;        // default mode
    // candidate & correction related data
    // non-dictionary, pre-correction related data
    private HashMap<String, String> infExpMap_ = null;    // informal express Map
    // Dictinoary related data
    //RootDictionary checkDic_ = new BasicDictionary();
    // dic for spelling checker for spelling error detection
    private RootDictionary checkDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dic for spelling checker for spelling suggestion
    private RootDictionary suggestDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dic for split word suggestion - English words + proper nouns
    // no acronyms and abbreviation
    private RootDictionary splitWordDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dictionary include verified multiwords    
    private RootDictionary mwDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dictionary include properNoun    
    private RootDictionary pnDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dictionary include abbreviations and acronyms
    private RootDictionary aaDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dictionary include spVar
    private RootDictionary svDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    // dictionary include unit
    private RootDictionary unitDic_
        = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC);
    
    // score and ranking related data
    // frequency map: word|WC
    private WordWcMap wordWcMap_ = null;
    // wrod2Vec: word2VecObj Input and output matrix
    private Word2Vec word2VecIm_ = null;
    private Word2Vec word2VecOm_ = null;
    // detector
    private int dRwSplitWordMinLength_ = 3;// min RW split word length
    private int dRwSplitWordMinWc_ = 200;// min RW split word wc
    private int dRw1To1WordMinLength_ = 3;// min RW 1-to-1 word length
    private int dRw1To1WordMinWc_ = 65;// min RW 1-to-1 word wc
    // candidate
    private int cMaxCandNo_ = 25;// max candidate no, configurable
    // candidate non-word
    private int cNdMaxSplitNo_ = 5;// max non-dictionary split recurNo
    private int cNw1To1WordMaxLength_ = 25;// non-word 1To1 word max length
    private int cNwMaxSplitNo_ = 5;// max non-word split no
    private int cNwMaxMergeNo_ = 2;// max non-word merge no
    private boolean cNwMergeWithHyphen_ = true;    // for non-word merge
    // candidate real-word
    private int cRw1To1WordMaxLength_ = 10;// real-word 1To1 word max length
    private int cRwMaxSplitNo_ = 2;// max real-word split no
    private int cRwMaxMergeNo_ = 2;// max real-word merge no
    private boolean cRwMergeWithHyphen_ = false;    // for real-word merge
    // candidate
    private int cRwShortSplitWordLength_ = 3;// rw short split word length
    private int cRwMaxShortSplitWordNo_ = 2;// RW max short split word no 
    private int cRwMergeCandMinWc_ = -1;// min candidate wc, configurable
    private int cRwSplitCandMinWc_ = 200;// min candidate wc, configurable
    private int cRw1To1CandMinLength_ = 2;// min candidate Length
    private int cRw1To1CandMinWc_ = -1;// min candidate wc, configurable
    private int cRw1To1CandMaxKeySize_ = 1000000000;// max key size for hashMap
    // rankers - score factor for real-word merge rule: 0.0 ~ 1.0
    private double rNwS1RankRangeFac_ = 0.05;    // use for split and 1To1
    private double rNwS1MinOScore_ = 2.7;    // use for split and 1To1
    private double rRw1To1CFac_ = 0.00;
    private double rRwSplitCFac_ = 0.01;
    private double rRwMergeCFac_ = 0.60;
    private double rRw1To1WordMinCs_ = -0.085;
    private double rRw1To1CandCsFac_ = 0.01;
    private double rRw1To1CandMinCs_ = 0.00;
    private double rRw1To1CandCsDist_ = 0.085; // these two numbers is close
    private double rRw1To1CandFsFac_ = 0.035;
    private double rRw1To1CandMinFs_ = -0.0006;
    private double rRw1To1CandFsDist_ = 0.02;
    // Score
    private double orthoScoreEdDistFac_ = 1.00;
    private double orthoScorePhoneticFac_ = 0.70;
    private double orthoScoreOverlapFac_ = 0.80;
    // Context - context radius, configurable
    private boolean word2VecSkipWord_ = true;
    private int nw1To1ContextRadius_ = 2; 
    private int nwSplitContextRadius_ = 2; 
    private int nwMergeContextRadius_ = 2; 
    private int rw1To1ContextRadius_ = 2; 
    private int rwSplitContextRadius_ = 2; 
    private int rwMergeContextRadius_ = 2; 

    private int detectNo_ = 0;    // detect No
    private int correctNo_ = 0;    // correct No, detect not necessary correct
}
