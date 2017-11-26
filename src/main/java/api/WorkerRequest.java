package api;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.Instant;

public class WorkerRequest {
    private String key;
    private String nonce;
    private String signature;
    private static final Logger log = LogManager.getLogger(WorkerRequest.class);

    public WorkerRequest(String key, String secret, String userId) {
        this.key = key;

        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        nonce = Long.toString(Instant.now().toEpochMilli());
        signature = apiSignature(userId, nonce, secret);


    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    public void setNonceUnixTS() {

    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
    @NotNull
    private String apiSignature(String userId, String nonce, String secret)  {
        String data = userId + key + nonce;
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmacSha256.init(secretKey);
            return Hex.encodeHexString(hmacSha256.doFinal(data.getBytes())).toUpperCase();
        }
        catch (Exception e) {
            log.error("HMAC Exception, should never happen");
            return "HMAC Exception"; //very bad code
        }
    }



}
