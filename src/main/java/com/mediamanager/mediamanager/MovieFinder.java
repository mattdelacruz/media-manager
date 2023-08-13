package com.mediamanager.mediamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MovieFinder {
    public static final String TEST_PATH = "/top250.txt";
    public static final String TEST_REGEX = "\\d+\\s+\\d+\\s+\\d+\\.\\d+\\s+(.+?)\\s+\\(\\d{4}\\)";
    public static final int TEST_COUNT = 10;
    public static final String RESULT_QUERY = "[class='sc-17bafbdb-2 ffAEHI']";
    public static final String TITLE_QUERY = "h1";
    public static final String SUMMARY_QUERY = "[class='sc-466bb6c-0 kJJttH']";

    public MovieFinder() {
    }

    public void findMovie(String movie) {
        movie.replace(" ", "+");
        String url = "https://www.imdb.com/find?q=" + movie.replace(" ", "+");
        String endImgUrl = "";
        String linkedImgUrl = "";
        try {
            Document doc = Jsoup.connect(url).get();
            Elements results = doc.select(RESULT_QUERY);
            if (!results.isEmpty()) {
                Element firstResult = results.first();
                Elements var10000 = firstResult.select("a");
                String moviePageUrl = "https://www.imdb.com" + var10000.attr("href");
                Document movieDoc = Jsoup.connect(moviePageUrl).get();
                String title = movieDoc.select(TITLE_QUERY).text();
                System.out.println("Title: " + title);
                String summary = movieDoc.select(SUMMARY_QUERY).text();
                System.out.println("Summary: " + summary);

                Elements anchorElements = movieDoc.select("[class='sc-e226b0e3-7 ZYxwn']");

                if (!anchorElements.isEmpty()) {
                        endImgUrl = anchorElements.select("[class='ipc" +
                                "-lockup" +
                                "-overlay ipc-focusable']").attr("href");
                }

                if (endImgUrl != "") {
                    linkedImgUrl = "https://www.imdb.com" + endImgUrl;
                    String finalImgUrl = "";
                    try {
                        Document imgDoc = Jsoup.connect(linkedImgUrl).get();
                        Elements finalImg = imgDoc.select("[class='sc" +
                                "-7c0a9e7c-2 kEDMKk']");
                        if (!finalImg.isEmpty()) {
                            finalImgUrl = finalImg.select("[class='sc" +
                                    "-7c0a9e7c-0 fEIEer']").attr("src");
                        }
                        System.out.println(
                                "Real and bigger image url: " + finalImgUrl);
                    } catch (IOException var11) {
                        var11.printStackTrace();
                    }
                } else {
                    System.out.println("No poster found!");
                }
            } else {
                System.out.println("No results found");
            }
        } catch (IOException var10) {
            var10.printStackTrace();
        }
    }

    public void testMovieScrape() {
        try {
            InputStream inputStream = Main.class.getResourceAsStream(TEST_PATH);

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                Pattern titlePattern = Pattern.compile(TEST_REGEX);
                int count = 0;

                String line;
                while((line = reader.readLine()) != null && count < TEST_COUNT) {
                    Matcher matcher = titlePattern.matcher(line);
                    if (matcher.find()) {
                        String title = matcher.group(1);
                        this.findMovie(title);
                        ++count;
                    }
                }
            } catch (Throwable e) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e1) {
                        e1.addSuppressed(e1);
                    }
                }

                throw e;
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}