package blockchain;

import java.io.*;


public class Transaction implements Serializable {
    private final long currentTransactionId;
    private final Client name;
    private final long summ;
    private final Client address;


    public Transaction(Client name, Client adress, long summ) {
        this.name = name;
        this.address = adress;
        this.summ = summ;
        this.currentTransactionId = Blockchain.getCurrentTransactionId();
    }

    public long getCurrentTransactionId() {
        return currentTransactionId;
    }

    public Client getClient() {
        return name;
    }


    public long getSumm() {
        return summ;
    }

    public Client getAddress() {
        return address;
    }

    public static byte[] convertTransactionToBytes(Transaction transaction) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(transaction);
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

    public static Transaction convertBytesToTransaction(byte[] array){
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object object = in.readObject();
            return (Transaction) object;
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


    @Override
    public String toString() {
        return "{" +
                "ID" + currentTransactionId +
                " " + name.getName() +
                " send " +
                + summ +
                "$ to " + address.getName() +
                "} ";
    }
}