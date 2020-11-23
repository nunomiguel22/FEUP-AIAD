package planetExplorer.environment;


import java.awt.Color;
import java.io.Serializable;

public class Resource implements Serializable {
    static final long serialVersionUID = 1423L;
    private final Vec2 position;
    private int amount;
    private Color clr;
    
    public Resource(Vec2 pos, int amount) {
        this.position = pos;
        this.amount = amount;
        this.clr = Color.DARK_GRAY;
    }


    public int getAmount() {
        return this.amount;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setColor(Color nclr) {
                clr = nclr;
    }
    
    public Color getColor() {
    	return this.clr;
    }
}
