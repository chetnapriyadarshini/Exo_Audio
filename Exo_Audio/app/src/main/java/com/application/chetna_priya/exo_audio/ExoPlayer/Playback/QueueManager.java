package com.application.chetna_priya.exo_audio.ExoPlayer.Playback;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.application.chetna_priya.exo_audio.AlbumArtCache;
import com.application.chetna_priya.exo_audio.ExoPlayer.Utils.MediaIDHelper;
import com.application.chetna_priya.exo_audio.ExoPlayer.Utils.QueueHelper;
import com.application.chetna_priya.exo_audio.R;
import com.application.chetna_priya.exo_audio.data.PodcastProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple data provider for queues. Keeps track of a current queue and a current index in the
 * queue. Also provides methods to set the current queue based on common queries, relying on a
 * given PodcastProvider to provide the actual media metadata.
 */
public class QueueManager {
    private static final String TAG = QueueManager.class.getSimpleName();

    private PodcastProvider mPodcastProvider;
    private MetadataUpdateListener mListener;
    private Resources mResources;

    // "Now playing" queue:
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;

    public QueueManager(@NonNull PodcastProvider PodcastProvider,
                        @NonNull Resources resources,
                        @NonNull MetadataUpdateListener listener) {
        this.mPodcastProvider = PodcastProvider;
        this.mListener = listener;
        this.mResources = resources;

        mPlayingQueue = Collections.synchronizedList(new ArrayList<MediaSessionCompat.QueueItem>());
        mCurrentIndex = 0;
    }

    public boolean isSameBrowsingCategory(@NonNull String mediaId) {
        String[] newBrowseHierarchy = MediaIDHelper.getHierarchy(mediaId);
        MediaSessionCompat.QueueItem current = getCurrentPodcast();
        if (current == null) {
            return false;
        }
        String[] currentBrowseHierarchy = MediaIDHelper.getHierarchy(
                current.getDescription().getMediaId());

        return Arrays.equals(newBrowseHierarchy, currentBrowseHierarchy);
    }

    private void setCurrentQueueIndex(int index) {
        if (index >= 0 && index < mPlayingQueue.size()) {
            mCurrentIndex = index;
            mListener.onCurrentQueueIndexUpdated(mCurrentIndex);
        }
    }

    public boolean setCurrentQueueItem(long queueId) {
        // set the current index on queue from the queue Id:
        int index = QueueHelper.getPodcastIndexOnQueue(mPlayingQueue, queueId);
        setCurrentQueueIndex(index);
        return index >= 0;
    }

    public boolean setCurrentQueueItem(String mediaId) {
        // set the current index on queue from the podcast Id:
        int index = QueueHelper.getPodcastIndexOnQueue(mPlayingQueue, mediaId);
        setCurrentQueueIndex(index);
        return index >= 0;
    }

    public boolean skipQueuePosition(int amount) {
        int index = mCurrentIndex + amount;
        if (index < 0) {
            // skip backwards before the first song will keep you on the first song
            index = 0;
        } else {
            // skip forwards when in last song will cycle back to start of the queue
            index %= mPlayingQueue.size();
        }
        if (!QueueHelper.isIndexPlayable(index, mPlayingQueue)) {
            Log.e(TAG, "Cannot increment queue index by "+ amount+
                    ". Current="+ mCurrentIndex+ " queue length="+ mPlayingQueue.size());
            return false;
        }
        mCurrentIndex = index;
        return true;
    }

    public boolean setQueueFromSearch(String query, Bundle extras) {
        List<MediaSessionCompat.QueueItem> queue =
                QueueHelper.getPlayingQueueFromSearch(query, extras, mPodcastProvider);
        setCurrentQueue(mResources.getString(R.string.search_queue_title), queue);
        return queue != null && !queue.isEmpty();
    }


    public void setQueueFromPodcast(String mediaId) {
        Log.d(TAG, "setQueueFromPodcast"+ mediaId);

        // The mediaId used here is not the unique PodcastId. This one comes from the
        // MediaBrowser, and is actually a "hierarchy-aware mediaID": a concatenation of
        // the hierarchy in MediaBrowser and the actual unique PodcastID. This is necessary
        // so we can build the correct playing queue, based on where the track was
        // selected from.
        boolean canReuseQueue = false;
        if (isSameBrowsingCategory(mediaId)) {
            canReuseQueue = setCurrentQueueItem(mediaId);
        }
        if (!canReuseQueue) {
            String queueTitle = mResources.getString(R.string.browse_podcast_by_genre_subtitle,
                    MediaIDHelper.extractBrowseCategoryValueFromMediaID(mediaId));
            setCurrentQueue(queueTitle,
                    QueueHelper.getPlayingQueue(mediaId, mPodcastProvider), mediaId);
        }
        updateMetadata();
    }

    public MediaSessionCompat.QueueItem getCurrentPodcast() {
        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
            return null;
        }
        return mPlayingQueue.get(mCurrentIndex);
    }

    public int getCurrentQueueSize() {
        if (mPlayingQueue == null) {
            return 0;
        }
        return mPlayingQueue.size();
    }

    protected void setCurrentQueue(String title, List<MediaSessionCompat.QueueItem> newQueue) {
        setCurrentQueue(title, newQueue, null);
    }

    protected void setCurrentQueue(String title, List<MediaSessionCompat.QueueItem> newQueue,
                                   String initialMediaId) {
        mPlayingQueue = newQueue;
        int index = 0;
        if (initialMediaId != null) {
            index = QueueHelper.getPodcastIndexOnQueue(mPlayingQueue, initialMediaId);
        }
        mCurrentIndex = Math.max(index, 0);
        mListener.onQueueUpdated(title, newQueue);
    }

    public void updateMetadata() {
        MediaSessionCompat.QueueItem currentPodcast = getCurrentPodcast();
        if (currentPodcast == null) {
            mListener.onMetadataRetrieveError();
            return;
        }
        final String PodcastId = MediaIDHelper.extractpodcastIDFromMediaID(
                currentPodcast.getDescription().getMediaId());
        MediaMetadataCompat metadata = mPodcastProvider.getPodcast(PodcastId);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid PodcastId " + PodcastId);
        }

        mListener.onMetadataChanged(metadata);

        // Set the proper album artwork on the media session, so it can be shown in the
        // locked screen and in other places.
        if (metadata.getDescription().getIconBitmap() == null &&
                metadata.getDescription().getIconUri() != null) {
            String albumUri = metadata.getDescription().getIconUri().toString();
            AlbumArtCache.getInstance().fetch(albumUri, new AlbumArtCache.FetchListener() {
                @Override
                public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                    mPodcastProvider.updatePodcastArt(PodcastId, bitmap, icon);

                    // If we are still playing the same Podcast, notify the listeners:
                    MediaSessionCompat.QueueItem currentPodcast = getCurrentPodcast();
                    if (currentPodcast == null) {
                        return;
                    }
                    String currentPlayingId = MediaIDHelper.extractpodcastIDFromMediaID(
                            currentPodcast.getDescription().getMediaId());
                    if (PodcastId.equals(currentPlayingId)) {
                        mListener.onMetadataChanged(mPodcastProvider.getPodcast(currentPlayingId));
                    }
                }
            });
        }
    }

    public interface MetadataUpdateListener {
        void onMetadataChanged(MediaMetadataCompat metadata);
        void onMetadataRetrieveError();
        void onCurrentQueueIndexUpdated(int queueIndex);
        void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> newQueue);
    }
}
