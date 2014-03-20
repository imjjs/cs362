package cs362.image_denoise;

import java.util.Map;
import java.util.TreeMap;
import java.lang.Math;
/**
 * Created by yli on 14-3-16.
 */
public class MRFImageProcessor {
    protected int row;
    protected int col;
    protected double eta;
    protected double beta;
    protected int iter_time;
    protected int[][] bigX;
    protected int[][] _bigX;
    protected int[][] bigY;
    protected int num_color;
    public MRFImageProcessor(double _eta, double _beta, int _iter_time, int _num_color){
        eta = _eta;
        beta = _beta;
        iter_time = _iter_time;
        num_color = _num_color;
    }

    protected int[][] arrayCopy(int[][] a){
        int[][] result = new int[row][col];
        for(int i = 0; i < row; ++i)
            for(int j = 0; j < col; ++j)
            result[i][j] = a[i][j];
        return result;
    }


    protected int compare_2color(int a, int b){
        if(a == b)
            return -1;
        else
            return 1;
    }
    protected double compare_greyscal(int a, int b){
        return Math.log(Math.abs(a - b) + 1) - 1;
    }

    protected double compare(int a, int b, int num_color){
        if(num_color == 2)
            return compare_2color(a, b);
        else
            return compare_greyscal(a, b);
    }
    protected double neibhbour(int i, int j, int value){
        double n1 = 0;
        double n2 = 0;
        double n3 = 0;
        double n4 = 0;
        if(0 != i)
            n1 = compare(bigX[i-1][j], value, num_color);
        if(0 != j)
            n2 = compare(bigX[i][j-1], value, num_color);
        if(row - 1 != i)
            n3 = compare(bigX[i+1][j], value, num_color);
        if(col - 1 != j)
            n4 = compare(bigX[i][j+1], value, num_color);
        return n1 + n2 + n3 + n4;
    }
    protected int update(int i, int j){
        TreeMap<Double, Integer> tmp = new TreeMap <Double, Integer>();
        for(int p = 0; p < num_color; ++p){
            double value = beta * neibhbour(i, j, p) + eta * compare(bigY[i][j], p, num_color);
            tmp.put(value, p);
        }
        return tmp.firstEntry().getValue();
    }

    protected void process(){
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                _bigX[i][j] = update(i,j);
            }
        }
    }

    public int[][] denoisifyImage(int[][] image1, int[][] i2){
        row = image1.length;
        col = image1[0].length;
        bigY = image1;
        bigX = new int[row][col];
        bigX = arrayCopy(bigY);
        for(int i = 0; i < iter_time; ++i){
            //System.out.print(i);
            _bigX = new int[row][col];
            process();
            bigX = _bigX;
        }
        return bigX;
    }
}
