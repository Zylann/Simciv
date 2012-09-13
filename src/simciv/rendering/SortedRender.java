package simciv.rendering;

import java.util.ArrayList;
import java.util.TreeMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import backend.IRenderable;


public class SortedRender
{
	private TreeMap<Integer, ArrayList<IRenderable> > renders;
	
	public SortedRender()
	{
		renders = new TreeMap<Integer, ArrayList<IRenderable> >();
	}
	
	public void add(IRenderable render)
	{
		int d = render.getDrawOrder();
		ArrayList<IRenderable> list = renders.get(d);
		if(list == null)
		{
			list = new ArrayList<IRenderable>();
			renders.put(d, list);
		}
		list.add(render);
	}
	
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		for(ArrayList<IRenderable> list : renders.values())
		{
			for(IRenderable r : list)
				r.render(gc, game, gfx);
		}
	}
	
	public void clear()
	{
		renders.clear();
	}
	
}


