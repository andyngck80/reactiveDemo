package com.fusion.coding;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URL;

public class Application {

    public static void main(String[] args) {
        String combine = Application.combine();
        System.out.println("combined JSON = " + combine);
    }

    public static String combine() {
        StringBuffer concatenated = new StringBuffer()
                .append("\n")
                .append("[\n");

        // reactive reading of files
        Flux.just(
                read("test-data-1.json"),
                read("test-data-2.json"),
                read("test-data-3.json"),
                read("test-data-4.json"))
            .log()
            .filter(StringUtils::isNotBlank)
            .map(doc -> doc + ",\n")
            .subscribe(new Subscriber<String>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(String doc) {
                    concatenated.append(doc);
                }

                @Override
                public void onError(Throwable t) {}

                @Override
                public void onComplete() {
                    int lastElementIndex = concatenated.length() - 1;
                    concatenated.replace(lastElementIndex - 1, lastElementIndex, "\n]");
                }
            });

        return concatenated.toString();
    }

    private static String read(String filePath) {
        try {
            URL url = Resources.getResource(filePath);
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Resource reading error. " + e);
            return "";
        }
    }
}
