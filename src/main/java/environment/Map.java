package environment;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Map {
    private Vec2 bounds;
    private Color bgColor;
    private List<Resource> resources = new ArrayList<>();
    private Wharehouse wharehouse;

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

            // Create Resources
            reader.readLine();
            reader.readLine();
            String resource = reader.readLine();
            while (resource != null && !resource.isEmpty()) {
                String[] coords = resource.split(" ");
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);

                resources.add(new Resource(x, y));
                resource = reader.readLine();
            }

            // Wharehouse
            reader.readLine();
            line = reader.readLine();
            String[] wharehouseCoords = line.split(" ");
            wharehouse = new Wharehouse(Double.parseDouble(wharehouseCoords[0]), Double.parseDouble(wharehouseCoords[1]));

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

    public List<Resource> getResources() {
        return resources;
    }

    public Wharehouse getWharehouse() {
        return wharehouse;
    }
}
