package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Blockchain implements Serializable {


    private static final long serialVersionUID = 34L;
    private static final long reward = 100;
    private static long currentId;
    private static long currentTransactionId;
    private static String prevHash;
    private static volatile ArrayList<Block> blocks;
    static int proofOfWork;
    static final Client blockchainClient = new Client("BLOCKCHAIN");


    //Constructor
    public Blockchain() {
        blocks = new ArrayList<>();
        currentId = 1;
        currentTransactionId = 1;
        prevHash = "0";
        proofOfWork = 0;
    }

    public static synchronized void addNewBlock(Block block){
        blocks.add(block);
        increaseID();
        updatePrevHash(block);
    }

    public static Block lastBlock() {
        int len = blocks.size();
        if (len > 0) {
            return blocks.get(len - 1);
        }
        System.out.println("Trying access not initialised Block");
        return null;
    }


    public static void increaseTransactionID() {
        //int randomNum = ThreadLocalRandom.current().nextInt(1, 15);
        currentTransactionId ++;
    }

    public static void increaseID() {
        currentId++;
    }

    public static void updatePrevHash(Block prevblock) {
        prevHash = prevblock.getCurrentHash();
    }

    public static ArrayList<Block> getBlocks() {
        return blocks;
    }

    public synchronized static long getBlockchainSize() {
        return blocks.size();
    }

    public static long getCurrentId() {
        return currentId;
    }

    public static String getPrevHash() {
        return prevHash;
    }

    public static String getInitHash() {
        return prevHash;
    }

    public static int getProofOfWork() {
        return proofOfWork;
    }

    public static long getReward() {
        return reward;
    }

    public static Client getBlockchainClient() {
        return blockchainClient;
    }

    public static long getCurrentTransactionId() {
        return currentTransactionId;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Block block : Blockchain.getBlocks()) {
            System.out.println(block + "\n");
        }

        return result.toString();
    }
}