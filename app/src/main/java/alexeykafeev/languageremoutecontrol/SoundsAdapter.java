package alexeykafeev.languageremoutecontrol;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by jeam999 on 12.06.2017.
 */

public class SoundsAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<SoundItem> objects;

    SoundsAdapter(Context context, ArrayList<SoundItem> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.sound_item, parent, false);
        }

        final SoundItem p = getProduct(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.soundName)).setText(p.getFilename());


        final View.OnClickListener PLAY = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sendUDPMessagePLAY(MainActivity.chosenStudentIP, p.getFilename());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        };
        final TextView soundName = (TextView) view.findViewById(R.id.soundName);

        ((LinearLayout) view.findViewById(R.id.soundLineItem)).setOnClickListener(PLAY);

        if (p.isLastPlayed()) {
            view.setBackgroundColor(Color.parseColor("#a1c4d7"));
        } else view.setBackgroundColor(Color.TRANSPARENT);

        ((LinearLayout) view.findViewById(R.id.soundLineItem)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    soundName.setBackgroundColor(Color.parseColor("#0e6eb8"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundName.setBackgroundColor(Color.TRANSPARENT);

                }
                return false;
            }
        });

        return view;
    }

    // товар по позиции
    SoundItem getProduct(int position) {
        return ((SoundItem) getItem(position));
    }
}
