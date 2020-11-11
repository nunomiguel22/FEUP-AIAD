package environment;

import java.awt.Color;
import java.io.*;

public class Map {
    private Vec2 bounds;
    private Color bgColor;

    public Map(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
            String line = reader.readLine();
            String[] splitLine = line.split(" ");

            int colorR = Integer.parseInt(splitLine[0]);
            int colorG = Integer.parseInt(splitLine[1]);
            int colorB = Integer.parseInt(splitLine[2]);

            this.bgColor = new Color(colorR, colorG, colorB);

            line = reader.readLine();
            splitLine = line.split(" ");

            int xBound = Integer.parseInt(splitLine[0]);
            int yBound = Integer.parseInt(splitLine[1]);

            this.bounds = new Vec2(xBound, yBound);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vec2 getBounds() {
        return this.bounds;
    }

    public Color getBGColor() {
        return this.bgColor;
    }

}
