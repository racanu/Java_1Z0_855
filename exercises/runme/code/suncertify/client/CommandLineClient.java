package suncertify.client;

import suncertify.db.*;

public class CommandLineClient {

    private static class RandomUpdater implements Runnable {
        private Data db;
        
        RandomUpdater(Data db) {
            this.db = db;
        }
        
        @Override
        public void run() {
            System.out.println("RandomUpdater started.");
            // Simulate random changes
            for (int i = 0; i < 100; i++) {
                int k = (int)Math.floor(Math.random() * db.getRecordCount());
                try {
                    System.out.println("Updating record #" + k + ".");
                    long cookie = db.lock(k);
                    db.update(k, db.read(k), cookie);
                    db.unlock(k, cookie);
                    Thread.sleep(Math.round(Math.random() * 1000));
                } catch (suncertify.db.SecurityException e) {
                    e.printStackTrace();
                } catch (DatabaseException e) {
                    // do nothing
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
            System.out.println("RandomUpdater ended.");
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the database path.");
            return;
        }
        
        try {
            Data db = new Data(args[0]);
            db.printDataCache();
            // Start the random updater
            Thread ru = new Thread(new RandomUpdater(db));
            ru.start();
            ru.join();
            db.printDataCache();
        } catch (DatabaseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //
        }
    }

}
