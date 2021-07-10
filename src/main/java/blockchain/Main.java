package blockchain;

import javax.crypto.Cipher;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static String filename = "blockchain.db";

    public static void main(String[] args) throws InterruptedException {

        Blockchain blockchain = new Blockchain();
        if (new File(filename).exists()) {
            blockchain = readBlockChain();
        }

        if (blockchain != null && BlockchainDriver.isValidBlockchain()) {
              int minerAndClientsNumbers = 4 + 4;
            ExecutorService executor = Executors.newFixedThreadPool(minerAndClientsNumbers);

            Client[] clients = {new Client("Cat"), new Client("John"), new Client("Rose"), new Client("Sara"), new Client("Poul")};
            for (Client client :
                    clients) {
                Transaction result = new Transaction(Blockchain.getBlockchainClient(), client, Blockchain.getReward());
                BlockchainDriver.currentTransactionsContainer.addTransaction(result);
                Blockchain.increaseTransactionID();
            }


            Miner miner = new Miner(blockchain);
            executor.submit(miner);
            Thread.sleep(120);
            for (int i = 0; i < 4; i++) {
                miner = new Miner(blockchain);
                executor.submit(miner);
            }

            for (int i = 0; i < 5; i++) {
                Thread.sleep(350);
                executor.submit(clients[i]);
            }
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

        }
    }

    public static Blockchain readBlockChain() {
        try {
            return (Blockchain) SerializationUtils.deserialize(filename);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBlockChain(Blockchain blockchain) {
        try {
            SerializationUtils.serialize(blockchain, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class SerializationUtils {
    /**
     * Serialize the given object to the file
     */
    public static void serialize(Object obj, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * Deserialize to an object from the file
     */
    public static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }
}