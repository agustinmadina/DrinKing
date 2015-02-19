package co.mobilemakers.drinking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

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

    TextView mTextViewChallenge;

    public ChallengeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_challenge, container, false);
        mChallenges = retrieveChallenges();
        wireUpChallengeText(rootView);
        prepareChallengeText();
        this.getView().setFocusableInTouchMode(true);

        this.getView().setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    return true;
                }
                return false;
            }
        } );
        return rootView;
    }

    private void wireUpChallengeText(View rootView) {
        mTextViewChallenge = (TextView) rootView.findViewById(R.id.text_view_challenge);
    }

    private void prepareChallengeText() {
        Random random = new Random();
        Challenge challenge = mChallenges.get(random.nextInt(mChallenges.size()));
        mTextViewChallenge.setText(challenge.getContent());
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

}
