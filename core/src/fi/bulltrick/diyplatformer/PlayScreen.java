package fi.bulltrick.diyplatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.prism.image.ViewPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bulltrick on 20.10.2015.
 */
public class PlayScreen implements Screen {
    DIYPlatformer diy;


    TextureRegion dpad;
    TextureRegion left;
    TextureRegion right;
    TextureRegion jump;
    TextureRegion cubeControl;
    TextureRegion cubeFollow;


    private Stage stage;
    private Skin skin;

    TextButton bUp;
    TextButton bLeft;
    TextButton bRight;
    TextButton bFly;

    boolean boolUp;
    boolean boolLeft;
    boolean boolRight;
    boolean boolFly;

    //BOX2D
    World world;
    Box2DDebugRenderer debugRenderer;
    Rectangle rectPlayer;
    Body playerBody;
    Texture playerTexture;
    Sprite playerSprite;
    Body platformBody;
    Sprite platformSprite;

    List<Body> platforms;
    List<Polygon> debugPolygons;

    int worldScale;

    OrthographicCamera camera;
    Viewport viewport;
    SpriteBatch batch;
    Texture background;

    ShapeRenderer shapeRenderer;

    public PlayScreen(DIYPlatformer diy) {
        this.diy = diy;
        //loadAssets();
    }

    public PlayScreen(DIYPlatformer diy, Texture background, List<Polygon> polygons) {
        this.diy = diy;

        diy.platform.SetOrientation("landscape");




        camera = new OrthographicCamera();
        camera.setToOrtho(false,background.getWidth(),background.getHeight());//1280,720);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(background.getWidth(),background.getHeight(),camera);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        //stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        stage = new Stage(new ScreenViewport());

        batch = new SpriteBatch();
        this.background = background;

        //BOX2D

        worldScale = 10;
        debugPolygons = new ArrayList<Polygon>();

        /*
        debugPolygons = polygons;
        for (Polygon p :
                debugPolygons) {
            p.setScale(worldScale,worldScale);
            /*
            p.setPosition(p.getX() * worldScale, p.getY() * worldScale);
            for (float f:
                 p.getVertices()) {

            }
        }*/

        platforms = new ArrayList<Body>();

        rectPlayer = new Rectangle(300,50,50,100);

        Pixmap pixmap = new Pixmap(60,60, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);

        pixmap.fillCircle(30, 30, 30);
        //pixmap.fillRectangle(0, 0, 500, 500);
        playerTexture = new Texture(pixmap);
        playerSprite = new Sprite(playerTexture);

        Box2D.init();
        world = new World(new Vector2(0,-10),true);
        debugRenderer = new Box2DDebugRenderer();

        // PLAYER DEFINITION
        BodyDef playerBodyDef = new BodyDef();
        playerBodyDef.type = BodyDef.BodyType.DynamicBody;
        playerBodyDef.position.set(40,80);
        playerBody = world.createBody(playerBodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(3f);

        FixtureDef playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = circle;
        playerFixtureDef.density = 0.5f;
        playerFixtureDef.friction = 0.4f;
        playerFixtureDef.restitution = 0.6f;

        Fixture playerFixture = playerBody.createFixture(playerFixtureDef);

        circle.dispose();

        Pixmap pixmap2 = new Pixmap(250,20, Pixmap.Format.RGBA8888);
        pixmap2.setColor(Color.BLUE);
        //pixmap2.fillRectangle(0,0,150,6);
        pixmap2.fill();
        Texture platformTexture = new Texture(pixmap2);
        platformSprite = new Sprite(platformTexture);

        BodyDef platformBodyDef = new BodyDef();
        platformBodyDef.type = BodyDef.BodyType.StaticBody;
        platformBodyDef.position.set(new Vector2(background.getWidth()/worldScale,background.getHeight()/worldScale));

        platformBody = world.createBody(platformBodyDef);
        PolygonShape platformBox = new PolygonShape();
        platformBox.setAsBox(2f,40.0f);

        platformBody.createFixture(platformBox, 0.0f);

        platformBox.dispose();

        createWorld(polygons);

    }

    private void drawPlatformDebugging(List<Body> bodies) {
        for (Body b :
                bodies) {

        }
    }

    private void drawDebugPolygons() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (Polygon p :
                debugPolygons) {
            shapeRenderer.polygon(p.getVertices());
        }

        shapeRenderer.end();

        //batch.draw(playerSprite,playerBody.getPosition().x*worldScale-playerSprite.getWidth()/2,playerBody.getPosition().y*worldScale-playerSprite.getHeight()/2);

    }

    private void createWorld(List<Polygon> polygons) {
        //List<Body> pList = new ArrayList<Body>();
        BodyDef pBodyDef = new BodyDef();

        pBodyDef.type = BodyDef.BodyType.StaticBody;
        for (Polygon p :
                polygons) {
            PolygonShape pShape = new PolygonShape();

            //pBodyDef.position.set((p.getY())*(1f/worldScale),(background.getHeight()-p.getX())*(1f/worldScale));
            //float y = ((p.getBoundingRectangle().getX()- background.getHeight()/2))+background.getWidth()/2;
            //float x = (-(p.getBoundingRectangle().getY()- background.getWidth()/2))+background.getHeight()/2;
            float x = p.getBoundingRectangle().getX();// - background.getWidth()/2;// + background.getWidth()/2;
            //x = -x;
            float y = p.getBoundingRectangle().getY() - background.getHeight()*2;// + background.getHeight()/2;
            y = -y;
            pBodyDef.position.set(x/worldScale,y/worldScale);
            //pBodyDef.position.set((y + p.getBoundingRectangle().getWidth() / 2f) * (1f / worldScale), (x + p.getBoundingRectangle().getHeight() / 2f) * (1f / worldScale));
            Body pBody = world.createBody(pBodyDef);
            float[] scaledVertices = p.getVertices();
            //float prevX = 0f;
            //float prevY = 0f;
            for (int i = 0; i+1 < p.getVertices().length; i++) {
                /*
                if (prevX != 0f) {
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    shapeRenderer.setColor(0,1,0,1);
                    shapeRenderer.line(p.getY()+prevX, background.getHeight()-p.getX()+prevY, p.getY()+p.getVertices()[i + 1],background.getHeight()-p.getX()+ p.getVertices()[i]);
                    shapeRenderer.end();
                }
                prevX = p.getVertices()[i+1];
                prevY = p.getVertices()[i];
                */
                scaledVertices[i] = p.getVertices()[i]*(1f/worldScale);
                scaledVertices[i+1] = p.getVertices()[i+1]*(1f/worldScale);
                i++;
            }
            /*
            int a = p.getVertices().length;

            if (a > 7 && a%7 < 3) {
                for (; i < 3 ; i++) {
                    scaledVertices[i] = p.getVertices()[i+1]*(1f/worldScale);
                    scaledVertices[i+1] = p.getVertices()[i]*(1f/worldScale);
                    i++;
                }
                pShape.set(scaledVertices);
                pBody.createFixture(pShape,1f);
            }


            int i = 0;
            int j = 0;
            int a = p.getVertices().length;
            int b = a % 7;
            int c = a / 7;
            int firstPoly;
            int secondPoly;
            if (a > 7) {
                if (b < 3) {
                    for (; i < 3 ; i++) {
                        scaledVertices[i] = p.getVertices()[i+1]*(1f/worldScale);
                        scaledVertices[i+1] = p.getVertices()[i]*(1f/worldScale);
                        i++;
                    }
                if (b < (7+3-b)) {
                    for (; i < 7+3-b ; i++) {
                        scaledVertices[i] = p.getVertices()[i+1]*(1f/worldScale);
                        scaledVertices[i+1] = p.getVertices()[i]*(1f/worldScale);
                        i++;
                    }
                    //firstPoly = 3;
                    //secondPoly = 7 + 3 - b;
                }
                else {
                    for (; i < b ; i++) {
                        scaledVertices[i] = p.getVertices()[i+1]*(1f/worldScale);
                        scaledVertices[i+1] = p.getVertices()[i]*(1f/worldScale);
                        i++;
                    }
                    //firstPoly = b;
                }
            }
            for (; i+1 < p.getVertices().length - ; i++) {
                scaledVertices[i] = p.getVertices()[i+1]*(1f/worldScale);
                scaledVertices[i+1] = p.getVertices()[i]*(1f/worldScale);
                i++;
            }

             */
            pShape.set(scaledVertices);

            pBody.createFixture(pShape,1f);
            platforms.add(pBody);
        }
    }

    @Override
    public void show() {
        //diy.platform.SetOrientation("landscape");
        //stage = new Stage(new ScreenViewport())
        Gdx.input.setInputProcessor(stage);
//Gdx.graphics.getWidth(),Gdx.graphics.getHeight()
        createScene2DUI();

    }

    private void createScene2DUI() {
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();

        skin = new Skin();
        skin.add("white", new Texture(pixmap));

        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        skin.add("default", font);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.RED);

        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        bUp = new TextButton("^",textButtonStyle);
        bLeft = new TextButton("<",textButtonStyle);
        bRight = new TextButton(">",textButtonStyle);
        bFly = new TextButton("FLY",textButtonStyle);

        //bUp.setDebug(true);
        //bUp.setBounds(stage.getHeight() - stage.getHeight() / 10 - bUp.getHeight(), stage.getWidth() / 10 ,stage.getHeight()/10,stage.getHeight()/10);
        //bUp.setBounds(stage.getWidth() - stage.getWidth() / 10 - bUp.getWidth(), stage.getWidth() / 10 ,stage.getWidth()/10,stage.getHeight()/10);

        bUp.setSize(stage.getHeight()/15,stage.getHeight()/15);
        bLeft.setSize(stage.getHeight()/15, stage.getHeight()/15);
        bRight.setSize(stage.getHeight()/15, stage.getHeight()/15);
        bFly.setSize(stage.getHeight()/15, stage.getHeight()/15);

        //bUp.setPosition(stage.getWidth() - stage.getWidth() / 10 - bUp.getWidth(), stage.getHeight() / 10 );
        //bLeft.setPosition(stage.getWidth()/10, stage.getHeight()/10);
        //bRight.setPosition(stage.getWidth()/10 + bLeft.getWidth() + bRight.getWidth()/10, stage.getHeight() / 10 );
        //bFly.setPosition(stage.getWidth() - stage.getWidth() / 10 - bFly.getWidth(), stage.getHeight()*0.9f );

        bUp.setPosition(stage.getHeight()-stage.getHeight()/10- bUp.getWidth()/2,stage.getWidth()/10-bUp.getHeight()/2);
        bLeft.setPosition(stage.getHeight()/10-bLeft.getWidth()/2, stage.getWidth()/10-bLeft.getHeight()/2);
        bRight.setPosition(bLeft.getX()+bRight.getWidth()*2, stage.getWidth()/10-bRight.getHeight()/2);
        bFly.setPosition(stage.getHeight()-stage.getHeight()/10-bFly.getWidth()/2,stage.getWidth()-stage.getWidth()/10-bFly.getHeight()/2);

        bUp.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolUp = true;
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boolUp = false;
            }
        });

        bLeft.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                boolLeft = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                boolLeft = false;
            }
        });

        bRight.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                boolRight = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                boolRight = false;
            }
        });

        bFly.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                boolFly = true;
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                boolFly = false;
            }
        });

        stage.addActor(bUp);
        stage.addActor(bLeft);
        stage.addActor(bRight);
        stage.addActor(bFly);
    }

    private void loadAssets () {
        Texture texture = new Texture(Gdx.files.internal("controls.png"));
        TextureRegion[] buttons = TextureRegion.split(texture, 64, 64)[0];
        left = buttons[0];
        right = buttons[1];
        jump = buttons[2];
        cubeControl = buttons[3];
        cubeFollow = TextureRegion.split(texture, 64, 64)[1][2];
        dpad = new TextureRegion(texture, 0, 64, 128, 128);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 6, 2);
        debugRenderer.render(world, camera.combined);

        Vector2 pos = playerBody.getPosition();
        Vector2 vel = playerBody.getLinearVelocity();
        if (boolLeft) {
            playerBody.applyLinearImpulse(-8.80f,0,pos.x,pos.y,true);
        }
        if (boolRight) {
            playerBody.applyLinearImpulse(8.80f,0,pos.x,pos.y,true);
        }
        if (boolUp) {
            playerBody.setLinearVelocity(vel.x,0);
            playerBody.applyLinearImpulse(0,80f,pos.x,pos.y,true);
            //boolUp = false;
        }


        camera.update();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (!boolFly) batch.draw(background, 0, 0);
        batch.draw(platformSprite,platformBody.getPosition().x*worldScale-platformSprite.getWidth() / 2, platformBody.getPosition().y * worldScale - platformSprite.getHeight() / 2);
        batch.draw(playerSprite, playerBody.getPosition().x * worldScale-playerSprite.getWidth()/2,playerBody.getPosition().y*worldScale-playerSprite.getHeight() / 2);
        batch.end();

        //drawDebugPolygons();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (Polygon p :
                debugPolygons) {
            for (float f :
                    p.getVertices()) {

            }
            shapeRenderer.polygon(p.getVertices());
        }

        shapeRenderer.end();

        stage.getViewport().apply();
        stage.act(delta);
        if (!boolFly) stage.draw();

        /*
        batch.begin();
        batch.draw(dpad, 0, 0);
        batch.draw(cubeFollow, 480 - 64, 320 - 138);
        batch.draw(cubeControl, 480 - 64, 320 - 64);
        batch.end();*/
    }

    @Override
    public void resize(int width, int height) {
        //stage.getViewport().setScreenSize(height, width);
        stage.getViewport().update(width,height,true);
        viewport.update(width,height,true);
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
}
