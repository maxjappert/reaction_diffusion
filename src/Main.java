import processing.core.PApplet;

public class Main extends PApplet {

    float[][][] concentration;
    float gridSize;
    int numCells = 2048;

    float D_A = 1f;
    float D_B = 0.5f;
    float f = 0.055f;
    float k = 0.062f;

    float delta_t = 1.0f;

    public void settings() {
        size(2048, 2048);
    }

    public void setup() {
        concentration = new float[numCells][numCells][2];
        gridSize = (float) width / numCells;

        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                concentration[i][j] = new float[]{1, 0};
            }
        }

        int startIndex = numCells / 2;

        for (int i = startIndex; i < startIndex + 5; i++) {
            for (int j = startIndex; j < startIndex + 5; j++) {
                concentration[i][j] = new float[]{1, 1};
            }
        }

        frameRate(3000);
    }

    public void draw() {
        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                float A = concentration[i][j][0];
                float B = concentration[i][j][1];

                stroke(A*255, B*255, 255);
                fill(A*255, B*255, 255);
                rect(gridSize*i, gridSize*j, gridSize, gridSize);
            }
        }

        float[][][] nextConcentration = new float[numCells][numCells][2];

        float maxA = -999;
        float minA = 999;

        for (int i = 0; i < numCells; i++) {
            for (int j = 0; j < numCells; j++) {
                float A = concentration[i][j][0];
                float B = concentration[i][j][1];

                if (A > maxA) {
                    maxA = A;
                }

                if (A < minA) {
                    minA = A;
                }

                float A_prime = A + (D_A * convolution(i, j, 0)-A*B*B + f*(1 - A))*delta_t;
                float B_prime = B + (D_B * convolution(i, j, 1)+A*B*B - (k + f)*B)*delta_t;

                nextConcentration[i][j][0] = constrain(A_prime, 0, 1);
                nextConcentration[i][j][1] = constrain(B_prime, 0, 1);
            }

            //println(minA + ", " + maxA);
        }

        System.arraycopy(nextConcentration, 0, concentration, 0, numCells);

        //filter(BLUR);

        saveFrame("frames/#####.png");
    }

    private float convolution(int i, int j, int compound) {
        float sum = -1f * concentration[i][j][compound];
        int counter = 0;

        //println(i + ", " + j);

        // Top-left neighbor
        if (i - 1 >= 0 && j - 1 >= 0) {
            sum += 0.05f * concentration[i - 1][j - 1][compound];
            counter++;
        }

        // Top neighbor
        if (i - 1 >= 0) {
            sum += 0.2f * concentration[i - 1][j][compound];
            counter++;
        }

        // Top-right neighbor
        if (i - 1 >= 0 && j + 1 < numCells) {
            sum += 0.05f * concentration[i - 1][j + 1][compound];
            counter++;
        }

        // Left neighbor
        if (j - 1 >= 0) {
            sum += 0.2f * concentration[i][j - 1][compound];
            counter++;
        }

        // Right neighbor
        if (j + 1 < numCells) {
            sum += 0.2f * concentration[i][j + 1][compound];
            counter++;
        }

        // Bottom-left neighbor
        if (i + 1 < numCells && j - 1 >= 0) {
            sum += 0.05f * concentration[i + 1][j - 1][compound];
            counter++;
        }

        // Bottom neighbor
        if (i + 1 < numCells) {
            sum += 0.2f * concentration[i + 1][j][compound];
            counter++;
        }

        // Bottom-right neighbor
        if (i + 1 < numCells && j + 1 < numCells) {
            sum += 0.05f * concentration[i + 1][j + 1][compound];
            counter++;
        }


        return sum;// / numTerms;
    }

    public static void main(String[] args) {
        PApplet.main("Main");
    }
}
