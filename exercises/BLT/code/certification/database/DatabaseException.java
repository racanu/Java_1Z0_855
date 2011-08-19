package certification.database;

public class DatabaseException extends Exception { 
    // We don't allow direct instantiation of this exception, because it is too generic to be useful     // Subclasses are more explanatory
    protected DatabaseException() { }
    protected DatabaseException(String message) { super(message); }
}

