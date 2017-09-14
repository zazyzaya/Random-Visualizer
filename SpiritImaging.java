// Ghost in the machine
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SpiritImaging {
    public static void main(String[] args){
        int numRows = 300;
        int numCols = 300;
        int length = 1;
        
        SmartRect[][] grid = generateSRArray(numRows, numCols, length);
        JFrame window = new JFrame();
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(50, 50, numRows * length * 2, 2 * numCols * length + 20);
        
        MyCanvas canv = new MyCanvas(generateSRArray(numRows, numCols, length));
        canv.updateRect(grid);

        while (true){
            window.getContentPane().add(canv);
            grid = smartGenerateSRArray(numRows, numCols, length, grid);
            canv.updateRect(grid);
            window.setVisible(true);
           /* try        
            {
                Thread.sleep(100);
            } 
            catch(InterruptedException ex) 
            {
                Thread.currentThread().interrupt();
            } */
        }
    }
    
    public static SmartRect[][] generateSRArray (int rows, int columns, int len) {
        SmartRect[][] r = new SmartRect[rows][columns];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                r[i][j] = new SmartRect(j * len, i * len, len, len);
            }
        }
        return r;
    }
    public static SmartRect[][] smartGenerateSRArray (int rows, int columns, int len, SmartRect[][] r ) {
        int[] neighborColors = new int[3];
        Random rand = new Random();
        int[] emptyList = new int[3];

        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                neighborColors = getAvgColors(r, i, j);
                r[i][j] = new SmartRect(j * len, i * len, len, len, neighborColors);
            }
        }
        return r;
    }
    public static int[] getAvgColors (SmartRect[][] sr, int row, int col){
        int[][] neighbors = getNeighborColors(sr, row, col, sr.length, sr[0].length);
        int avgColor[] = {0, 0, 0};
        int numColors = 0;
        Random r = new Random();

        for (int[] color : neighbors){
            numColors = 0;
            if (color != new int[3]) {
                for (int i = 0; i < 3; i++){
                    avgColor[i] += color[i];
                    numColors += 1;
                }
            } else {
                break;
            }
        }
        for (int i = 0; i < 3; i++){
            int plusMinus = r.nextInt(2);
            if (plusMinus == 0){
                plusMinus = -1;
            }

            int avg = (int) (avgColor[i]/numColors);
            avgColor[i] = (int) (
                //avg + (plusMinus * r.nextInt(255)/(1 + r.nextInt(col + row + 1)))// Add fancyness here
                //(avg + plusMinus * (r.nextInt(1 + col) + r.nextInt(1+row)/(1 + r.nextInt(row + col + 1)))) % (avg + 1)
                //avg + (avg / (1 + row*col)) + (int) (r.nextGaussian()*(10))
                avg + r.nextInt(2)
                );  
            if (avgColor[i] > 500) {
                avgColor[i] = Math.abs((avgColor[i] % 240)) + 1; 
            } else if (avgColor[i] >= 250){
                avgColor[i] = 0 + r.nextInt(2);
            } else {
                avgColor[i] = Math.abs(avgColor[i] % 220) + 1;
            } 
            //avgColor[i] = Math.abs((avgColor[i] % 220)) + 1;
        }
        return avgColor;
    }
    public static int[][] getNeighborColors(SmartRect[][] sr, int row, int col, int maxRow, int maxCol){
        int[] colIndexes = new int[3];
        int[] rowIndexes = new int[3];
        int[][] neighborColors = new int[9][3];

        for (int i = -1; i <=1 ; i++) {         // Finds neighbors
            if (row + i < maxRow && row + i >= 0){
                rowIndexes[i+1] = row + i;
            }
            if (col + i < maxCol && col + i >= 0){
                colIndexes[i+1] = col + i;
            }
        }
        int iter = 0;
        for(int r : rowIndexes){            // Gets their colors
            for (int c : colIndexes){
                neighborColors[iter] = sr[r][c].getColor();
                iter++;
            }
        }
        return neighborColors;
    }
}

class MyCanvas extends JComponent {
    private SmartRect[][] rs;

    public void updateRect(SmartRect[][] rect){
        rs = rect;
        repaint();
    }
    MyCanvas(SmartRect[][] srArg){
        super();
        rs = srArg;
    }
    MyCanvas(){
        super();
    }
    public void paint(Graphics g) {
        SmartRect r;
        int sizeOfCanvas = 300;

        for (int rows = 0; rows < rs.length; rows++){
            for (int cols = 0; cols < rs[0].length; cols++){
                r = rs[rows][cols];
                int[] color = r.getColor();
                g.setColor(new Color(color[0], color[1], color[2]));
                g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
                g.fillRect((int) ((r.getX() - sizeOfCanvas) * -1) + sizeOfCanvas-1, (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
                g.fillRect((int) r.getX(), (int) ((r.getY() - sizeOfCanvas) * -1) + sizeOfCanvas-1, (int) r.getWidth(), (int) r.getHeight());
                g.fillRect((int) ((r.getX() - sizeOfCanvas) * -1) + sizeOfCanvas-1, (int) ((r.getY() - sizeOfCanvas) * -1) + sizeOfCanvas-1, (int) r.getWidth(), (int) r.getHeight());
            }
        } 
    }
}

class SmartRect extends Rectangle {
    public String[] neighbors;
    public int[] color;

    public SmartRect(int xArg, int yArg, int wArg, int hArg, int[] cArg) {
       super(xArg, yArg, wArg, hArg);
       color = cArg;
    }
    public SmartRect(int xArg, int yArg, int wArg, int hArg){
        super(xArg, yArg, wArg, hArg);
        color = randomColor();
    }
    public int[] getColor() {
        return color;
    }
    private int[] randomColor() {
        Random r = new Random();
        int[] retList = {r.nextInt(256), r.nextInt(256), r.nextInt(256)};
        return retList;
    }
    public Rectangle castToRect(){
        return new Rectangle(x, y, width, height);
    }
}