package main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class RunDesktop
{
	public static void main(String args[])
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 0;
		config.resizable = false;

		config.width = 1280;
		config.height = 720;
		
		config.title = "Physics Master";
		
		new LwjglApplication(new Game(), config);
	}
}
