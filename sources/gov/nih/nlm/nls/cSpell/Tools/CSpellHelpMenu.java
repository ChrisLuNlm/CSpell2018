package gov.nih.nlm.nls.cSpell.Tools;
import java.io.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class prints out help menu of CSpell.
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
public class CSpellHelpMenu
{
    // public method
    public CSpellHelpMenu(BufferedWriter bw, boolean fileOutput, Out out)
    {
        bw_ = bw;
        fileOutput_ = fileOutput;
        out_ = out;
    }
    // protected method
    protected static void FunctionHelp(BufferedWriter bw, boolean fileOutFlag,
        Out out)
    {
        if(cSpellHelpMenu_ == null)
        {
            cSpellHelpMenu_ = new CSpellHelpMenu(bw, fileOutFlag, out);
        }
        // print out the usage
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("Description:");
        cSpellHelpMenu_.Println("  Consumer Spelling Correction Tool.");
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("Function Options:");
        cSpellHelpMenu_.Println("  -f:nd    Set function to non-dictionary.");
        cSpellHelpMenu_.Println("  -f:nw1   Set function to non-word 1-to-1.");
        cSpellHelpMenu_.Println("  -f:nws   Set function to non-word split.");
        cSpellHelpMenu_.Println("  -f:nwm   Set function to non-word merge.");
        cSpellHelpMenu_.Println("  -f:nws1  Set function to non-word split and 1-to-1.");
        cSpellHelpMenu_.Println("  -f:nw    Set function to non-word.");
        cSpellHelpMenu_.Println("  -f:rw1   Set function to non-word + real-word 1-to-1.");
        cSpellHelpMenu_.Println("  -f:rws   Set function to non-word + real-word split.");
        cSpellHelpMenu_.Println("  -f:rwm   Set function to non-word + real-word merge.");
        cSpellHelpMenu_.Println("  -f:rwms  Set function to non-word + real-word merge and split.");
        cSpellHelpMenu_.Println("  -f:rw    Set function to non-word + real-word (default).");
    }
    protected static void RankHelp(BufferedWriter bw, boolean fileOutFlag,
        Out out)
    {
        if(cSpellHelpMenu_ == null)
        {
            cSpellHelpMenu_ = new CSpellHelpMenu(bw, fileOutFlag, out);
        }
        // print out the usage
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("Description:");
        cSpellHelpMenu_.Println("  Consumer Spelling Correction Tool.");
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("NW-Spelling Ranking Options:");
        cSpellHelpMenu_.Println("  -r:o     Set ranking mode to orthographic similarity.");
        cSpellHelpMenu_.Println("  -r:f     Set ranking mode to corpus frequency.");
        cSpellHelpMenu_.Println("  -r:w     Set ranking mode to word context.");
        cSpellHelpMenu_.Println("  -r:n     Set ranking mode to nosiy channel.");
        cSpellHelpMenu_.Println("  -r:e     Set ranking mode to ensemble method. This is the default");
        cSpellHelpMenu_.Println("  -r:c     Set ranking mode to cSpell (default).");
    }
    protected static void MainHelp(BufferedWriter bw, boolean fileOutFlag,
        Out out)
    {
        if(cSpellHelpMenu_ == null)
        {
            cSpellHelpMenu_ = new CSpellHelpMenu(bw, fileOutFlag, out);
        }
        // print out the usage
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("Synopsis:");
        cSpellHelpMenu_.Println("  cSpell [options]");
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("Description:");
        cSpellHelpMenu_.Println("  Consumer Spelling Correction Tool.");
        cSpellHelpMenu_.Println("");
        cSpellHelpMenu_.Println("Options:");
        cSpellHelpMenu_.Println("  -ci      Print configuration information.");
        cSpellHelpMenu_.Println("  -d       Turn on debug mode.");
        cSpellHelpMenu_.Println("  -f:nd    Set function to non-dictionary.");
        cSpellHelpMenu_.Println("  -f:nw    Set function to non-word.");
        cSpellHelpMenu_.Println("  -f:rw    Set function to real-word (default).");
        cSpellHelpMenu_.Println("  -f:h     Print function help information.");
        cSpellHelpMenu_.Println("  -h       Print program help information (this is it).");
        cSpellHelpMenu_.Println("  -hs      Print option's hierarchy structure."); 
        cSpellHelpMenu_.Println("  -i:STR   Specify input file name.  The default is screen input.");
        cSpellHelpMenu_.Println("  -mcn:INT Specify Max. candidate no.");
        cSpellHelpMenu_.Println("  -o:STR   Specify output file name.  The default is screen output.");
        cSpellHelpMenu_.Println("  -p       Show the prompt. The default is no prompt.");
        cSpellHelpMenu_.Println("  -r:h     Print NW-Spelling ranking help information.");
        cSpellHelpMenu_.Println("  -si      Show input text.");
        cSpellHelpMenu_.Println("  -t       Show token operations.");
        cSpellHelpMenu_.Println("  -v       Print the version of cSpell.");
        cSpellHelpMenu_.Println("  -x:STR   Loading an alternative configuration file.");
    }
    // private method
    public void Println(String text)
    {
        try
        {
            out_.Println(bw_, text, fileOutput_, false);
        }
        catch (IOException e)
        {
            System.err.println("**ERR@CSpellHelpMenu.PrintlnHelpMenu(" 
                + text + ")");
        }
    }
    // data member
    private static CSpellHelpMenu cSpellHelpMenu_ = null;
    private BufferedWriter bw_ = null;
    private boolean fileOutput_ = false;
    private Out out_ = null;
}
