package suncertify.db;

public class FieldValueException extends DatabaseException {
    /**
     * 
     */
    private static final long serialVersionUID = 2567989260117557335L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    FieldValueException() { }
    FieldValueException(String message) { super(message);  }
}
