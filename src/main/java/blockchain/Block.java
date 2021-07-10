package blockchain;

import java.io.Serializable;

public class Block implements Serializable {

    private final long id;
    private final String parentHash;
    private final long timeStamp;
    private final String currentHash;
    private final TransactionsContainer transactions;
    private final long magickNumber;
    private final int proofOfWork;
    private final String miner;


    public Block(String currentHash, long timeStamp, TransactionsContainer transactions, long magickNumber, String prevHash, long currentId, int proofOfWork, String miner) {
        this.currentHash = currentHash;
        this.timeStamp = timeStamp;
        this.transactions = transactions;
        this.magickNumber = magickNumber;
        this.proofOfWork = proofOfWork;
        this.miner = miner;

        //Global fields
        this.parentHash = prevHash;
        this.id = currentId;
    }

    public long getId() {
        return id;
    }

    public String getParentHash() {
        return parentHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public String getMiner() {
        return miner;
    }

    public long getMagickNumber() {
        return magickNumber;
    }

    public int getProofOfWork() {
        return proofOfWork;
    }

    @Override
    public String toString() {
        return "Block:\n" +
                "Created by miner # " + this.getMiner() + "\n" +
                this.getMiner() + " gets " + Blockchain.getReward() + " VC" + "\n" +
                "Id: " +
                +this.id + "\n" +
                "Timestamp: " + this.timeStamp + "\n" +
                "Magic number: " + this.magickNumber + "\n" +
                "Hash of the previous block:\n" + this.parentHash + "\n" +
                "Hash of the block:\n" + this.currentHash + "\n" +
                "Block data: " + this.transactions.toString() + "\n";
    }

    public TransactionsContainer getTransactions() {
        return transactions;
    }
}