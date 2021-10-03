package com.api.address.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.api.address.entity.Endereco;
import com.api.address.entity.Municipio;
import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Service
public class RestServico {
    private static final Logger LOGGER = Logger.getLogger(RestServico.class.getName());
    private static final String ERRO = "ERRO: {0}";

    private Client cliente;

    @PostConstruct
    private void init() {
        cliente = ClientBuilder.newBuilder().sslContext(getContextoSSL()).hostnameVerifier(getNomeHostVerificador())
                .build();
    }

    public Collection<Municipio> buscarMunicipios(String target, String path) throws Exception {
        try {
            String json = buscarJson(target, path);
            return new Gson().fromJson(new JsonParser().parse(json).getAsJsonArray(),
                    new TypeToken<ArrayList<Municipio>>() {
                    }.getType());
        } catch (BadRequestException | NotFoundException e) {
            LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(e));
            throw new Exception("Não foi possível conectar à API.");
        }
    }

    public Endereco buscarEndereco(String target, String path) throws Exception {
        try {
            String json = buscarJson(target, path);
            return new Gson().fromJson(new JsonParser().parse(json).getAsJsonObject(), new TypeToken<Endereco>() {
            }.getType());
        } catch (BadRequestException | NotFoundException e) {
            throw new Exception("Não foi possível conectar à API.");
        }
    }

    private String buscarJson(String target, String path) {
        WebTarget url = cliente.target(target).path(path);
        Invocation.Builder invocationBuilder = url.request(MediaType.APPLICATION_JSON);
        String json = new String(invocationBuilder.get(String.class));
        return json;
    }

    /**
     * Classe é responsável pela verificação do nome do host durante a fase de
     * handshaking com o localidadesapi.
     *
     * @return host name verifier.
     */
    private HostnameVerifier getNomeHostVerificador() {
        return new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
    }

    /**
     * Classe responsável por fábricar sockets com protocolo de segurança SSL para
     * conexões HTTPS com o localidadesapi.
     *
     * @return SSL context.
     */
    private SSLContext getContextoSSL() {
        SSLContext ctx = null;
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        } catch (java.security.GeneralSecurityException ex) {
            LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(ex));
        }
        return ctx;
    }
}
