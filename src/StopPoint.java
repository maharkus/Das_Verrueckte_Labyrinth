public class StopPoint {
    public float[] getPos() {
        return pos;
    }

    float[] pos = new float[3];
    int[] directions;


    public int[] getDirections() {
        return directions;
    }

    public void setDirections(int[] directions) {
        this.directions = directions;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    StopPoint(float[] pos, int[] directions) {
        this.pos = pos;
        this.directions = directions;
    }



}
