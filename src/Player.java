public class Player {
    float[] position;
    float[] focus;
    float[] directionVector = new float[3];


    Player(float[] position, float[] focus) {
        this.position = position;
        this.focus = focus;
        for(int i=0; i<directionVector.length; i++) {
            directionVector[i] = this.focus[i] - this.position[i];
        }
    }

    //Getters and setters

    public float[] getPosition() {
        return position;
    }

    public float getPositionX() {
        return position[0];
    }

    public float getPositionY() {
        return position[1];
    }

    public float getPositionZ() {
        return position[2];
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

}
