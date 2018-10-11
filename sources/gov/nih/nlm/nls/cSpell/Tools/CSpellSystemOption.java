package gov.nih.nlm.nls.cSpell.Tools;
import java.util.*;
import gov.nih.nlm.nls.cSpell.CmdLineSyntax.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class provides adaptor to SystemOption from CmdLineSyntax package.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public abstract class CSpellSystemOption extends SystemOption
{
    // public constructor
    /**
    * Create a cSpell object (default).
    */
    public CSpellSystemOption()
    {
        super();
    }
    /**
    * Get the boolean flag to indicate if the option is legal or not.
    *
    * @param option command line option object
    * @return  a boolean flag to indicate if the option is legal or not.
    */
    public boolean IsLegalOption(Option option)
    {
        boolean isLegalOption =
            SystemOption.CheckSyntax(option, GetOption(), false, true);
        return isLegalOption;
    }
    // protected methods
    // update Db according to version, not used until Db is implemented
    protected boolean SetPropertiesInConfig(String version)
    {
        boolean flag = true;
        /*** TBD
        if(version.equals("2017") == true)
        {
            flag = true;
            properties_ = new Hashtable<String, String>();
            properties_.put("DATA_DIR", "data/");
            properties_.put("DB_NAME", "cSpell2017");
        }
        else    // illegal version
        {
            flag = false;
        }
        ***/
        return flag;
    }
    protected static String GetOptionStr(String[] args)
    {
        // define the default option for showing help menu
        String optionStr = new String();
        // capture option form args
        if(args.length > 0)
        {
            optionStr = "";
            for(int i = 0; i < args.length; i++)
            {
                if(i == 0)
                {
                    optionStr = args[i];
                }
                else
                {
                    optionStr += (" " + args[i]);
                }
            }
        }
        return optionStr;
    }
    // This method is used to get a local copy of config
    protected Configuration GetConfig(String configFile)
    {
        // get config file from environment variable
        boolean useClassPath = false;
        if(configFile == null)    // use the default config file if not specified
        {
            useClassPath = true;
            configFile = "data.Config.cSpell";
        }
        Configuration conf = new Configuration(configFile, useClassPath);
        // overwrite properties to configuration
        if(properties_ != null)
        {
            conf.OverwriteProperties(properties_);
        }
        return conf;
    }
    protected void InitCmdLineOption(Option option, Out out)
    {
         // options to args
        Vector<String> args = GetOptions(option.GetOptionStr());
        // go through all options
        for(int i = 0; i < args.size(); i++)
        {
            String temp = args.elementAt(i);
            // Decode input option to form options
            ExecuteCommands(option, GetOption(), out);
        }
    }
    protected static Vector<String> GetOptions(String inStr)
    {
        Vector<String> out = new Vector<String>();
        StringTokenizer buf = new StringTokenizer(inStr, " \t");
        while(buf.hasMoreTokens() == true)
        {
            out.addElement(buf.nextToken());
        }
        return out;
    }
    // data member
    protected boolean runFlag_ = true;      // flag for running Program
    // configuration related vars
    protected Hashtable<String, String> properties_ = null;  // overwrite properties
}
