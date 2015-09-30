package com.qwertyzzz18.ccgame.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.Gson;

public class MyGdxGame extends ApplicationAdapter implements ApplicationListener, InputProcessor {

	private static final int STATE_MAIN = 0;
	private static final int STATE_EDITSTAGE = 1;
	private static final int STATE_MAPSETTINGS = 2;
	private static final int STATE_NEWSTAGE = 3;
	private static final int STATE_EDITNEWSTAGE = 4;

	SpriteBatch batch;
	Texture button;
	TextureRegion buttonRegion;
	Texture grid;
	TextureRegion gridRegion;
	Texture sel;
	TextureRegion selRegion;
	Map map;
	int ticks=0;
	int curstageX;
	int curstageY;
	int editField;
	boolean isInputting=false;
	String inputstr;
	int state;
	BitmapFont font;
	BitmapFont font2;
	GlyphLayout glyph;
	@Override
	public void create () {
		Gdx.graphics.setDisplayMode(1300, 650, false);
		state=0;
		button = new Texture("stageicon.png");
		buttonRegion = new TextureRegion(button);
		grid = new Texture("stagegrid.png");
		gridRegion = new TextureRegion(grid);
		sel = new Texture("stagesel.png");
		selRegion = new TextureRegion(sel);
		font = new BitmapFont();
		font.setColor(Color.RED);
		font2 = new BitmapFont();
		font2.setColor(Color.WHITE);
		glyph=new GlyphLayout();
		batch = new SpriteBatch();
		map = new Map();
		map.addStage(new Stage(0, 0, 0, "Stage0"));
		map.addStage(new Stage(1, 1, 0, "Stage1"));
		map.addStage(new Stage(2, 0, 1, "Stage2"));
		map.addStage(new Stage(3, 2, 2, "Stage3"));
		map.addStage(new Stage(4, 9, 9, "Stage4"));
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		ticks++;
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		map.draw(batch);
		String promptstr;
		switch (state)
		{
		case STATE_MAIN:
			batch.draw(buttonRegion, 10, Gdx.graphics.getHeight()-36, 80, 20);
			font.draw(batch, "New Stage", 16, Gdx.graphics.getHeight()-20);
			batch.draw(buttonRegion, 100, Gdx.graphics.getHeight()-36, 95, 20);
			font.draw(batch, "Map Settings", 107, Gdx.graphics.getHeight()-20);
			batch.draw(buttonRegion, 205, Gdx.graphics.getHeight()-36, map.isDirty()?57:45, 20);
			font.draw(batch, map.isDirty()?"*Save*":"Save", 212, Gdx.graphics.getHeight()-20);
			break;
		case STATE_NEWSTAGE:
			font2.draw(batch, "Click on a grid intersection to create a new map", 16, Gdx.graphics.getHeight()-20);
			for (int i=0;i<map.getWidth();i++)
			{
				batch.draw(gridRegion, 19+16*i, 19, 2, 16*(map.getHeight()-1));
			}
			for (int i=0;i<map.getHeight();i++)
			{
				batch.draw(gridRegion, 19, 19+16*i, 16*(map.getWidth()-1), 2);
			}
			break;
		case STATE_EDITSTAGE:
		case STATE_EDITNEWSTAGE:
			batch.draw(selRegion, 16*curstageX+16, 16*curstageY+16);
			promptstr="ERROR MyGDXGame.java:EP000";			//EP000
			switch (editField)
			{
			case 0:
				promptstr="Level Name>";
				break;
			}
			glyph.setText(font, promptstr+inputstr);
			batch.draw(buttonRegion, 10, Gdx.graphics.getHeight()-36, glyph.width+(ticks%60<30?10:15), 20);
			font.draw(batch, glyph, 16, Gdx.graphics.getHeight()-20);
			break;
		case STATE_MAPSETTINGS:
			promptstr="ERROR MyGDXGame.java:EP000";			//EP000
			switch (editField)
			{
			case 0:
				promptstr="Map Width>";
				break;
			case 1:
				promptstr="Map Height>";
				break;
			}
			glyph.setText(font, promptstr+inputstr);
			batch.draw(buttonRegion, 10, Gdx.graphics.getHeight()-36, glyph.width+(ticks%60<30?10:15), 20);
			font.draw(batch, glyph, 16, Gdx.graphics.getHeight()-20);
		}
		
		batch.end();
	}
	
	public void save()
	{
		Gson gson = new Gson();
		String json = gson.toJson(map);
		System.out.println(json);
		PrintStream output;
		try {
			output = new PrintStream(new File("map.json"));
			output.println(json);
			map.clean();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finishInput()
	{
		switch (state)
		{
		case STATE_EDITSTAGE:
		case STATE_EDITNEWSTAGE:
			map.getStage(curstageX, curstageY).setName(inputstr);
			state=STATE_MAIN;
			break;
		case STATE_MAPSETTINGS:
			switch (editField)
			{
			case 0:
				map.setWidth(Integer.parseInt(inputstr));
				editField++;
				inputstr="";
				return;
			case 1:
				map.setHeight(Integer.parseInt(inputstr));
				map.resize();
				state=STATE_MAIN;
			}
		}
		editField=0;
		isInputting=false;
		inputstr="";
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (isInputting)
		{
			switch (keycode)
			{	
			case Keys.BACKSPACE:
				if (inputstr.length()>0)
					inputstr=inputstr.substring(0, inputstr.length()-1);
				break;
			case Keys.ENTER:
				finishInput();
				break;
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (character=='\b')
			return false;
		if (character=='\n')
			return false;
		if (character=='\r')
			return false;
		if (isInputting)
			inputstr+=character;
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button==Buttons.LEFT)
		{
			switch(state)
			{
			case STATE_MAIN:
				if ((screenY>=16)&&(screenY<=36))
				{
					if ((screenX>=10)&&(screenX<=90))
					{
						state=STATE_NEWSTAGE;
					}
					if ((screenX>=100)&&(screenX<=195))
					{
						inputstr="";
						isInputting=true;
						state=STATE_MAPSETTINGS;
					}
					if ((screenX>=205)&&(screenX<=250))
					{
						save();
					}

				}
				if ((screenX>=12)&&(screenX<=12+16*map.getWidth()))
				{
					if ((Gdx.graphics.getHeight()-screenY>=12)&&(Gdx.graphics.getHeight()-screenY<=12+16*map.getHeight()))
					{
						int x=(screenX-12)/16;
						int y=(Gdx.graphics.getHeight()-screenY-12)/16;
						if (map.getStage(x, y)==null)
							break;
						curstageX=x;
						curstageY=y;
						inputstr=map.getStage(x,y).getName();
						isInputting=true;
						state=STATE_EDITSTAGE;
					}
				}
				break;
			case STATE_NEWSTAGE:
				if ((screenX>=12)&&(screenX<=12+16*map.getWidth()))
				{
					if ((Gdx.graphics.getHeight()-screenY>=12)&&(Gdx.graphics.getHeight()-screenY<=12+16*map.getHeight()))
					{
						int x=(screenX-12)/16;
						int y=(Gdx.graphics.getHeight()-screenY-12)/16;
						if (map.getStage(x,y)!=null)
							break;
						map.addStage(new Stage(map.getNextID(), x, y, "UNNAMED"));
						curstageX=x;
						curstageY=y;
						inputstr="";
						isInputting=true;
						state=STATE_EDITNEWSTAGE;
					}
				}
				break;
			case STATE_EDITSTAGE:
				break;
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
