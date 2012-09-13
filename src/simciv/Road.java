package simciv;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import backend.Direction2D;

import simciv.content.Content;

public class Road
{
	public static final int cost = 1;
	public static final Color minimapColor = new Color(224, 224, 224);
	
	static Image tileset;
	static Image tiles[];
	
	public static void initialize() throws SlickException
	{
		tileset = Content.sprites.terrainRoad;
		
		int nbTiles = 16;		
		int tilePos[][] =
		{
				{3, 3}, // 0
				{2, 3}, // 1
				{0, 3}, // 2
				{1, 3}, // 3
				{3, 2}, // 4
				{2, 2}, // 5
				{0, 2}, // 6
				{1, 2}, // 7
				{3, 0}, // 8
				{2, 0}, // 9
				{0, 0}, // 10
				{1, 0}, // 11
				{3, 1}, // 12
				{2, 1}, // 13
				{0, 1}, // 14
				{1, 1}  // 15
		};
		
		for(int i = 0; i < nbTiles; i++)
		{
			tilePos[i][0] *= Game.tilesSize;
			tilePos[i][1] *= Game.tilesSize;
		}
		
		tiles = new Image[nbTiles];		
		for(int i = 0; i < nbTiles; i++)
		{
			tiles[i] = tileset.getSubImage(
					tilePos[i][0], tilePos[i][1], Game.tilesSize, Game.tilesSize);
		}
	}
	
	/**
	 * Returns an index from 4 bits.
	 * If one bit is set only, the index will be a power or 2.
	 * @param west
	 * @param east
	 * @param north
	 * @param south
	 * @return : index
	 */
	private static byte getIndex(boolean west, boolean east, boolean north, boolean south)
	{
		byte i = 0;
		if(west)
			i |= 1; // 0b00000001
		if(east)
			i |= 2; // 0b00000010
		if(north)
			i |= 4; // 0b00000100
		if(south)
			i |= 8; // 0b00001000
		return i;
	}
	
	public static byte getIndex(MapGrid map, int x, int y)
	{
		return getIndex(
				map.isRoad(x-1, y),
				map.isRoad(x+1, y),
				map.isRoad(x, y-1),
				map.isRoad(x, y+1));
	}
	
	public static boolean isAvailableDirections(MapGrid map, int x, int y)
	{
		return getIndex(map, x, y) != 0;
	}
	
	public static List<Byte> getAvailableDirections(MapGrid map, int x, int y)
	{
		return getAvailableDirections(map, x, y, (byte)-1);
	}
	
	public static List<Byte> getAvailableDirections(MapGrid map, int x, int y, byte except)
	{
		ArrayList<Byte> res = new ArrayList<Byte>();
						
		if(map.isRoad(x-1, y) && except != Direction2D.WEST)
			res.add(Direction2D.WEST);
		if(map.isRoad(x+1, y) && except != Direction2D.EAST)
			res.add(Direction2D.EAST);
		if(map.isRoad(x, y-1) && except != Direction2D.NORTH)
			res.add(Direction2D.NORTH);
		if(map.isRoad(x, y+1)  && except != Direction2D.SOUTH)
			res.add(Direction2D.SOUTH);
		
		return res;
	}
		
	public static void render(Graphics gfx, byte index, int gx, int gy)
	{
		gfx.drawImage(tiles[index], gx, gy);
	}

}

