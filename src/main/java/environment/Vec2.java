package environment;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class Vec2 implements Serializable {
    static final long serialVersionUID = 14224L;
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double calcDistance(Vec2 vec) {
        double a = this.x - vec.x;
        double b = this.y - vec.y;
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public static Vec2 divVec2(Vec2 a, Vec2 b) {
        double x = a.getX() / b.getX();
        double y = a.getY() / b.getY();
        return new Vec2(x, y);
    }

    public static Vec2 mulVec2(Vec2 a, Vec2 b) {
        double x = a.getX() * b.getX();
        double y = a.getY() * b.getY();
        return new Vec2(x, y);
    }

    public static Vec2 subVec2(Vec2 a, Vec2 b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        return new Vec2(x, y);
    }

    public void addVec2(Vec2 vec) {
        if (vec == null)
            return;
        this.x += vec.x;
        this.y += vec.y;
    }

    public void mulVec2(Vec2 vec) {
        if (vec == null)
            return;
        this.x *= vec.x;
        this.y *= vec.y;
    }

    public void divVec2(Vec2 vec) {
        if (vec == null)
            return;
        this.x /= vec.x;
        this.y /= vec.y;
    }

    public Vec2 scaledVec2(Vec2 vec) {
        if (vec == null)
            return null;
        this.x *= vec.x;
        this.y *= vec.y;
        return new Vec2(this.x * vec.x, this.y * vec.y);
    }

    public void rotate(int degrees) {
        double x2 = this.x * Math.cos(degrees) - this.y * Math.sin(degrees);
        this.y = this.x * Math.sin(degrees) - this.y * Math.cos(degrees);
        this.x = x2;
    }

    public void setVec2(Vec2 vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public double calcMagnitude() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public static Vec2 getRandomDirection() {
        Vec2 res = new Vec2(0, 1);
        int randomRot = ThreadLocalRandom.current().nextInt(0, 359);
        res.rotate(randomRot);
        return res;
    }

    public static Vec2 getDirection(Vec2 origin, Vec2 dest) {
        Vec2 dir = Vec2.subVec2(dest, origin);
        double mag = dir.calcMagnitude();
        dir.divVec2(new Vec2(mag, mag));
        return dir;
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

    public static Vec2 of(double x, double y) {
        return new Vec2(x, y);
    }

}
