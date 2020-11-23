package planetExplorer.ui;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import planetExplorer.agents.ExplorerAgent;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class ExplorerStyle extends DefaultStyleOGL2D {
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial)  {
		ExplorerAgent res = (ExplorerAgent) agent;
		
        int x = (int) (res.getPosition().getX());
        int y = (int) (res.getPosition().getY());
        
        Ellipse2D oval = new Ellipse2D.Double(res.getPosition().getX(), res.getPosition().getY(), 10.0, 10.0);
       
		spatial = shapeFactory.createShape(oval);
	
		return spatial;
	}
	
	@Override
	public float getScale(Object object) {
		return 1.5f;
	}
	
	@Override
	public Color getColor(Object object) {
		return Color.BLUE;
	}
	
	@Override
	public Color getBorderColor(Object object) {
		return Color.WHITE;
	}
}
