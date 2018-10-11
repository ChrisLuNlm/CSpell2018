package gov.nih.nlm.nls.cSpell.Util;
import java.util.*;
import java.util.stream.Collectors;
// simple implmentation of double Vector object for:
/*****************************************************************************
* This is a simple implementation for Vector operation:
* - length
* - inner dot (dot product)
* - cosine similarity = inner dot(v1, v2) / length(v1) * length(v2) 
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
public class DoubleVecUtil
{
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java DoubleVecUtil");
            System.exit(0);
        }
        // test
        Test();
    }
    // private constructor
    private DoubleVecUtil()
    {
    }
    // public methods
    public static DoubleVec Divide(DoubleVec vec, int count)
    {
        DoubleVec outVec = null;
        if((count != 0) && (vec != null))
        {
            outVec = new DoubleVec(vec.GetSize());
            for(int i = 0; i < outVec.GetSize(); i++)
            {
                outVec.SetElement(vec.GetElement(i)/count, i);
            }
        }
        return outVec;
    }
    public static DoubleVec Add(DoubleVec vec1, DoubleVec vec2)
    {
        int size1 = vec1.GetSize();
        int size2 = vec2.GetSize();
        DoubleVec outVec = new DoubleVec(size1);
        if(size1 == size2)
        {
            for(int i = 0; i < size1; i++)
            {
                double out = vec1.GetElement(i) + vec2.GetElement(i);
                outVec.SetElement(out, i);
            }
        }
        return outVec;
    }
    public static DoubleVec Minus(DoubleVec vec1, DoubleVec vec2)
    {
        int size1 = vec1.GetSize();
        int size2 = vec2.GetSize();
        DoubleVec outVec = new DoubleVec(size1);
        if(size1 == size2)
        {
            for(int i = 0; i < size1; i++)
            {
                double out = vec1.GetElement(i) - vec2.GetElement(i);
                outVec.SetElement(out, i);
            }
        }
        return outVec;
    }
    //the output is between 0.0 ~ 1.0
    public static double GetCosineSimilarity(DoubleVec vec1, DoubleVec vec2)
    {
        double similarity = 0.0d;
        double length1 = GetLength(vec1);
        double length2 = GetLength(vec2);
        //must has same size
        if((vec1 != null)
        && (vec2 != null)
        && (length1 > 0.0d)
        && (length2 > 0.0d)
        && (vec1.GetSize() == vec2.GetSize()))
        {
            similarity = GetInnerDot(vec1, vec2)/(length1*length2);
        }
        return similarity;
    }
     
    public static double GetInnerDot(DoubleVec vec1, DoubleVec vec2)
    {
        double innerDot = 0.0d;
        double[] vec1a = vec1.GetVec();
        double[] vec2a = vec2.GetVec();
        // proceed the calculation only if same size
        if(vec1.GetSize() == vec2.GetSize())
        {
            for(int i = 0; i < vec1.GetSize(); i++)
            {
                innerDot += (vec1a[i] * vec2a[i]);
            }
        }
        return innerDot;
    }
    public static double GetLength(DoubleVec inVec)
    {
        double length = 0.0d;
        double sum = 0.0d;
        double[] vec = inVec.GetVec();
        for(int i = 0; i < vec.length; i++)
        {
            sum += (vec[i] * vec[i]);
        }
        if(sum > 0.0)
        {
            length = Math.sqrt(sum);
        }
        return length;
    }
    // private methods
    private static void Test()
    {
        double[] a1 = {1.0d, 1.0d, 1.0d};
        double[] a2 = {1.0d, 2.0d, 3.0d};
        double[] a3 = {9.0d, 18.0d, 27.0d};
        double[] a4 = {0.0d, 1.0d, 0.0d};
        double[] a5 = {0.0d, -1.0d, 0.0d};
        DoubleVec v1 = new DoubleVec(a1);
        DoubleVec v2 = new DoubleVec(a2);
        DoubleVec v3 = new DoubleVec(a3);
        DoubleVec v4 = new DoubleVec(a4);
        DoubleVec v5 = new DoubleVec(a5);
        System.out.println("v1: " + v1.ToString() + ", length: " + GetLength(v1));
        System.out.println("v2: " + v1.ToString() + ", length: " + GetLength(v2));
        System.out.println("v3: " + v1.ToString() + ", length: " + GetLength(v3));
        System.out.println("v4: " + v1.ToString() + ", length: " + GetLength(v4));
        System.out.println("v5: " + v1.ToString() + ", length: " + GetLength(v5));
        System.out.println("v1.v2: dot: " + GetInnerDot(v1, v2) + ", s: "
            + GetCosineSimilarity(v1, v2));    
        System.out.println("v2.v3: dot: " + GetInnerDot(v2, v3) + ", s: "
            + GetCosineSimilarity(v2, v3));    
        System.out.println("v3.v4: dot: " + GetInnerDot(v3, v4) + ", s: "
            + GetCosineSimilarity(v3, v4));    
        System.out.println("v4.v5: dot: " + GetInnerDot(v4, v5) + ", s: "
            + GetCosineSimilarity(v4, v5));    
        double[] a6 = {2.0d, 1.0d, 0.0d, 2.0d, 0.0d, 1.0d, 1.0d, 1.0d}; 
        double[] a7 = {2.0d, 1.0d, 1.0d, 1.0d, 1.0d, 0.0d, 1.0d, 1.0d};
        DoubleVec v6 = new DoubleVec(a6);
        DoubleVec v7 = new DoubleVec(a7);
        System.out.println("v6: " + v6.ToString());
        System.out.println("v7: " + v7.ToString());
        System.out.println("v6.v7: dot: " + GetInnerDot(v6, v7) + ", s (0.822): "
            + GetCosineSimilarity(v6, v7));    
        DoubleVec v8 = Add(v6, v7);
        System.out.println("Add v8: " + v8.ToString());
        DoubleVec v9 = Divide(v8, 4);
        System.out.println("Divide v9: " + v9.ToString());
        System.out.println("v8: " + v8.ToString());
        DoubleVec v10 = Minus(v8, v7);
        System.out.println("Minus v10: " + v10.ToString());
    }
}
