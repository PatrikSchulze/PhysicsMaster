package main;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
 
public class Game implements ApplicationListener
{
	public static final int PIXELS_PER_METER = 16;
	public static final float PARALLAX_FACTOR = 10.0f;
	private static final float MIN_ZOOM = 0.4f;
	private OrthographicCamera camera;
	private OrthographicCamera hud_camera;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
    boolean 	gamerun 			= true;
    private float targetZoom;
    private World mWorld;
	private Box2DDebugRenderer debugRenderer;
    private Vector3 calcV3 = new Vector3(0,0,1);
    private CameraController controller;
    private MyGestureDetector gestureDetector;
    private ArrayList<Element> elements;
    private Texture texBox64;
    private Texture texBox32;
    int numberOfFingers = 0;
    int fingerOnePointer;
    int fingerTwoPointer;
    Vector2 fingerOne = new Vector2();
    Vector2 fingerTwo = new Vector2();
 
	@Override
	public void create()
	{
		Pixmap icon128 = new Pixmap(Gdx.files.internal("content/grafx/icon128.png"));
		Pixmap icon64  = new Pixmap(Gdx.files.internal("content/grafx/icon32.png"));
		Pixmap icon32  = new Pixmap(Gdx.files.internal("content/grafx/icon32.png"));
		Pixmap icon16  = new Pixmap(Gdx.files.internal("content/grafx/icon16.png"));
		Gdx.graphics.setIcon(new Pixmap[] { icon128, icon64, icon32, icon16 });
		System.out.println(Display.getTitle()+"\n");
		spriteBatch 	= new SpriteBatch();
		font 			= new BitmapFont(Gdx.files.internal("content/font/sans_20.fnt"), true);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		font.setColor(Color.BLACK);
		
		debugRenderer = new Box2DDebugRenderer();
		mWorld = new World(new Vector2(0,9.81f), true); // create box2d world  
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true);
		hud_camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hud_camera.setToOrtho(true);
		targetZoom = camera.zoom;
		
		texBox64 = new Texture(Gdx.files.internal("content/box64.png"));
		texBox64.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		texBox32 = new Texture(Gdx.files.internal("content/box32.png"));
		texBox32.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		elements = new ArrayList<Element>();
		
		for (int j=-5000;j<=Gdx.graphics.getWidth()+5000;j+=64)
		{
			Element e = new Element(texBox64, j+32, Gdx.graphics.getHeight()-32, mWorld, BodyDef.BodyType.StaticBody);
			e.setColor(0.6f, 0.6f, 0.6f, 1);
			elements.add(e);
		}
		
		
		
		controller 		= new CameraController();
		gestureDetector = new MyGestureDetector(20, 0.5f, 2, 0.15f, controller);
        Gdx.input.setInputProcessor(gestureDetector);
		
		
	}
 
	@Override
	public void resume()
	{
	}
	
	private boolean isSpriteWithinFrustum(Sprite spr)
	{
		float size = (spr.getWidth() > spr.getHeight()) ? spr.getWidth() : spr.getHeight();
		
		calcV3.x = spr.getX();
		calcV3.y = spr.getY();
		if (camera.frustum.sphereInFrustum(calcV3, size))
		{
			return true;
		}
		
		return false;
	}
	
	public void checkInputs()
	{
		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			Gdx.app.exit();
		}
		
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
		{
			for (int i = elements.size() - 1; i >= 0; i--)
			{
				Element e = elements.get(i);
				if (e.getBodyType() != BodyType.StaticBody && !e.isWithinFrustum(camera))
				{
					e.dead = true;
				}
			}
			
		}
		
		if (Gdx.input.isKeyPressed(Keys.NUM_0))
		{
			for (int i = elements.size() - 1; i >= 0; i--)
			{
				Element e = elements.get(i);
				if (e.getBodyType() != BodyType.StaticBody)
				{
					e.dead = true;
				}
			}
		}
    	
		if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
    	{
    		Element e = new Element(texBox32, getRelativeMouseX(), getRelativeMouseY(), mWorld, BodyDef.BodyType.DynamicBody);
    		MassData mass = new MassData();
    		mass.mass = 80f;
    		mass.center.set(0, 0);
    		mass.I = 12.0f;
    		e.setMassData(mass);
    		elements.add(e);
    	}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE))
		{
			for (int i=0;i<20;i++)
			{
				Element e = new Element(texBox32, getRelativeMouseX(), getRelativeMouseY(), mWorld, BodyDef.BodyType.DynamicBody);
	    		MassData mass = new MassData();
	    		mass.mass = 80f;
	    		mass.center.set(0, 0);
	    		mass.I = 12.0f;
	    		e.setMassData(mass);
	    		elements.add(e);
			}
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
		{
			camera.position.add(-Gdx.input.getDeltaX() * camera.zoom, -Gdx.input.getDeltaY() * camera.zoom, 0);
		}
	}

	
	public void adjustCamera()
	{
		if (Math.abs(targetZoom-camera.zoom) > 0.01f)
		{
			camera.zoom+=(targetZoom-camera.zoom)/20.0f;
		}

	}

	
	private void computeMainGame()
    {
    	
    }


	private int getRelativeMouseX()
	{
		return (int)(Gdx.input.getX()*camera.zoom)+(int)(camera.position.x)-(int)((camera.viewportWidth*camera.zoom)/2.0f);
	}
	
	private int getRelativeMouseY()
	{
		return (int)(Gdx.input.getY()*camera.zoom)+(int)(camera.position.y)-(int)((camera.viewportHeight*camera.zoom)/2.0f);
	}
 
	@Override
	public void render()
	{
		checkInputs();
		computeMainGame();
		mWorld.step(0.01f, 1, 1);
		
		adjustCamera();
		
		camera.update();
		
        
		Gdx.graphics.getGL10().glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
//		//RENDERING
		spriteBatch.begin();
		
		
		spriteBatch.setProjectionMatrix(camera.combined);
		
		
		for (int i = elements.size() - 1; i >= 0; i--)
		{
			Element e = elements.get(i);
//			if (e.getBodyType() != BodyType.StaticBody && !e.isWithinFrustum(camera)) e.dead = true;
			
			if (e.dead)
			{
				mWorld.destroyBody(e.uBody);
				elements.remove(i);
			}
			else
			{
				e.compute();
				e.draw(spriteBatch);
			}
		}
		
		//1 M = 16 pixel
		for (int y = 0; y < Gdx.graphics.getHeight()/PIXELS_PER_METER; y++) // y in METER, every meter
		{
			StringBuilder msg = new StringBuilder("-");
			if (y%5 == 0) { msg.append("  "); msg.append(y); msg.append("m"); }
			font.draw(spriteBatch, msg.toString(), 5, (y*PIXELS_PER_METER));
		}
		
//		ENABLE ONLY IN DEBUG MODE!
//		VERY CPU CONSUMPTING!
//		debugRenderer.render( mWorld, camera.combined.scl(PIXELS_PER_METER) );
//		camera.combined.scl(1.0f/(float)PIXELS_PER_METER);
		

		spriteBatch.setProjectionMatrix(hud_camera.combined);
			font.draw(spriteBatch, "FPS: "+Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth()-10-font.getBounds("FPS: 00").width,  10);
			font.draw(spriteBatch, "Elements: "+elements.size(), Gdx.graphics.getWidth()-10-font.getBounds("Elements: "+elements.size()).width,  30);
			font.draw(spriteBatch, "World Bodies: "+mWorld.getBodyCount(), Gdx.graphics.getWidth()-10-font.getBounds("World Bodies: "+mWorld.getBodyCount()).width,  50);
		spriteBatch.end();
	}
	
	private float getDistanceExact(Vector2 one, Vector2 two)
	{
		int dx = (int)(one.x-two.x);
		int dy = (int)(one.y-two.y);
		return (float)Math.sqrt((int)(dx*dx + dy*dy));
	}
	
	private float getDistanceFast(Vector2 one, Vector2 two)
	{
		int dx = (int)(one.x-two.x);
		int dy = (int)(one.y-two.y);
		return SquareRoot.fastSqrt((int)(dx*dx + dy*dy));
	}
 
	@Override
	public void resize(int width, int height)
	{
	}
 
	@Override
	public void pause()
	{
		 numberOfFingers = 0;
	}
 
	@Override
	public void dispose() {
	}
	
	public static final int getRandom(int minimum, int maximum)
    {
        return (int)(Math.random()*((maximum+1)-minimum)+minimum);
    }
	
	public static final float getFloatRandom(float minimum, float maximum)
    {
        return ((float)Math.random()*((maximum+1.0f)-minimum)+minimum);
    }
 
	class CameraController implements GestureListener
	{
        @Override
        public boolean touchDown (int x, int y, int pointer)
        {
            return false;
        }

        @Override
        public boolean tap (int x, int y, int count)
        {
            return false;
        }

        @Override
        public boolean longPress (int x, int y)
        {
        	return false;
        }

        @Override
        public boolean pan (int x, int y, int deltaX, int deltaY)
        {
            return false;
        }

        @Override
        public boolean zoom (float originalDistance, float currentDistance)
        {
            return false;
        }

		@Override
		public boolean pinch(Vector2 arg0, Vector2 arg1, Vector2 arg2, Vector2 arg3)
		{
			return false;
		}

		@Override
		public boolean fling(float arg0, float arg1)
		{
			return false;
		}
	}
	
	private class MyGestureDetector extends GestureDetector
	{
		public MyGestureDetector(GestureListener listener)
		{
			super(listener);
		}

		public MyGestureDetector(int halfTapSquareSize, float tapCountInterval, float longPressDuration, float maxFlingDelay, GestureListener listener)
		{
			super(halfTapSquareSize, tapCountInterval, longPressDuration, maxFlingDelay, listener);
		}
		
		@Override
        public boolean touchUp(int x, int y, int pointer, int button)
        {
			numberOfFingers--;
			
			if (pointer == fingerOnePointer)
			{
				fingerOne.set(0, 0);
			}
			
			if (pointer == fingerTwoPointer)
			{
				fingerTwo.set(0, 0);
			}
			 
			// just some error prevention... clamping number of fingers (ouch! :-)
			if(numberOfFingers<0)
			{
				numberOfFingers = 0;
			}
        	return super.touchUp(x, y, pointer, button);
        }
		
		public boolean touchDragged(int x, int y, int pointer)
		{
			if (pointer == fingerOnePointer)
			{
				fingerOne.set(x, y);
			}
			
			if (pointer == fingerTwoPointer)
			{
				fingerTwo.set(x, y);
			}
			return super.touchDragged(x, y, pointer);
		}
        
        @Override
        public boolean touchDown (int x, int y, int pointer, int button)
        {
        	numberOfFingers++;
        	if(numberOfFingers == 1)
        	{
        		fingerOnePointer = pointer;
        		fingerOne.set(x, y);
        	}
        	else if(numberOfFingers == 2)
        	{
        		fingerTwoPointer = pointer;
        		fingerTwo.set(x, y);
        	}
        	
        	return super.touchDown(x, y, pointer, button);
        }
		
		@Override public boolean scrolled(int amount) // MOUSE WHEEL
		{

			if (amount > 0) // zoom out
			{
				if (targetZoom < (50.0f))
					targetZoom+=0.2f;
			}
			
			if (amount < 0)
			{
				if (targetZoom >= MIN_ZOOM+0.2f)
					targetZoom-=0.2f;
			}
			
			return super.scrolled(amount);
		}
		
	}
}