package co.mobilemakers.drinking;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlayerFragment extends Fragment {


    Button mButtonConfirmPlayer;
    ImageButton mPhotoPlayer;
    EditText mEditTextPlayername;
    Switch mSwitchTeam;
    Player mPlayer;
    String mName, mTeam;
    TextView mTeamBlue,mTeamRed;

    Bitmap mPhoto;
    byte[] mImage;

    public AddPlayerFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_player, container, false);
        wireUpViews(rootView);
        prepareImageButton();
        prepareConfirmButtonListener();
        mEditTextPlayername.addTextChangedListener(watcher);
        if (getActivity().getIntent().getStringExtra(SelectModeFragment.GAME_MODE).equals("Solo")){
            mSwitchTeam.setVisibility(View.INVISIBLE);
            mTeamBlue.setVisibility(View.INVISIBLE);
            mTeamRed.setVisibility(View.INVISIBLE);
        }else{
            mSwitchTeam.setVisibility(View.VISIBLE);
            mTeamBlue.setVisibility(View.VISIBLE);
            mTeamRed.setVisibility(View.VISIBLE);

        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PlayerListFragment.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mPhoto = (Bitmap) data.getExtras().get("data");
            mPhotoPlayer.setImageBitmap(mPhoto);
        }
    }


    private void prepareConfirmButtonListener() {
        mButtonConfirmPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preparePlayer();
                mPlayer = new Player(mName, mTeam, mImage);
                Bundle extrasBundle = new Bundle();
                extrasBundle.putParcelable("player", mPlayer);
                Activity activity = getActivity();
                Intent intentResult = new Intent();
                intentResult.putExtras(extrasBundle);
                activity.setResult(Activity.RESULT_OK, intentResult);
                activity.finish();
            }
        });
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mEditTextPlayername.getText())) {
                mButtonConfirmPlayer.setEnabled(false);
            } else {
                mButtonConfirmPlayer.setEnabled(true);
            }
        }

        ;
    };

    private void preparePlayer() {
        mName=mEditTextPlayername.getText().toString();
        if (mSwitchTeam.isChecked()){
            mTeam="Red";
        }
        else{
            mTeam="Blue";
        }
        convertBitmapImageToByteArray();
    }


    private void wireUpViews(View rootView) {
        mButtonConfirmPlayer=(Button) rootView.findViewById(R.id.button_confirm_player);
        mPhotoPlayer=(ImageButton)rootView.findViewById(R.id.image_button_player_photo);
        mEditTextPlayername=(EditText)rootView.findViewById(R.id.edit_text_player_name);
        mSwitchTeam=(Switch)rootView.findViewById(R.id.switch_player_team);
        mTeamRed=(TextView)rootView.findViewById(R.id.text_view_red_team);
        mTeamBlue=(TextView)rootView.findViewById(R.id.text_view_blue_team);
    }

    private void convertBitmapImageToByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        mImage = stream.toByteArray();
    }

    private void prepareImageButton() {

        mPhoto = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.placeholder_male_superhero);

        mPhotoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PlayerListFragment.REQUEST_CODE);
            }
        });
    }
}


