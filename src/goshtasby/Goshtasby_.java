/*
 * Copyright (C) 2016 David Barry <david.barry at crick.ac.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package goshtasby;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Goshtasby_ {
    // C2 to be mapped onto C1
    public Goshtasby_(){
        
    }

    public RealVector goshtasby(ArrayList<Vector2D> C1, ArrayList<Vector2D> C2, int d) {

        int l = C1.size();
        double A[][] = new double[l + 3][l + 3];
        for (int a = 0; a < A.length; a++) {
            Arrays.fill(A[a], 0.0);
        }

        for (int j = 0; j < l; j++) {
            A[j][0] = 1;
            double x1 = C2.get(j).getX();
            double y1 = C2.get(j).getY();
            A[j][1] = x1;
            A[j][2] = y1;
            for (int i = 3; i < l + 3; i++) {
                double r = Math.pow(x1 - C2.get(i - 3).getX(), 2.0) + Math.pow(y1 - C2.get(i - 3).getY(), 2.0);
                double R = r * Math.log(r);
                A[j][i] = R;
                if (i - 3 == j) {
                    A[j][i] = 0;
                }
            }
        }
        for (int j = l; j < l + 3; j++) {
//            A[j][0] = 0;
//            A[j][1] = 0;
//            A[j][2] = 0;
            for (int i = 3; i < l + 3; i++) {
                if (j == l) {
                    A[j][i] = 1;
                } else if (j == l + 1) {
                    A[j][i] = C2.get(i - 3).getX();
                } else if (j == l + 2) {
                    A[j][i] = C2.get(i - 3).getY();
                }
            }
        }

        double B[] = new double[l + 3];
        Arrays.fill(B, 0.0);
        for (int b = 0; b < C1.size(); b++) {
            if (d < 1) {
                B[b] = C1.get(b).getX();
            } else {
                B[b] = C1.get(b).getY();
            }
        }
        RealMatrix matA = new Array2DRowRealMatrix(A, false);
        DecompositionSolver solver = new QRDecomposition(matA).getSolver();
        RealVector matB = new ArrayRealVector(B, false);
        return solver.solve(matB);
    }
}
