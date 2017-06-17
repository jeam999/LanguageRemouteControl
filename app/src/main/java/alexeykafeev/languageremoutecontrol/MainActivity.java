package alexeykafeev.languageremoutecontrol;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity implements View.OnClickListener {

    public static SlidingMenu menu;

    RelativeLayout MainMenuSlidingMenu;

    public static String ClassRoomNumber;
    public static final int broadcastPosrt = 7070;
    public static final int commandPort = 7072;
    //public static final int commandToStudentPosrt=7071;
    public static final String ANDROID = "ANDROID";
    public static final String STUDENT = "STUDENT";
    public static final String GET_IP = "GET_IP";
    public static final String PLAY = "PLAY";
    public static final String STOP = "STOP";
    public static final String FILES = "FILES";

    public static boolean broadcast;
    public static InetAddress ServerAddress;

    public static boolean allStudents;

    public static InetAddress chosenStudentIP;

    ListView soudListView;
    public static TextView studentName;
    public static TextView chooseAllStudent;

    LinearLayout stopBTN;

    static ListView studentsListView;

    public static ArrayList<Student_Item> student_Items = new ArrayList<>();
    public int port;

    public static ArrayList<SoundItem> soundItems = new ArrayList<>();

    static StudentAdapter studentAdapter;
    static SoundsAdapter soundsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        broadcast = true;

        MainMenuSlidingMenu = (RelativeLayout) findViewById(R.id.MainMenuSlidingMenu);
        MainMenuSlidingMenu.setOnClickListener(this);

        soudListView = (ListView) findViewById(R.id.soudListView);
        studentName = (TextView) findViewById(R.id.studentName);
        stopBTN = (LinearLayout) findViewById(R.id.stopBTN);
        stopBTN.setOnClickListener(this);

        stopBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stopBTN.setBackgroundColor(Color.parseColor("#0e6eb8"));
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopBTN.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.10f);
        // ///////////
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        // //////////
        menu.setMenu(R.layout.activity_menu);

        setIpAndStudentName("", null, true);
        studentsListView = (ListView) findViewById(R.id.studentsListView);

        studentAdapter = new StudentAdapter(this, student_Items);
        //soundsAdapter = new SoundsAdapter(this, soundItems);
        studentsListView.setAdapter(studentAdapter);

        findViewById(R.id.hideSlidingMenu).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menu.toggle();
                    }
                });

        chooseAllStudent = (TextView) findViewById(R.id.chooseAllStudent);
        chooseAllStudent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chooseAllStudent.setBackgroundColor(Color.parseColor("#0e6eb8"));

                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    chooseAllStudent.setBackgroundColor(Color.TRANSPARENT);
                    setIpAndStudentName("",null,true);
                    menu.toggle();
                }
                return false;
            }
        });

        soundsAdapter = new SoundsAdapter(this, soundItems);
        soudListView.setAdapter(soundsAdapter);

        //studentAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            public void run() {
                Utilite.setInet();
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(broadcastPosrt, InetAddress.getByName("0.0.0.0"));
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                try {
                    socket.setBroadcast(true);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                try {
                    System.out.println("Listen on " + socket.getLocalAddress() + " from " + socket.getInetAddress() + " port " + socket.getBroadcast());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (true) {
                    Log.d("CLIENT", ": Waiting for data");

                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String message = null;
                    try {
                        message = new String(packet.getData(), 0, packet.getLength(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String[] code = message.split(",");

                    switch (code[0]) {
                        case GET_IP:
                            try {
                                ServerAddress = InetAddress.getByName(packet.getAddress().getHostAddress());
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            try {
                                sendUDPMessage(ANDROID + "," + InetAddress.getLocalHost().getHostAddress(), ServerAddress);
                            } catch (SocketException e) {
                                e.printStackTrace();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            break;
                        case STUDENT:
                            Log.d("CLIENT", ": " + "student added");
                            InetAddress address = null;
                            try {
                                address = InetAddress.getByName(code[1]);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            student_Items.add(new Student_Item(address, code[2], code[3]));
                            updateListView();
                            //studentsListView.setAdapter(studentAdapter);


                            break;
                        case FILES:
                            soundItems.add(new SoundItem(code[1], false));
                            updateListViewSound();

                            break;
                    }

                    Log.d("CLIENT", ": Data received" + "\nadress: " + packet.getAddress().getHostAddress() + "\nData: " + message + "\n");
                    System.out.println("Data received");
                }
            }
        }).start();

    }

    public void updateListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                studentAdapter.notifyDataSetChanged();
//stuff that updates ui

            }
        });

    }

    public void updateListViewSound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                soundsAdapter.notifyDataSetChanged();
//stuff that updates ui

            }
        });
    }

    public static void sendUDPMessage(String message, InetAddress address) throws SocketException {
        Utilite.setInet();
        System.out.println("Отправка UDP сообшение \"" + message + "\" по адресу \"" + address.getHostAddress() + "\" на порт \"" + commandPort + "\"");
        final DatagramSocket socket = new DatagramSocket();
        final byte mess_b[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(mess_b, mess_b.length, address, commandPort);

        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(Utilite.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            socket.close();
        }
    }

    public static void sendUDPMessagePLAY(InetAddress address, String FileName) throws SocketException {
        Utilite.setInet();
        String message = PLAY + "," + address.getHostName() + "," + FileName;
        System.out.println("Отправка UDP сообшение \"" + message + "\" по адресу \"" + address.getHostAddress() + "\" на порт \"" + commandPort + "\"");
        final DatagramSocket socket = new DatagramSocket();
        final byte mess_b[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(mess_b, mess_b.length, ServerAddress, commandPort);

        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(Utilite.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            socket.close();
        }
    }

    public static void sendUDPMessageSTOP(InetAddress address) throws SocketException {
        Utilite.setInet();
        String message = STOP + "," + address.getHostName();
        System.out.println("Отправка UDP сообшение \"" + message + "\" по адресу \"" + address.getHostAddress() + "\" на порт \"" + commandPort + "\"");
        final DatagramSocket socket = new DatagramSocket();
        final byte mess_b[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(mess_b, mess_b.length, ServerAddress, commandPort);

        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(Utilite.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            socket.close();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.MainMenuSlidingMenu:
                menu.toggle();
                break;
            case R.id.stopBTN:
                try {
                    sendUDPMessageSTOP(MainActivity.chosenStudentIP);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public static void setIpAndStudentName(String studentFullName, InetAddress studentIP, boolean allstudents) {

        menu.toggle();
        allStudents = allstudents;

        if (!allstudents) {
            studentName.setText(studentFullName);
            chosenStudentIP = studentIP;
        } else {
            studentName.setText("Все\nстуденты");
            try {
                chosenStudentIP = InetAddress.getByName("255.255.255.255");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}
