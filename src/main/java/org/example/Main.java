package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static final String SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=AJCUvFbuKVFnbkpnzUipJGCm8QkyefC2PhBrdY4E";
    public static final ObjectMapper mapper = new ObjectMapper();

    private static Image loadImage(String fileName, String url) {
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Загружено изображение:" + fileName);
            }else {
                System.out.println("Файл был загружен ранее: " + fileName);
            }
            ImageIO.write(img, "jpeg", file);
            return img;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false).build())
                .build();

        HttpGet request = new HttpGet(SERVICE_URL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = closeableHttpClient.execute(request);

        Post posts = mapper.readValue(response.getEntity().getContent(),
                new TypeReference<>() {
                });
        System.out.println(posts.getUrl());

        loadImage(Paths.get(
                new URI(posts.getUrl())
                        .getPath())
                        .getFileName()
                        .toString(),
                posts.getUrl());

        response.close();
        closeableHttpClient.close();
    }
}