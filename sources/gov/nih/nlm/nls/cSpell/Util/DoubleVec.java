package gov.nih.nlm.nls.cSpell.Util;
import java.util.*;
import java.util.stream.Collectors;
/*****************************************************************************
* This is a simple implementation of Vector operations in double.
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
public class DoubleVec
{
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java DoubleVec");
            System.exit(0);
        }
        // test
        Test();
    }
    // private constructor
    // init a vector with all element to 0.0
    public DoubleVec(int size)
    {
        vec_ = new double[size];
    }
    public DoubleVec(double[] vec)
    {
        // use clone, not = to avoid the assign the reference
        vec_ = vec.clone();
    }
    // index start from 0
    public double GetElement(int index)
    {
        double out = 0.0d;
        if((vec_ != null)
        && ((index >= 0) && (index < vec_.length)))
        {
            out = vec_[index];
        }
        return out;
    }
    public void SetElement(double element, int index)
    {
        if((vec_ != null)
        && ((index >= 0) && (index < vec_.length)))
        {
            vec_[index] = element;
        }
    }
    public double[] GetVec()
    {
        return vec_;
    }
    public int GetSize()
    {
        return vec_.length;
    }
    public void Add(DoubleVec vec)
    {
        if((vec != null)
        && (GetSize() == vec.GetSize()))
        {
            for(int i = 0; i < GetSize(); i++)
            {
                vec_[i] = vec_[i] + vec.GetElement(i);
            }
        }
    }
    public void Minus(DoubleVec vec)
    {
        if((vec != null)
        && (GetSize() == vec.GetSize()))
        {
            for(int i = 0; i < GetSize(); i++)
            {
                vec_[i] = vec_[i] - vec.GetElement(i);
            }
        }
    }
    public void Divide(int num)
    {
        if(num != 0)
        {
            for(int i = 0; i < GetSize(); i++)
            {
                vec_[i] = vec_[i]/num;
            }
        }
    }
    // public methods
    public String ToString()
    {
        String outStr = "[";
        for(int i = 0; i < vec_.length; i++)
        {
            if(i == vec_.length -1)
            {
                outStr += vec_[i] + "]";
            }
            else
            {
                outStr += vec_[i] + ", ";
            }
        }
        return outStr;
    }
    
    // private methods
    private static void Test()
    {
        DoubleVec vec = new DoubleVec(5);
        System.out.println("- size: " + vec.GetSize());
        System.out.println("- vec: " + vec.ToString());
        double[] a1 = {1.0d, 2.0d, 3.0d};
        DoubleVec v1 = new DoubleVec(a1);
        System.out.println("- size: " + v1.GetSize());
        System.out.println("- v1: " + v1.ToString());
        a1[0] = 5.0d;
        System.out.println("- v1 (check ref): " + v1.ToString());
        double[] a2 = {3.0d, 5.0d, 3.0d};
        DoubleVec v2 = new DoubleVec(a2);
        System.out.println("- v2: " + v2.ToString() + ", size:" + v2.GetSize());
        v2.Add(v1);
        System.out.println("- Add, v1:  " + v1.ToString());
        System.out.println("- Add, v2: " + v2.ToString()); 
        v2.Minus(v1);
        System.out.println("- Minus, v1:  " + v1.ToString());
        System.out.println("- Minus, v2: " + v2.ToString()); 
        v2.Divide(4);
        System.out.println("- Div, v1: " + v1.ToString());
        System.out.println("- Div, v2: " + v2.ToString()); 
    }
    // data member
    private double[] vec_ = null;
}
