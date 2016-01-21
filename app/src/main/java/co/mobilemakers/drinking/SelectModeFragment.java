package co.mobilemakers.drinking;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectModeFragment extends Fragment {

    ImageButton mButtonBeginSoloGame;
    ImageButton mButtonBeginTeamGame;

    public static final String GAME_MODE = "game mode";
    public static final String GAME_MODE_SOLO = "Solo";
    public static final String GAME_MODE_TEAM = "Team";

    public SelectModeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_mode, container, false);
        mButtonBeginSoloGame = (ImageButton) rootView.findViewById(R.id.image_button_solo);
        mButtonBeginTeamGame = (ImageButton) rootView.findViewById(R.id.image_button_team);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(GAME_MODE, getGameMode(v));
                PlayerListFragment playerListFragment = new PlayerListFragment();
                playerListFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, playerListFragment).addToBackStack(null).commit();
            }
        };
        mButtonBeginSoloGame.setOnClickListener(onClickListener);
        mButtonBeginTeamGame.setOnClickListener(onClickListener);
        return rootView;
    }

    private String getGameMode(View v) {
        String gameMode;
        if (v.getId()==R.id.image_button_team){
            gameMode = GAME_MODE_TEAM;
        }
        else{
            gameMode = GAME_MODE_SOLO;
        }
        return gameMode;
    }
}
