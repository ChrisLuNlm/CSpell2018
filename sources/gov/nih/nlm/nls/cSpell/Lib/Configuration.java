package gov.nih.nlm.nls.cSpell.Lib;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the configuration object that is used to store and retrieve 
* configurable varaibles from the configuration file. 
*
* <p><b>History:</b>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class Configuration
{
    // public constructor
    /**
    * Create a Configuration object.  There are two ways of reading 
    * configuration files.  First, finding xxx.properties from Java class path.
    * Second, finding file by a specified path. 
    *  
    * @param  fName  the path of the configuration file or base name when
    * using classpath.
    * @param  useClassPath  a boolean flag of finding configuration file 
    * from classpath
    */
    public Configuration(String fName, boolean useClassPath)
    {
        SetConfiguration(fName, useClassPath);
    }
    // public methods
    /**
    * Get the size of key of config hashtable.
    *
    * @return  the size of configuration item (keys)
    */
    public int GetSize()
    {
        int size = 0;
        if(config_ != null)
        {
            size = config_.size();
        }
        return size;
    }
    /**
    * Get a value from configuration file by specifying the key.
    *
    * @param  key  key (name) of the configuration value to be get
    *
    * @return  the value of the configuration item in a string format
    */
    public String GetProperty(String key)
    {
        String out = config_.get(key);
        return out;
    }
    /**
    * Overwrite the value if it is specified in the properties.
    *
    * @param  properties  properties to be overwrite in the configuration
    */
    public void OverwriteProperties(Hashtable<String, String> properties)
    {
        for(Enumeration<String> e = properties.keys(); e.hasMoreElements();)
        {
            String key = e.nextElement();
            String value = properties.get(key);
            config_.put(key, value);
        }
    }
    /**
    * Get system level information from configuration.  This includes
    * CS_DIR 
    *
    * @return  the value of the configuration item in a string format
    */
    public String GetInformation()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("========== Files/Dirctory Setup ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_DIR: [" + GetProperty(CS_DIR) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CHECK_DIC_FILES: [" + GetProperty(CS_CHECK_DIC_FILES) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_SUGGEST_DIC_FILES: [" + GetProperty(CS_SUGGEST_DIC_FILES) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_SPLIT_WORD_DIC_FILES: [" + GetProperty(CS_SPLIT_WORD_DIC_FILES) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_MW_DIC_FILE: [" + GetProperty(CS_MW_DIC_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_PN_DIC_FILE: [" + GetProperty(CS_PN_DIC_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_AA_DIC_FILE: [" + GetProperty(CS_AA_DIC_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_SV_DIC_FILE: [" + GetProperty(CS_SV_DIC_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_UNIT_DIC_FILE: [" + GetProperty(CS_UNIT_DIC_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_FREQUENCY_FILE: [" + GetProperty(CS_FREQUENCY_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_W2V_IM_FILE: [" + GetProperty(CS_W2V_IM_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_W2V_OM_FILE: [" + GetProperty(CS_W2V_OM_FILE) + "]");
        buffer.append(GlobalVars.LS_STR);

        buffer.append("========== CSpell Mode ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_FUNC_MODE: [" + GetProperty(CS_FUNC_MODE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANK_MODE: [" + GetProperty(CS_RANK_MODE) + "]");
        buffer.append(GlobalVars.LS_STR);

        buffer.append("========== Detectors Setup ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_DETECTOR_RW_SPLIT_WORD_MIN_LENGTH: [" 
            + GetProperty(CS_DETECTOR_RW_SPLIT_WORD_MIN_LENGTH) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_DETECTOR_RW_SPLIT_WORD_MIN_WC: [" 
            + GetProperty(CS_DETECTOR_RW_SPLIT_WORD_MIN_WC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_DETECTOR_RW_SPLIT_1TO1_MIN_LENGTH: [" 
            + GetProperty(CS_DETECTOR_RW_1TO1_WORD_MIN_LENGTH) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_DETECTOR_RW_1TO1_WORD_MIN_WC: [" 
            + GetProperty(CS_DETECTOR_RW_1TO1_WORD_MIN_WC) + "]");
        buffer.append(GlobalVars.LS_STR);

        buffer.append("========== Candidates Setup ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_ND_MAX_SPLIT_NO: [" 
            + GetProperty(CS_CAN_ND_MAX_SPLIT_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_NW_1TO1_WORD_MAX_LENGTH: [" 
            + GetProperty(CS_CAN_NW_1TO1_WORD_MAX_LENGTH) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_NW_MAX_SPLIT_NO: [" 
            + GetProperty(CS_CAN_NW_MAX_SPLIT_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_NW_MAX_MERGE_NO: [" 
            + GetProperty(CS_CAN_NW_MAX_MERGE_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_NW_MERGE_WITH_HYPHEN: [" 
            + GetProperty(CS_CAN_NW_MERGE_WITH_HYPHEN) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_1TO1_WORD_MAX_LENGTH: [" 
            + GetProperty(CS_CAN_RW_1TO1_WORD_MAX_LENGTH) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_MAX_SPLIT_NO: [" 
            + GetProperty(CS_CAN_RW_MAX_SPLIT_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_MAX_MERGE_NO: [" 
            + GetProperty(CS_CAN_RW_MAX_MERGE_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_MERGE_WITH_HYPHEN: [" 
            + GetProperty(CS_CAN_RW_MERGE_WITH_HYPHEN) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_SHORT_SPLIT_WORD_LENGTH: [" 
            + GetProperty(CS_CAN_RW_SHORT_SPLIT_WORD_LENGTH) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_MAX_SHORT_SPLIT_WORD_NO: [" 
            + GetProperty(CS_CAN_RW_MAX_SHORT_SPLIT_WORD_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_MAX_CANDIDATE_NO: [" 
            + GetProperty(CS_CAN_MAX_CANDIDATE_NO) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_MERGE_CAND_MIN_WC: [" 
            + GetProperty(CS_CAN_RW_MERGE_CAND_MIN_WC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_SPLIT_CAND_MIN_WC: [" 
            + GetProperty(CS_CAN_RW_SPLIT_CAND_MIN_WC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_1TO1_CAND_MIN_LENGTH: [" 
            + GetProperty(CS_CAN_RW_1TO1_CAND_MIN_LENGTH) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_CAN_RW_1TO1_CAND_MAX_KEY_SIZE: [" 
            + GetProperty(CS_CAN_RW_1TO1_CAND_MAX_KEY_SIZE) + "]");
        buffer.append(GlobalVars.LS_STR);

        buffer.append("========== Rankers Setup ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_NW_S1_RANK_RANGE_FAC: [" 
            + GetProperty(CS_RANKER_NW_S1_RANK_RANGE_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_NW_S1_MIN_OSCORE: [" 
            + GetProperty(CS_RANKER_NW_S1_MIN_OSCORE) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_C_FAC: [" 
            + GetProperty(CS_RANKER_RW_1TO1_C_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_SPLIT_C_FAC: [" 
            + GetProperty(CS_RANKER_RW_SPLIT_C_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_MERGE_C_FAC: [" 
            + GetProperty(CS_RANKER_RW_MERGE_C_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_WORD_MIN_CS: [" 
            + GetProperty(CS_RANKER_RW_1TO1_WORD_MIN_CS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_CAND_CS_FAC: [" 
            + GetProperty(CS_RANKER_RW_1TO1_CAND_CS_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_CAND_MIN_CS: [" 
            + GetProperty(CS_RANKER_RW_1TO1_CAND_MIN_CS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_CAND_CS_DIST: [" 
            + GetProperty(CS_RANKER_RW_1TO1_CAND_CS_DIST) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_CAND_FS_FAC: [" 
            + GetProperty(CS_RANKER_RW_1TO1_CAND_FS_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_CAND_MIN_FS: [" 
            + GetProperty(CS_RANKER_RW_1TO1_CAND_MIN_FS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RANKER_RW_1TO1_CAND_FS_DIST: [" 
            + GetProperty(CS_RANKER_RW_1TO1_CAND_FS_DIST) + "]");
        buffer.append(GlobalVars.LS_STR);

        // Score
        buffer.append("========== Score Setup ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_ORTHO_SCORE_ED_DIST_FAC: [" 
            + GetProperty(CS_ORTHO_SCORE_ED_DIST_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_ORTHO_SCORE_PHONETIC_FAC: [" 
            + GetProperty(CS_ORTHO_SCORE_PHONETIC_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_ORTHO_SCORE_OVERLAP_FAC: [" 
            + GetProperty(CS_ORTHO_SCORE_OVERLAP_FAC) + "]");
        buffer.append(GlobalVars.LS_STR);

        // Context
        buffer.append("========== Context Setup ==========");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_W2V_SKIP_WORD: [" 
            + GetProperty(CS_W2V_SKIP_WORD) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_NW_1TO1_CONTEXT_RADIUS: [" 
            + GetProperty(CS_NW_1TO1_CONTEXT_RADIUS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_NW_SPLIT_CONTEXT_RADIUS: [" 
            + GetProperty(CS_NW_SPLIT_CONTEXT_RADIUS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_NW_MERGE_CONTEXT_RADIUS: [" 
            + GetProperty(CS_NW_MERGE_CONTEXT_RADIUS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RW_1TO1_CONTEXT_RADIUS: [" 
            + GetProperty(CS_RW_1TO1_CONTEXT_RADIUS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RW_SPLIT_CONTEXT_RADIUS: [" 
            + GetProperty(CS_RW_SPLIT_CONTEXT_RADIUS) + "]");
        buffer.append(GlobalVars.LS_STR);
        buffer.append("CS_RW_MERGE_CONTEXT_RADIUS: [" 
            + GetProperty(CS_RW_MERGE_CONTEXT_RADIUS) + "]");
        return buffer.toString();
    }
    // private methods
    private void SetConfiguration(String fName, boolean useClassPath)
    {
        try
        {
            // get config data from fName.properties in class path
            if(useClassPath == true)
            {
                configSrc_ = 
                    (PropertyResourceBundle) ResourceBundle.getBundle(fName);
            }
            else // get config data from fName (path) file
            {
                // check if fName exist
                FileInputStream file = new FileInputStream(fName);
                configSrc_ = new PropertyResourceBundle(file);
                file.close();
            }
        }
        catch (Exception e)
        {
            System.err.println("** Configuration Error: " + e.getMessage());
            System.err.println(
                "** Error: problem of opening/reading config file: '" +
                fName + "'. Use -x option to specify the config file path.");
        }
        // put properties from configSrc_ into config_
        if(configSrc_ != null)
        {
            for(Enumeration<String> e = configSrc_.getKeys(); 
                e.hasMoreElements();)
            {
                String key = e.nextElement();
                String value = configSrc_.getString(key);
                config_.put(key, value);
            }
        }
        // reset TOP_DIR
        String cSpellDir = GetProperty(CS_DIR); 
        if((cSpellDir != null)
        && (cSpellDir.equals(CS_AUTO_MODE) == true))
        {
            File file = new File(System.getProperty("user.dir"));
            String curDir = file.getAbsolutePath()
                + System.getProperty("file.separator");
            config_.put(CS_DIR, curDir);    
        }
    }
    // data member
    public static final String CS_AUTO_MODE = "CS_AUTO_MODE";

    /** key for the path of CSpell directory defined in configuration file */
    public static final String CS_DIR = "CS_DIR";
    /** key for the path of informal expression file in configuration file */
    public static final String CS_INFORMAL_EXP_FILE = "CS_INFORMAL_EXP_FILE";
    /** key for the path of check dictionary file in configuration file */
    public static final String CS_CHECK_DIC_FILES = "CS_CHECK_DIC_FILES";
    /** key for the path of suggestion dictionary file in configuration file */
    public static final String CS_SUGGEST_DIC_FILES = "CS_SUGGEST_DIC_FILES";
    /** key for the path of split word dictionary file in configuration file */
    public static final String CS_SPLIT_WORD_DIC_FILES 
        = "CS_SPLIT_WORD_DIC_FILES";
    /** key for the path of multiwords dictionary file in configuration file */
    public static final String CS_MW_DIC_FILE = "CS_MW_DIC_FILE";
    /** key for the path of properNoun dictionary file in configuration file */
    public static final String CS_PN_DIC_FILE = "CS_PN_DIC_FILE";
    /** key for the path of Abb/Acr dictionary file in configuration file */
    public static final String CS_AA_DIC_FILE = "CS_AA_DIC_FILE";
    /** key for the path of spVar dictionary file in configuration file */
    public static final String CS_SV_DIC_FILE = "CS_SV_DIC_FILE";
    /** key for the path of unit file in configuration file */
    public static final String CS_UNIT_DIC_FILE = "CS_UNIT_DIC_FILE";
    /** key for the path of frequency file in configuration file */
    public static final String CS_FREQUENCY_FILE = "CS_FREQUENCY_FILE";
    /** key for the path of word2Vec files in configuration file */
    // input matrix between inputs and hidden layer in word2Vec
    public static final String CS_W2V_IM_FILE = "CS_W2V_IM_FILE";
    // output matrix between hidden layer and output in word2Vec
    public static final String CS_W2V_OM_FILE = "CS_W2V_OM_FILE";

    // function mode
    public static final String CS_FUNC_MODE = "CS_FUNC_MODE";
    // NW, 1To1 and Split rank mode
    public static final String CS_RANK_MODE = "CS_RANK_MODE";

    // detector
    /** key for the min split word length */
    public static final String CS_DETECTOR_RW_SPLIT_WORD_MIN_LENGTH 
        = "CS_DETECTOR_RW_SPLIT_WORD_MIN_LENGTH";
    /** key for the min split word word count */
    public static final String CS_DETECTOR_RW_SPLIT_WORD_MIN_WC 
        = "CS_DETECTOR_RW_SPLIT_WORD_MIN_WC";
    /** key for the min 1-to-1 word length */
    public static final String CS_DETECTOR_RW_1TO1_WORD_MIN_LENGTH 
        = "CS_DETECTOR_RW_1TO1_WORD_MIN_LENGTH";
    /** key for the min 1-to-1 word word count */
    public static final String CS_DETECTOR_RW_1TO1_WORD_MIN_WC 
        = "CS_DETECTOR_RW_1TO1_WORD_MIN_WC";

    // Candidates    
    /** key for the max candidates no */
    public static final String CS_CAN_MAX_CANDIDATE_NO 
        = "CS_CAN_MAX_CANDIDATE_NO";
    /** key for the max recursive split  no for ND splitter */
    public static final String CS_CAN_ND_MAX_SPLIT_NO 
        = "CS_CAN_ND_MAX_SPLIT_NO";
    /** key for the max length of word for non-word 1To1 */
    public static final String CS_CAN_NW_1TO1_WORD_MAX_LENGTH 
        = "CS_CAN_NW_1TO1_WORD_MAX_LENGTH";
    /** key for the max space split no for non-word split */
    public static final String CS_CAN_NW_MAX_SPLIT_NO 
        = "CS_CAN_NW_MAX_SPLIT_NO";
    /** key for the max space merge no for non-word merge */
    public static final String CS_CAN_NW_MAX_MERGE_NO 
        = "CS_CAN_NW_MAX_MERGE_NO";
    /** key for the non-word merge with hyphen */
    public static final String CS_CAN_NW_MERGE_WITH_HYPHEN 
        = "CS_CAN_NW_MERGE_WITH_HYPHEN";
    /** key for the max length of word for real-word 1To1 */
    public static final String CS_CAN_RW_1TO1_WORD_MAX_LENGTH 
        = "CS_CAN_RW_1TO1_WORD_MAX_LENGTH";
    /** key for the max space split no for real-word split */
    public static final String CS_CAN_RW_MAX_SPLIT_NO 
        = "CS_CAN_RW_MAX_SPLIT_NO";
    /** key for the max space merge no for real-word merge */
    public static final String CS_CAN_RW_MAX_MERGE_NO 
        = "CS_CAN_RW_MAX_MERGE_NO";
    /** key for the real-word merge with hyphen */
    public static final String CS_CAN_RW_MERGE_WITH_HYPHEN 
        = "CS_CAN_RW_MERGE_WITH_HYPHEN";
    /** key for the rw short split word length */
    public static final String CS_CAN_RW_SHORT_SPLIT_WORD_LENGTH 
        = "CS_CAN_RW_SHORT_SPLIT_WORD_LENGTH";
    /** key for the max no of rw short split word */
    public static final String CS_CAN_RW_MAX_SHORT_SPLIT_WORD_NO 
        = "CS_CAN_RW_MAX_SHORT_SPLIT_WORD_NO";
    /** key for the min wc of rw merge candidate */
    public static final String CS_CAN_RW_MERGE_CAND_MIN_WC 
        = "CS_CAN_RW_MERGE_CAND_MIN_WC";
    /** key for the min wc of rw split candidate */
    public static final String CS_CAN_RW_SPLIT_CAND_MIN_WC 
        = "CS_CAN_RW_SPLIT_CAND_MIN_WC";
    /** key for the min wc of rw 1-to-1 candidate */
    public static final String CS_CAN_RW_1TO1_CAND_MIN_LENGTH 
        = "CS_CAN_RW_1TO1_CAND_MIN_LENGTH";
    /** key for the min wc of rw 1-to-1 candidate */
    public static final String CS_CAN_RW_1TO1_CAND_MIN_WC 
        = "CS_CAN_RW_1TO1_CAND_MIN_WC";
    /** key for the max key size of rw 1-to-1 candidate */
    public static final String CS_CAN_RW_1TO1_CAND_MAX_KEY_SIZE 
        = "CS_CAN_RW_1TO1_CAND_MAX_KEY_SIZE";

    // rankers
    /** key for the non-word Split and 1-to-1 rank range factor */
    public static final String CS_RANKER_NW_S1_RANK_RANGE_FAC 
        = "CS_RANKER_NW_S1_RANK_RANGE_FAC";
    /** key for the non-word Split and 1-to-1 min oScore */
    public static final String CS_RANKER_NW_S1_MIN_OSCORE 
        = "CS_RANKER_NW_S1_MIN_OSCORE";
    /** key for the real-word 1-to-1 context score confidence factor */
    public static final String CS_RANKER_RW_1TO1_C_FAC 
        = "CS_RANKER_RW_1TO1_C_FAC";
    /** key for the real-word merge context score confidence factor */
    public static final String CS_RANKER_RW_SPLIT_C_FAC 
        = "CS_RANKER_RW_SPLIT_C_FAC";
    /** key for the real-word merge context score confidence factor */
    public static final String CS_RANKER_RW_MERGE_C_FAC 
        = "CS_RANKER_RW_MERGE_C_FAC";
    /** key for the real-word 1to1 word min context score */
    public static final String CS_RANKER_RW_1TO1_WORD_MIN_CS 
        = "CS_RANKER_RW_1TO1_WORD_MIN_CS";
    /** key for the real-word 1to1 cand context score factor */
    public static final String CS_RANKER_RW_1TO1_CAND_CS_FAC 
        = "CS_RANKER_RW_1TO1_CAND_CS_FAC";
    /** key for the real-word 1to1 word min context score */
    public static final String CS_RANKER_RW_1TO1_CAND_MIN_CS 
        = "CS_RANKER_RW_1TO1_CAND_MIN_CS";
    /** key for the real-word 1to1 cand context score dist */
    public static final String CS_RANKER_RW_1TO1_CAND_CS_DIST 
        = "CS_RANKER_RW_1TO1_CAND_CS_DIST";
    /** key for the real-word 1to1 cand frequency score factor */
    public static final String CS_RANKER_RW_1TO1_CAND_FS_FAC 
        = "CS_RANKER_RW_1TO1_CAND_FS_FAC";
    /** key for the real-word 1to1 cand min frequency score */
    public static final String CS_RANKER_RW_1TO1_CAND_MIN_FS 
        = "CS_RANKER_RW_1TO1_CAND_MIN_FS";
    /** key for the real-word 1to1 cand frequency score dist */
    public static final String CS_RANKER_RW_1TO1_CAND_FS_DIST 
        = "CS_RANKER_RW_1TO1_CAND_FS_DIST";

    // score
    /** key for orthographic score weight factor of edit-distance score */
    public static final String CS_ORTHO_SCORE_ED_DIST_FAC
        = "CS_ORTHO_SCORE_ED_DIST_FAC";
    /** key for orthographic score weight factor of phonetic score */
    public static final String CS_ORTHO_SCORE_PHONETIC_FAC
        = "CS_ORTHO_SCORE_PHONETIC_FAC";
    /** key for orthographic score weight factor of overlap score */
    public static final String CS_ORTHO_SCORE_OVERLAP_FAC
        = "CS_ORTHO_SCORE_OVERLAP_FAC";

    // context
    // boolean flag for skip word in the context that does not have word2vec
    public static final String CS_W2V_SKIP_WORD = "CS_W2V_SKIP_WORD";
    /** key for the nw 1-to-1 context radius, window size = 2*contextRadius+1 */
    public static final String CS_NW_1TO1_CONTEXT_RADIUS 
        = "CS_NW_1TO1_CONTEXT_RADIUS";
    /** key for the nw split context radius */    
    public static final String CS_NW_SPLIT_CONTEXT_RADIUS 
        = "CS_NW_SPLIT_CONTEXT_RADIUS";
    /** key for the nw merge context radius */    
    public static final String CS_NW_MERGE_CONTEXT_RADIUS 
        = "CS_NW_MERGE_CONTEXT_RADIUS";
    /** key for the rw 1-to-1 context radius */    
    public static final String CS_RW_1TO1_CONTEXT_RADIUS 
        = "CS_RW_1TO1_CONTEXT_RADIUS";
    /** key for the rw split context radius */    
    public static final String CS_RW_SPLIT_CONTEXT_RADIUS 
        = "CS_RW_SPLIT_CONTEXT_RADIUS";
    /** key for the rw merge context radius */    
    public static final String CS_RW_MERGE_CONTEXT_RADIUS 
        = "CS_RW_MERGE_CONTEXT_RADIUS";
    
    // private data member
    private PropertyResourceBundle configSrc_ = null;
    private Hashtable<String, String> config_ =
        new Hashtable<String, String>();    // the real config vars
}
