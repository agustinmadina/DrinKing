package co.mobilemakers.drinking;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChallengeFragment extends Fragment {

    DatabaseHelper mDBHelper;
    List<Challenge> mChallenges;
    ArrayList<Player> mTeamRed;
    ArrayList<Player> mTeamBlue;
    ArrayList<Player> mTeamUnique;
    Random mRandom = new Random();
    int mRedPlayer;
    int mBluePlayer;
    String mGameMode;
    String mPenaltyText;
    int mPenaltyDrinks;

    TextView mTextViewChallenge;
    TextView mTextViewChallengeTitle;
    TextView mTextViewNamePlayer1;
    TextView mTextViewNamePlayer2;
    TextView mTextViewBlueScore;
    TextView mTextViewRedScore;
    ImageButton mButtonWinPlayer1;
    ImageButton mButtonWinPlayer2;
    Bundle mBundle;
    TextView mTextViewPenalty;
    LinearLayout mScoreLayout;



    public ChallengeFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_challenge, container, false);
        prepareWinButtonsAndNextChallenge(rootView);
        mChallenges = retrieveChallenges();
        mTextViewPenalty = (TextView) rootView.findViewById(R.id.text_view_penalty);
        wireUpChallengeText(rootView);
        prepareChallengeText();
        mBundle = this.getArguments();
        mGameMode = mBundle.getString(SelectModeFragment.GAME_MODE);
        wireUpPlayersView(rootView);
        setPenaltyAmountOfDrinks();
        if (mGameMode.equals(SelectModeFragment.GAME_MODE_SOLO)) {
            retrieveUniqueTeam();
            checkIfThereIsAWinner();
            preparePlayer1Solo();
            preparePlayer2Solo();
            preparePenaltyTextSoloMode();
            mScoreLayout.setVisibility(View.GONE);

        } else {
            retrieveTeams();
            checkIfTeamRedIsTheWinner();
            checkIfTeamBlueIsTheWinner();
            preparePenaltyTextTeamMode();
            prepareRedTeamPlayerView();
            prepareBlueTeamPlayerView();
        }
        setTextPenalty();
        return rootView;
    }

    private void setTextPenalty() {
        mTextViewPenalty.setText(mPenaltyText);
    }

    private void preparePenaltyTextTeamMode() {
        int penaltySelection = mRandom.nextInt(4);
        switch (penaltySelection) {
            case 0:
                Player playerBlue = mTeamBlue.get(mRandom.nextInt(mTeamBlue.size()));
                mPenaltyText = String.format(getString(R.string.has_to_drink),playerBlue.getName()) + mPenaltyDrinks;
                break;
            case 1:
                Player playerRed = mTeamBlue.get(mRandom.nextInt(mTeamBlue.size()));
                mPenaltyText = String.format(getString(R.string.has_to_drink),playerRed.getName()) + mPenaltyDrinks;
                break;
            case 2:
                mPenaltyText = String.format(getString(R.string.has_to_drink),"Blue Team") + mPenaltyDrinks;
                mTextViewPenalty.setTextColor(getResources().getColor(R.color.material_blue));
                break;
            case 3:
                mPenaltyText = String.format(getString(R.string.has_to_drink),"Red Team") + mPenaltyDrinks;
                mTextViewPenalty.setTextColor(getResources().getColor(R.color.accent));
                break;
        }
    }

    private void preparePenaltyTextSoloMode() {
        Player playerWithPenalty = mTeamUnique.get(mRandom.nextInt(mTeamUnique.size()));
        mPenaltyText = String.format(getString(R.string.has_to_drink), playerWithPenalty.getName()) + mPenaltyDrinks;
    }

    private void setPenaltyAmountOfDrinks() {
        mPenaltyDrinks = mRandom.nextInt(5) + 1;
    }

    private void checkIfTeamBlueIsTheWinner() {
        int sum = 0;
        for (Player p:mTeamBlue) {
            sum += p.getScore();
        }
        mTextViewBlueScore.setText(String.valueOf(sum));
        if (sum == 10) {
            FragmentManager fragmentManager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(SelectModeFragment.GAME_MODE, mGameMode);
            bundle.putString(FinalScoreFragment.WINNER_TEAM, FinalScoreFragment.WINNER_TEAM_BLUE);
            bundle.putParcelableArrayList(PlayerListFragment.TEAM_BLUE, mTeamBlue);
            bundle.putParcelableArrayList(PlayerListFragment.TEAM_RED, mTeamRed);
            FinalScoreFragment finalScoreFragment = new FinalScoreFragment();
            finalScoreFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.container, finalScoreFragment).commit();
        }
    }

    private void checkIfTeamRedIsTheWinner() {
        int sum = 0;
        for (Player p:mTeamRed) {
            sum += p.getScore();
        }
        mTextViewRedScore.setText(String.valueOf(sum));
        if (sum == 10) {
            FragmentManager fragmentManager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(SelectModeFragment.GAME_MODE, mGameMode);
            bundle.putString(FinalScoreFragment.WINNER_TEAM, FinalScoreFragment.WINNER_TEAM_RED);
            bundle.putParcelableArrayList(PlayerListFragment.TEAM_RED, mTeamRed);
            bundle.putParcelableArrayList(PlayerListFragment.TEAM_BLUE, mTeamBlue);
            FinalScoreFragment finalScoreFragment = new FinalScoreFragment();
            finalScoreFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.container, finalScoreFragment).commit();
        }
    }

    private void checkIfThereIsAWinner() {
        for (Player p:mTeamUnique) {
            if (p.getScore() == 5) {
                FragmentManager fragmentManager = getFragmentManager();
                FinalScoreFragment finalScoreFragment = new FinalScoreFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SelectModeFragment.GAME_MODE, mGameMode);
                bundle.putParcelable(FinalScoreFragment.WINNER, p);
                bundle.putParcelableArrayList(PlayerListFragment.UNIQUE_TEAM, mTeamUnique);
                finalScoreFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, finalScoreFragment).commit();
            }
        }
    }

    private void preparePlayer2Solo() {
        mBluePlayer = mRandom.nextInt(mTeamUnique.size());
        while (mBluePlayer == mRedPlayer) {
            mBluePlayer = mRandom.nextInt(mTeamUnique.size());
        }
        Player player = mTeamUnique.get(mBluePlayer);
        mTextViewNamePlayer2.setText(player.getName());
        mTextViewNamePlayer2.setTextColor(getResources().getColor(R.color.material_blue));
        Drawable drawable = new BitmapDrawable(getResources(), getBitmap(player));
        if (drawable.equals(getResources().getDrawable(R.mipmap.placeholder))) {
            drawable = DrawableCompat.wrap(getResources().getDrawable(R.mipmap.placeholder));
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.material_blue));
            drawable = DrawableCompat.unwrap(drawable);

        }
        if (mButtonWinPlayer2 != null) {
            mButtonWinPlayer2.setImageDrawable(drawable);
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private void preparePlayer1Solo() {
        mRedPlayer = mRandom.nextInt(mTeamUnique.size());
        Player player = mTeamUnique.get(mRedPlayer);
        mTextViewNamePlayer1.setText(player.getName());
        mTextViewNamePlayer2.setTextColor(getResources().getColor(R.color.accent));
        Drawable drawable = new BitmapDrawable(getResources(), getBitmap(player));
        if (drawable.equals(getResources().getDrawable(R.mipmap.placeholder))) {
            drawable = DrawableCompat.wrap(getResources().getDrawable(R.mipmap.placeholder));
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.accent));
            drawable = DrawableCompat.unwrap(drawable);

        }
        if (mButtonWinPlayer1 != null) {
            mButtonWinPlayer1.setImageDrawable(drawable);
        }

    }

    private void retrieveUniqueTeam() {
        mTeamUnique = mBundle.getParcelableArrayList(PlayerListFragment.UNIQUE_TEAM);
    }

    private void prepareWinButtonsAndNextChallenge(View rootView) {
        mButtonWinPlayer1 = (ImageButton) rootView.findViewById(R.id.button_win_player_1);
        mButtonWinPlayer2 = (ImageButton) rootView.findViewById(R.id.button_win_player_2);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_win_player_1:
                        if (mGameMode.equals(SelectModeFragment.GAME_MODE_SOLO)) {
                            mTeamUnique.get(mRedPlayer).setScore(mTeamUnique.get(mRedPlayer).getScore() + 1);
                        } else {
                            mTeamRed.get(mRedPlayer).setScore(mTeamRed.get(mRedPlayer).getScore() + 1);
                        }
                        break;
                    case R.id.button_win_player_2:
                        if (mGameMode.equals(SelectModeFragment.GAME_MODE_SOLO)) {
                            mTeamUnique.get(mBluePlayer).setScore(mTeamUnique.get(mBluePlayer).getScore() + 1);
                        } else {
                            mTeamBlue.get(mBluePlayer).setScore(mTeamBlue.get(mBluePlayer).getScore() + 1);
                        }
                        break;
                }
                FragmentManager fragmentManager = getFragmentManager();
                ChallengeFragment challengeFragment = new ChallengeFragment();
                mBundle.putParcelableArrayList(PlayerListFragment.TEAM_BLUE, mTeamBlue);
                mBundle.putParcelableArrayList(PlayerListFragment.TEAM_RED, mTeamRed);
                challengeFragment.setArguments(mBundle);
                fragmentManager.beginTransaction().replace(R.id.container, challengeFragment).commit();
            }
        };
        mButtonWinPlayer1.setOnClickListener(onClickListener);
        mButtonWinPlayer2.setOnClickListener(onClickListener);
    }

    private void prepareBlueTeamPlayerView() {
        mBluePlayer = mRandom.nextInt(mTeamBlue.size());
        Player player = mTeamBlue.get(mBluePlayer);
        Drawable drawable = new BitmapDrawable(getResources(), getBitmap(player));
        if (drawable.equals(getResources().getDrawable(R.mipmap.placeholder))) {
            drawable = DrawableCompat.wrap(getResources().getDrawable(R.mipmap.placeholder));
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.material_blue));
            drawable = DrawableCompat.unwrap(drawable);

        }
        if (mButtonWinPlayer2 != null) {
            mButtonWinPlayer2.setImageDrawable(drawable);
        }
        mTextViewNamePlayer2.setText(player.getName());
        mTextViewNamePlayer2.setTextColor(getResources().getColor(R.color.material_blue));

    }

    private void retrieveTeams() {
        mTeamRed = mBundle.getParcelableArrayList(PlayerListFragment.TEAM_RED);
        mTeamBlue = mBundle.getParcelableArrayList(PlayerListFragment.TEAM_BLUE);
    }

    private void prepareRedTeamPlayerView() {
        mRedPlayer = mRandom.nextInt(mTeamRed.size());
        Player player = mTeamRed.get(mRedPlayer);
        Drawable drawable = new BitmapDrawable(getResources(), getBitmap(player));
        if (drawable.equals(getResources().getDrawable(R.mipmap.placeholder))) {
            drawable = DrawableCompat.wrap(getResources().getDrawable(R.mipmap.placeholder));
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.accent));
            drawable = DrawableCompat.unwrap(drawable);
        }
        if (mButtonWinPlayer2 != null) {
            mButtonWinPlayer2.setImageDrawable(drawable);
        }
        mTextViewNamePlayer1.setText(player.getName());
        mTextViewNamePlayer1.setTextColor(getResources().getColor(R.color.accent));
    }

    private void wireUpPlayersView(View rootView) {
        mTextViewNamePlayer1 = (TextView) rootView.findViewById(R.id.text_view_player_1);
        mTextViewNamePlayer2 = (TextView) rootView.findViewById(R.id.text_view_player_2);
        mTextViewBlueScore = (TextView) rootView.findViewById(R.id.text_view_blue_score);
        mTextViewRedScore = (TextView) rootView.findViewById(R.id.text_view_red_score);

    }

    private void wireUpChallengeText(View rootView) {
        mTextViewChallenge = (TextView) rootView.findViewById(R.id.text_view_challenge);
        mTextViewChallengeTitle = (TextView) rootView.findViewById(R.id.text_view_challenge_title);
    }

    private void prepareChallengeText() {
        Challenge challenge = mChallenges.get(mRandom.nextInt(mChallenges.size()));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!(challenge.getTool().equals(challenge.NO_TOOLS))) {
            while (!(sharedPreferences.getBoolean(challenge.getTool(), true))) {
                challenge = mChallenges.get(mRandom.nextInt(mChallenges.size()));
            }
        }
    mTextViewChallenge.setText(challenge.getContent());
    mTextViewChallengeTitle.setText(challenge.getName());
    }

    public DatabaseHelper getDBHelper() {
        if (mDBHelper == null){
            mDBHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return mDBHelper;
    }

    private List<Challenge> retrieveChallenges(){
        List<Challenge> challenges = new ArrayList<>();
        try{
            Dao<Challenge, Integer> challengeDao = getDBHelper().getContactDao();
            challenges = challengeDao.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return challenges;
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
