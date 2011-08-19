package suncertify.util;

public class AutoIncrementNumberGenerator {
    private long generator = 0;
    
    public AutoIncrementNumberGenerator() {
        generator = 0;
    }

    AutoIncrementNumberGenerator(int start) {
        generator = start;
    }

    public long newNumber() {
        return generator++;
    }
}
