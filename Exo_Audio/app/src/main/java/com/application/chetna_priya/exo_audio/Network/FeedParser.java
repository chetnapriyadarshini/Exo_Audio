package com.application.chetna_priya.exo_audio.Network;

import android.support.annotation.NonNull;
import android.util.Xml;

import com.application.chetna_priya.exo_audio.Entity.Episode;
import com.application.chetna_priya.exo_audio.Entity.Podcast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;


public class FeedParser {

    private static final int TAG_ID = 1;
    private static final int TAG_TITLE = 2;
    private static final int TAG_SUMMARY = 3;
    private static final int TAG_IMAGE_LINK = 4;
    private static final int TAG_POD_AUDIO_LINK = 5;
    private static final int TAG_POD_DURATION = 6;
    private static final int TAG_PUBLISHED = 7;

    // We don't use XML namespaces
    private final String ns = null;
    private final String PODCAST_EPISODE_START_TAG = "item";
    private final String BEGIN_PARSE_CHANNEL_TAG = "channel";
    private final String BEGIN_PARSE_TAG = "rss";
    //private final String PARENT_ATTRIBUTE_IMAGE_TAG = "image";
    private final String ATTRIBUTE_TAG_TITLE = "title";
    private final String MAIN_ATTRIBUTE_URL_TAG = "itunes:image";
    private final String SUB_ATTRIBUTE_HREF_URL_TAG = "href";
    private final String ATTRIBUTE_TAG_SUMMARY = "itunes:summary";

    private final String ATTRIBUTE_TAG_EPISODE_PUBLISHED_DATE = "pubDate";
    private final String ATTRIBUTE_TAG_EPISODE_SUMMARY = "itunes:summary";
    private final String ATTRIBUTE_TAG_EPISODE_DURATION = "itunes:duration";
  //  private final String ATTRIBUTE_TAG_EPISODE_IMAGE_URL = "media:thumbnail";
    private final String MAIN_ATTRIBUTE_TAG_POD_AUDIO_URL = "enclosure";

    private static final String TAG = FeedParser.class.getSimpleName();
    private String podcast_title;
    private String podcast_summary;
    private String podcast_image_link;

    /** Parse an Atom feed, returning a collection of Episode objects.
     *
     * @param in Atom feed, as a stream.
     * @throws IOException on I/O error.
     */
    public ArrayList<Episode> parseEpisodes(@NonNull InputStream in, Podcast podcast) throws IOException {
        ArrayList<Episode> episodes = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            episodes = readEpisodeFeed(parser, podcast);
        } catch (ParseException | XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return episodes;
    }

    public Podcast parsePodcast(@NonNull InputStream in, long id, @NonNull String feedUrl)
             {
        Podcast podcast = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            podcast = readPodcastFeed(parser, id, feedUrl);

        }catch (XmlPullParserException | ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return podcast;
    }

    /**
     * Decode a feed attached to an XmlPullParser.
     *
     * @param parser Incoming XMl
     * @throws XmlPullParserException on error parsing feed.
     * @throws IOException on I/O error.
     */
    private Podcast readPodcastFeed(XmlPullParser parser, long id, String feedUrl)
            throws XmlPullParserException, IOException, ParseException {

        parser.require(XmlPullParser.START_TAG, ns, BEGIN_PARSE_TAG);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, BEGIN_PARSE_CHANNEL_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if(parser.getName().equals(ATTRIBUTE_TAG_TITLE)){
                podcast_title = readTag(parser, TAG_TITLE);
            }else if(parser.getName().equals(ATTRIBUTE_TAG_SUMMARY)){
                podcast_summary = readTag(parser, TAG_SUMMARY);
            }else if(parser.getName().equals(MAIN_ATTRIBUTE_URL_TAG)){
                podcast_image_link = readTag(parser, TAG_IMAGE_LINK);
            }else if(parser.getName().equals(PODCAST_EPISODE_START_TAG))
            {
                /*
                We return from here because the main podcast feed is complete and the
                episode feed has started, all the info to instantiate a podcast object should
                be available by now
                 */
                return new Podcast(id, podcast_title, podcast_image_link,podcast_summary,feedUrl);
            }else
                skip(parser);
        }
       return null;
    }

    private ArrayList<Episode> readEpisodeFeed(XmlPullParser parser, Podcast podcast) throws IOException, XmlPullParserException, ParseException {
        ArrayList<Episode> episodes = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, BEGIN_PARSE_TAG);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, BEGIN_PARSE_CHANNEL_TAG);

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            if (parser.getName().equals(PODCAST_EPISODE_START_TAG)) {

                Episode episode = readEpisodes(parser, podcast);
                if(episode != null) {
                    episodes.add(episode);
                }
            }else
                skip(parser);
        }
        return episodes;
    }



    private Episode readEpisodes(XmlPullParser parser, Podcast podcast)
            throws XmlPullParserException, IOException, ParseException {

        String episode_title = null;
        String episode_duration = null;
        String episode_summary = null;
        String episode_pod_url = null;
        String publishedOn = null;
        parser.require(XmlPullParser.START_TAG, ns, PODCAST_EPISODE_START_TAG);
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            switch (parser.getName()) {
                case ATTRIBUTE_TAG_EPISODE_SUMMARY:
                    episode_summary = readTag(parser, TAG_SUMMARY);
                    break;
                case ATTRIBUTE_TAG_EPISODE_DURATION:
                    episode_duration = readTag(parser, TAG_POD_DURATION);
                    break;
                case ATTRIBUTE_TAG_TITLE:
                    episode_title = readTag(parser, TAG_TITLE);
                    break;
                case MAIN_ATTRIBUTE_TAG_POD_AUDIO_URL:
                    episode_pod_url = readTag(parser, TAG_POD_AUDIO_LINK);
                    break;
                case ATTRIBUTE_TAG_EPISODE_PUBLISHED_DATE:
                   // Time t = new Time();
                    String date =  readTag(parser, TAG_PUBLISHED);

                    //DateFormat dateFormat = new DateFormat();

                  //  t.parse(readTag(parser, TAG_PUBLISHED));
                    publishedOn = getDateString(date);
                    break;
                default:
                    skip(parser);
            }

        }
        Episode episode = new Episode(podcast, episode_title, episode_duration,episode_summary,episode_pod_url,publishedOn);
        return episode;
    }

    private String getDateString(String date) {
        String[] dateStringArr = date.split(" ");//Eg Date: Tue, 04 Dec 2007 24:00:00 EST
        String newDate="";
        for(int i=1; i <= 3; i++){
            newDate = newDate.concat(dateStringArr[i])+" ";
        }
       // Log.d(TAG, newDate);
        return newDate;
    }

    private String readTag(XmlPullParser parser, int tagType) throws IOException, XmlPullParserException {
        String tag = null;
        String endTag = null;
        switch (tagType){
            case TAG_SUMMARY:
                return readBasicTag(parser,ATTRIBUTE_TAG_SUMMARY);
            case TAG_POD_DURATION:
                return readBasicTag(parser, ATTRIBUTE_TAG_EPISODE_DURATION);
            case TAG_TITLE:
                return readBasicTag(parser, ATTRIBUTE_TAG_TITLE);
            case TAG_IMAGE_LINK:
                return readAlternateLink(parser);
            case TAG_POD_AUDIO_LINK:
                return readAlternateLink(parser);
            case TAG_PUBLISHED:
                return readBasicTag(parser, ATTRIBUTE_TAG_EPISODE_PUBLISHED_DATE);
            default:
                throw new IllegalArgumentException();
        }

    }

    /**
     * Processes link tags in the feed.
     */
    private String readAlternateLink(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        final String SUB_ATTRIBUTE_TAG_POD_AUDIO_URL = "url";
        String link = null;
      //  parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        switch (tag){
            case MAIN_ATTRIBUTE_URL_TAG:
                parser.require(XmlPullParser.START_TAG, ns, MAIN_ATTRIBUTE_URL_TAG);
                link = parser.getAttributeValue(ns,SUB_ATTRIBUTE_HREF_URL_TAG);
                break;
            case MAIN_ATTRIBUTE_TAG_POD_AUDIO_URL:
                parser.require(XmlPullParser.START_TAG, ns, MAIN_ATTRIBUTE_TAG_POD_AUDIO_URL);
                link = parser.getAttributeValue(ns, SUB_ATTRIBUTE_TAG_POD_AUDIO_URL);
                break;
        }/*
        String relType = parser.getAttributeValue(null, "rel");
        if (relType.equals("alternate")) {
            link = parser.getAttributeValue(null, "href");
        }*/
        while (true) {
            if (parser.nextTag() == XmlPullParser.END_TAG) break;
            // Intentionally break; consumes any remaining sub-tags.
        }
        return link;
    }

    private String readBasicTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return result;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
     * if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
     * finds the matching END_TAG (as indicated by the value of "depth" being 0).
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
