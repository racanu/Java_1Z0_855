package certification.database;

import certification.domain.*;

public interface DB {
    // Creates a new record in the database
    public void createExcursion(Excursion excursion) throws DuplicateExcursionException;
    // Updates the fields of a given excursion
    public void updateExcursion(long recordNumber, Excursion excursion) throws RecordNotFoundException;
    // Deletes a particular record
    public void deleteExcursion(long recordNumber) throws RecordNotFoundException;
    // Reads a record from the database
    public Excursion readExcursion(long recordNumber) throws RecordNotFoundException;
    // Locks a particular record so that it can only be updated by or deleted by this client
    public void lock(long recordNumber) throws RecordNotFoundException;
    // Unlocks a particular record
    public void unlock(long recordNumber) throws RecordNotFoundException;
    // Other methods...
}

