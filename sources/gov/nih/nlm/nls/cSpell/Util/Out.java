package gov.nih.nlm.nls.cSpell.Util;
import java.io.*;
/*****************************************************************************
* This class is the output for cSpell.
*
* <p><b>History:</b>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class Out
{
    // public methods
    /**
    * Print message to a designated output buffer
    *
    * @param   outWriter  the designated outputs 
    * @param   msg  massage for printing out
    * @param   fileOutput  a boolean flag for out, true: file, false: std
    * @param   toStringFlag  a boolean flag for sending output to a String
    *
    * @throws IOException if excaption caught when print out
    */
    public void Print(BufferedWriter outWriter, String msg,
        boolean fileOutput, boolean toStringFlag) throws IOException
    {
        if(toStringFlag == true)    // output to a String
        {
            if(outString_ == null)
            {
                outString_ = msg; 
            }
            else
            {
                outString_ += msg;
            }
        }
        else if(fileOutput == false)  // std output
        {
            outWriter.write(msg);
            outWriter.flush();
        }
        else
        {
            outWriter.write(msg);
        }
    }
    /**
    * Print message and append a linebreaker to a designated output buffer
    *
    * @param   outWriter  the designated outputs 
    * @param   msg  massage for printing out
    * @param   fileOutFlag  a boolean flag for out, true: file, false: std
    * @param   toStringFlag  a boolean flag for sending output to a String
    *
    * @throws IOException if excaption caught when print out
    */
    public void Println(BufferedWriter outWriter, String msg, 
        boolean fileOutFlag, boolean toStringFlag) throws IOException
    {
        if(toStringFlag == true)    // output to a String
        {
            StringBuffer buffer = new StringBuffer();
            if(outString_ == null)
            {
                buffer.append(msg);
                buffer.append(System.getProperty("line.separator").toString());
                outString_ = buffer.toString();
            }
            else
            {
                buffer.append(outString_);
                buffer.append(msg);
                buffer.append(System.getProperty("line.separator").toString());
                outString_ = buffer.toString();
            }
        }
        else if(fileOutFlag == false)  // std output
        {
            outWriter.write(msg);
            outWriter.newLine();
            outWriter.flush();
        }
        else // file output
        {
            outWriter.write(msg);
            outWriter.newLine();
        }
    }
    /**
    * Reset the output string
    */
    public void ResetOutString()
    {
        outString_ = null;
    }
    /**
    * Get the output String
    *
    * @return output string
    */
    public String GetOutString()
    {
        return outString_;
    }
    // data member
    private String outString_ = null;
}
