package co.mobilemakers.drinking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agustin on 17/02/2015.
 */
public class PlayerAdapter extends ArrayAdapter<Player> {

    Context mContext;
    ArrayList<Player> mPlayers;

    public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, R.layout.player_item, players);
        mContext = context;
        mPlayers= players;
    }

    private void displayContentInView(int position, View rowView) {
        if (rowView != null) {
            TextView textViewName = (TextView) rowView.findViewById(R.id.text_view_player_name);
            textViewName.setText(mPlayers.get(position).getName());
            if (mPlayers.get(position).getTeam().equals("Red")){
                textViewName.setTextColor(mContext.getResources().getColor(R.color.accent));
            }
            else{
                textViewName.setTextColor(mContext.getResources().getColor(R.color.material_blue));
            }

            ImageView imageViewPhoto = (ImageView) rowView.findViewById(R.id.image_view_player_photo);
            Bitmap bmp = getBitmap(position);
            imageViewPhoto.setImageBitmap(getCircularBitmap(bmp));
        }
    }


    private View reuseOrGenerateRowView(View convertView, ViewGroup parent) {
        View rowView;
        if (convertView != null) {
            rowView = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.player_item, parent, false);
        }
        return rowView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = reuseOrGenerateRowView(convertView, parent);
        displayContentInView(position, rowView);

        return rowView;
    }

    private Bitmap getBitmap(int position) {
        Bitmap bmp;
        byte[] image;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        image = mPlayers.get(position).getImage();
        bmp = BitmapFactory.decodeByteArray(image, 0, image.length, options);
        return bmp;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}

