package github.antmonitor.api;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

class WorkerRequest {

  private static final String URL_PARAM_TEMPLATE = "?key={0}&nonce={1}&signature={2}";
  private static final Logger log = LogManager.getLogger(WorkerRequest.class);
  private String key;
  private String nonce;
  private String signature;

  public WorkerRequest(String key, String secret, String userId) {
    this.key = key;

    nonce = Long.toString(Instant.now().toEpochMilli());
    signature = apiSignature(userId, nonce, secret);


  }

  @NotNull
  private String apiSignature(String userId, String nonce, String secret) {
    String data = userId + key + nonce;
    try {
      Mac hmacSha256 = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
      hmacSha256.init(secretKey);
      return Hex.encodeHexString(hmacSha256.doFinal(data.getBytes())).toUpperCase(Locale.ENGLISH);
    } catch (Exception e) {
      log.error("HMAC Exception, should never happen");
      return "HMAC Exception"; //very bad code
    }
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

  public String getParams() {
    return MessageFormat.format(URL_PARAM_TEMPLATE, key, nonce, signature);
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
}
