package alexeykafeev.languageremoutecontrol;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by jeam999 on 07.06.2017.
 */

abstract public class AUDPServer implements Runnable {

    /**
     * порт, который слушает сервер
     */
    private final int port;
    private final Thread thread;
    private DatagramSocket socket;
    private boolean isActive = true;

    public boolean isActivate() {
        return isActive;
    }

    public AUDPServer(int port) {
        this.port = port;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        Utilite.setInet();
        byte[] buffer = new byte[1024];

        //Uses.log.logger.trace("Старт UDP сервера на порту \"" + port + "\"");
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            isActive = false;
            try {
                throw new Exception("Невозможно создать UDP-сокет на порту " + port + ". " + ex.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        while (true) {
            //Receive request from client
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException ex) {
                try {
                    throw new Exception("Невозможно отослать UDP-сообщение. " + ex.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InetAddress client = packet.getAddress();
            int client_port = packet.getPort();
            final String message = new String(buffer, packet.getOffset(), packet.getLength());
            //Uses.log.logger.trace("Приём UDP сообшение \"" + message + "\" ОТ адреса \"" + client.getHostName() + "\" с порта \"" + port + "\"");
            getData(message, client, client_port);
            Log.d("CLIENT",": BROADCAST:  m:"+message);
        }

    }

    /**
     * Обработка события получения сообщения
     * @param data Текст сообщения
     * @param clientAddress адрес, откуда пришло сообщение
     * @param clientPort порт, с которого послали сообщение
     */
    abstract protected void getData(String data, InetAddress clientAddress, int clientPort);

    /**
     * Остонавливаем сервер
     */
    @SuppressWarnings("static-access")
    public void stop() {
        thread.interrupted();
        socket.close();
        //Uses.log.logger.trace("Остановка UDP сервера на порту \"" + port + "\"");
    }
}
