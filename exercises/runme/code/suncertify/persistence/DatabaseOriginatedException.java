package suncertify.persistence;

import suncertify.db.DatabaseRuntimeException;

public class DatabaseOriginatedException extends PersistenceException {
    /**
     * 
     */
    private static final long serialVersionUID = -131266590151589980L;
    private final DatabaseRuntimeException originalException;

    // There is no reason for instantiating this exception from outside the package
    // hence the constructors use default access
    DatabaseOriginatedException() { this.originalException = null; }
    DatabaseOriginatedException(String message) { super(message); this.originalException = null; }
    DatabaseOriginatedException(DatabaseRuntimeException e) { this.originalException = e;  }
    
    public DatabaseRuntimeException getOriginalException() { return this.originalException; }
}
