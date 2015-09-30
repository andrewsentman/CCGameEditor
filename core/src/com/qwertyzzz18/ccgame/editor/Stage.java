package com.qwertyzzz18.ccgame.editor;

public class Stage {

    private int     id;
    private int     x;
    private int     y;
    private String  name;
    private boolean dirty = true;

    public Stage(int id, int x, int y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public void setX(int x) {
        this.x = x;
        this.dirty = true;
    }

    public void setY(int y) {
        this.y = y;
        this.dirty = true;
    }

    public void setName(String name) {
        this.name = name;
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clean() {
        this.dirty = false;
    }
}
