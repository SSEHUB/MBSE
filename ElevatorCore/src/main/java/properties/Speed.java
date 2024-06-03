package properties;

/**
 * Some example elevator speeds.
 * 
 * @author SSE
 */
public enum Speed {
    
    SLOW(70), 
    MEDIUM(40), 
    FAST(10);

    private int value;

    /**
     * Creates a speed constant.
     * 
     * @param speed delay in ms
     */
    private Speed(int speed) {
        this.value = speed;
    }

    /**
     * Returns the speed delay.
     * 
     * @return the delay in ms
     */
    public int getValue() {
        return value;
    }

}
