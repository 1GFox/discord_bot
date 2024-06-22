package com.sedmelluq.discord.lavaplayer.source.yamusic;

import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class CustomAuthYandexMusicUrlLoader extends DefaultYandexMusicDirectUrlLoader {

    @Value("${ya.token}")
    private String yaToken;



    @Override
    protected <T> T extractFromApi(String url, ApiExtractor<T> extractor) {
        try {
            HttpInterface httpInterface = this.httpInterfaceManager.getInterface();

            T var14;
            try {
                HttpGet request = new HttpGet(url);
                request.addHeader("Authorization", "OAuth " + yaToken);
                CloseableHttpResponse resp = httpInterface.execute(request);

                String responseText;
                try {
                    int statusCode = resp.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        throw new IOException("Invalid status code: " + statusCode);
                    }

                    responseText = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
                } catch (Throwable var10) {
                    if (resp != null) {
                        try {
                            resp.close();
                        } catch (Throwable var9) {
                            var10.addSuppressed(var9);
                        }
                    }

                    throw var10;
                }

                if (resp != null) {
                    resp.close();
                }

                JsonBrowser response = JsonBrowser.parse(responseText);
                if (response.isNull()) {
                    throw new FriendlyException("Couldn't get API response.", FriendlyException.Severity.SUSPICIOUS, (Throwable) null);
                }

                response = response.get("result");
                if (response.isNull() && !response.isList()) {
                    throw new FriendlyException("Couldn't get API response result.", FriendlyException.Severity.SUSPICIOUS, (Throwable) null);
                }

                var14 = extractor.extract(httpInterface, response);
            } catch (Throwable var11) {
                if (httpInterface != null) {
                    try {
                        httpInterface.close();
                    } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                    }
                }

                throw var11;
            }

            if (httpInterface != null) {
                httpInterface.close();
            }

            return var14;
        } catch (Exception var12) {
            throw ExceptionTools.wrapUnfriendlyExceptions("Loading information for a Yandex Music track failed.", FriendlyException.Severity.FAULT, var12);
        }
    }
}
