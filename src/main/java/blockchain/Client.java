package blockchain;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Client implements Runnable, Serializable {
    public final String name;
    public static ArrayList<Client> allClients = new ArrayList<>();
    private byte[] privateKey;
    public byte[] publicKey;

    public Client(String name) {
        this.name = name;
        allClients.add(this);
        GenerateKeys generateKeys = new GenerateKeys(1024);
        generateKeys.createKeys();
        privateKey = generateKeys.getPrivateKey().getEncoded();
        publicKey = generateKeys.getPublicKey().getEncoded();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(30L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Transaction transaction = generateTransaction();
            SignedTransaction signedTransaction = signTransaction(transaction);
            sendTransaction(signedTransaction);
        }
    }

    public Transaction generateTransaction() {
        Client address = chooseRandomAddress();
        long amount = chooseRandomAmount();
        return new Transaction(this, address, amount);
    }


    private SignedTransaction signTransaction(Transaction transaction) {
        try {
            return new SignedTransaction(transaction, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendTransaction(SignedTransaction transaction) {
        try {
            BlockchainDriver.addToBufferedTransactionContainer(transaction);
            Blockchain.increaseTransactionID();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client chooseRandomAddress() {
        int rndIndx = new Random().nextInt(allClients.size());
        Client result = allClients.get(rndIndx);
        if (result.getName().equals("BLOCKCHAIN") || result.getName().equals(name)) {
            result = chooseRandomAddress();
        }
        return result;
    }

    public long chooseRandomAmount() {
        return ThreadLocalRandom.current().nextLong(1, 20);
    }

    public String getName() {
        return name;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}