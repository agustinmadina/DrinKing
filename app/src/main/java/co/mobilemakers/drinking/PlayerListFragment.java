package co.mobilemakers.drinking;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerListFragment extends ListFragment {

    PlayerAdapter mAdapter;
    Button mButtonAddPlayer;
    Button mButtonStartGame;
    ArrayList<Player> mPlayers;
    Player mPlayer;
    String mGameMode;


    public final static Integer REQUEST_CODE = 0;
    public final static String BLUE = "Blue";
    public final static String RED = "Red";
    public final static String TEAM_RED = "team red";
    public final static String TEAM_BLUE = "team blue";
    public final static String UNIQUE_TEAM = "unique team";

    public PlayerListFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_list, container, false);
        wireUpViews(rootView);
        prepareButtonAddPlayerListener();
        prepareListView();
        setGameMode();
        prepareButtonStartGameAndSetTeams(rootView);
        return rootView;
    }

    private void prepareButtonStartGameAndSetTeams(View rootView) {
        mButtonStartGame = (Button) rootView.findViewById(R.id.button_initiate_game);
        mButtonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ChallengeFragment challengeFragment = new ChallengeFragment();
                ArrayList<Player> teamRed = new ArrayList<Player>();
                ArrayList<Player> teamBlue = new ArrayList<Player>();
                Bundle bundle = new Bundle();
                bundle.putString(SelectModeFragment.GAME_MODE, mGameMode);
                if (mGameMode.equals(SelectModeFragment.GAME_MODE_TEAM)) {
                    for (Player p:mAdapter.mPlayers) {
                        if (p.getTeam().equals(RED)) {
                            teamRed.add(p);
                        } else {
                            teamBlue.add(p);
                        }
                    }
                    bundle.putParcelableArrayList(TEAM_RED, teamRed);
                    bundle.putParcelableArrayList(TEAM_BLUE, teamBlue);
                } else {
                    bundle.putParcelableArrayList(UNIQUE_TEAM, mAdapter.mPlayers);
                }
                challengeFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, challengeFragment).commit();
            }
        });
    }

    private void setGameMode() {
        mGameMode = this.getArguments().getString(SelectModeFragment.GAME_MODE);
    }

    private void prepareButtonAddPlayerListener() {
        mButtonAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddPlayerActivity.class);
                i.putExtra(SelectModeFragment.GAME_MODE, mGameMode);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    private void wireUpViews(View rootView) {
        mButtonAddPlayer=(Button)rootView.findViewById(R.id.button_add_player);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mPlayer = data.getParcelableExtra("player");
                mAdapter.add(mPlayer);
            }
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
    }

    private void prepareListView() {
        mPlayers= new ArrayList<>();
        mAdapter = new PlayerAdapter(getActivity(), mPlayers);
        setListAdapter(mAdapter);
    }
}




