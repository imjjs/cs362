package cs362.image_denoise;

import java.util.Map;
import java.util.TreeMap;

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


    protected int update(int i, int j){
        Map<Integer, Integer> tmp = new TreeMap <Integer, Integer>();
        for(int i = 0; i < num_color; ++i){

        }
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
