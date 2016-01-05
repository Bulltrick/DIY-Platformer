package fi.bulltrick.diyplatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;

import java.io.Reader;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Invert;
import Catalano.Imaging.Filters.Threshold;

/**
 * Created by Bulltrick on 29.9.2015.
 */
public class CreateLevel implements Screen {
    DIYPlatformer diy;
    Label lblDebug;

    Image imgOriginal;
    Image imgBinary;
    String fileUri;
    Stage stage;
    Table table;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;
    ScrollPane scrollpane;
    Table tableforscroll;


    TextButton bTakePicture;
    TextButton bConvert;
    TextButton bBlobs;
    TextButton bTestlevel;

    Texture background;

    public CreateLevel(DIYPlatformer diy) {
        this.diy = diy;
    }

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        //table.setFillParent(true);
        scrollpane = new ScrollPane(table);

        //stage.addActor(table);
        table.setDebug(true);


        tableforscroll = new Table();
        tableforscroll.setFillParent(true);
        tableforscroll.add(scrollpane).fill().expand();
        stage.addActor(tableforscroll);

        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();

        skin = new Skin();
        skin.add("white", new Texture(pixmap));

        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle LblStyle = new Label.LabelStyle(font,Color.WHITE);
        lblDebug = new Label(null,LblStyle);

        bTakePicture = new TextButton("Take a Picture", textButtonStyle);
        bTakePicture.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fileUri = diy.platform.TakePicture();
                fileUri = fileUri.substring(fileUri.indexOf(":") + 3);
                lblDebug.setText(fileUri);
                //lblDebug.setText(fileUri = fileUri.substring(fileUri)));


            }
        });

        bConvert = new TextButton("Convert image", textButtonStyle);
        bConvert.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                background = diy.platform.loadImage(800,0); //(720, 480); // x ja y halutun kentän koon mukaan...
                imgOriginal = new Image(background);
                imgOriginal.setScaling(Scaling.fill);
                refreshTable();
            }
        });

        bBlobs = new TextButton("Count blobs", textButtonStyle);
        bBlobs.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Texture temp = diy.platform.filterImage(95);
                /*
                Pixmap pixmap = new Pixmap(temp.getWidth(),temp.getHeight(), Pixmap.Format.RGBA8888);
                pixmap.setColor(Color.BLUE);
                List<Polygon> polygons = diy.platform.getPolygons();
                for (Polygon p :
                        polygons) {
                    for (int i = 0 ; p.getVertices().length >= i+3 ;) {
                        pixmap.drawLine((int)p.getVertices()[i++],(int)p.getVertices()[i++],(int)p.getVertices()[i++],(int)p.getVertices()[i++]);
                        i = i-2;
                    }
                }


                temp.draw(pixmap,0,0);
                */
                imgBinary = new Image(temp);
                imgBinary.setScaling(Scaling.fill);
                refreshTable();


            }
        });

        bTestlevel = new TextButton("Test level", textButtonStyle);
        bTestlevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //diy.platform.SetOrientation("landscape");
                diy.setScreen(new PlayScreen(diy, background,diy.platform.getPolygons()));
            }
        });

        table.defaults().pad(25);

        refreshTable();



    }

    private void refreshTable() {
        table.clearChildren();

        table.add(bTakePicture);
        table.row();
        table.add(bConvert);
        table.row();
        table.add(bBlobs);
        table.row();
        table.add(lblDebug);
        table.row();
        table.add(imgOriginal);
        table.row();
        table.add(imgBinary);
        table.row();
        table.add(bTestlevel);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //camera.update();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void extractBlobs() {

    }

    public void loadImage(String uri) {

        FileHandle file = Gdx.files.absolute(uri);
        Texture texture1 = new Texture(Gdx.files.absolute(uri));
        //Gdx.files.absolute(uri).moveTo(Gdx.files.local("temp.jpg"));
        /*
        if (file.exists()) {
            skin.add("image", new Texture(Gdx.files.local("temp.jpg")));
            imgTexture.setDrawable(skin, "image");
            table.add(imgTexture);

        }*/


        Reader reader = file.reader();

        FastBitmap fastBitmap;

        fastBitmap = new FastBitmap();
/*
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(uri));
        } catch (IOException e) {

        }
        fastBitmap = new FastBitmap(bufferedImage);
*/
        fastBitmap.toGrayscale();

        Threshold filterThreshold = new Threshold(65);
        filterThreshold.applyInPlace(fastBitmap);

        Invert filterInvert = new Invert();
        filterInvert.applyInPlace(fastBitmap);





    }

}
