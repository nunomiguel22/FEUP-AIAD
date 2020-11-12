package environment;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;

public class Map {
    private Vec2 bounds;
    private Vec2 baseCoord;
    private ArrayList<Vec2> tpCoord;
    private Color bgColor;

    public Map(String filepath) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
            readHeader(reader);
            String line = "";
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;
                if (line.equals("Transporters"))
                    readTransporters(reader, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHeader(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        line = reader.readLine();

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
        line = reader.readLine();
        line = reader.readLine();
        splitLine = line.split(" ");
        int baseX = Integer.parseInt(splitLine[0]);
        int baseY = Integer.parseInt(splitLine[1]);
        this.baseCoord = new Vec2(baseX, baseY);
    }

    private void readTransporters(BufferedReader reader, String line) throws IOException {
        tpCoord = new ArrayList<Vec2>();

        while (true) {
            line = reader.readLine();

            if (line == null || line.equals("Collectors") || line.equals("Resources"))
                return;

            String[] splitLine = line.split(" ");
            int xCoord = Integer.parseInt(splitLine[0]);
            int yCoord = Integer.parseInt(splitLine[1]);
            tpCoord.add(new Vec2(xCoord, yCoord));
        }

    }

    public Vec2 getBaseCoords() {
        return this.baseCoord;
    }

    public ArrayList<Vec2> getTransporterCoords() {
        return this.tpCoord;
    }

    public Vec2 getBounds() {
        return this.bounds;
    }

    public Color getBGColor() {
        return this.bgColor;
    }
}
