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
    public static final String TEST_REGEX = "\\d+\\s+\\d+\\s+\\d+\\.\\d+\\" +
            "s+(.+?)\\s+\\(\\d{4}\\)";
    public static final int TEST_COUNT = 10;
    public static final String RESULT_QUERY = "[class='sc-17bafbdb-2 ffAEHI']";
    public static final String TITLE_QUERY = "h1";
    public static final String SUMMARY_QUERY = "[class='sc-466bb6c-0 kJJttH']";
    public static final String RATING_QUERY = "[class='sc-bde20123-1 iZlgcd']";
    public static final String IMG_ELEMENTS_QUERY = "[class='sc-e226b0e3-7" +
            " ZYxwn']";
    public static final String END_LINKED_IMG_QUERY = "[class='ipc" +
            "-lockup" +
            "-overlay ipc-focusable']";
    public static final String POSTER_QUERY = "[class='sc" +
            "-7c0a9e7c-0 fEIEer']";
    public static final String GENERAL_INFO_QUERY = "[class='ipc-inline-list " +
            "ipc-inline-list--show-dividers sc-afe43def-4 kdXikI " +
            "baseAlt']";
    public static final String IMDBRANK_QUERY = "[class='sc-5f7fb5b4-1 " +
            "bhuIgW']";
    public static final String DIRECTORS_CONTAINER_QUERY = "[class='ipc-inline-list__item']";
    public Document movieDoc;
    public String linkedImgUrl, endLinkedImgUrl, poster, rating, year,
            motionPictureRating, duration, IMDBRank;
    public String[] directors, writers, stars;
    public void findMovie(String movieName) {
        String url = "https://www.imdb.com/find?q=" + movieName.replace(
                " ", "+");
        String title, description;

        try {
            Document doc = Jsoup.connect(url).get();
            Elements results = doc.select(RESULT_QUERY);
            if (!results.isEmpty()) {
                Element firstResult = results.first();
                Elements result = firstResult.select("a");
                String moviePageUrl = "https://www.imdb.com" + result.attr(
                        "href");

                movieDoc = Jsoup.connect(moviePageUrl).get();
                title = movieDoc.select(TITLE_QUERY).text();
                description = movieDoc.select(SUMMARY_QUERY).text();
                rating = movieDoc.select(RATING_QUERY).first().text();
                findPoster();
                findGeneralInfo();
                IMDBRank = movieDoc.select(IMDBRANK_QUERY).first().text();
                Elements directorsList =
                        movieDoc.select(DIRECTORS_CONTAINER_QUERY).not(
                                ":empty");
                if (!directorsList.isEmpty()) {
                    int i = 0;
                    directors = new String[directorsList.size()];
                    for (Element director : directorsList) {
                        directors[i] =
                                director.select("a[href*=/name/]").text();
                        System.out.println("DIRECTOR " + directors[i]);
                        i++;
                    }
                }

                // creates a movie object, should be stored inside a list
                Movie movie = new Movie(title, description, poster, year,
                        motionPictureRating, duration, rating, IMDBRank,
                        directors, writers, stars);
            } else {
                System.out.println("No results found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testMovieScrape() {
        try {
            InputStream inputStream = Main.class.getResourceAsStream(TEST_PATH);

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                Pattern titlePattern = Pattern.compile(TEST_REGEX);
                int count = 0;

                String line;
                while((line = reader.readLine()) != null &&
                        count < TEST_COUNT) {
                    Matcher matcher = titlePattern.matcher(line);
                    if (matcher.find()) {
                        String title = matcher.group(1);
                        this.findMovie(title);
                        count++;
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

    //Poster on movie page is not large enough, need to query for the link that
    // provides a
    // larger poster
    private void findPoster() {
        Elements imgElements = movieDoc.select(IMG_ELEMENTS_QUERY);

        if (!imgElements.isEmpty()) {
            endLinkedImgUrl =
                    imgElements.select(END_LINKED_IMG_QUERY).attr(
                            "href");
        }

        if (endLinkedImgUrl != "") {
            linkedImgUrl = "https://www.imdb.com" + endLinkedImgUrl;
            try {
                Document imgDoc = Jsoup.connect(linkedImgUrl).get();
                poster = imgDoc.select(POSTER_QUERY).attr("src");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No poster found!");
        }
    }
    private void findGeneralInfo() {
        Elements generalInfo = movieDoc.select(GENERAL_INFO_QUERY);

        if (!generalInfo.isEmpty()) {
            for (Element info : generalInfo) {
                String[] finalInfo = info.text().split("\\s+");
                year = finalInfo[0];
                motionPictureRating = finalInfo[1];
                duration =
                        finalInfo[2] + " " + finalInfo[3];
                System.out.printf("year: %s motionPictureRating: %s " +
                                "duration: %s\n", year, motionPictureRating,
                        duration);
            }
        }
    }
}