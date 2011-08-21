package suncertify.db;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import suncertify.util.*;

class DatabaseFieldDefinition {
    protected final String fieldName;
    protected final int fieldLength;
    
    DatabaseFieldDefinition(String name, int length) {
        assert(length > 0);        
        this.fieldName = name; 
        this.fieldLength = length; 
    }
    
    String getFieldName() { 
        return this.fieldName; 
    }
    
    int getFieldLength() { 
        return this.fieldLength;
    }
    
    /**
     * Reads a field definition (name and length) from the database file using the provided DataInput stream
     * and checks if it matches the expected definition of the field.
     * 
     * @param di The DataInput stream from which to read the field definition.
     */
    void readAndCheckFieldDefinition(DataInput di) {
        try {
            byte fnl = di.readByte();
    
            byte[] b = new byte[fnl];
            di.readFully(b);
            String fn = new String(b);
            if (!fn.toLowerCase().equals(fieldName)) {
                throw new DatabaseRuntimeException("Field name mismatch. " + 
                        "Expected \"" + fieldName + "\" encountered \"" + fn + "\"");
            }
            
            byte fl = di.readByte();
            
            if (fl != fieldLength) {
                throw new DatabaseRuntimeException("Field length mismatch for field " + fieldName + ". " 
                        + "Expected " + fieldLength + " found " + fl);
            }
        } catch (EOFException e) {
            throw new DatabaseRuntimeException("End of file encountered while reading schema.");
        } catch (IOException e) {
            throw new DatabaseRuntimeException("IO Exception while reading schema.");
        }
    }
    
    /**
     * Gets a raw value, trims leading and trailing whitespace and truncates it to the fieldLength.  
     * 
     * @param value The raw value
     * @return The trimmed value
     */
    String fitValue(String value) {
        try {        
            // Trim spaces and truncate excess characters
            if (value.length() > fieldLength) value = value.substring(0, fieldLength);
            return value.trim();
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    /**
     * Reads the value of the field from the database file through the provided DataInput stream.
     * It reads exactly the amount of bytes corresponding to the field's length.
     * The return value is trimmed and fitted using fitValue().
     * 
     * @param di The DataInput stream to read the value from.
     * @return The fit-and-trim value for the field.
     */
    String readFieldValue(DataInput di) throws DatabaseRuntimeException {
        try {
            byte[] b = new byte[fieldLength];
            di.readFully(b);
            return fitValue(new String(b));
        } catch (EOFException e) {
            throw new DatabaseRuntimeException("EOF encountered while reading value for field " + fieldName);
        } catch (IOException e) {
            throw new DatabaseRuntimeException("IO exception encountered while reading value for field " + fieldName);
        }
    }
    
    /**
     * Writes the provided field value to the database file through the provided DataOutput stream.
     * The amount of bytes written is exactly the field's length. The value is first fitted using 
     * fitValue() to make sure it fits in the field, then padded with whitespaces at the end to fill
     * the field's space if necessary. 
     * 
     * Errors that may occur during writing are silently suppressed because the DB interface does not
     * provide for reporting them. This has the potential of corrupting the database file.
     * 
     * @param dout The DataOutput stream to write the value to.
     * @param value The value to write. It is padded with whitespaces as necessary.
     */
    void writeFieldValue(DataOutput dout, String value) throws DatabaseRuntimeException {
        return ;
        
/*        try {
            dout.writeBytes(fitValue(value););
            for (short i = 0; i < fieldLength - value.length(); i++)
            {
                dout.writeBytes(" ");
            }
        } catch (IOException e) {
            throw new DatabaseRuntimeException("IO exception encountered while writing value for field " + fieldName);
        }
*/    }
}

/**
 * 
 * @author racanu
 *
 */
class DatabaseRecord {
    //
    // The following are the static fields and operations.
    // They deal with the definition of a record (i.e. the schema)
    //
    private static final int magicCookie = 259;

    private static final DatabaseFieldDefinition[] fields = {
        new DatabaseFieldDefinition("name", 64),
        new DatabaseFieldDefinition("location", 64),
        new DatabaseFieldDefinition("size", 4),
        new DatabaseFieldDefinition("smoking", 1),
        new DatabaseFieldDefinition("rate", 8),
        new DatabaseFieldDefinition("date", 10),
        new DatabaseFieldDefinition("owner", 8)
    };
    private static final short recordSize;
    
    static {
        short rs = 0;
        for (short i=0; i<fields.length; i++) {
            rs += fields[i].getFieldLength();
        }
        recordSize = rs;
    }

    static DatabaseFieldDefinition getFieldDefinition(short index) {
        if (index < fields.length) {
            return fields[index];
        } else {
            return null;
        }
    }

    static short getNumberOfFields() {
        return (short)fields.length;
    }
    
    static short getRecordSize() {
        return recordSize;
    }
    
    static void skipSchema(DataInput di) throws DatabaseRuntimeException {
        // This is just a semantic method. 
        // We could implement an actual skipping (i.e. moving the file pointer) 
        // but for simplicity's sake we choose to just read the schema.
        // The assignment allows us to assume that only one process (this one) has access to the database file.
        // Hence the schema cannot have been changed by an external party. 
        // To support a scenario in which the schema can be changed by an external party, would require a lot more than the
        // current assignment targets.
        readSchema(di);
    }
    
    static void readSchema(DataInput di) throws DatabaseRuntimeException {
        try {
            int mc = di.readInt();
            if (mc != magicCookie) { 
                throw new DatabaseRuntimeException("Incorrect magic cookie. " 
                        + "Expected " + magicCookie + " encountered " + mc);
            }
            
            short fpr = di.readShort();
            if (fpr != fields.length) {
                throw new DatabaseRuntimeException("Incorrect number of fields per record. " 
                        + "Expected " + fields.length + "encountered " + fpr);
            }
            
            for (short i = 0; i < fields.length; i++ ) {
                fields[i].readAndCheckFieldDefinition(di);
            }
        } catch (EOFException e) {
            throw new DatabaseRuntimeException("EOF encountered while reading schema from database file.");
        } catch (IOException e) {
            throw new DatabaseRuntimeException("IO exception encountered while reading schema from database file.");
        }
    }
    
    /**
     * Reads a record from the database file using the provided DataInput stream.
     * 
     * @param di
     * @return
     * @throws DatabaseRuntimeException
     */
    static DatabaseRecord readRecord(DataInput di) throws DatabaseRuntimeException {
        DatabaseRecord dr = new DatabaseRecord();
        dr.read(di);
        return dr;
    }    
    
    /**
     * Skips a record by moving the file pointer forward a number of bytes equals to the size of the record.
     * 
     * @param di
     * @return
     * @throws DatabaseRuntimeException
     */
    static void skipRecord(DataInput di) throws DatabaseRuntimeException{
        try {
            di.skipBytes(DatabaseRecord.getRecordSize());
        } catch (IOException e) {
            throw new DatabaseRuntimeException("IO exception while skipping record.");
        }
    }
    
    //
    // The following are instance related fields and operations
    //
    private boolean locked = false;
    private long lockCookie = 0;
    private boolean dirty = true;
    private byte flag;
    private final String[] values = new String[fields.length];
    
    void setRecordDirty() { this.dirty = true; }
    boolean isRecordDirty() { return this.dirty == true; }
    void setRecordClean() { this.dirty = false; }
    boolean isRecordClean() { return this.dirty == false; }
    void setRecordValid() { this.flag = 0; }
    boolean isRecordValid() { return this.flag == 0; }
    void setRecordDeleted() { this.flag = (byte)0xFF; }
    boolean isRecordDeleted() { return this.flag == (byte)0xFF; }

    /**
     * Locks the record for exclusive access by the holder of the given cookie.
     * All subsequent operations on this record must provide this cookie.
     * The record can only be locked by one entity at a time. So only one cookie is active at any time.
     * When attempting to lock an already locked record, the thread will be put to sleep.
     * 
     * @param cookie The value that allows the holder of it to access the record while it is locked.
     */
    synchronized void lock(long cookie) {
        try {
            //TODO: while ???
            if (this.locked && this.lockCookie != cookie) {
                // Block thread
                this.wait();
            }
            this.locked = true;
            this.lockCookie = cookie;
        } catch (InterruptedException e) {
            //
        }
    }
    
    /**
     * Unlocks a previously locked record, releasing it for changes.
     * To unlock the record, the owner of it must provide the cookie it used when locking it.
     * 
     * @param cookie The cookie that was used to lock the record.
     * @throws SecurityException When attempting to unlock a record that is not locked 
     * or using a cookie other than the one used for locking it.
     */
    synchronized void unlock(long cookie) throws SecurityException { 
        if (!this.locked || this.lockCookie != cookie)
            throw new SecurityException();
        this.locked = false;
        this.notifyAll();
    } 

    /**
     * Takes a list of values in the form of an array of String and updates the fields
     * of the record with them. Leading and trailing spaces are trimmed off. Values that
     * are too long to fit in the corresponding field are silently truncated.
     * 
     * @param cookie The cookie that was used to lock the record. 
     * @param stringValues An array of field values for the record.
     * The order of values or the format of them is not checked in any way.
     * The fields are updated in the order they are present in the database.
     * If less values are provided than fields, only the first fields will be updated.
     * If more values are provided than fields, excess values will be ignored. 
     * 
     * @throws SecurityException When attempting to update a record that is not locked 
     * or that is locked with a cookie other than the one used for locking it.
     */
    void setRecordValues(long cookie, String[] stringValues) throws SecurityException {
        if (!this.locked || this.lockCookie != cookie) {
            throw new SecurityException();
        }
        
        try {
            for (short i = 0; i < this.values.length; i++)
            {
                this.values[i] = fields[i].fitValue(stringValues[i]);
            }
            this.setRecordDirty();
        } catch (NullPointerException e) {
            // Do nothing; we can't report the error upstream
            // When receiving a null (empty) array, we leave the record unchanged
            // and don't even set the dirty flag
        } catch (ArrayIndexOutOfBoundsException e) {
            // Do nothing; we can't report the error upstream
            // When receiving less values than what we need, 
            // we set the first values and leave the rest unchanged 
            this.setRecordDirty();
        }
    }
    
    /**
     * Returns the values in the record as an array of strings.
     * Leading and trailing spaces are not present in the values (trimmed off).
     *  
     * @return The list of values in as an array of String,
     * in the order of the fields as they are found in the database file.
     */
    String[] getRecordValues() {
        String[] stringValues = values.clone();
        return stringValues; 
    }
    
    /**
     * Reads the values for the current record from the database file using the provided DataInput.
     * The amount of bytes read from the file is the same for each record and is known beforehand.
     * 
     * @param di The DataInput stream associated with the database file.
     * @throws DatabaseRuntimeException
     */
    void read(DataInput di) {
        //TODO: Cannot read or write while locked
        // if (this.locked) throw new SecurityException();

        try {
            byte flag = di.readByte();
            if (flag==0) {
                this.setRecordValid();
            } else if (flag==(byte)0xFF) {
                this.setRecordDeleted();
            } else {
                // Ignore the error.
            }

            for (short i = 0; i < values.length; i++) {
                values[i] = fields[i].readFieldValue(di);
            }
            // In case any errors occur, the record will not be marked clean
            // and will be written to the database file at the first update
            this.setRecordClean();
        } catch (EOFException e) {
            // Ignore the error. Just print it to the console.
            e.printStackTrace();
        } catch (IOException e) {
            // Ignore the error. Just print it to the console.
            e.printStackTrace();
        }
    }    

    void write(DataOutput dout) throws DatabaseRuntimeException {
        //TODO: Cannot read or write while locked
        // if (this.locked) throw new SecurityException();
        
        for (short i = 0; i < values.length; i++) {
            fields[i].writeFieldValue(dout, values[i]);
        }
        this.setRecordClean();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((this.isRecordDirty()?"*":" ") + (this.isRecordValid()?"V":"D") + " ");
        for (short f = 0; f < DatabaseRecord.getNumberOfFields(); f++) {
            sb.append(String.format("%-" + fields[f].getFieldLength() + "s", this.values[f]) + " ");
        }
        return sb.toString();
    }
}

/**
 * The database wrapper class.
 * Records are cached in memory in their entirety.
 * Records are not physically deleted; they are only flagged as such and reused as new records are added.
 * 
 * Choice:
 * The deletion flag is part of the database schema that thus allows for persisting deleted records in the database file.
 * This allows for easier updates to the database file because only changed records can be updated (relatively small chunks
 * of date written). On the other hand it never allows the database file to shrink. For the task at hand this will not become
 * a problem and if it does, a special maintenance operation might be added later to compact the database by rewriting it
 * and skipping deleted records.
 * 
 * Record numbers:
 *  - are generated as new records are added; this might create problems for very long run sessions
 *    but such usage is not envisioned 
 *  - are stored in the cache along with the records; the index of the record in the cache list is not suitable
 *    to be used as record number, especially in multithreaded environments where one thread may hold a record number
 *    and another may change the record list order by deleting records, rendering the record number useless or invalid  
 *  - are not reused during a run session of the application
 *  - are not persisted in the database file (since the schema does not provide for this)
 *  - are re-generated in a new session, as the database is loaded into the cache upon startup
 */
public class Data implements DB {
    
    // Memory Cache
    // Records are stored in the order in which they are read from the file or appended from the UI
    // and their position in the list never changes, to allow selective writing to the database of only the updated records
    private static final List<DatabaseRecord> data = new ArrayList<DatabaseRecord>();
    // This is an indexed view on the previous list; it has to be kept in sync with the main list 
    private static final Map<Integer, DatabaseRecord> dataByRecordNumber = new TreeMap<Integer, DatabaseRecord>(); 
    
    
    // Database path used to write the records back to the file when the application finishes
    private String dbPath = null;
    
    // This class is meant to be run by a separate thread and it takes care of periodically
    // writing the database cache to the database file. When the thread is stopped, the cache
    // is written to the file for one last time, to allow last pending changes to be written.
/*    private class CacheSynchronizer implements Runnable {
        private boolean stopCst = false;
        private boolean lastRun = false;
        
        public void stop() {
            if (!stopCst) {
                stopCst = true;
                lastRun = true;
            }
        }

        @Override
        public void run() {
            while (!stopCst || lastRun) {
                if (lastRun) {
                    System.out.println("CacheSynchronizer: last run");
                    lastRun = false;
                }
                        
                try {
                    writeData(dbPath);
                    Thread.sleep(5000);
                } catch (DatabaseException e) {
                    // do nothing ???
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }
*/    
    private final static AutoIncrementNumberGenerator recordNumberGenerator 
        = new AutoIncrementNumberGenerator();

    private final static AutoIncrementNumberGenerator lockCookieGenerator 
        = new AutoIncrementNumberGenerator();
    
    public Data(String dbPath) throws DatabaseRuntimeException {
        super();
  
        // Reads the given database file and fills the memory cache with its content, possibly throwing a DatabaseException
        try {
            readData(dbPath);
            this.dbPath = dbPath;
        } catch (Exception e) {
            throw new DatabaseRuntimeException("Some error occured while reading the database. Check the path and file.");
        }
    }
  
    // Reads a record from the file. Returns an array where each
    // element is a record value
    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        // synchronized(data) ??
        try {
            return dataByRecordNumber.get(recNo).getRecordValues();
        } catch (Exception e) {
            return null;
        }
    }

    // Modifies the fields of a record. The new value for field n
    // appears in data[n]. Throws SecurityException
    // if the record is locked with a cookie other than lockCookie.
    @Override
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
        try {
            DatabaseRecord dr = dataByRecordNumber.get(recNo);
            dr.setRecordValues(lockCookie, data);
            this.writeData(this.dbPath);
        } catch (SecurityException e) {
            throw e;
        } catch (DatabaseRuntimeException e) {
            // All other DatabaseExceptions are silently ignored
            // because the interface does not allow reporting them
            e.printStackTrace();
        } catch (NullPointerException e) {
            throw new RecordNotFoundException();
        }
    }
    
    // Deletes a record, making the record number and associated disk
    // storage available for reuse
    // Throws SecurityException if the record is locked with a cookie
    // other than lockCookie.
    @Override
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
    }
        
    @Override
    public int[] find(String[] criteria) {
        return new int[] {0};
    }

    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return 0;
    }

    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        try {
            DatabaseRecord dr = dataByRecordNumber.get(recNo);
            long cookie = lockCookieGenerator.newNumber();
            dr.lock(cookie);
            return cookie;
        } catch (NullPointerException e) {
            throw new RecordNotFoundException();
        }
    }

    @Override
    public void unlock(int recNo, long cookie)
            throws RecordNotFoundException, SecurityException {
        try {
            DatabaseRecord dr = dataByRecordNumber.get(recNo);
            dr.unlock(cookie);
        } catch (NullPointerException e) {
            throw new RecordNotFoundException();
        }
    }

    public int getRecordCount() {
        return data.size();
    }
    
    public void printDataCache() {
        System.out.println("HEADER");
        System.out.println("Number of field: " + DatabaseRecord.getNumberOfFields());
        System.out.println("Record size: " + DatabaseRecord.getRecordSize());
        
        for (short i = 0; i < DatabaseRecord.getNumberOfFields(); i++) {
            DatabaseFieldDefinition dbfd = DatabaseRecord.getFieldDefinition(i);
            System.out.println("Field " + i +": \"" + dbfd.getFieldName() + "\" size: " + dbfd.getFieldLength());
        }
        
        System.out.println("\nRECORDS");
        for (int i = 0; i < data.size(); i++) {
            DatabaseRecord dr = data.get(i);
            System.out.println(dr);
        }
    }

    private void readData(String dbPath) throws DatabaseRuntimeException {
        try {
            RandomAccessFile raf = new RandomAccessFile(dbPath,"rws");
            
            data.clear();
            dataByRecordNumber.clear();
            DatabaseRecord.readSchema(raf);
            while ( raf.getFilePointer() < raf.length() ) {
                DatabaseRecord dr = DatabaseRecord.readRecord(raf);
                data.add(dr);
                dataByRecordNumber.put((int)recordNumberGenerator.newNumber(), dr);
            }
            raf.close();
        } catch (FileNotFoundException e) {
            throw new DatabaseRuntimeException("FileNotFoundException while reading database.");
        } catch (IOException e) {
            throw new DatabaseRuntimeException("IOException while reading database.");
        }
    }
    
    private void writeData(String dbPath) throws DatabaseRuntimeException {
        try {
            RandomAccessFile raf = new RandomAccessFile(dbPath,"rws");
            // This will place the file pointer on the first record
            DatabaseRecord.skipSchema(raf);
    
            int dirtyCount = 0;
            synchronized (data) {
                for (int i = 0; i < data.size(); i++) {
                    DatabaseRecord dr = data.get(i);
                    if (dr.isRecordDirty()) {
                        dirtyCount++;
                        dr.write(raf);
                        
                        dr.setRecordClean();
                    } else {
                        DatabaseRecord.skipRecord(raf);
                    }
                }
            }
            if (dirtyCount > 0) {
                System.out.println("Written " + dirtyCount + " dirty record(s) to the database file.");
            }
        } catch (FileNotFoundException e) {
            throw new DatabaseRuntimeException("FileNotFoundException while writing database file.");
        }
    }
}
