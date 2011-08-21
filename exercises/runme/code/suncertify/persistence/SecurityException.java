package suncertify.persistence;

public class SecurityException extends PersistenceException {
    /**
     * 
     */
    private static final long serialVersionUID = -1145552626051875193L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    SecurityException() { }
    SecurityException(String message) { super(message);  }
}

