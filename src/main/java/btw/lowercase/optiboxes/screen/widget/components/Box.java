package btw.lowercase.optiboxes.screen.widget.components;

public class Box {
    private int x;
    private int y;
    private int width;
    private int height;

    public Box(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(int x, int y) {
        return (x >= this.x && x <= this.x + this.width) && (y >= this.y && y <= this.y + this.height);
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}
