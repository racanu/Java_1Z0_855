package suncertify.db;

public class DuplicateKeyException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -5879640629714176690L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    DuplicateKeyException() { }
    DuplicateKeyException(String message) { super(message);  }
}
