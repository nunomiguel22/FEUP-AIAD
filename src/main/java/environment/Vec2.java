package environment;

public class Vec2 {
    private double x, y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double calcDistance(Vec2 vec) {
        double a = this.x - vec.x;
        double b = this.y - vec.y;
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public void addVec2(Vec2 vec) {
        if (vec == null)
            return;
        this.x += vec.x;
        this.y += vec.y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (!(o instanceof Vec2))
            return false;
        Vec2 obj = (Vec2) o;

        if (this.x == obj.getX() && this.y == obj.getY())
            return true;

        return false;
    }

}
