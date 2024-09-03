package com.example.algoyweb.service.allen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class HttpURLConnectionEx {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String get(String requestUrl, Map<String, String> headers) {
        try {
            URL obj = new URL(requestUrl);
            HttpURLConnection con = requestUrl.startsWith("https") ? (HttpsURLConnection) obj.openConnection() : (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("charset", "utf-8");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setRequestMethod("GET");
            con.setDoOutput(false);
            con.connect();
            int resCode = con.getResponseCode();

            if (resCode != HttpURLConnection.HTTP_OK) {
                con.disconnect();
                throw new MyHttpFailRuntimeException("HTTP response code: " + resCode);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            con.disconnect();
            return response.toString();
        } catch (IOException e) {
            throw new MyException("IO Exception occurred", e);
        } catch (MyHttpFailRuntimeException e) {
            throw e;
        }
    }
}

class MyHttpFailRuntimeException extends RuntimeException {
    public MyHttpFailRuntimeException(String message) {
        super(message);
    }
}

class MyException extends RuntimeException {
    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}