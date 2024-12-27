package com.demo.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwsHeader;

@SuppressWarnings("unchecked")
public class JwtTest {

    Key[] keys;

    {
        URL resource = this.getClass().getResource("/pri.key");
        URL resource2 = this.getClass().getResource("/pub.key");
        String basePath = "src/main/resources";

        if (Objects.isNull(resource) || Objects.isNull(resource2)) {
            saveKeys(basePath);
        } else {
            try {
                Key priKey = KeyFactory
                        .getInstance(SignatureAlgorithm.RS256.getFamilyName())
                        .generatePrivate(new PKCS8EncodedKeySpec(readFile2(resource)));
                Key pubKey = KeyFactory
                        .getInstance(SignatureAlgorithm.RS256.getFamilyName())
                        .generatePublic(new X509EncodedKeySpec(readFile2(resource2)));

                keys = new Key[] { pubKey, priKey };
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] readFile(URL resource) {
        try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()))) {
            System.out.println(resource.getPath());

            String line = "";
            String s = "";
            while ((line = reader.readLine()) != null) {
                s += line;
            }

            return s.getBytes();

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] readFile2(URL resource) {
        try (InputStream openStream = resource.openStream()) {
            DataInputStream dis = new DataInputStream(openStream);
            byte[] b = new byte[openStream.available()];
            dis.readFully(b);

            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private void saveKeys(String basePath) {
        keys = genKey("This@#!@#EWQ2222222");
        try (
                FileOutputStream priInOutputStream = new FileOutputStream(
                        String.format("%s%s", basePath, "/pri.key"));
                FileOutputStream pubInOutputStream = new FileOutputStream(
                        String.format("%s%s", basePath, "/pub.key"))) {

            priInOutputStream.write(keys[1].getEncoded());
            pubInOutputStream.write(keys[0].getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void t1() {
        // String str = "WSSKWWJWJW";
        // crypto(SignatureAlgorithm.HS256, str, str);
    }

    private <T extends Key> void crypto(SignatureAlgorithm sign, T key, T unKey) {
        Map<String, Object> map = new HashMap<>();
        map.put("a", "1");
        map.put("true", 0);
        String token = Jwts
                .builder()
                .claim("ts", System.currentTimeMillis())
                .addClaims(map)
                .signWith(sign, key)
                .setExpiration(new Date(System.currentTimeMillis() + 100000))
                .compact();

        Jwt<DefaultJwsHeader, Claims> parse = parseJwt(unKey, token);
        System.out.println(token);
        System.out.println();
        System.out.println(parse.getBody());

    }

    private <T extends Key> Jwt<DefaultJwsHeader, Claims> parseJwt(T unKey, String token) {
        try {
            Jwt<DefaultJwsHeader, Claims> parse = Jwts.parser().setSigningKey(unKey).parse(token);
            return parse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void t2() {
        // 加密公钥 解密私钥
        crypto(SignatureAlgorithm.RS256, keys[1], keys[0]);
    }

    public Key[] genKey(String password) {
        try {
            KeyPairGenerator instance = KeyPairGenerator.getInstance(SignatureAlgorithm.RS256.getFamilyName());
            instance.initialize(1024, new SecureRandom(password.getBytes()));
            KeyPair genKeyPair = instance.genKeyPair();

            PrivateKey pri = genKeyPair.getPrivate();
            PublicKey pub = genKeyPair.getPublic();

            return new Key[] { pub, pri };
        } catch (Exception e) {
            e.printStackTrace();
            return new Key[0];
        }
    }

    @Test
    public void test() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ0cyI6MTY3Nzk5OTkxMTkxMSwiYSI6IjEiLCJ0cnVlIjowLCJleHAiOjE2NzgwMDAwMTF9.JkP-hu7OsoRRZwXpXCIpjWjx8A03bidM1GMMS6cFnqCEtf12WaujrYvJW2yETxsrH41QxRmpaSHWkUAi9Tl0G_3y4HdRJeeSeO9zgiK79GXM7-ngjyBl67QGQNPRUtz9yEWwZ3UMsr2OZUOKMSzUpnnXDp7kc21nujNRvcXZB6w";
        Object v = parseJwt(keys[0], token);

        System.out.println(v);
    }
}
