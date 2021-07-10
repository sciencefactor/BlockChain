package blockchain;

import java.io.File;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;


public class VerifyTransaction {

    public static boolean verifySignature(SignedTransaction transaction, byte[] keyFile) throws Exception {
        byte[] data = transaction.getSignedTransaction().get(0);
        byte[] signature = transaction.getSignedTransaction().get(1);
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(getPublic(keyFile));
        sig.update(data);

        return sig.verify(signature);
    }

    //Method to retrieve the Public Key from a file
    private static PublicKey getPublic(byte[] keyBytes) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}