package certification.database;

public class RecordNotFoundException extends DatabaseException {
    RecordNotFoundException() { }
    RecordNotFoundException(String message) { super(message);  }
}

