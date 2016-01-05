package fi.bulltrick.diyplatformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class DIYPlatformer extends Game {
	SpriteBatch batch;
	OrthographicCamera camera;
	Rectangle rectPlayer;
	Texture badlogic;

	//BOX2D
	World world;
	Box2DDebugRenderer debugRenderer;
	Body body;

	MainMenu mainMenu;

	Platform platform;
	public DIYPlatformer(Platform platform) {
		this.platform = platform;
	}

	@Override
	public void create () {

		//mainMenu = new MainMenu(this);

		//camera = new OrthographicCamera();
		//camera.setToOrtho(false, 800, 480);
		/*
		batch = new SpriteBatch();
		badlogic = new Texture(Gdx.files.internal("badlogic.jpg"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		rectPlayer = new Rectangle(120+40,480-120, 40, 30);

		// BOX2D
		Box2D.init();
		world = new World(new Vector2(0,-10), true);
		debugRenderer = new Box2DDebugRenderer();

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(20, 480 - 20);
		body = world.createBody(bodyDef);

		CircleShape circle = new CircleShape();
		circle.setRadius(6f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;

		Fixture fixture = body.createFixture(fixtureDef);

		circle.dispose();

		BodyDef platformBodyDef = new BodyDef();
		platformBodyDef.position.set(new Vector2(0,20));
		Body platformBody = world.createBody(platformBodyDef);
		PolygonShape platformBox = new PolygonShape();
		platformBox.setAsBox(100f,5.0f);
		platformBody.createFixture(platformBox, 0.0f);
		platformBox.dispose();
		*/
		setScreen(new MainMenu(this));
	}

	@Override
	public void render () {
		super.render();

		if (Gdx.input.isTouched()) {

		}
		//mainMenu.render();
		/*
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(badlogic, rectPlayer.x, rectPlayer.y);
		batch.end();

		// Box2d



		world.step(1/60f, 6, 2);
		debugRenderer.render(world, camera.combined);

		if (Gdx.input.isTouched()) {
			body.applyLinearImpulse(10.0f,0,body.getPosition().x,body.getPosition().y, true);
		}
		*/

	}

	public void dispose() {
		//batch.dispose();

	}
}
