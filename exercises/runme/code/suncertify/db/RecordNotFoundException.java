package suncertify.db;

public class RecordNotFoundException extends DatabaseException {
    /**
     * 
     */
    private static final long serialVersionUID = 6916318427459181332L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    RecordNotFoundException() { }
    RecordNotFoundException(String message) { super(message);  }
}

