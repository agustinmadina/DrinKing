package co.mobilemakers.drinking;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    Button mButtonStartGame;
    Button mButtonAddChallenge;
    Button mButtonRules;
    Button mExit;


    public StartFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        wireUpButtons(rootView);
        return rootView;
    }

    private void wireUpButtons(View rootView) {
        mButtonStartGame = (Button)rootView.findViewById(R.id.button_start_game);
        mButtonAddChallenge = (Button)rootView.findViewById(R.id.button_add_challenge);
        mButtonRules = (Button)rootView.findViewById(R.id.button_rules);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                switch (v.getId()){
                    case R.id.button_start_game:
                        SelectModeFragment selectModeFragment = new SelectModeFragment();
                        fragmentManager.beginTransaction().
                                replace(R.id.container, selectModeFragment).addToBackStack(null).
                                commit();
                        break;
                    case R.id.button_add_challenge:
                        fragmentManager.beginTransaction().
                                replace(R.id.container, new AddChallengeFragment()).addToBackStack(null).
                        commit();
                        break;
                    case R.id.button_rules:
                        fragmentManager.beginTransaction().
                                replace(R.id.container, new RulesFragment()).addToBackStack(null).
                        commit();
                        break;
                };
            }
        };
        mButtonStartGame.setOnClickListener(onClickListener);
        mButtonAddChallenge.setOnClickListener(onClickListener);
        mButtonRules.setOnClickListener(onClickListener);
    }
}