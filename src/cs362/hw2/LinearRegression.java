package cs362.hw2;

import cs362.*;
import org.apache.commons.math3.linear.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yli on 1/26/14.
 */
public class LinearRegression extends Predictor implements Serializable {
    RealMatrix small_w;
    RealMatrix big_x;
    RealMatrix small_y;

            //= new Array2DRowRealMatrix(1000,1000);
    private int getRow(List<Instance> instances){return instances.size();}
    private int getCol(List<Instance> instances){return instances.iterator().next().getFeatureVector().getEntrySet().size();}
    public void train(List<Instance> instances){
        int row = getRow(instances);
        int col = getCol(instances);
        big_x = new Array2DRowRealMatrix(row,col);
        small_y = new Array2DRowRealMatrix(row,1);
        for(int i = 0; i < row; ++i){
            Instance tmp = instances.get(i);
            Map<Integer, Double> _m = tmp.getFeatureVector().getMap();
            for(int j = 0;j < col; ++j){
                Double value = _m.get(j + 1);
                if(value == null)
                    value = 0d;
                big_x.addToEntry(i, j, value);
            }
            small_y.addToEntry(i, 0, Double.parseDouble(tmp.getLabel().toString()));
        }

//        for(int p = 0; p < row; ++p){
//            for(int q = 0; q < col; ++q){
//                System.out.print(big_x.getEntry(p,q));
//                System.out.print(" ");
//            }
//            System.out.print("\n");
//        }
        RealMatrix tmp = big_x.transpose().multiply(big_x);
        tmp = new LUDecomposition(tmp).getSolver().getInverse();
        small_w = tmp.multiply(big_x.transpose());
        small_w = small_w.multiply(small_y);

    }

    public Label predict(Instance instance){
        Map<Integer, Double> feature = instance.getFeatureVector().getMap();
        int row = instance.getFeatureVector().getEntrySet().size();
        RealMatrix input = new Array2DRowRealMatrix(row,1);
        for(int i = 0; i < row; ++i){
            Double v = feature.get(i+1);
            if(null == v){
                input.addToEntry(i, 0, 0.0);
            }
            else{
                input.addToEntry(i, 0, v);
            }
        }
        RealMatrix output;
        output = small_w.transpose().multiply(input);
//        System.out.println(output.getRowDimension());
//        System.out.println(output.getColumnDimension());
        Label result = new RegressionLabel(output.getEntry(0,0));
        return result;
    }
}
