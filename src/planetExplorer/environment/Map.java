package planetExplorer.environment;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;

public class Map {
    private Vec2 bounds;
    private Vec2 baseCoord;
    private ArrayList<Vec2> exCoords;
    private ArrayList<Vec2> tpCoord;
    private ArrayList<Vec2> collectorCoord;
    private ArrayList<Resource> resources;
    private Color bgColor;
    private ContinuousSpace<Object> space;
    private Context<Object> context;
    private BufferedReader reader;

    public Map(String filepath) {
    	
    	
        try {
            reader = new BufferedReader(new FileReader(new File(filepath)));
            readHeader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void init(ContinuousSpace<Object> space, Context<Object> context) {
    	this.space = space;
    	this.context = context;
        try {
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;
                if (line.equals("Explorers")) {
                    line = readExplorers(reader);
                }
                if (line.equals("Transporters"))
                    line = readTransporters(reader);
                if (line.equals("Collectors"))
                    line = readCollectors(reader);
                if (line.equals("Resources"))
                    line = readResources(reader);
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

    private String readExplorers(BufferedReader reader) throws IOException {
        exCoords = new ArrayList<Vec2>();
        String line;
        while (true) {
            line = reader.readLine();

            if (line.equals("Transporters"))
                return line;

            String[] splitLine = line.split(" ");
            int xCoord = Integer.parseInt(splitLine[0]);
            int yCoord = Integer.parseInt(splitLine[1]);
            exCoords.add(new Vec2(xCoord, yCoord));
        }
    }

    private String readTransporters(BufferedReader reader) throws IOException {
        tpCoord = new ArrayList<Vec2>();
        String line;
        while (true) {
            line = reader.readLine();

            if (line == null || line.equals("Collectors") || line.equals("Resources"))
                return line;

            String[] splitLine = line.split(" ");
            int xCoord = Integer.parseInt(splitLine[0]);
            int yCoord = Integer.parseInt(splitLine[1]);
            tpCoord.add(new Vec2(xCoord, yCoord));
        }
    }

    private String readCollectors(BufferedReader reader) throws IOException {
        collectorCoord = new ArrayList<>();
        String line;
        while (true) {
            line = reader.readLine();

            if (line == null || line.equals("Resources"))
                return line;

            String[] splitLine = line.split(" ");
            int xCoord = Integer.parseInt(splitLine[0]);
            int yCoord = Integer.parseInt(splitLine[1]);
            collectorCoord.add(new Vec2(xCoord, yCoord));
        }
    }

    private String readResources(BufferedReader reader) throws IOException {
        resources = new ArrayList<Resource>();
        String line;
        while (true) {
            line = reader.readLine();

            if (line == null || line.equals("Collectors") || line.equals("Transporters"))
                return line;

            String[] splitLine = line.split(" ");
            int xCoord = Integer.parseInt(splitLine[0]);
            int yCoord = Integer.parseInt(splitLine[1]);
            int amount = Integer.parseInt(splitLine[2]);
            Resource res = new Resource(new Vec2(xCoord, yCoord), amount);
            this.context.add(res);
            this.space.moveTo(res, res.getPosition().getX(), res.getPosition().getY());
            resources.add(res);
        }
    }

    public Vec2 getBaseCoords() {
        return this.baseCoord;
    }

    public ArrayList<Vec2> getExplorerCoords() {
        return exCoords;
    }

    public ArrayList<Vec2> getTransporterCoords() {
        return this.tpCoord;
    }

    public ArrayList<Vec2> getCollectorCoords() {
        return collectorCoord;
    }

    public ArrayList<Resource> getResources() {
        return this.resources;
    }

    public Resource getResourceAt(Vec2 position) {
        for (Resource resource : resources) {
            Vec2 resourcePosition = resource.getPosition();
            if (resourcePosition.getX() == position.getX() && resourcePosition.getY() == position.getY())
                return resource;
        }

        return null;
    }

    public void removeResource(Resource resource) {
        this.resources.remove(resource);
        this.context.remove(resource);
    }


    public Vec2 getBounds() {
        return this.bounds;
    }

    public Color getBGColor() {
        return this.bgColor;
    }

    public boolean hasResources() {
        return !this.resources.isEmpty();
    }
    
    public ContinuousSpace<Object> getSpace(){
    	return space;
    }
    
    public Context<Object> getContext(){
    	return context;
    }
}
