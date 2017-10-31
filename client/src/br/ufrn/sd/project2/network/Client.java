package br.ufrn.sd.project2.network;

import br.ufrn.sd.project2.Main;
import br.ufrn.sd.project2.util.Log;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import javafx.application.Platform;

/**
 *
 * @author dhiogoboza
 */
public class Client implements Closeable {
	
	private static final String TAG = "Client";
    
    public static String CODE_SEND_FUNTION = "3";
	
	private ClientConfig config;
	
	private Socket socket;
    private OutputStream output;
    private BufferedReader bufferedReader;
	private StatusChangeListener listener;
	
	private boolean connected = false;
	
	private Thread receiveMessagesThread;
	
	private final Runnable receiveMessagesRunnable = new Runnable() {
		
        @Override
        public void run() {
            try {
                Log.d(TAG, "Connection established " + socket.getInetAddress() + ": " + socket.getPort());
                
				String data;
				
                do {
                    data = bufferedReader.readLine();
                    
					if (data != null) {
						Log.d(TAG, "Data received: " + data);
                        
                        switch (data.charAt(0)) {
                            case '1':
                                // password verification result
                                if (data.charAt(2) == '0') {
                                    disconnect();
                                }
                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Main.getInstance().getConnectionStage().onLoginFinished();
                                    }
                                });
                                
                            break;
                            case '4':
                                Main.getInstance().getMainStage().updateData(data);
                                break;
                        }
                                
                        
					}
                } while (connected);
            } catch (IOException ex) {
                Log.e(TAG, "In loop", ex);
            }
        }
		
    };
	
	public static interface StatusChangeListener {
		public void onStatusChange(boolean connected);
	}
	
	public Client(ClientConfig config) {
		this.config = config;
		receiveMessagesThread = new Thread(receiveMessagesRunnable);
	}
	
	public boolean isConnected() {
    	return connected;//this.socket != null && this.socket.isConnected();
    }

    public void connect() {
        if (socket != null && socket.isConnected()) {
            connected = true;
        }

        try {
            socket = new Socket(config.getIp(), config.getPort());

            socket.setKeepAlive(true);
            socket.setSoTimeout(0);
            socket.setTcpNoDelay(true);
			
            output = socket.getOutputStream();
            output.write(("0," + config.getPassword()).getBytes("ASCII"));
            output.flush();

            bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), Charset.forName("ASCII")));

            Log.d(TAG, "Connection result: " + socket.isConnected());

            connected = socket.isConnected();

            if (connected) {
                try {
                    receiveMessagesThread.start();
                } catch (Exception e) {
                    receiveMessagesThread = new Thread(receiveMessagesRunnable);
                    receiveMessagesThread.start();
                }
            }

            return;
        } catch (Exception ex) {
			connected = false;
            Log.e(TAG, "Could not create socket: " + config, ex);
        }


        connected = false;
    }

    public boolean disconnect() throws IOException {
		Log.d(TAG, "Closing socket: " + socket);
		
        connected = false;
        
        if (this.socket != null) {
			try {
				socket.close();
				socket= null;
				
				if (listener != null) {
                    listener.onStatusChange(false);
                }
				
				return true;
			} catch (IOException ex) {
				Log.e(TAG, "Closing socket", ex);
			}
        }
		
		return true;
    }

    public void sendData(String data) throws IOException {
        Log.d(TAG, "Sending data: " + data);
        
		if (output != null) {
			output.write(data.getBytes("ASCII"));
			output.flush();
		} else {
			throw new IOException("Client not connected");
		}
    }
    
	@Override
	public void close() throws IOException {
		disconnect();
	}
	
	public void connectionChanged(boolean connected) {
		connectionChanged(connected, "Could not connect to device: " + config);
	}
	
	public void connectionChanged(boolean connected, String message) {
		listener.onStatusChange(connected);
		
		if (!connected) {
			Log.e(TAG, message);
		}
	}
	
	public void setStatusChangeListener(StatusChangeListener statusChangeListener) {
		listener = statusChangeListener;
	}
	
	public void setConfig(ClientConfig config) {
		this.config = config;
	}
	
	public ClientConfig getConfig() {
		return config;
	}
}
