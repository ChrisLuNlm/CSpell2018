package gov.nih.nlm.nls.cSpell.Tools;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.CmdLineSyntax.*;
import gov.nih.nlm.nls.cSpell.Api.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/***************************************************************
* This class is the CSpell, with cmdLine interface.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
***************************************************************/
public class CSpell extends CSpellSystemOption
{
    // public constructor
    /**
    * Create a CSpell tool object by specifying option.
    * 
    * @param option    option object for command line interface
    * @param out output
    */
    public CSpell(Option option, Out out)
    {
        super();
        Init(out, option);
    }
    // Test driver
    public static void main(String[] args)
    {
        // PreProscess: get option form input args
        Option option = Option.GetOptonByArgs(args);
        // define the system option flag & argument
        Out out = new Out();
        CSpell cSpell = new CSpell(option, out);
        // Process: check the input option, if legal, process the input term
        if(cSpell.IsLegalOption(option) == true)
        {
            try
            {
                while(true) // Loop forever
                {
                    // execute command according to option & argument
                    if(cSpell.Process(out) == false)
                    {
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                System.err.println("** Err@CSpell.Process(): " 
                    + e.getMessage());
            }
        }
        else
        {
            CSpellHelpMenu.MainHelp(cSpell.GetOutWriter(), cSpell.GetFileOutFlag(), out);
        }
        // Post Process: Close
        cSpell.Close();
    }
    /**
    * Process the cSpell correction
    *
    * @param out output
    * @return  true or false as the process is to be executed or exit
    * @throws IOException throws IOException when exception happens
    */
    public boolean Process(Out out) throws IOException
    {
        return Process(false, out);
    }
    /**
    * Get the boolean flag to indicate if the option is legal or not.
    *
    * @return  a boolean flag to indicate if the option is legal or not.
    */
    public boolean IsLegalOption(Option option)
    {
        boolean isLegalOption =
            SystemOption.CheckSyntax(option, GetOption(), false, true);
        return isLegalOption;
    }
    /**
    * Close the input file, output file, and database.  This mehtod must 
    * be called before exit. 
    */
    public void Close()
    {
        try
        {
            // close files
            if((outWriter_ != null) && (fileOutFlag_ == true))
            {
                outWriter_.close();
            }
            if(inReader_ != null)
            {
                inReader_.close();
            }
        }
        catch (Exception e)
        {
        }
        // close db connection
        if(cSpellApi_ != null)
        {
            // not used since no DB connection
            cSpellApi_.Close();
        }
    }
    // protected methods
    // define legal option flags for cSpell, from super()
    protected void DefineFlag()
    {
        // define all option flags & arguments by giving a option string
        String flagStr = "-ci -d -f:h:nd:nw1:nws:nwm:nws1:nw:rw1:rws:rwm:rwms:rw -h -hs -i:STR -mcn:INT -o:STR -p -r:h:o:f:w:n:e:c -si -t -v -x:STR";
        // init the system option
        systemOption_ = new Option(flagStr);
        // Add the full name for flags
        systemOption_.SetFlagFullName("-ci", "Show_Config_Info");
        systemOption_.SetFlagFullName("-d", "Debug_Mode");
        systemOption_.SetFlagFullName("-f:h", "Function_Mode_Help");
        systemOption_.SetFlagFullName("-f:nd", "Set_Correct_to_Non_Dictionary");
        systemOption_.SetFlagFullName("-f:nw1", "Set_Correct_to_NW_1_to_1");
        systemOption_.SetFlagFullName("-f:nws", "Set_Correct_to_NW_Split");
        systemOption_.SetFlagFullName("-f:nwm", "Set_Correct_to_NW_Merge");
        systemOption_.SetFlagFullName("-f:nws1", "Set_Correct_to_NW_S_1");
        systemOption_.SetFlagFullName("-f:nw", "Set_Correct_to_NonWord");
        systemOption_.SetFlagFullName("-f:rw1", "Set_Correct_to_RW_1_to_1");
        systemOption_.SetFlagFullName("-f:rws", "Set_Correct_to_RW_1_Split");
        systemOption_.SetFlagFullName("-f:rwm", "Set_Correct_to_RW_Merge");
        systemOption_.SetFlagFullName("-f:rwms", "Set_Correct_to_RW_M_S");
        systemOption_.SetFlagFullName("-f:rw", "Set_Correct_to_RealWord");
        systemOption_.SetFlagFullName("-h", "Help");
        systemOption_.SetFlagFullName("-hs", "Hierarchy_Struture");
        systemOption_.SetFlagFullName("-i:STR", "Input_File");
        systemOption_.SetFlagFullName("-mcn:INT", "Max_Candidate_No");
        systemOption_.SetFlagFullName("-o:STR", "Output_File");
        systemOption_.SetFlagFullName("-p", "Show_Prompt");
        systemOption_.SetFlagFullName("-r:h", "NW_1To1_Ranking_Mode_Help");
        systemOption_.SetFlagFullName("-r:o", "Set_Rank_to_Orthographic");
        systemOption_.SetFlagFullName("-r:f", "Set_Rank_to_Frequency");
        systemOption_.SetFlagFullName("-r:w", "Set_Rank_to_Word_Embedding");
        systemOption_.SetFlagFullName("-r:n", "Set_Rank_to_Noisy_Channel");
        systemOption_.SetFlagFullName("-r:e", "Set_Rank_to_Ensemble (TBD)");
        systemOption_.SetFlagFullName("-r:c", "Set_Rank_to_CSpell");
        systemOption_.SetFlagFullName("-si", "Show_Input");
        systemOption_.SetFlagFullName("-t", "Token_Operation");
        systemOption_.SetFlagFullName("-v", "Version");
        systemOption_.SetFlagFullName("-x:STR", "Load_Configuration_File");
    }
    // execute command for each option
    protected void ExecuteCommand(OptionItem optionItem, Option systemOption,
        Out out)
    {
        OptionItem nameItem 
            = OptionUtility.GetItemByName(optionItem, systemOption, false);
        if(CheckOption(nameItem, "-ci") == true)    
        {
            try
            {
                // get config file from environment variable
                boolean useClassPath = false;
                String configFile = configFile_;
                if(configFile == null)
                {
                    useClassPath = true;
                    configFile = "data.Config.cSpell";
                }
                Configuration conf = conf_;
                if(conf == null)
                {
                    conf = new Configuration(configFile, useClassPath);
                }
                out.Println(outWriter_, conf.GetInformation(), fileOutFlag_,
                    false);
            }
            catch (IOException e) { }
            runFlag_ = false;
        }
        else if(CheckOption(nameItem, "-d") == true)
        {
            debugFlag_ = true;
        }
        else if(CheckOption(nameItem, "-f:h") == true)
        {
            CSpellHelpMenu.FunctionHelp(outWriter_, fileOutFlag_, out);
            runFlag_ = false;
        }
        else if(CheckOption(nameItem, "-f:nd") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_ND;
        }
        else if(CheckOption(nameItem, "-f:nw1") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_NW_1;
        }
        else if(CheckOption(nameItem, "-f:nws") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_NW_S;
        }
        else if(CheckOption(nameItem, "-f:nwm") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_NW_M;
        }
        else if(CheckOption(nameItem, "-f:nws1") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_NW_S_1;
        }
        else if(CheckOption(nameItem, "-f:nw") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_NW_A;
        }
        else if(CheckOption(nameItem, "-f:rw1") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_RW_1;
        }
        else if(CheckOption(nameItem, "-f:rws") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_RW_S;
        }
        else if(CheckOption(nameItem, "-f:rwm") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_RW_M;
        }
        else if(CheckOption(nameItem, "-f:rwms") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_RW_M_S;
        }
        else if(CheckOption(nameItem, "-f:rw") == true)
        {
            funcMode_ = CSpellApi.FUNC_MODE_RW_A;
        }
        else if(CheckOption(nameItem, "-h") == true)
        {
            CSpellHelpMenu.MainHelp(outWriter_, fileOutFlag_, out);
            runFlag_ = false;
        }
        else if(CheckOption(nameItem, "-hs") == true)
        {
            systemOption.PrintOptionHierachy();
            runFlag_ = false;
        }
        else if(CheckOption(nameItem, "-i:STR") == true)
        {
            String inFile = nameItem.GetOptionArgument();
            if(inFile != null)
            {
                try
                {
                    inReader_ = new BufferedReader(new InputStreamReader(
                        new FileInputStream(inFile), "UTF-8"));
                }
                catch (IOException e)
                {
                    runFlag_ = false;
                    System.err.println(
                        "**Err@CSpell.ExecuteCommand( ): problem of reading a file " + inFile);
                }
            }
        }
        else if(CheckOption(nameItem, "-r:h") == true)
        {
            CSpellHelpMenu.RankHelp(outWriter_, fileOutFlag_, out);
            runFlag_ = false;
        }
        else if(CheckOption(nameItem, "-r:o") == true)
        {
            rankMode_ = CSpellApi.RANK_MODE_ORTHOGRAPHIC;
        }
        else if(CheckOption(nameItem, "-r:f") == true)
        {
            rankMode_ = CSpellApi.RANK_MODE_FREQUENCY;
        }
        else if(CheckOption(nameItem, "-r:w") == true)
        {
            rankMode_ = CSpellApi.RANK_MODE_CONTEXT;
        }
        else if(CheckOption(nameItem, "-r:n") == true)
        {
            rankMode_ = CSpellApi.RANK_MODE_NOISY_CHANNEL;
        }
        else if(CheckOption(nameItem, "-r:e") == true)
        {
            rankMode_ = CSpellApi.RANK_MODE_ENSEMBLE;
        }
        else if(CheckOption(nameItem, "-r:c") == true)
        {
            rankMode_ = CSpellApi.RANK_MODE_CSPELL;
        }
        else if(CheckOption(nameItem, "-mcn:INT") == true)
        {
            maxCandNo_ = Integer.parseInt(nameItem.GetOptionArgument());
        }
        else if(CheckOption(nameItem, "-o:STR") == true)
        {
            String outFile = nameItem.GetOptionArgument();
            if(outFile != null)
            {
                try
                {
                    outWriter_ = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outFile), "UTF-8"));
                    fileOutFlag_ = true;    
                }
                catch (IOException e)
                {
                    runFlag_ = false;
                    System.err.println(
                        "**Err@CSpell.ExecuteCommand(): problem of writing a file " + outFile);
                }
            }
        }
        else if(CheckOption(nameItem, "-p") == true)
        {
            promptFlag_ = true;
        }
        else if(CheckOption(nameItem, "-si") == true)
        {
            showInput_ = true;
        }
        else if(CheckOption(nameItem, "-t") == true)
        {
            tokenFlag_ = true;
        }
        else if(CheckOption(nameItem, "-v") == true)
        {
            try
            {
                String releaseStr = "cSpell." + GlobalVars.YEAR;
                out.Println(outWriter_, releaseStr, fileOutFlag_, false);
            }
            catch (IOException e) { }
            runFlag_ = false;
        }
        else if(CheckOption(nameItem, "-x:STR") == true)
        {
            configFile_ = nameItem.GetOptionArgument();
        }
    }
    // private methods
    private boolean Process(boolean toStringFlag, Out out) throws IOException
    {
        // check RunFlag
        if(runFlag_ == false)
        {
            return false;
        }
        // prompt interface
        if(promptFlag_ == true)
        {
            GetPrompt(out);
        }
        // read line from System.in or a file
        String line = null;
        if(inReader_ == null)
        {
            inReader_ = new BufferedReader(new InputStreamReader(System.in,
                "UTF-8"));
        }
        line = inReader_.readLine();
        // check if the input is a command for quiting
        if((line == null) || (quitStrList_.contains(line)))
        {
            return false;
        }
        // process the line
        ProcessLine(line, toStringFlag, out);
        return true;
    }
    // process each line at a time
    private void ProcessLine(String line, boolean toStringFlag, Out out) 
        throws IOException
    {
        String inText = line;
        String outText = line;
        // update option setting to cSpellApi
        if(maxCandNo_ > 0)
        {
            cSpellApi_.SetCanMaxCandNo(maxCandNo_);
        }
        // process spelling correction: main process
        ArrayList<TokenObj> outTokenList = cSpellApi_.ProcessToTokenObj(
            inText, funcMode_, rankMode_, debugFlag_);

        if(showInput_ == true)
        {
            outText = line + GlobalVars.FS_STR 
                + TextObj.TokenListToText(outTokenList);
        }
        else
        {
            outText = TextObj.TokenListToText(outTokenList);
        }

        if(outText != null)
        {
            out.Println(outWriter_, outText, fileOutFlag_, false);
        }
        // print token operation details at the end
        if(tokenFlag_ == true)
        {
            String dStr = TextObj.TokenListToOperationDetailStr(
                outTokenList);
            out.Println(outWriter_, dStr, fileOutFlag_, false);
        }
    }
    private void GetPrompt(Out out) throws IOException
    {
        out.Println(outWriter_, promptStr_, fileOutFlag_, false);
    }
    // This method must be call after the optionStr is set and before Process
    private void Init(Out out, Option option)
    {
        try
        {
            outWriter_ = new BufferedWriter(new OutputStreamWriter(
                System.out, "UTF-8"));
        }
        catch (IOException e)
        {
            System.err.println("**Err@CSpell.Init( ): problem of opening Std-out.");
        }
        // init cmdLine options, must put before InitConfigVars()
        InitCmdLineOption(option, out);
        // check if the input option legit before init the cSpellApi
        if(IsLegalOption(option) == true)
        {
            // Init config vars
            InitConfigVars();
            // Init Api object: init db conn in the object
            // check if it continue to run the prompt
            if(runFlag_ == true)
            {
                cSpellApi_ = new CSpellApi(configFile_, debugFlag_);
            }
        }
    }
    private void InitConfigVars()
    {
        // this method must called after InitCmdLineOption() for -x: to work
        // get config file from environment variable
        /** this is used when pass the config obj to cSpell Api
        boolean useClassPath = false;
        if(configFile_ == null)
        {
            useClassPath = true;
            configFile_ = "data.Config.cSpell";
        }
        // read in configuration file
        conf_ = new Configuration(configFile_, useClassPath);
        // overwrite properties to configuration
        if(properties_ != null)
        {
            conf_.OverwriteProperties(properties_);
        }
        **/
        // Init varaibles that defines in configuration file
        //if(cSpellApi_ == null)
        
        // set default prompt
        if(Platform.IsWindow() == true)
        {
            promptStr_ =
            "- Please input a term (type \"Ctl-z\" then \"Enter\" to quit) >";
        }
        else
        {
            promptStr_ = "- Please input a term (type \"Ctl-d\" to quit) >";
        }
    }
    private BufferedWriter GetOutWriter()
    {
        return outWriter_;
    }
    private boolean GetFileOutFlag()
    {
        return fileOutFlag_;
    }
    // data member
    // standard cmdLine api vars
    protected Vector<String> quitStrList_ = new Vector<String>();  // str to quit
    protected BufferedReader inReader_ = null;   // infile buffer
    protected BufferedWriter outWriter_ = null;    // outfile buffer
    private boolean fileOutFlag_ = false;    // flag for file output
    private String promptStr_ = null;
    // cutomerize cmdLine api vars
    private boolean promptFlag_ = false;     // flag for display prompt
    private boolean runFlag_ = true;         // flag for running cSpell
    // configuration related vars
    private String configFile_ = null;
    private Configuration conf_ = null;
    private boolean debugFlag_ = false;        // flag for debug print mode
    private boolean tokenFlag_ = false;    // flag for toekn operation
    private int maxCandNo_ = 0;            // max Candidate no
    // Api related
    private CSpellApi cSpellApi_ = null;      // cSpell api
    private int rankMode_ = CSpellApi.RANK_MODE_CSPELL;        // ranking 
    //function: NON-Dictionary + NW-RW: Merge, Split, 1-To-1
    private int funcMode_ = CSpellApi.FUNC_MODE_RW_A;
    private boolean showInput_ = false;
}
