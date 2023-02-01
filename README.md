# RSocket Sample with SSL

Start server with profile `server` or with env `RSOCKET_CLOUDEVENTS_PROFILE=server`

Start client with profile `client` or with env `RSOCKET_CLOUDEVENTS_PROFILE=client`

# PKCS12 Key

1. keytool.exe -genkeypair -alias fortycoderplus -keyalg RSA -keysize 4096 -validity 3650 -dname "CN=localhost" -keypass fortycoderplus -keystore keystore.p12 -storeType PKCS12 -storepass fortycoderplus
2. keytool.exe -list -v -keystore keystore.p12
3. keytool.exe -export -alias fortycoderplus -keystore keystore.p12 -rfc -file cert.cer
4. keytool.exe -import -alias fortycoderplus -file cert.cer -keystore truststore.p12
5. keytool.exe -list -v -keystore truststore.p12