public class HandMotionCounter {

    int [] counter = new int[4];

    public int[] getCounter() {
        return counter;
    }
    HandMotionCounter() {
        this.counter[0] = 0;
        this.counter[1] = 0;
        this.counter[2] = 0;
        this.counter[3] = 0;
    }
    public void increaseCounter0() {
        counter[0]++;
    }
    public void increaseCounter1() {
        counter[1]++;
    }
    public void increaseCounter2() {
        counter[2]++;
    }
    public void increaseCounter3() {
        counter[3]++;
    }
    public void resetCounter(){
        this.counter = new int[4];
    }
}
