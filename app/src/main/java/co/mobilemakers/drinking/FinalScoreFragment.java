package co.mobilemakers.drinking;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinalScoreFragment extends Fragment {

    public static final String WINNER = "winner";
    public static final String WINNER_TEAM = "winner team";
    public static final String WINNER_TEAM_BLUE = "Blue Team wins";
    public static final String WINNER_TEAM_RED = "Red Team wins";


    Bundle mBundle;
    String mGameMode;

    ImageView mImageViewWinner;
    TextView mTextViewTeamWinner;
    Button mButtonRestart;
    Button mButtonNewGame;
    TextView mTextViewQuotes;

    public FinalScoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_final_score, container, false);
        mBundle = this.getArguments();
        mGameMode = mBundle.getString(SelectModeFragment.GAME_MODE);
        wiringUpWinnerView(rootView);
        prepareButtonRestart(rootView);
        displayingWinnerView();
//        prepareTextViewQuotesAndGetQuote(rootView);
        return rootView;
    }
//
//    private void prepareTextViewQuotesAndGetQuote(View rootView) {
//        mTextViewQuotes = (TextView) rootView.findViewById(R.id.text_view_quotes);
//        try {
//            URL url = constructURLQuery();
//            Request request = new Request.Builder().url(url.toString()).build();
//            OkHttpClient client = new OkHttpClient();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    final String responseString = response.body().string();
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mTextViewQuotes.setText(responseString);
//                        }
//                    });
//                }
//            });
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private URL constructURLQuery() throws MalformedURLException {
//        final String IHEART_QUOTES_BASE_URL = "iheartquotes.com";
//        final String API = "api";
//        final String VERSION_PATH = "v1";
//        final String QUOTES_ENDPOINT = "random";
//
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("http").authority(IHEART_QUOTES_BASE_URL).
//                appendPath(API).
//                appendPath(VERSION_PATH).
//                appendPath(QUOTES_ENDPOINT);
//        Uri uri = builder.build();
//
//        return new URL(uri.toString());
//    }

    private void wiringUpWinnerView(View rootView) {
        mImageViewWinner = (ImageView) rootView.findViewById(R.id.image_view_winner);
        mTextViewTeamWinner = (TextView) rootView.findViewById(R.id.text_view_winner);
    }

    private void displayingWinnerView() {
        if (mGameMode.equals(SelectModeFragment.GAME_MODE_SOLO)) {
            Player winner = mBundle.getParcelable(WINNER);
            mImageViewWinner.setImageBitmap(getBitmap(winner));
            mTextViewTeamWinner.setText(String.format(getString(R.string.winner_solo_text), winner.getName()));
        } else {
            String teamWinner = mBundle.getString(FinalScoreFragment.WINNER_TEAM);
            mTextViewTeamWinner.setText(teamWinner);
            if (teamWinner.equals(WINNER_TEAM_BLUE)) {
                mImageViewWinner.setImageDrawable(getResources().getDrawable(R.drawable.keep_calm_blue));
                mTextViewTeamWinner.setTextColor(Color.BLUE);
            } else {
                mImageViewWinner.setImageDrawable(getResources().getDrawable(R.drawable.keep_calm_red));
                mTextViewTeamWinner.setTextColor(Color.RED);
            }
        }
    }

    private void prepareButtonRestart(View rootView) {
        mButtonRestart = (Button) rootView.findViewById(R.id.button_restart);
        mButtonNewGame = (Button) rootView.findViewById(R.id.button_new_game);
        View.OnClickListener onClickListenerButtonsRestart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                switch (v.getId()) {
                    case R.id.button_new_game:
                        fragmentManager.beginTransaction().replace(R.id.container, new StartFragment()).commit();
                        break;
                    case R.id.button_restart:
                        Bundle bundle = new Bundle();
                        bundle.putString(SelectModeFragment.GAME_MODE, mGameMode);
                        if (mGameMode.equals(SelectModeFragment.GAME_MODE_SOLO)) {
                            ArrayList<Player> teamUnique = mBundle.getParcelableArrayList(PlayerListFragment.UNIQUE_TEAM);
                            for (Player p:teamUnique) {
                                p.setScore(0);
                            }
                            bundle.putParcelableArrayList(PlayerListFragment.UNIQUE_TEAM, teamUnique);
                        } else {
                            ArrayList<Player> teamRed = mBundle.getParcelableArrayList(PlayerListFragment.TEAM_RED);
                            for (Player p:teamRed) {
                                p.setScore(0);
                            }
                            bundle.putParcelableArrayList(PlayerListFragment.TEAM_RED, teamRed);
                            ArrayList<Player> teamBlue = mBundle.getParcelableArrayList(PlayerListFragment.TEAM_BLUE);
                            for (Player p:teamBlue) {
                                p.setScore(0);
                            }
                            bundle.putParcelableArrayList(PlayerListFragment.TEAM_BLUE, teamBlue);
                        }
                        ChallengeFragment challengeFragment = new ChallengeFragment();
                        challengeFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.container, challengeFragment).commit();
                        break;
                }

            }
        };
        mButtonRestart.setOnClickListener(onClickListenerButtonsRestart);
        mButtonNewGame.setOnClickListener(onClickListenerButtonsRestart);
    }

    private Bitmap getBitmap(Player player) {
        Bitmap bmp;
        byte[] image;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        image = player.getImage();
        bmp = BitmapFactory.decodeByteArray(image, 0, image.length, options);
        return bmp;
    }


}
