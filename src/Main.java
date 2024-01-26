import processing.core.PApplet;
import processing.data.JSONObject;

import java.io.File;

public class Main extends PApplet {

    float[][][] concentration;
    float gridSize;
    //int height = 2048;
    float[][][] nextConcentration;

    float D_A = 1f;
    float D_B = 0.5f;
    float f = 0.055f;
    float k = 0.062f;

    float delta_t = 1.0f;

    JSONObject json;
    boolean json_exists = false;
    String json_filename = "data.json";

    public void settings() {
        size(3840, 2160);
    }

    public void setup() {
        json_exists = new File(json_filename).exists();

        if (json_exists) {
            json = loadJSONObject("data.json");
        }

        nextConcentration = new float[height][width][2];
        concentration = new float[height][width][2];
        gridSize = (float) width / height;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                concentration[i][j] = new float[]{1, 0};
            }
        }

        placeBlob();

        frameRate(3000);
    }

    private void placeBlob() {
        int startIndex = height / 2;

        for (int i = startIndex; i < startIndex + 5; i++) {
            for (int j = startIndex; j < startIndex + 5; j++) {
                concentration[i][j] = new float[]{1, 1};
            }
        }
    }

    public void draw() {
        if (json_exists) {
            f = json.getFloat("f", 0.055f);
            k = json.getFloat("k", 0.062f);
        }

        loadPixels();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                float A = concentration[i][j][0];
                float B = concentration[i][j][1];

                pixels[i * width + j] = color(A * 255, B * 255, 255);
            }
        }
        updatePixels();

        float maxA = -999;
        float minA = 999;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
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

        System.arraycopy(nextConcentration, 0, concentration, 0, height);

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
        if (i - 1 >= 0 && j + 1 < width) {
            sum += 0.05f * concentration[i - 1][j + 1][compound];
            counter++;
        }

        // Left neighbor
        if (j - 1 >= 0) {
            sum += 0.2f * concentration[i][j - 1][compound];
            counter++;
        }

        // Right neighbor
        if (j + 1 < width) {
            sum += 0.2f * concentration[i][j + 1][compound];
            counter++;
        }

        // Bottom-left neighbor
        if (i + 1 < height && j - 1 >= 0) {
            sum += 0.05f * concentration[i + 1][j - 1][compound];
            counter++;
        }

        // Bottom neighbor
        if (i + 1 < height) {
            sum += 0.2f * concentration[i + 1][j][compound];
            counter++;
        }

        // Bottom-right neighbor
        if (i + 1 < height && j + 1 < width) {
            sum += 0.05f * concentration[i + 1][j + 1][compound];
            counter++;
        }


        return sum;// / numTerms;
    }

    public void keyPressed() {
        float increment = 0.01f; // Adjust this value as needed for the rate of change
        float k_increment = 0.0001f;

        switch(key) {
            case 'q':
                D_A += increment;
                break;
            case 'a':
                D_A = max(0, D_A - increment); // Prevents the variable from going negative
                break;
            case 'w':
                D_B += increment;
                break;
            case 's':
                D_B = max(0, D_B - increment);
                break;
            case 'e':
                f += increment;
                break;
            case 'd':
                f = max(0, f - increment);
                break;
            case 'r':
                k += k_increment;
                break;
            case 'f':
                k = k - k_increment;
                break;
            case 't':
                delta_t += increment;
                break;
            case 'g':
                delta_t = max(0, delta_t - increment);
                break;
        }

        if (keyCode == ENTER) {
            placeBlob();
        }
    }


    public static void main(String[] args) {
        PApplet.main("Main");
    }
}
