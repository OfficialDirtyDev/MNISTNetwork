package gui.components.data;

public class Section {

    private int x;
    private int y;
    private int width;
    private int height;
    private double black;

    public Section(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.black = 0;
    }

    public double getBlack() {
        return black;
    }

    public void erase() {
    	black = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void setBlack(double black) {
		this.black = black;
	}
}
