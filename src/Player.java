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

    public void setPosition(float[] position) {
        this.position = position;
    }
    public void setFocus(float[] position) {
        this.focus = focus;
    }

    public float getFocusX() {
        return focus[0];
    }

    public float getFocusY() {return focus[1]; }

    public float getFocusZ() {
        return focus[2];
    }


}
