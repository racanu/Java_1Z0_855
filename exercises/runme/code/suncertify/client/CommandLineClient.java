package suncertify.client;

import suncertify.db.*;
import suncertify.db.SecurityException;

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
                System.out.println("Updating record #" + k + ".");
                long cookie = 0;
                try {
                    Thread.sleep(Math.round(Math.random() * 1000));
                    cookie = db.lock(k); 
                    String[] v = db.read(k);
                    // v[4] = "\u00A5111.11";
                    // v[6] = "12345bla";
                    db.update(k, v, cookie);
                } catch (SecurityException e) {
                    e.printStackTrace();
                    break;
                } catch (RecordNotFoundException e) {
                    e.printStackTrace();
                    break;
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    try {
                        db.unlock(k, cookie);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        break;
                    } catch (RecordNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
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
        } catch (DatabaseRuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //
        }
    }

}
