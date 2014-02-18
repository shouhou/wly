package com.fourw.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Helper {	
	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}
	
	public static String getInetIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					return inetAddress.getHostAddress();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void exitProcedure(){
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);	
	}
}
