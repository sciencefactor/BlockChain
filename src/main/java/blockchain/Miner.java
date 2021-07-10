package blockchain;

import java.security.MessageDigest;

import java.util.Date;



public class Miner implements Runnable{

    Blockchain blockchain;
    Client minerClient;

    public Miner(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public void run() {
        this.minerClient = new Client("Miner-" + Thread.currentThread().getName().substring(14));

        while (Blockchain.getBlockchainSize() <= 14) {
            minerClient.run();
            Block block = generateBlock();
            BlockchainDriver.addBlock(block);

        }
    }

    //Generate new block, but not add it to blockchain
    public Block generateBlock() {
        TransactionsContainer transactions = BlockchainDriver.getCurrentTransactionsContainer();
        Transaction reward = generateReward();
        transactions.addTransaction(reward);

        HashWrapper hashResult = generateProofedHash(transactions);

        return new Block(hashResult.hash,
                hashResult.timeStamp,
                transactions,
                hashResult.magickNumber,
                Blockchain.getPrevHash(),
                Blockchain.getCurrentId(),
                Blockchain.getProofOfWork(),
                minerClient.getName());
    }

    public Transaction generateReward(){
        Transaction result = new Transaction(Blockchain.getBlockchainClient(), this.minerClient, Blockchain.getReward());
        Blockchain.increaseTransactionID();
        return result;
    }


    //Generate new Hash based on current data and message
    public HashWrapper generateProofedHash(TransactionsContainer transactions) {
        String combinedString;
        long timeStamp;
        int proofOfWork;
        synchronized (this) {
            timeStamp = new Date().getTime();
            proofOfWork = Blockchain.getProofOfWork();

            String parentHash;
            long currentID;
            if (Blockchain.getBlockchainSize() > 0) {
                parentHash = Blockchain.lastBlock().getCurrentHash(); // взять хэш предыдущего блока
                currentID = Blockchain.lastBlock().getId() + 1; // взять id предыдущего блока
            } else {                                               // если предыдущего нет, то взять стандартные начальные значения
                parentHash = Blockchain.getInitHash();
                currentID = Blockchain.getCurrentId();
            }
            combinedString = combineFields(transactions, timeStamp, parentHash, currentID, proofOfWork);  // соединить все поля в одну строку
        }


        long magickNumber = (long) (Math.random() * 100000000000L);       //генерировать рандомное число
        String compliteString = combinedString + magickNumber;         //добавить число ко всем полям
        String hash = applySha256(compliteString);

        while (!hash.substring(0, proofOfWork).matches("0{" + proofOfWork + "}")) {  // пока первые proofOfWork чисел не будут нулями делать:
            magickNumber = (long) (Math.random() * 100000000000L);       //генерировать рандомное число
            compliteString = combinedString + magickNumber;         //добавить число ко всем полям
            hash = applySha256(compliteString);
        }
        return new HashWrapper(hash, magickNumber, timeStamp);     // вернуть результатные поля, обёрнутые в класс
    }


    public static String combineFields(TransactionsContainer transactions, long timeStamp, String parentHash, long currentID, int proofOfWork) {
        return transactions.toString()
                + timeStamp
                + parentHash
                + currentID
                + proofOfWork;
    }


    public static String applySha256(String input) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte elem : hash) {
                String hex = Integer.toHexString(0xff & elem);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class HashWrapper {
        String hash;
        long magickNumber;
        long timeStamp;

        public HashWrapper(String hash, long magickNumber, long timeStamp) {
            this.hash = hash;
            this.magickNumber = magickNumber;
            this.timeStamp = timeStamp;
        }
    }

}

// For parallel mining
class MineRequest extends Miner implements Runnable {

    public Client minerReqClient;

    public MineRequest(Blockchain blockchain) {
        super(blockchain);
    }

    @Override
    public void run() {
        minerReqClient = new Client(Thread.currentThread().getName());
        for (int i = 0; i < 5; i++) {
            if (Blockchain.getBlockchainSize() == 5) {
                break;
            }
            minerReqClient.run();
            Block block = generateBlock();
            BlockchainDriver.addBlock(block);

        }
    }
}