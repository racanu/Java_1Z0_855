package suncertify.db;

public class RecordFormatException extends DatabaseException {
    /**
     * 
     */
    private static final long serialVersionUID = -131266590151589980L;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    RecordFormatException() { }
    RecordFormatException(String message) { super(message);  }
}
