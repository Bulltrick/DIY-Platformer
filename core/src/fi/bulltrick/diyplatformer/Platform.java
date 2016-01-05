package fi.bulltrick.diyplatformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;

import java.util.List;

import Catalano.Imaging.Tools.Blob;

/**
 * Created by Bulltrick on 29.9.2015.
 */
public interface Platform {
    public void SetOrientation(String string);
    public String TakePicture();
    public Texture loadImage(int x, int y);
    public Texture filterImage(int threshold);
    public List<Blob> getBlobs();
    public List<Polygon> getPolygons();
}
