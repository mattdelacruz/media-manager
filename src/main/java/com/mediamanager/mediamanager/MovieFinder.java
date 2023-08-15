package com.mediamanager.mediamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MovieFinder {
    public static final String IMDB_LINK = "https://www.imdb.com";
    public static final String IMDB_SEARCH_LINK = "https://www.imdb" +
            ".com/find?q=";
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
    public static final String AFFILIATES_CONTAINER_QUERY = "[class='sc" +
            "-acac9414-3 hKIseD']";
    public static final String AFFILIATES_QUERY = "[class='ipc-metadata-list-item__content" +
            "-container']";
    public static final String AFFILIATES_LIST_QUERY = "[class='ipc" +
            "-metadata-list-item__list-content-item " +
            "ipc-metadata-list-item__list-content-item--link']";

    
    public Document movieDoc;
    public String title, description, linkedImgUrl, endLinkedImgUrl, poster,
            rating, year, motionPictureRating, duration, IMDBRank;
    public ArrayList<String> directors, writers, stars;

    public void findMovie(String movieName) {
        String searchUrl = IMDB_SEARCH_LINK + movieName.replace(
                " ", "+");
        directors = new ArrayList<String>();
        writers = new ArrayList<String>();
        stars = new ArrayList<String>();
        try {
            Document doc = Jsoup.connect(searchUrl).get();
            Elements results = doc.select(RESULT_QUERY);
            if (!results.isEmpty()) {
                connectToMoviePage(results);
                findGeneralInfo();
                findPoster();
                findAffiliatesList();

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
                while ((line = reader.readLine()) != null &&
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
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void connectToMoviePage(Elements results) {
        Element firstResult = results.first();
        Elements result = firstResult.select("a");
        String moviePageUrl = IMDB_LINK + result.attr(
                "href");

        try {
            movieDoc = Jsoup.connect(moviePageUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void findAffiliatesList() {
        Elements affiliatesList =
                movieDoc.select(AFFILIATES_CONTAINER_QUERY).select(
                        AFFILIATES_QUERY);

        if (!affiliatesList.isEmpty()) {
            int i = 0;
            for (Element affiliates : affiliatesList) {
                Elements list =
                        affiliates.select(AFFILIATES_LIST_QUERY);
                for (Element l : list) {
                    switch (i) {
                        case 0 -> directors.add(l.text());
                        case 1 -> writers.add(l.text());
                        case 2 -> stars.add(l.text());
                    }
                }
                i++;
            }
        }
    }
    private void findPoster() {
        Elements imgElements = movieDoc.select(IMG_ELEMENTS_QUERY);

        if (!imgElements.isEmpty()) {
            endLinkedImgUrl =
                    imgElements.select(END_LINKED_IMG_QUERY).attr(
                            "href");
        }

        if (endLinkedImgUrl != "") {
            linkedImgUrl = IMDB_LINK + endLinkedImgUrl;
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
        title = movieDoc.select(TITLE_QUERY).text();
        description = movieDoc.select(SUMMARY_QUERY).text();
        rating = movieDoc.select(RATING_QUERY).first().text();
        IMDBRank = movieDoc.select(IMDBRANK_QUERY).first().text();
        if (!generalInfo.isEmpty()) {
            for (Element info : generalInfo) {
                String[] finalInfo = info.text().split("\\s+");
                year = finalInfo[0];
                motionPictureRating = finalInfo[1];
                duration =
                        finalInfo[2] + " " + finalInfo[3];
            }
        }
    }
}