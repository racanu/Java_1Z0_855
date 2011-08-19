package suncertify.db;

public class SchemaException extends DatabaseException {
    /**
     * 
     */
    private static final long serialVersionUID = -5142666947920522494L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    SchemaException() { }
    SchemaException(String message) { super(message);  }
}
