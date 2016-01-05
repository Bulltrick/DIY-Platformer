package fi.bulltrick.diyplatformer.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Catalano.Core.IntPoint;
import Catalano.Imaging.Concurrent.Filters.Threshold;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Invert;
import Catalano.Imaging.Tools.Blob;
import Catalano.Imaging.Tools.BlobDetection;
import Catalano.Math.Geometry.GrahamConvexHull;
import fi.bulltrick.diyplatformer.Platform;

/**
 * Created by Bulltrick on 29.9.2015.
 */
public class PlatformAndroid extends AndroidApplication implements Platform {
    private Activity activity;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private Bitmap picture;
    private boolean pictureTaken;
    private boolean pictureResultOK;
    private List<Blob> blobs;
    private List<Polygon> polygons;

    @Override
    public void SetOrientation(String string){
        if (string == "landscape") {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (string == "portrait") {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public String TakePicture() {
        pictureTaken = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(); // create a file to save the image

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name


        activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        //while (!pictureTaken) {}
        return fileUri.toString();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES), "DiyPlatformer");

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "DiyPlatformer");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("DiyPlatformer", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                pictureResultOK = true;
                pictureTaken = true;

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                pictureResultOK = false;
                pictureTaken = true;
            } else {
                // Image capture failed, advise user
                pictureResultOK = false;
                pictureTaken = true;
            }
        }

    }

    @Override
    public Texture loadImage(int MinWidth, int MinHeight) {
        picture = decodeSampledBitmapFromUri(fileUri, MinWidth, MinHeight);

        Texture tex = new Texture(picture.getWidth(), picture.getHeight(), Pixmap.Format.RGBA8888);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, picture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return tex;
    }

    public static Bitmap decodeSampledBitmapFromUri(Uri fUri,
                                                    int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fUri.getPath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fUri.getPath(), options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public Texture filterImage(int threshold) {
        Bitmap tempPicture;
        tempPicture = picture.copy(picture.getConfig(),true);
        FastBitmap fBM = new FastBitmap();
        fBM.setImage(tempPicture);

        fBM.toGrayscale();

        Threshold tFilter = new Threshold(threshold);
        tFilter.applyInPlace(fBM);


        Invert iFilter = new Invert();
        iFilter.applyInPlace(fBM);

        /*
        BlobsFiltering bFilter = new BlobsFiltering();
        bFilter.setMinArea(25);
        bFilter.applyInPlace(fBM);
        */

        BlobDetection blobDetection = new BlobDetection();
        blobDetection.setFilterBlob(true);
        blobDetection.setMinArea(10);
        blobs = blobDetection.ProcessImage(fBM);

        /*
        for (Blob b : blobs) {
            GrahamConvexHull hullFinder = new GrahamConvexHull();
            List<IntPoint> hull = hullFinder.FindFull(b.getPoints());
        }*/

        Bitmap bitmap = fBM.toBitmap();


        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i< pixels.length;i++) {
            int pixel = pixels[i];
            pixels[i] = (pixel << 8) | ((pixel >> 24) & 0xFF);
        }
        Pixmap pixmap = new Pixmap(bitmap.getWidth(),bitmap.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.getPixels().asIntBuffer().put(pixels);
        pixmap.setBlending(Pixmap.Blending.SourceOver);


        Texture tex = new Texture(bitmap.getWidth(),bitmap.getHeight(), Pixmap.Format.RGBA8888);

        pixmap.setColor(Color.RED);

        polygons = createPolygons();
        for (Polygon p :
                polygons) {
            int i = 0;
            for (; p.getVertices().length >= i+3 ;) {
                pixmap.drawLine((int)p.getVertices()[1+i],(int)p.getVertices()[i],(int)p.getVertices()[3+i],(int)p.getVertices()[2+i]);
                i = i+2;
            }
            pixmap.drawLine((int)p.getVertices()[1],(int)p.getVertices()[0],(int)p.getVertices()[1+i],(int)p.getVertices()[i]);
        }

        tex.draw(pixmap, 0, 0);
        //Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(), Pixmap.Format.RGBA8888);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
        //GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, pixmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();

        return tex;


    }

    @Override
    public List<Blob> getBlobs() {
        if (blobs == null); //Throw exception
        return blobs;
    }

    @Override
    public List<Polygon> getPolygons() {
        if (!polygons.isEmpty()) return polygons;
        else return createPolygons();
    }

    public List<Polygon> createPolygons() {
        List<Polygon> polygons = new ArrayList<Polygon>();


        String str = "";
        for (Blob b : blobs) {
            GrahamConvexHull hullFinder = new GrahamConvexHull();
            List<IntPoint> hull = hullFinder.FindFull(b.getPoints());

            str += "x:"+b.getBoundingBox().getX() + " y:" + b.getBoundingBox().getY() + " - ";
            //FLATANGLEOPTIMIZATION!

            //int i = hull.size()*2;
            //float[] vertices = new float[i];
            List<Integer> vertices = new ArrayList<Integer>();
            int j = 0;
            IntPoint temp = new IntPoint();
            int pointsMerged = 0;
            boolean first = true;
            for (IntPoint point :
                    hull) {
                if (first) {
                    first = false;
                    temp = point;
                    continue;
                }
                if (pointsMerged > 20 || temp.DistanceTo(point) >= 10) {
                    vertices.add(temp.x);
                    vertices.add(temp.y);
                    j = j+2;
                    temp = point;
                    pointsMerged = 0;
                }
                else {
                    temp.x = (temp.x + point.x)/2;
                    temp.y = (temp.y + point.y)/2;
                    pointsMerged++;
                }

            }
            if (vertices.size() < 6) continue;
            float[] verticeArray =  new float[vertices.size()];
            int i = 0;
            for (Integer f :
                    vertices) {
                verticeArray[i] = (f != null ? f : 0);
                i++;
            }
            //float[] cleanedVertices = new float[j];
            Polygon p = new Polygon(verticeArray);
            p.setPosition(b.getBoundingBox().getX(),b.getBoundingBox().getY()); //TODO disposition?
            polygons.add(p);

        }
        log("blobs",str);
        return polygons;
    }
}
