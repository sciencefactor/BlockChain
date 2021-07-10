package blockchain;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionsContainer implements Serializable {
    public ArrayList<Transaction> transactionsBuffer;

    public TransactionsContainer(){
        transactionsBuffer = new ArrayList<>();
    }

    public static byte[] convertContainerToBytes(TransactionsContainer container) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(container);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static TransactionsContainer convertBytesToContainer(byte[] array){
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object object = in.readObject();
            return (TransactionsContainer) object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }


    public void addTransaction(Transaction transaction) {
        this.transactionsBuffer.add(transaction);
    }

    public synchronized static long calcMoneyOfClientInBlockchain(Client client) {
        ArrayList<Block> blocks = Blockchain.getBlocks();
        return blocks.stream()
                .map(Block::getTransactions)
                .map(transactions -> calcMoneyOfClientInContainer(client, transactions))
                .reduce((long) 0, Long::sum);
    }

    public static long calcMoneyOfClientInContainer(Client client, TransactionsContainer transactions){
        long sent = transactions.getTransactionsBuffer()
                .stream()
                .filter(trans -> trans.getClient().getName().equals(client.getName()))
                .map(Transaction::getSumm)
                .reduce((long) 0, Long::sum);
        long received = transactions.getTransactionsBuffer()
                .stream()
                .filter(trans -> trans.getAddress().getName().equals(client.getName()))
                .map(Transaction::getSumm)
                .reduce((long) 0, Long::sum);
        return received-sent;
    }

    public ArrayList<Transaction> getTransactionsBuffer() {
        return transactionsBuffer;
    }

    public void clear(){
        transactionsBuffer = new ArrayList<>();
    }

    public synchronized static TransactionsContainer deepCopyOf(TransactionsContainer transactions){
        byte[] serialise = TransactionsContainer.convertContainerToBytes(transactions);
        return TransactionsContainer.convertBytesToContainer(serialise);
    }

    @Override
    public String toString() {
        return "Transactions Container: " + transactionsBuffer.stream().map(Transaction::toString).collect(Collectors.joining());
    }
}