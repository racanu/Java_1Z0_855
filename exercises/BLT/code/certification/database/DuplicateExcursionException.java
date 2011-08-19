package certification.database;

public class DuplicateExcursionException extends DatabaseException {
    DuplicateExcursionException() { }
    DuplicateExcursionException(String message) { super(message);  }
}

