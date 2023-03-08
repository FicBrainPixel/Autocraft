package com.fbp.autocraft;

import static com.fbp.autocraft.MainPage.TAG;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class DataExchange
{
    static int send;
    static String pendingMessage;
    Context context;
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    public final int MESSAGE_STATE_CHANGE = 1;
    public final int MESSAGE_READ = 2;
    public final int MESSAGE_WRITE = 3;
    public final int MESSAGE_DEVICE_OBJECT = 4;
    public final int MESSAGE_TOAST = 5;
    public final String DEVICE_OBJECT = "device_name";
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ReadWriteThread connectedThread;
    private int state;
    static final int STATE_NONE = 0;
    static final int STATE_LISTEN = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    BluetoothSocket socket;
    public DataExchange(Context context)
    {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        handler = new Handler(msg ->
        {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case DataExchange.STATE_CONNECTED:
                            Log.d(TAG, "Connected to: " + send + " " + pendingMessage);
                            if(send == 1)
                            {
                                DeviceModelAdapter.sendData(context, pendingMessage);
                            }
                            else if(send == 2)
                            {
                                DeviceController.sendData(context, pendingMessage);
                            }
                            break;
                        case DataExchange.STATE_CONNECTING:
                            Log.d(TAG, "Connecting...");
                            break;
                        case DataExchange.STATE_LISTEN:
                        case DataExchange.STATE_NONE:
                            Log.d(TAG, "Not connected");
                            ((Activity)context).runOnUiThread(() ->
                                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show());
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "Me: " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "BD:  " + readMessage);
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    Log.d(TAG, "Connected to ");
                    break;
                case MESSAGE_TOAST:
                    Log.d(TAG, msg.getData().getString("toast"));
                    break;
            }
            return false;
        });
    }

    // Set the current state of the chat connection
    private synchronized void setState(int state)
    {
        this.state = state;
        handler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // get current connection state
    public synchronized int getState()
    {
        return state;
    }

    // start service
    
    public synchronized void start()
    {
        // Cancel any thread
        if (connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any running thread
        if (connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);
        if (acceptThread == null)
        {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // initiate connection to remote device
    
    public synchronized void connect(BluetoothDevice device)
    {
        // Cancel any thread
        if (state == STATE_CONNECTING)
        {
            if (connectThread != null)
            {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel running thread
        if (connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    // manage Bluetooth connection
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        // Cancel the thread
        if (connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel running thread
        if (connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null)
        {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ReadWriteThread(socket);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(MESSAGE_DEVICE_OBJECT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DEVICE_OBJECT, device);
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    // stop all threads
    public synchronized void stop()
    {
        if (connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null)
        {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null)
        {
            acceptThread.cancel();
            acceptThread = null;
        }
        ((Activity)context).runOnUiThread(() ->
                Toast.makeText(context, "Stopped", Toast.LENGTH_SHORT).show());
        setState(STATE_NONE);
    }

    public void write(byte[] out)
    {
        ReadWriteThread r;
        synchronized (this)
        {
            if (state != STATE_CONNECTED)
                return;
            r = connectedThread;
        }
        r.write(out);
    }
    
    private void connectionFailed()
    {
        Message msg = handler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        ((Activity)context).runOnUiThread(() ->
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show());

        // Start the service over to restart listening mode
        DataExchange.this.start();
    }
    
    private void connectionLost()
    {
        Message msg = handler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);
        ((Activity)context).runOnUiThread(() ->
                Toast.makeText(context, "Lost", Toast.LENGTH_SHORT).show());

        // Start the service over to restart listening mode
        DataExchange.this.start();
    }

    // runs while listening for incoming connections
    private class AcceptThread extends Thread
    {
        private BluetoothServerSocket serverSocket;
        
        public AcceptThread()
        {
            BluetoothServerSocket tmp = null;
            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    return;
                }
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(String.valueOf(R.string.app_name), MY_UUID);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run()
        {
            setName("AcceptThread");
            while (state != STATE_CONNECTED)
            {
                try
                {
                    socket = serverSocket.accept();
                }
                catch (IOException e)
                {
                    break;
                }

                // If a connection was accepted
                if (socket != null)
                {
                    synchronized (DataExchange.this)
                    {
                        switch (state)
                        {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try
                                {
                                    socket.close();
                                } catch (IOException ignored) {}
                                break;
                        }
                    }
                }
            }
        }

        public void cancel()
        {
            try
            {
                serverSocket.close();
            } catch (IOException ignored) {}
        }
    }

    // runs while attempting to make an outgoing connection
    private class ConnectThread extends Thread
    {
        private BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device)
        {
            this.device = device;
            BluetoothSocket tmp = null;
            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                    return;
                }
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            socket = tmp;
        }
        
        public void run()
        {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                return;
            }
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    return;
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                    return;
                }
                else
                {
                    socket.connect();
                }
            }
            catch (Exception e)
            {
                ((Activity)context).runOnUiThread(() ->
                    Toast.makeText(context, String.valueOf(e), Toast.LENGTH_LONG).show());
                Log.d(TAG, String.valueOf(e));
                try
                {
                    socket.close();
                } catch (IOException ignored) {}
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (DataExchange.this)
            {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, device);
        }

        public void cancel()
        {
            try
            {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    // runs during a connection with a remote device
    private class ReadWriteThread extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ReadWriteThread(BluetoothSocket socket)
        {
            this.bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {}

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        
        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream
            while (true)
            {
                try
                {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    connectionLost();
                    // Start the service over to restart listening mode
                    DataExchange.this.start();
                    break;
                }
            }
        }

        // write to OutputStream
        public void write(byte[] buffer)
        {
            try
            {
                Log.d(TAG, Arrays.toString(buffer));
                outputStream.write(buffer);
                handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException ignored) {}
        }

        public void cancel()
        {
            try
            {
                bluetoothSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}