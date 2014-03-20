package cs362.image_denoise;

/**
 * Created by yli on 14-3-16.
 */
public class MRFImageProcessor {
    int row;
    int col;
    double eta;
    double beta;
    int iter_time;
    int[][] bigX;
    int[][] _bigX;
    int[][] bigY;
    public MRFImageProcessor(double _eta, double _beta, int _iter_time){
        eta = _eta;
        beta = _beta;
        iter_time = _iter_time;
    }

    private int neighbour(int i, int j, int value){
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        if(0 != i)
            n1 = bigX[i-1][j] * value;
        if(0 != j)
            n2 = bigX[i][j-1] * value;
        if(row != i)
            n3 = bigX[i+1][j] * value;
        if(col != j)
            n4 = bigX[i][j+1] * value;
        return n1 + n2 + n3 + n4;

    }

    private double neg_one(int i, int j){
        return beta * neighbour(i, j, -1) + eta * bigY[i][j] * -1;
    }

    private double one(int i, int j){
        return beta * neighbour(i, j, 1) + eta * bigY[i][j] * 1;
    }

    private void process(){
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                if(one(i, j) > neg_one(i, j))
                    _bigX[i][j] = 1;
                else
                    _bigX[i][j] = -1;
            }
        }
    }
    public int[][] denoisifyImage(int[][] image1, int[][] i2){
        row = image1.length;
        col = image1[0].length;
        bigY = image1;
        bigX = bigY.clone();
        for(int i = 0; i < iter_time; ++i){
            _bigX = bigX.clone();
            process();
            bigX = _bigX;
        }
        return bigX;
    }
}
