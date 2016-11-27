package com.application.chetna_priya.exo_audio.Utils;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.Model.PodcastProvider;

import java.util.ArrayList;
import java.util.List;

import static com.application.chetna_priya.exo_audio.Utils.MediaIDHelper.MEDIA_ID_PODCASTS_BY_GENRE;
import static com.application.chetna_priya.exo_audio.Utils.MediaIDHelper.MEDIA_ID_PODCASTS_BY_SEARCH;

public class QueueHelper
{

    private static final String TAG = QueueHelper.class.getSimpleName();

    private static final int RANDOM_QUEUE_SIZE = 10;

    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(String mediaId,
                                                                     PodcastProvider podcastProvider) {

        // extract the browsing hierarchy from the media ID:
        String[] hierarchy = MediaIDHelper.getHierarchy(mediaId);

        if (hierarchy.length != 2) {
            Log.e(TAG, "Could not build a playing queue for this mediaId: "+ mediaId);
            return null;
        }

        String categoryType = hierarchy[0];
        String categoryValue = hierarchy[1];
        Log.d(TAG, "Creating playing queue for "+ categoryType + categoryValue);

        Iterable<MediaMetadataCompat> tracks = null;
        // This sample only supports genre and by_search category types.
        if (categoryType.equals(MEDIA_ID_PODCASTS_BY_GENRE)) {
            tracks = podcastProvider.getPodcastByGenre(categoryValue);
        } else if (categoryType.equals(MEDIA_ID_PODCASTS_BY_SEARCH)) {
            tracks = podcastProvider.searchPodcastByEpisodeTitle(categoryValue);
        }

        if (tracks == null) {
            Log.e(TAG, "Unrecognized category type: "+ categoryType+ " for media "+ mediaId);
            return null;
        }

        return convertToQueue(tracks, hierarchy[0], hierarchy[1]);
    }

    public static int getPodcastIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           String mediaId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static int getPodcastIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           long queueId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (queueId == item.getQueueId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(
            Iterable<MediaMetadataCompat> tracks, String... categories) {
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        int count = 0;
        for (MediaMetadataCompat track : tracks) {

            // We create a hierarchy-aware mediaID, so we know what the queue is about by looking
            // at the QueueItem media IDs.
            String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                    track.getDescription().getMediaId(), categories);

            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                    .build();

            // We don't expect queues to change after created, so we use the item index as the
            // queueId. Any other number unique in the queue would work.
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(
                    trackCopy.getDescription(), count++);
            queue.add(item);
        }
        return queue;

    }

    /**
     * Create a random queue with at most {@link #RANDOM_QUEUE_SIZE} elements.
     *
     * @param podcastProvider the provider used for fetching music.
     * @return list containing {@link MediaSessionCompat.QueueItem}'s
     */
    public static List<MediaSessionCompat.QueueItem> getRandomQueue(PodcastProvider podcastProvider) {
        List<MediaMetadataCompat> result = new ArrayList<>(RANDOM_QUEUE_SIZE);
        Iterable<MediaMetadataCompat> shuffled = podcastProvider.getShuffledPodcast();
        for (MediaMetadataCompat metadata: shuffled) {
            if (result.size() == RANDOM_QUEUE_SIZE) {
                break;
            }
            result.add(metadata);
        }
        Log.d(TAG, "getRandomQueue: result.size="+ result.size());

        return convertToQueue(result, MEDIA_ID_PODCASTS_BY_SEARCH, "random");
    }

    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }

}