package fi.bulltrick.diyplatformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by Bulltrick on 29.9.2015.
 */
public class MainMenu implements Screen {

    Stage stage;
    Table table;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;

    Button b;
    Button.ButtonStyle buttonStyle;

    OrthographicCamera camera;
    DIYPlatformer diy;

    public MainMenu(DIYPlatformer diy) {
        //camera = new OrthographicCamera();
        //camera.setToOrtho(false, 800, 480);

        this.diy = diy;

    }

    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //camera.update();
        //stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void show() {

        //this.diy.platform.SetOrientation("landscape");
        //stage = new Stage(new ExtendViewport(640, 480, 800, 480));
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setDebug(true);



        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("diy-texture-v1.pack"));
        skin.addRegions(buttonAtlas);
        /*
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("create");
        buttonStyle.down = skin.getDrawable("play");

        b = new Button(buttonStyle);
        Button b2 = new Button(buttonStyle);
        table.add(b);
        table.row();
        table.add(b2);
        */
        final Button create = new Button(skin.getDrawable("create"));
        final Button play = new Button(skin.getDrawable("play"));

        create.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                diy.setScreen(new CreateLevel(diy));
            }
        });

        table.defaults().pad(25);

        table.add(create);
        table.row();
        table.add(play);


    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        skin.dispose();
        buttonAtlas.dispose();
        font.dispose();
        stage.dispose();
    }
}
