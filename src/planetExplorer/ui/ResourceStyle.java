package planetExplorer.ui;

import planetExplorer.environment.Resource;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

import java.awt.Color;
import java.awt.Polygon;


public class ResourceStyle extends DefaultStyleOGL2D {
	private final int size = 5;
	
	@Override
	public VSpatial getVSpatial(Object resource, VSpatial spatial) {
		Resource res = (Resource) resource;
		
        int x = (int) (res.getPosition().getX());
        int y = (int) (res.getPosition().getY());
		
        Polygon triangle = new Polygon(new int[] { x - size, x, x + size }, new int[] { y + size, y - size, y + size },
                3);
		spatial = shapeFactory.createShape(triangle);
	
		return spatial;
	}
	
	@Override
	public Color getColor(Object object) {
		Resource res = (Resource) object;
		return res.getColor();
	}
	
	@Override
	public Color getBorderColor(Object object) {
		return Color.WHITE;
	}

	@Override
	public float getScale(Object object) {
		return 1.5f;
	}
}
