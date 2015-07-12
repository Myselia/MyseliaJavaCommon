package com.myselia.javacommon.framework.communication;

import io.netty.channel.Channel;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;

public class WebSocketHelper {

	public static String keyStringSearch = "Sec-WebSocket-Key: ";
	private static String webSocketUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	
	public static int getPayloadSize(byte payloadByte) {
		return (int)payloadByte;
	}
	/**
	 * This function is used to wrap a string passed into it with a websocket compatible frame
	 * This frame is RFC 6455 compatible and available at: http://tools.ietf.org/html/rfc6455#page-20.
	 * @param s	The string to be wrapped with the frame
	 * @return	An array of bytes to be sent over a connection
	 */
	public static byte[] encodeWebSocketPayload(String s) {
		byte[] payloadBytes = s.getBytes();
		byte[] contructedWebSocketFrame = new byte[10];
		byte[] replyWebSocketFrame;
		int payloadLen = payloadBytes.length;
		int amountOfFrames = 0;
		int totalFrameLength = 0;

		/*
		 * SETUP OF FIRST BYTE, BY RFC 6455 THIS IS 129d -> "1000 0001"
		 */
		contructedWebSocketFrame[0] = (byte) 129; /* FOR TEXT FRAME*/

		/*
		 * SETUP OF LENGTH BYTES
		 * PAYLOAD LENGTH
		 * 	0-125 : NO ADDITIONAL BYTES
		 * 	126-65535 : TWO ADDITIONAL BYTES, SECOND(IN FRAME) BYTE 126
		 * 	>65536 : EIGHT ADDITIONAL BYTES, SECOND(IN FRAME) BYTE 127
		 */
		if (payloadBytes.length <= 125) {
			contructedWebSocketFrame[1] = (byte) payloadBytes.length;
			amountOfFrames = 2;
		} else if (payloadBytes.length >= 126 && payloadBytes.length <= 65535) {
			contructedWebSocketFrame[1] = (byte) 126;
			contructedWebSocketFrame[2] = (byte) ((payloadLen >> 8) & (byte) 255);
			contructedWebSocketFrame[3] = (byte) (payloadLen & (byte) 255);
			amountOfFrames = 4;
		} else {
			contructedWebSocketFrame[1] = (byte) 127;
			contructedWebSocketFrame[2] = (byte) ((payloadLen >> 56) & (byte) 255);
			contructedWebSocketFrame[3] = (byte) ((payloadLen >> 48) & (byte) 255);
			contructedWebSocketFrame[4] = (byte) ((payloadLen >> 40) & (byte) 255);
			contructedWebSocketFrame[5] = (byte) ((payloadLen >> 32) & (byte) 255);
			contructedWebSocketFrame[6] = (byte) ((payloadLen >> 24) & (byte) 255);
			contructedWebSocketFrame[7] = (byte) ((payloadLen >> 16) & (byte) 255);
			contructedWebSocketFrame[8] = (byte) ((payloadLen >> 8) & (byte) 255);
			contructedWebSocketFrame[9] = (byte) (payloadLen & (byte) 255);
			amountOfFrames = 10;
		}

		totalFrameLength = amountOfFrames + payloadBytes.length;

		replyWebSocketFrame = new byte[totalFrameLength];

		int frameByteLimiter = 0;
		for (int i = 0; i < amountOfFrames; i++) {
			replyWebSocketFrame[frameByteLimiter] = contructedWebSocketFrame[i];
			frameByteLimiter++;
		}
		for (int i = 0; i < payloadBytes.length; i++) {
			replyWebSocketFrame[frameByteLimiter] = payloadBytes[i];
			frameByteLimiter++;
		}
		
		return replyWebSocketFrame;
	}

	public static byte[] decodeWebSocketPayload(byte[] framedPacket, int bytesRead) {
		byte[] message;
		byte rLength = 0;
		int totalMessageLength;
		int rMaskIndex = 2;
		int rDataStart = 0;
		
		// b[0] is always text in my case so no need to check;
		byte data = framedPacket[1];
		byte op = (byte) 127;
		rLength = (byte) (data & op);

		if (rLength == (byte) 126)
			rMaskIndex = 4;
		if (rLength == (byte) 127)
			rMaskIndex = 10;

		byte[] masks = new byte[4];

		int j = 0;
		int i = 0;
		for (i = rMaskIndex; i < (rMaskIndex + 4); i++) {
			masks[j] = framedPacket[i];
			j++;
		}

		rDataStart = rMaskIndex + 4;
		totalMessageLength = bytesRead - rDataStart;

		message = new byte[totalMessageLength];
		for (i = rDataStart, j = 0; i < bytesRead; i++, j++) {
			message[j] = (byte) (framedPacket[i] ^ masks[j % 4]);
		}

		return message;
	}

	@SuppressWarnings("unused")
	private static void printBytes(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String s = String.format("%8s", Integer.toBinaryString(b[i] & 0xFF)).replace(' ', '0');
			System.out.println(s);
		}
	}
	
	public static void sendHandshakeResponse(Channel ch, String clientKey) {
		ch.write("HTTP/1.1 101 Switching Protocols\r\n");
		ch.write("Upgrade: WebSocket\r\n");
		ch.write("Connection: Upgrade\r\n");
		ch.write("Sec-WebSocket-Accept: " + generateOK(clientKey));
		ch.write("\r\n\r\n");
		ch.flush();
	}
	
	public static boolean isEndStreamSignal(byte[] bytePayload) {
		if (bytePayload[0] == (byte)3 && bytePayload[1] == (byte)233)
			return true;
		return false;
	}
	
	public static void printHex(byte[] arr) {
		for (int i = 0; i < arr.length; i++) {
			System.out.format(i + " : " + "0x%02X", arr[i]);
			System.out.println();
		}
		
	}
	private static String generateOK(String key) {
		Encoder encoder = Base64.getEncoder();
		String genWith = key + webSocketUID;
		String encodedKey = null;
		MessageDigest md = null;
		byte[] sha1hash = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			sha1hash = new byte[40];
			md.update(genWith.getBytes("iso-8859-1"), 0, genWith.length());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		sha1hash = md.digest();
		
		encodedKey = new String(encoder.encode(sha1hash));
		return encodedKey;
	}
}
