package com.fourw.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//import android.util.Log;

public class Client implements Runnable {

	/** 服务器的IP地址，需要根据具体情况进行修 */
	private static final String HOST = "192.168.1.105";
	private static final int PORT = 9999;
	private Socket socket = null;

	private static String TAG = "ClientTAG";
	public static boolean DEBUG = true;

	public Handler handler;

	public Client(Handler handler) {
		this.handler = handler;
	}

	public void collect() {
		try {
			socket = new Socket(HOST, PORT);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		collect();
		String code;
		if (socket == null || !socket.isConnected()) {
			Message hMsg = new Message();
			hMsg.what = Msg.DISCONNECTED;
			handler.sendMessage(hMsg);
		}

		while (socket != null || socket.isConnected()) {
			Log.d(TAG, "socketIP:" + socket.getInetAddress().toString());
			code = receiveMessageViaString();
			if (code == null) {
				Message hMsg = new Message();
				hMsg.what = Msg.DISCONNECTED;
				handler.sendMessage(hMsg);
				break;
			}
			if (code.equals(Msg.STRMSG)) {
				receiveMessageViaString();
			}
			if (code.equals(Msg.OBJMSG)) {
				receiveMessageViaObject();
			}
			if (code.equals(Msg.LISTMSG)) {
				receiveMessageViaList();
			}
		}
	}

	/**
	 * 关闭socket.
	 */
	public void closeAll() {
		try {
			if (!socket.isClosed())
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送String消息.
	 */
	public void sendMessageViaString(String msg) {
		if (socket.isConnected()) {
			if (!socket.isOutputShutdown()) {
				PrintWriter pout = null;
				try {
					pout = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);
					pout.println(msg);
					if (DEBUG)
						System.out.println(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Log.d(TAG, "send message:" + msg + " ");
			}
		}
	}

	/**
	 * 发送Object消息.
	 */
	public void sendMessageViaObject(Object msg) {
		if (socket.isConnected()) {
			if (!socket.isOutputShutdown()) {
				ObjectOutputStream outObject = null;
				try {
					outObject = new ObjectOutputStream(socket.getOutputStream());
					outObject.writeObject(msg);
					outObject.flush();
					if (DEBUG)
						System.out.println(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Log.d(TAG, "send message:" + msg + " ");
			}
		}
	}

	/**
	 * 接收Object消息.
	 */
	public void receiveMessageViaObject() {
		try {
			Log.d(TAG, "client receive begin");
			// 对象数据的输入与输出，需要用ObjectInputStream和ObjectOutputStream进行
			ObjectInputStream inObject = new ObjectInputStream(
					socket.getInputStream());
			Log.d(TAG, "client receive go on");
			Msg msg = (Msg) inObject.readObject();
			Log.d(TAG, "client receive msg from server");
			handMsg(msg);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接收String消息.
	 */
	public String receiveMessageViaString() {
		String message = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			message = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * 发送Object消息.
	 */
	public void sendMessage(Object msg) {
		if (socket.isConnected()) {
			if (!socket.isOutputShutdown()) {
				ObjectOutputStream outObject = null;
				try {
					outObject = new ObjectOutputStream(socket.getOutputStream());
					outObject.writeObject(msg);
					outObject.flush();
					if (DEBUG)
						System.out.println(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Log.d(TAG, "send message:" + msg + " ");
			}
		}
	}

	/**
	 * 发送List<Object>消息.
	 */
	public void sendMessageViaList(List<Object> msg) {
		if (socket.isConnected()) {
			if (!socket.isOutputShutdown()) {
				ObjectOutputStream outObject = null;
				try {
					outObject = new ObjectOutputStream(socket.getOutputStream());
					for (Object obj : msg) {
						outObject.writeObject(obj);
						outObject.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Log.d(TAG, "send message:" + msg + " ");
			}
		}
	}

	/**
	 * 接收Object消息.
	 */
	public Object receiveMessage() {
		try {
			// 对象数据的输入与输出，需要用ObjectInputStream和ObjectOutputStream进行
			ObjectInputStream inObject = new ObjectInputStream(
					socket.getInputStream());
			Object msg = inObject.readObject();
			if (msg instanceof Msg) {
				System.out.println("msg of object");
			} else if (msg instanceof String) {
				System.out.println("msg of string");
			}
			return msg;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 接收List<Object>消息.
	 */
	public List<Object> receiveMessageViaList() {
		List<Object> list = new ArrayList<Object>();
		try {
			// 对象数据的输入与输出，需要用ObjectInputStream和ObjectOutputStream进行
			ObjectInputStream inObject = new ObjectInputStream(
					socket.getInputStream());
			while (true) {
				Object obj = inObject.readObject();
				if (obj instanceof Student) {
					Student stu = (Student) obj;
					list.add(stu);
					// System.out.println("msg of object");
					Log.d(TAG, "name:" + ((Student) obj).getName() + "phone:"
							+ ((Student) obj).getPhone());

				} else if (obj instanceof String) {
					if (((String) obj).equalsIgnoreCase("exit"))
						break;
				}
			}

			Message pMsg = new Message();
			pMsg.what = Msg.SHOW2;
			pMsg.obj = list;
			handler.sendMessage(pMsg);

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void handMsg(Msg msg) {
		if (msg.getCode() == Msg.CALL) {
			Log.d(TAG, "client handle: " + msg);
			Message pMsg = new Message();
			pMsg.what = Msg.HINT;
			handler.sendMessage(pMsg);
		}
		if (msg.getCode() == Msg.RESULT) {
			String allInfo = msg.getInfo();
			String infos[] = allInfo.split(",");
			int allNum, absentNum = 0;
			allNum = Integer.parseInt(infos[0]);
			absentNum = Integer.parseInt(infos[1]);

			Message pMsg = new Message();
			pMsg.what = Msg.SHOW1;
			pMsg.arg1 = allNum;
			pMsg.arg2 = absentNum;
			handler.sendMessage(pMsg);

			Log.d(TAG, "Num:" + allNum + "," + absentNum);
		}
	}
}
