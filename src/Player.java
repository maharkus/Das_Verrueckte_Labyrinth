public class Player {

    int positionIndex;
    float[]position;
    float positionX;
    float positionY;
    float positionZ;
    float[]focus;
    float focusX;
    float focusY;
    float focusZ;
    float angle;

    Player(float[] position, float[] focus) {
        this.positionIndex = 0;
        this.positionX = position[0];
        this.positionY = position[1];
        this.positionZ = position[2];
        this.focusX = focus[0];
        this.focusY = focus[1];
        this.focusZ = focus[2];
        this.angle = 90;
    }

    //Getters and setters

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public void setPosition(float[] position) {
        this.positionX = position[0];
        this.positionY = position[1];
        this.positionZ = position[2];

    }
    public void setFocus(float[] focus) {
        this.focusX = focus[0];
        this.focusY = focus[1];
        this.focusZ = focus[2];
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float[] getPosition() {
        return new float[]{positionX, positionY, positionZ};
    }

    public float[] getFocus() {
        return new float[]{focusX, focusY, focusZ};
    }

    public float getAngle() { return angle;}

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {return positionY; }

    public float getPositionZ() {
        return positionZ;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public void setPositionZ(float positionZ) {
        this.positionZ = positionZ;
    }


    public float getFocusX() {
        return focusX;
    }

    public float getFocusY() {return focusY; }

    public float getFocusZ() {
        return focusZ;
    }

    public void setFocusX(float focusX) {
        this.focusX = focusX;
    }

    public void setFocusY(float focusY) {
        this.focusY = focusY;
    }

    public void setFocusZ(float focusZ) {
        this.focusZ = focusZ;
    }


}
