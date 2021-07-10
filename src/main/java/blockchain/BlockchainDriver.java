package blockchain;

import java.io.IOException;
import java.util.ArrayList;

public class BlockchainDriver {

    public static final String filename = "blockchain.db";
    static volatile TransactionsContainer currentTransactionsContainer = new TransactionsContainer();
    static volatile TransactionsContainer bufferedTransactionsContainer = new TransactionsContainer();




    public static synchronized void addBlock(Block block) {
        if (isValidBlock(block) && isValidBlockchain()) {
            Blockchain.addNewBlock(block);
            System.out.print(block);
            long execTime = calcGenerationTime(block);
            updateProofOfWork(execTime);
            updateCurrentTransactionContainer();
        }
    }

    public static synchronized void updateProofOfWork(long generateTime) {
        if (generateTime > 8 && Blockchain.proofOfWork > 0) {
            Blockchain.proofOfWork--;
            System.out.println("N was decreased to " + Blockchain.proofOfWork + "\n");
        } else if (generateTime < 2 && Blockchain.proofOfWork < 4) {
            Blockchain.proofOfWork++;
            System.out.println("N was increased to " + Blockchain.proofOfWork + "\n");
        } else {
            System.out.println("N stays the same" + "\n");
        }
    }


    public static void updateCurrentTransactionContainer() {
        currentTransactionsContainer = bufferedTransactionsContainer;
        bufferedTransactionsContainer.clear();
    }

    public static long calcGenerationTime(Block block) {
        long execTime;
        if (block.getId() > 1) {
            execTime = (block.getTimeStamp() - BlockchainDriver.getPreviousBlock(block).getTimeStamp())/1000;
        } else {
            execTime = 2;
        }
        System.out.printf("Block was generating for %d seconds\n", execTime);
        return execTime;
    }

    public static void addToBufferedTransactionContainer(SignedTransaction signedTransaction) throws Exception {
        boolean checkSign = VerifyTransaction
                .verifySignature(signedTransaction, signedTransaction.getUnsignedTransaction().getClient().getPublicKey());
        boolean checkID = checkTransactionID(signedTransaction
                .getUnsignedTransaction()
                .getCurrentTransactionId());
        boolean solvency = checkSolvency(signedTransaction.getUnsignedTransaction());
        //System.out.println(signedTransaction.getUnsignedTransaction() + " " + checkSign + " " + checkID + " " + solvency);

        if (checkSign && checkID && solvency) {
            bufferedTransactionsContainer.addTransaction(signedTransaction.getUnsignedTransaction());
            Blockchain.increaseTransactionID();
        }
    }



    private static boolean checkSolvency(Transaction transaction){
        if(transaction.getClient() == Blockchain.blockchainClient) {
            return true;
        }
        long solvency = TransactionsContainer.calcMoneyOfClientInBlockchain(transaction.getClient());
        //System.out.println(transaction.getClient().getName() + " has " + solvency + "$");
        if(transaction.getSumm() < solvency){
            return true;
        }
        return false;
    }


    private static boolean checkTransactionID(Long shipID) {
        return shipID >= Blockchain.getCurrentTransactionId();
    }


    public static boolean isValidBlock(Block block) {
        if (!block.getCurrentHash().substring(0, block.getProofOfWork()).matches("0{" + block.getProofOfWork() + "}")) {
            //System.out.println("!!!  Failed block proof of work  !!!");
            return false;
        }
//        System.out.println("" + block.getTransactions().toString() + " " +
//                " time " + block.getTimeStamp() + " " +
//                " parent " + block.getParentHash() + " " +
//                " id " + block.getId() + " " +
//                " proof " + block.getProofOfWork() + " " +
//                " magik " + block.getMagickNumber() + " " +
//                " hash " + block.getCurrentHash());
        String combinedString = Miner.combineFields(
                block.getTransactions(),
                block.getTimeStamp(),
                block.getParentHash(),
                block.getId(),
                block.getProofOfWork());
        combinedString += block.getMagickNumber();
        if (!Miner.applySha256(combinedString).equals(block.getCurrentHash())) {
            //System.out.println("!!!  Failed block hash check  !!! for" + block.getId() + " : " + Miner.applySha256(combinedString));
            return false;
        }
        if (block.getId() > 1) {
            // для старых блоков
            if (block.getId() <= Blockchain.lastBlock().getId()) {
                if (Blockchain.lastBlock() != null && !getPreviousBlock(block).getCurrentHash().equals(block.getParentHash())) {
//                    System.out.println(getPreviousBlock(block).getCurrentHash());
//                    System.out.println(block.getParentHash());
                    //System.out.println("!!!  Failed block data integrity check  !!!");
                    return false;
                }
            } else {
                // для новых блоков
                if (Blockchain.lastBlock() != null && !Blockchain.lastBlock().getCurrentHash().equals(block.getParentHash())) {
//                    System.out.println(Blockchain.lastBlock().getCurrentHash());
//                    System.out.println(block.getParentHash());
                    //System.out.println("!!!  Failed block data integrity check  !!!");
                    return false;
                }
            }
        }
        return true;
    }

    public static Block getPreviousBlock(Block block) {
        int index = 0;
        if (block.getId() > 1) {
            index = Blockchain.getBlocks().indexOf(block) - 1;
        }
        return Blockchain.getBlocks().get(index);
    }

    public static synchronized boolean isValidBlockchain() {
        if (Blockchain.getBlockchainSize() == 0) {
            return true;
        }
        ArrayList<Block> blocksToCheck = Blockchain.getBlocks();
        for (int i = 0; i < blocksToCheck.size(); i++) {
            if (!isValidBlock(blocksToCheck.get(i))) {
                System.out.println("!!!  Failed blockchain integrity check  !!!");
                return false;
            }
        }
        return true;
    }

    public synchronized static TransactionsContainer getCurrentTransactionsContainer() {
        return TransactionsContainer.deepCopyOf(currentTransactionsContainer);
    }

    public static void saveBlockChain(Blockchain blockchain) {
        try {
            SerializationUtils.serialize(blockchain, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}