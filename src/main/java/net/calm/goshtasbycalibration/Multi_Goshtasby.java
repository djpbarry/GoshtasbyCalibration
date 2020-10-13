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
package net.calm.goshtasbycalibration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.calm.iaclasslibrary.UtilClasses.GenUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealVector;

public class Multi_Goshtasby {

    private final String charset = "UTF-8";
    private final int M = 4;
    private final int N = 2;

//    public static void main(String args[]) {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.showOpenDialog(null);
//        (new Multi_Goshtasby()).readCoordFile("C0_X\tC0_Y\tC1_X\tC1_Y\tC0_\u03c3\tC1_\u03c3\tC0_Fit\tC1_Fit", fileChooser.getSelectedFile(), 3);
//        System.exit(0);
//    }
    public boolean run(File file, int headerSize) {
        try {
            Reader in = new InputStreamReader(new FileInputStream(file), charset);
            CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withDelimiter('\t'));
            List<CSVRecord> list = parser.getRecords();
            int size = list.size() - headerSize;
            Vector2D C0[] = new Vector2D[size];
            Vector2D C1[] = new Vector2D[size];
            double width = Double.parseDouble(list.get(1).get(1));
            double height = Double.parseDouble(list.get(2).get(1));
            for (int i = headerSize; i < list.size(); i++) {
                CSVRecord current = list.get(i);
                C0[i - headerSize] = new Vector2D(Double.parseDouble(current.get(0)), Double.parseDouble(current.get(1)));
                C1[i - headerSize] = new Vector2D(Double.parseDouble(current.get(2)), Double.parseDouble(current.get(3)));
            }
            subGoshtasby(C0, C1, M, N, width, height, new File(file.getParent()));
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    void subGoshtasby(Vector2D[] C0, Vector2D[] C1, int m, int n, double w, double h, File rootdir)
            throws FileNotFoundException {
        ArrayList<Vector2D> coordArray0[][] = new ArrayList[n][m];
        ArrayList<Vector2D> coordArray1[][] = new ArrayList[n][m];
        double xdiv = ((double) w) / n;
        double ydiv = ((double) h) / m;
        int size = C1.length;
        File dir = new File(rootdir + "/goshtasby/" + n + "_" + m);
        if (dir.exists()) {
            FileUtils.deleteQuietly(dir);
        }
        GenUtils.createDirectory(dir.getAbsolutePath(), false);
        for (int i = 0; i < size; i++) {
            int x1 = (int) Math.floor(C0[i].getX() / xdiv);
            int y1 = (int) Math.floor(C0[i].getY() / ydiv);
            if (coordArray0[x1][y1] == null) {
                coordArray0[x1][y1] = new ArrayList();
            }
            coordArray0[x1][y1].add(C0[i]);
            if (coordArray1[x1][y1] == null) {
                coordArray1[x1][y1] = new ArrayList();
            }
            coordArray1[x1][y1].add(C1[i]);
        }
        Goshtasby_ g = new Goshtasby_();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                ArrayList<Vector2D> tempC0 = coordArray0[i - 1][j - 1];
                ArrayList<Vector2D> tempC1 = coordArray1[i - 1][j - 1];
                if (tempC0 != null) {
                    RealVector XCoeffs = g.goshtasby(tempC1, tempC0, 0);
                    RealVector YCoeffs = g.goshtasby(tempC1, tempC0, 1);
                    File xFile = new File(dir + "/xcoeffs" + i + "_" + j + ".txt");
                    PrintWriter xStream = new PrintWriter(new FileOutputStream(xFile));
                    File yFile = new File(dir + "/ycoeffs" + i + "_" + j + ".txt");
                    PrintWriter yStream = new PrintWriter(new FileOutputStream(yFile));
                    File cFile = new File(dir + "/coords" + i + "_" + j + ".txt");
                    PrintWriter cStream = new PrintWriter(new FileOutputStream(cFile));
                    for (int k = 0; k < XCoeffs.getDimension(); k++) {
                        xStream.println(XCoeffs.getEntry(k));
                        yStream.println(YCoeffs.getEntry(k));
                        if (k < tempC0.size()) {
                            cStream.println(tempC0.get(k).getX() + "," + tempC0.get(k).getY());
                        }
                    }
                    xStream.close();
                    yStream.close();
                    cStream.close();
                }
            }
        }
    }

}
