package com.example.socialmedia;

import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class VideosFragment extends RecyclerView.ViewHolder {



    public VideosFragment(@NonNull View itemView) {
        super(itemView);
    }

    public void SetVideo(FragmentActivity activity, String name, String url, String postUri, String time, String uid, String type, String desc){



        SimpleExoPlayer exoPlayer;
        PlayerView playerView = itemView.findViewById(R.id.vv_post_ind);



            try{

//                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
//                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//                exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
//                Uri video = Uri.parse(postUri);
//                DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
//                ExtractorsFactory ef = new DefaultExtractorsFactory();
//                MediaSource mediaSource = new ExtractorMediaSource(video,df,ef,null,null);
//                playerView.setPlayer(exoPlayer);
//                exoPlayer.prepare(mediaSource);
//                exoPlayer.setPlayWhenReady(false);

                SimpleExoPlayer simpleExoPlayer= new SimpleExoPlayer.Builder(activity).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postUri);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);

            }catch(Exception e){
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }

        }

}
