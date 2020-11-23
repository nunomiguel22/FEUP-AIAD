package planetExplorer.ui;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import planetExplorer.agents.BaseAgent;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class BaseStyle extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		BaseAgent res = (BaseAgent) agent;
		
        int x = (int) (res.getPosition().getX());
        int y = (int) (res.getPosition().getY());
        
        Rectangle2D rect = new Rectangle2D.Double(res.getPosition().getX(), res.getPosition().getY(), 10.0, 10.0);
       
		spatial = shapeFactory.createShape(rect);
	
		return spatial;
	}
	
	@Override
	public float getScale(Object object) {
		return 1.5f;
	}
	
	@Override
	public Color getColor(Object object) {
		return Color.GREEN;
	}
	
	@Override
	public Color getBorderColor(Object object) {
		return Color.WHITE;
	}
	
}
