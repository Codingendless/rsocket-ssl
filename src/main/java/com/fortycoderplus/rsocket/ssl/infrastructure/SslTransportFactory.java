/*
 * (c) Copyright 2023 40CoderPlus. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortycoderplus.rsocket.ssl.infrastructure;

import com.fortycoderplus.rsocket.ssl.infrastructure.RSocketSslClientProperties.Ssl;
import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.tcp.TcpClient;

@Slf4j
@AllArgsConstructor
public class SslTransportFactory {

    private final Ssl sslConf;

    public ClientTransport sslClientTransport(String host, int port) {
        try {
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(sslConf.trustStoreType());
            keyStore.load(
                    new FileInputStream(sslConf.trustStore()),
                    sslConf.trustStorePassword().toCharArray());
            trustManagerFactory.init(keyStore);
            return TcpClientTransport.create(
                    TcpClient.create().host(host).port(port).secure(ssl -> {
                        try {
                            ssl.sslContext(SslContextBuilder.forClient()
                                    .keyStoreType(sslConf.trustStoreType())
                                    .trustManager(trustManagerFactory)
                                    .build());
                        } catch (SSLException ex) {
                            logger.error("Failed to build ssl context caused by: ", ex);
                            throw new RuntimeException(ex);
                        }
                    }));

        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException ex) {
            logger.error("Failed to init ssl caused by: ", ex);
            throw new RuntimeException(ex);
        }
    }
}
