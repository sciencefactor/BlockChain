package blockchain;


import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;


public class SignedTransaction {
    private List<byte[]> signedTransaction;
    private Transaction unsignedTransaction;


    public SignedTransaction(Transaction transaction, byte[] keyFile) throws Exception {
        this.unsignedTransaction = transaction;
        signedTransaction = new ArrayList<>();
        byte[] data = Transaction.convertTransactionToBytes(transaction);
        signedTransaction.add(data);
        assert data != null;
        signedTransaction.add(sign(data, keyFile));
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public byte[] sign(byte[] data, byte[] keyFile) throws InvalidKeyException, Exception{
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(getPrivate(keyFile));
        rsa.update(data);
        return rsa.sign();
    }

    //Method to retrieve the Private Key from a file
    public PrivateKey getPrivate(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public List<byte[]> getSignedTransaction() {
        return signedTransaction;
    }

    public Transaction getUnsignedTransaction() {
        return unsignedTransaction;
    }
}