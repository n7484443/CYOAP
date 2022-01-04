package cyoap_main.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TranslationUtil {
    static final TranslationUtil instance = new TranslationUtil();
    public final String clientId = "CwYh_xvUi4tm5hWsmZb_";
    public final String clientSecret = "fj4UJJCZQI";
    public final String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
    public String sourceLanguage = "ko";
    public String targetLanguage = "en";

    public static TranslationUtil getInstance() {
        return instance;
    }

    public String getTranslate(String input) {
        String text;
        try {
            text = URLEncoder.encode(input, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("인코딩 실패", e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        if (sourceLanguage.equals(targetLanguage)) {
            System.err.println("target language and source language are same!");
            return null;
        }
        String responseBody = post(apiURL, requestHeaders, text);
        ObjectMapper mapper = new ObjectMapper();
        try {
            var jsonData = mapper.readTree(responseBody);
            var output = jsonData.get("message").get("result").get("translatedText");
            return output.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String post(String apiUrl, Map<String, String> requestHeaders, String text) {
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=" + sourceLanguage + "&target=" + targetLanguage + "&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
        try {
            con.setRequestMethod("POST");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
