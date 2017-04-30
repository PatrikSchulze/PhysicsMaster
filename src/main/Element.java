package main;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Element extends Sprite
{
	boolean dead = false;
	protected Body uBody;
	private Vector3 calcV3 = new Vector3(0,0,1);

	public Element(TextureRegion treg, float _x, float _y, World mWorld, BodyType type)
	{
		super(treg);
		setX(_x);
		setY(_y);
		createBody(mWorld, type);
	}
	
	public Element(Texture t, float _x, float _y, World mWorld, BodyType type)
	{
		super(t);
		setX(_x);
		setY(_y);
		createBody(mWorld, type);
	}
	
	public void setMassData(MassData massData)
	{
		uBody.setMassData(massData);
	}
	
	public BodyType getBodyType()
	{
		return uBody.getType();
	}
	
	private void createBody(World mWorld, BodyType type)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(getX()/Game.PIXELS_PER_METER, getY()/Game.PIXELS_PER_METER);
 		uBody = mWorld.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(this.getWidth() / ( 2 * Game.PIXELS_PER_METER), this.getHeight() / (2 * Game.PIXELS_PER_METER));
		uBody.setFixedRotation(false);
		uBody.createFixture(shape, 100);
		uBody.setTransform(getX()/Game.PIXELS_PER_METER,getY()/Game.PIXELS_PER_METER,  (float) ((this.getRotation()/180.0f)*Math.PI));
	}
	
	public void compute()
	{
		if(uBody.isAwake())
		{
			setPosition((uBody.getPosition().x*(float)Game.PIXELS_PER_METER)-(getWidth()/2.0f), (uBody.getPosition().y*(float)Game.PIXELS_PER_METER)-(getHeight()/2.0f));
			super.setRotation((float)(uBody.getAngle()/Math.PI *180.0f));		
		}
	}
	
	public void setRotation(float degrees)
	{
		uBody.setTransform(uBody.getPosition(), (float) (degrees/180.0*Math.PI));
		compute();
	}
	
	public boolean isWithinFrustum(OrthographicCamera cam)
	{
		float biggestSize = (getWidth() > getHeight()) ? getWidth() : getHeight();
		
		calcV3.x = getX();
		calcV3.y = getY();
		if (cam.frustum.sphereInFrustum(calcV3, biggestSize))
		{
			return true;
		}
		
		return false;
	}
}
