package com.qwertyzzz18.ccgame.editor;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Map {
	public static Texture stagetexture = new Texture("stageicon.png");
	private ArrayList<Stage> stages = new ArrayList<Stage>();
	private int width=16;
	private int height=16;
	private int nextID=0;
	private boolean dirty=false;
	public void resize()
	{
		for (int i=0; i<stages.size();i++)
		{
			Stage stage=stages.get(i);
			if ((stage.getX()>width-1)|(stage.getY()>height-1))
			{
				stages.remove(stages.indexOf(stage));
				i-=1;
			}
		}
	}
	public void addStage(Stage stage)
	{
		stages.add(stage);
		nextID=stage.getId()+1;
		this.dirty=true;
	}
	public Stage getStage(int x, int y)
	{
		for (Stage stage:stages)
		{
			if ((stage.getX()==x)&&(stage.getY()==y))
			{
				return stage;
			}
		}
		return null;
	}
	public Stage getStage(int id)
	{
		for (Stage stage:stages)
		{
			if (stage.getId()==id)
			{
				return stage;
			}
		}
		return null;
	}
	public void draw(SpriteBatch batch)
	{
		for (Stage stage:stages)
		{
			batch.draw(stagetexture, 16+stage.getX()*16, 16+stage.getY()*16);
		}
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getNextID() {
		return nextID;
	}
	public boolean isDirty()
	{
		if (this.dirty)
			return true;
		for (Stage stage : stages)
			if (stage.isDirty())
				return true;
		return false;
	}
	public void clean()
	{
		this.dirty=false;
		for (Stage stage : stages)
			stage.clean();
	}
}
