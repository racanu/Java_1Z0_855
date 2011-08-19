package suncertify.db;

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
    
    void readFieldDefinition(DataInput di) throws SchemaException {
        try {
            byte fnl = di.readByte();
    
            byte[] b = new byte[fnl];
            di.readFully(b);
            String fn = new String(b);
            if (!fn.toLowerCase().equals(fieldName)) {
                throw new SchemaException("Field name mismatch. " + 
                        "Expected \"" + fieldName + "\" encountered \"" + fn + "\"");
            }
            
            byte fl = di.readByte();
            
            if (fl != fieldLength) {
                throw new SchemaException("Field length mismatch for field " + fieldName + ". " 
                        + "Expected " + fieldLength + " found " + fl);
            }
        } catch (EOFException e) {
            throw new SchemaException("End of file encountered while reading schema.");
        } catch (IOException e) {
            throw new SchemaException("IO Exception while reading schema.");
        }
    }
    
    String validateValue(String value) throws FieldValueException {
        try {        
            // Trim spaces and truncate excess characters
            if (value.length() > fieldLength) value = value.substring(0, fieldLength);
            return value.trim();
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    String readFieldValue(DataInput di) throws DatabaseException {
        try {
            byte[] b = new byte[fieldLength];
            di.readFully(b);
            //TODO: This might give problems if leading spaces are desirable
            return new String(b).trim();
        } catch (EOFException e) {
            throw new DatabaseException("End of file encountered while reading field value.");
        } catch (IOException e) {
            throw new DatabaseException("IOException encountered while reading field value.");
        }
    }
    
    void writeFieldValue(DataOutput dout, String value) throws DatabaseException {
        return ;
        
/*        try {
            if (value.length() > fieldLength) {
                throw new FieldValueException("Actual length of value (" 
                        + value.length() + ") larger than field length (" + fieldLength + ").");
            }
            
            dout.writeBytes(value);
            for (int i = 0; i < fieldLength - value.length(); i++)
            {
                dout.writeBytes(" ");
            }
        } catch (IOException e) {
            throw new DatabaseException("IOException encountered while reading field value.");
        }
*/    }
}

/**
 * 
 * @author racanu
 *
 */
class YesNoFieldDefinition extends DatabaseFieldDefinition {
    YesNoFieldDefinition(String name) {
        super(name, 1);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        value = value.toUpperCase();
        if ( !value.equals("Y") && !value.equals("N") ) {
            throw new FieldValueException("The value of field " + this.fieldName + " may only be Y or N");
        }
        return value;
    }
}

/**
 * 
 * @author racanu
 *
 */
class DateFieldDefinition extends DatabaseFieldDefinition {
    DateFieldDefinition(String name) {
        super(name, 10);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        if ( !value.matches("\\d{1,4}/\\d{1,2}/\\d{1,2}") ) {
            throw new FieldValueException("The value of field " + this.fieldName + " must follow the format yyyy/mm/dd");
        }
        return value;
    }
}

/**
 * 
 * @author racanu
 *
 */
class NumericFieldDefinition extends DatabaseFieldDefinition {
    private final int minFieldLength;
    
    NumericFieldDefinition(String name, int length, int minLength) {
        super(name, length);
        this.minFieldLength = minLength;
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        if ( !value.matches("\\d{" + this.minFieldLength + "," + this.fieldLength + "}") ) {
            throw new FieldValueException("The value of field " + this.fieldName + " must be a number of at least " 
                    + this.minFieldLength + " and at most " + this.fieldLength + " digits.");
        }
        return value;
    }
}

/**
 * 
 * @author racanu
 *
 */
class OwnerFieldDefinition extends DatabaseFieldDefinition {
    public static final int FIELD_SIZE = 8;
    
    OwnerFieldDefinition(String name) {
        super(name, FIELD_SIZE);
    }
    
    @Override
    String validateValue(String value) throws FieldValueException {
        value = super.validateValue(value);
        if ( !value.matches("\\d{" + FIELD_SIZE + "}") && (value.length() != 0) ) {
            throw new FieldValueException(
                    "The value of field " + this.fieldName 
                    + " must be a number of exactly " + FIELD_SIZE + " digits "
                    + " or an empty string.");
        }
        return value;
    }
}

/**
 * This class encapsulates a field value that is based on a specified field definition.
 * The field definition is passed along in the constructor and is used to validate and format the field's value
 * as well as read it and persist it to the database file.
 * 
 * @author racanu
 *
 */
class DatabaseFieldValue {
    private final DatabaseFieldDefinition definition;
    private String value;
    
    DatabaseFieldValue(DatabaseFieldDefinition definition) {
        this.definition = definition;
    }
    
    void setValue(String value) throws FieldValueException {
        this.value = definition.validateValue(value);
    }
    
    int getFieldLength() {
        return definition.fieldLength;
    }
    
    String getValue() {
        return value;
    }
    
    void readValue(DataInput di) throws DatabaseException {
        value = definition.readFieldValue(di);
    }
    
    void writeValue(DataOutput dout) throws DatabaseException {
        definition.writeFieldValue(dout, this.value);
    }

    @Override
    public String toString() {
        return String.format("%-"+ definition.fieldLength + "s", value);
    }

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
            new NumericFieldDefinition("size", 4, 0),
            new YesNoFieldDefinition("smoking"),
            new DatabaseFieldDefinition("rate", 8),
            new DateFieldDefinition("date"),
            new OwnerFieldDefinition("owner")
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

    static short indexOfField(String name) throws SchemaException {
        for (short i = 0; i < fields.length; i++) {
            if (fields[i].getFieldName().equals(name)) {
                return i;
            }
        }
        throw new SchemaException("Field \"" + name + "\" not found.");
    }
    
    static short getNumberOfFields() {
        return (short)fields.length;
    }
    
    static short getRecordSize() {
        return recordSize;
    }
    
    static void skipSchema(DataInput di) throws SchemaException {
        // This is just a semantic method. 
        // We could implement an actual skipping (i.e. moving the file pointer) 
        // but for simplicity's sake we choose to just read the schema.
        // The assignment allows us to assume that only one process (this one) has access to the database file.
        // Hence the schema cannot have been changed by an external party. 
        // To support a scenario in which the schema can be changed by an external party, would require a lot more than the
        // current assignment targets.
        readSchema(di);
    }
    
    static void readSchema(DataInput di) throws SchemaException {
        try {
            int mc = di.readInt();
            if (mc != magicCookie) { 
                throw new SchemaException("Incorrect magic cookie. " 
                        + "Expected " + magicCookie + " encountered " + mc);
            }
            
            short fpr = di.readShort();
            if (fpr != fields.length) {
                throw new SchemaException("Incorrect number of fields per record. " 
                        + "Expected " + fields.length + "encountered " + fpr);
            }
            
            for (short i = 0; i < fields.length; i++ ) {
                fields[i].readFieldDefinition(di);
            }
        } catch (EOFException e) {
            throw new SchemaException("EOF while reading schema.");
        } catch (IOException e) {
            throw new SchemaException("IOException while reading schema.");
        }
    }
    
    static void validateRecord(String[] record) throws DatabaseException {
        if (record.length != fields.length) 
            throw new RecordFormatException("The record supplied doesn't have the required number of fields.");
        
        for (short i = 0; i < fields.length; i++) {
            record[i] = fields[i].validateValue(record[i]);
        }
    }
    
    static DatabaseRecord readRecord(DataInput di) throws DatabaseException {
        DatabaseRecord dr = new DatabaseRecord();
        dr.read(di);
        return dr;
    }    
    
    static void skipRecord(DataInput di) throws DatabaseException{
        try {
            di.skipBytes(DatabaseRecord.getRecordSize());
        } catch (IOException e) {
            throw new DatabaseException("IOException while skipping record.");
        }
    }
    
    //
    // The following are instance related fields and operations
    //
    private boolean locked = false;
    private long lockCookie = 0;
    private boolean dirty = true;
    private byte flag;
    private final DatabaseFieldValue[] values;
    
    DatabaseRecord() {
        this.values = new DatabaseFieldValue[fields.length];
        for (short i = 0; i < this.values.length; i++) {
            this.values[i] = new DatabaseFieldValue(fields[i]);
        }
    }
    
    void setRecordDirty() { this.dirty = true; }
    boolean isRecordDirty() { return this.dirty == true; }
    void setRecordClean() { this.dirty = false; }
    boolean isRecordClean() { return this.dirty == false; }
    void setRecordValid() { this.flag = 0; }
    boolean isRecordValid() { return this.flag == 0; }
    void setRecordDeleted() { this.flag = (byte)0xFF; }
    boolean isRecordDeleted() { return this.flag == (byte)0xFF; }

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
    
    synchronized void unlock(long cookie) throws SecurityException { 
        if (!this.locked || this.lockCookie != cookie)
            throw new SecurityException();
        this.locked = false;
        this.notifyAll();
    } 

    void setRecordValues(long cookie, String[] stringValues) throws DatabaseException {
        if (!this.locked || this.lockCookie != cookie) {
            throw new SecurityException();
        }
        
        if (stringValues.length != this.values.length) {
            throw new SchemaException("The number of values suplied (" + values.length + ")" 
                    + " doesn't match the number of fields (" + this.values.length + ".");
        }
        
        for (short i = 0; i < this.values.length; i++)
        {
            this.values[i].setValue(stringValues[i]);
        }
        this.setRecordDirty();
    }
    
    String[] getRecordValues() {
        String[] stringValues = new String[this.values.length];
        
        for (short i = 0; i < this.values.length; i++)
        {
            stringValues[i] = this.values[i].getValue();
        }
        return stringValues; 
    }
    
    void setFieldValue(long cookie, short index, String value) throws DatabaseException { 
        if (!this.locked || this.lockCookie != cookie) {
            throw new SecurityException();
        }

        this.getField(index).setValue(value);
        this.setRecordDirty();
    }
    
    String getFieldValue(int index) throws SchemaException {
        return this.getField(index).getValue();
    }

    void read(DataInput di) throws DatabaseException {
        //TODO: Cannot read or write while locked
        // if (this.locked) throw new SecurityException();

        try {
            byte flag = di.readByte();
            if (flag==0) {
                this.setRecordValid();
            } else if (flag==(byte)0xFF) {
                this.setRecordDeleted();
            } else {
                throw new RecordFormatException("Invalid record flag. Expected 0 or 255. Found " + flag);
            }

            for (short i = 0; i < values.length; i++) {
                values[i].readValue(di);
            }
            this.setRecordClean();
        } catch (EOFException e) {
            throw new RecordFormatException("EOF while reading record.");
        } catch (IOException e) {
            throw new RecordFormatException("IOException while reading record.");
        }
    }    

    void write(DataOutput dout) throws DatabaseException {
        //TODO: Cannot read or write while locked
        // if (this.locked) throw new SecurityException();
        
        for (short i = 0; i < values.length; i++) {
            values[i].writeValue(dout);
        }
        this.setRecordClean();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append((this.isRecordDirty()?"*":" ") + (this.isRecordValid()?"V":"D") + " ");
        for (short f = 0; f < DatabaseRecord.getNumberOfFields(); f++) {
            sb.append(this.values[f] + " ");
        }
        return sb.toString();
    }

    private DatabaseFieldValue getField(int index) throws SchemaException {
        assert index >= 0;
        assert index < fields.length;

        if (index < 0 || index >= fields.length) {
            throw new SchemaException("Field #" + index + " doesn't exist. Valid range [0," + fields.length + "]");
        }

        return this.values[index];
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
    
    public Data(String dbPath) throws DatabaseException {
        super();
  
        // Reads the given database file and fills the memory cache with its content, possibly throwing a DatabaseException
        try {
            readData(dbPath);
            this.dbPath = dbPath;
        } catch (Exception e) {
            throw new DatabaseException("Some error occured while reading the database. Check the path and file.");
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
        } catch (DatabaseException e) {
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

    private void readData(String dbPath) throws DatabaseException {
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
            throw new DatabaseException("FileNotFoundException while reading database.");
        } catch (IOException e) {
            throw new DatabaseException("IOException while reading database.");
        }
    }
    
    private void writeData(String dbPath) throws DatabaseException {
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
            throw new DatabaseException("FileNotFoundException while writing database file.");
        }
    }
}
