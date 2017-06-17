package alexeykafeev.languageremoutecontrol;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by jeam999 on 09.06.2017.
 */

public class StudentAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Student_Item> objects;

    StudentAdapter(Context context, ArrayList<Student_Item> products) {
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
            view = lInflater.inflate(R.layout.student_item, parent, false);
        }

        final Student_Item p = getProduct(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.studentName)).setText(p.getFirstName() + " " + p.getLastName());


        final View.OnClickListener PLAY = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setIpAndStudentName(p.getFirstName() + "\n" + p.getLastName(),
                        p.getAddres(), false);
            }
        };
        final TextView studentName = (TextView) view.findViewById(R.id.studentName);

        studentName.setOnClickListener(PLAY);


        studentName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    studentName.setBackgroundColor(Color.parseColor("#0e6eb8"));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    studentName.setBackgroundColor(Color.TRANSPARENT);

                }
                return false;
            }
        });

        return view;
    }

    // товар по позиции
    Student_Item getProduct(int position) {
        return ((Student_Item) getItem(position));
    }
}