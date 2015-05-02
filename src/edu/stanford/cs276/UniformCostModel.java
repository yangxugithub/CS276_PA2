package edu.stanford.cs276;
//import java.util.Collections;
import java.util.Arrays;;

public class UniformCostModel implements EditCostModel
{
//    public static final Character[] alphabet = {
//        'a','b','c','d','e','f','g','h','i','j','k','l','m','n',
//        'o','p','q','r','s','t','u','v','w','x','y','z',
//        '0','1','2','3','4','5','6','7','8','9',
//        ' ',','};
    
    public static final double ProbDiff = 0.01;
    public static final double ProbSame = 0.95;
    public static int getMinValue(int... nums)
    {
//        int min = Integer.MAX_VALUE;
        int min = nums[0];
        for (int num : nums)
        {
            min = Math.min(min, num);
        }
        return min;
    }

    public static int getEditDistance(String src, String dst)
    {
        int alphabetLength = 150;
        int srcLength = src.length();
        int dstLength = dst.length();
        final int INFINITY = srcLength + dstLength;
        int[][] H = new int[srcLength + 2][dstLength + 2];
        H[0][0] = INFINITY;
        for (int i = 0; i <= srcLength; i++)
        {
            H[i + 1][1] = i;
            H[i + 1][0] = INFINITY;
        }
        for (int j = 0; j <= dstLength; j++)
        {
            H[1][j + 1] = j;
            H[0][j + 1] = INFINITY;
        }
        int[] DA = new int[alphabetLength];
        Arrays.fill(DA, 0);
        for (int i = 1; i <= srcLength; i++)
        {
            int DB = 0;
            for (int j = 1; j <= dstLength; j++)
            {
                int i1 = DA[dst.charAt(j - 1)];
                int j1 = DB;
                int d = ((src.charAt(i - 1) == dst.charAt(j - 1)) ? 0 : 1);
                if (d == 0)
                    DB = j;
                H[i + 1][j + 1] = getMinValue(
                        H[i][j] + d, 
                        H[i + 1][j] + 1,
                        H[i][j + 1] + 1, 
                        H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
            }
            DA[src.charAt(i - 1)] = i;
        }
        return H[srcLength + 1][dstLength + 1];
    }
  
    @Override
    public double editProbability(String original, String R, int distance)
    {
        if (original.compareTo(R) == 0)
        {
            return ProbSame;
        }
        int editDistance = getEditDistance(original, R);
        
        return Math.pow(ProbDiff, editDistance);

    }
}
