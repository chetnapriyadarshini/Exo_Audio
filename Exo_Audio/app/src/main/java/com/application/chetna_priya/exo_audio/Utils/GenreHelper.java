package com.application.chetna_priya.exo_audio.utils;

import android.net.Uri;

import com.application.chetna_priya.exo_audio.entity.Genre;
import com.application.chetna_priya.exo_audio.R;

import java.util.ArrayList;


public  class GenreHelper {

    public static final int TOTAL_ITEM_COUNT = 16;
    private static final String ARTS = "Arts";
    private static final String COMEDY = "Comedy";
    private static final String EDUCATION = "Education";
    private static final String KIDS_FAMILY= "Kids & Family";
    private static final String HEALTH = "Health";
    private static final String TV_FILM = "TV & Film";
    private static final String MUSIC = "Music";
    private static final String NEWS_POLITICS = "News & Politics";
    private static final String RELIGION_SPIRITUALITY = "Religion & Spirituality";
    private static final String SCIENCE_MEDICINE = "Science & Medicine";
    private static final String SPORTS_RECREATION = "Sports & Recreation";
    private static final String TECHNOLOGY = "Technology";
    private static final String BUSINESS = "Business";
    private static final String GAMES_HOBBIES = "Games & Hobbies";
    private static final String SOCIETY_CULTURE = "Society & Culture";
    private static final String GOVERNMENT_ORIGANIZATION = "Government & Organization";
    public static final String TOP_PODCASTS = "Top Podcasts";

    final int resId = R.drawable.ic_launcher;

    /*
    Some genre have sub genres, we want to accomodate all the sub genres in one
    main genre
     */
    public static String getMainGenreName(String genre) {

        switch (genre){
            case ARTS:
            case "Food":
            case "Literature":
            case "Design":
            case "Performing Arts":
            case "Visual Arts":
            case "Fashion & Beauty":
                return ARTS;

            case COMEDY:
                return COMEDY;

            case EDUCATION:
            case "K–12":
            case "Higher Education":
            case "Educational Technology":
            case "Language Courses":
            case "Training":
                return  EDUCATION;

            case KIDS_FAMILY:
                return KIDS_FAMILY;

            case HEALTH:
            case "Fitness & Nutrition":
            case "Self-Help":
            case "Sexuality":
            case "Alternative Health":
                return HEALTH;


            case TV_FILM:
                return  TV_FILM;

            case MUSIC:
                return MUSIC;

            case NEWS_POLITICS:
                return NEWS_POLITICS;

            case RELIGION_SPIRITUALITY:
            case "Buddhism":
            case "Christianity":
            case "Islam":
            case "Judaism":
            case "Spirituality":
            case "Hinduism":
            case "Other":
                return RELIGION_SPIRITUALITY;

            case SCIENCE_MEDICINE:
            case "Natural Sciences":
            case "Medicine":
            case "Social Sciences":
                return SCIENCE_MEDICINE;

            case SPORTS_RECREATION:
            case "Outdoor":
            case "Professional":
            case "College & High School":
            case "Amateur":
                return SPORTS_RECREATION;

            case TECHNOLOGY:
            case "Gadgets":
            case "Tech News":
            case "Podcasting":
            case "Software How-To":
                return TECHNOLOGY;

            case BUSINESS:
            case "Careers":
            case "Investing":
            case "Management & Marketing":
            case "Business News":
            case "Shopping":
                return BUSINESS;

            case GAMES_HOBBIES:
            case "Video Games":
            case "Automotive":
            case "Aviation":
            case "Hobbies":
            case "Other Games":
                return GAMES_HOBBIES;

            case SOCIETY_CULTURE:
            case "Personal Journals":
            case "Places & Travel":
            case "Philosophy":
            case "History":
                return SOCIETY_CULTURE;

            case GOVERNMENT_ORIGANIZATION:
            case "National":
            case "Regional":
            case "Local":
            case "Non-Profit":
                return GOVERNMENT_ORIGANIZATION;


            default:
                return "Default";
        }
    }

    private static int getGenreId(String category) {
        switch (category){
            case ARTS:
                return 1301;
            case COMEDY:
                return 1303;
            case EDUCATION:
                return 1304;
            case KIDS_FAMILY:
                return 1305;
            case HEALTH:
                return 1307;
            case TV_FILM:
                return 1309;
            case MUSIC:
                return 1310;
            case NEWS_POLITICS:
                return 1311;
            case RELIGION_SPIRITUALITY:
                return 1314;
            case SCIENCE_MEDICINE:
                return 1315;
            case SPORTS_RECREATION:
                return 1316;
            case TECHNOLOGY:
                return 1318;
            case BUSINESS:
                return 1321;
            case GAMES_HOBBIES:
                return 1323;
            case SOCIETY_CULTURE:
                return 1324;
            case GOVERNMENT_ORIGANIZATION:
                return 1325;
        }
        return -1;
    }

    public ArrayList<Genre> getGenreList(){
        ArrayList<Genre> genreArrayList = new ArrayList<>();

        for(int i = 0; i< TOTAL_ITEM_COUNT; i++){
            Genre genre = new Genre(getGenre(i), getGenreImage(i));
            genreArrayList.add(genre);
        }

        return  genreArrayList;
    }

    private int getGenreImage(int position) {
        return resId;
    }

    private String getGenre(int position){
        switch (position){
            case 0:
                return ARTS;
            case 1:
                return COMEDY;
            case 2:
                return EDUCATION;
            case 3:
                return KIDS_FAMILY;
            case 4:
                return HEALTH;
            case 5:
                return TV_FILM;
            case 6:
                return MUSIC;
            case 7:
                return NEWS_POLITICS;
            case 8:
                return RELIGION_SPIRITUALITY;
            case 9:
                return SCIENCE_MEDICINE;
            case 10:
                return SPORTS_RECREATION;
            case 11:
                return TECHNOLOGY;
            case 12:
                return BUSINESS;
            case 13:
                return GAMES_HOBBIES;
            case 14:
                return SOCIETY_CULTURE;
            case 15:
                return GOVERNMENT_ORIGANIZATION;
        }
        return null;
    }


    public static String getGenreUrl(String category, int limit) {
        /*
        	Available Media : movie, podcast, music, musicVideo, audiobook, shortFilm, tvShow, software, ebook, all
         */
        String LIMIT_PARAM = "limit";
        Uri.Builder uriBuilder;

        if(!(category.equals(TOP_PODCASTS))) {
            final String ITUNES_BASE_URL = "https://itunes.apple.com/search?";
            final String TERM_PARAM = "term";
            final String ENTITY_PARAM = "entity";
            //final String ATTRIBUTE_PARAM = "attribute";
            //final String MEDIA_PARAM = "media";
            final String podcastmediaVal = "podcast";
          //  final String attributeVal = "primaryGenreName";
            final String GENRE_PARAM = "genreId";
          //  final String musicmediaVal = "music";
          //  final String audiobookmediaVal = "audiobook";

            uriBuilder = Uri.parse(ITUNES_BASE_URL).buildUpon();


            //  uriBuilder.appendQueryParameter(MEDIA_PARAM, musicmediaVal);
            // uriBuilder.appendQueryParameter(MEDIA_PARAM, audiobookmediaVal);

            uriBuilder.appendQueryParameter(TERM_PARAM, podcastmediaVal);
            uriBuilder.appendQueryParameter("genreId", String.valueOf(getGenreId(category)));
          //  uriBuilder.appendQueryParameter(MEDIA_PARAM, podcastmediaVal);
            //uriBuilder.appendQueryParameter(ENTITY_PARAM, podcastmediaVal);
            //uriBuilder.appendQueryParameter(ATTRIBUTE_PARAM, attributeVal);

            /*
            This applies for cases when we want all podcasts in a category
             */
            if(limit != -1)
                uriBuilder.appendQueryParameter(LIMIT_PARAM, String.valueOf(limit));


            Uri builtUri = uriBuilder.build();
            return builtUri.toString();
        }
        else
        {
            String BASE_URI  =  "https://itunes.apple.com/us/rss/toppodcasts";
            uriBuilder = Uri.parse(BASE_URI).buildUpon();
            uriBuilder.appendQueryParameter(LIMIT_PARAM, String.valueOf(limit));
            uriBuilder.appendPath("xml");
            return uriBuilder.build().toString();
        }
    }

}
