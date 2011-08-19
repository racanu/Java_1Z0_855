package certification.database;

import certification.domain.*;
import java.util.*;

public class Database implements LocalDB {
  
    /**
     *  Memory Cache
     *  Records are stored in the order in which they are read from the file or appended from the UI
     *  Keys are stored in a separate set
     */
    private final Map<Long, Excursion> data = new HashMap<Long, Excursion>();
    private final Set<ExcursionPrimaryKey> keys = new HashSet<ExcursionPrimaryKey>();
    
    /**
     * Database path used to write the records back to the file when the application finishes
     */
    private String dbPath;
  
    public Database(String dbPath) throws DatabaseException {
        super();
  
        // Reads the given database file and fills the memory cache with its content, possibly throwing a DatabaseException
        readData(dbPath);
        this.dbPath = dbPath;
    }
  
    @Override
    public synchronized void createExcursion(Excursion excursion) throws DuplicateExcursionException {
        // Puts the excursion record in the memory cache if a record with the same primary key still does not exist
    }
  
    @Override
    public synchronized void updateExcursion(long recordNumber, Excursion excursion) throws RecordNotFoundException {
    
    }
  
    @Override
    public void deleteExcursion(long recordNumber) throws RecordNotFoundException {
    
    }
  
    @Override
    public Excursion readExcursion(long recordNumber) throws RecordNotFoundException {
        return new Excursion();
    }
  
    @Override
    public void lock(long recordNumber) throws RecordNotFoundException {
    
    }
  
    @Override
    public void unlock(long recordNumber) throws RecordNotFoundException {
    
    }
    
    @Override
    public void saveData() {
    
    }
    
    private void readData(String dbPath) {
    
    }
}
