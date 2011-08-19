package suncertify.db;

public class DatabaseException extends Exception { 
    /**
     * 
     */
    private static final long serialVersionUID = 2188187996691052280L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    DatabaseException() { }
    DatabaseException(String message) { super(message); }
}

