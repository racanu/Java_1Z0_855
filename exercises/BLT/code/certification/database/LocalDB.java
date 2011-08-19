package certification.database;

public interface LocalDB extends DB {
    // Flush cached data to the disk
    public void saveData() throws DatabaseException;
    // Other methods...
}

