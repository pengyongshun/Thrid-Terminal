package com.xt.mobile.terminal.network.sysim;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import com.xt.mobile.terminal.util.ToolLog;

public class IWebSocketClient extends WebSocketClient {

	public IWebSocketClient(URI serverUri) {
		super(serverUri, new Draft_6455());
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		ToolLog.i("+++WebSocket onOpen()");
	}

	@Override
	public void onMessage(String message) {
		ToolLog.i("+++WebSocket onMessage() : " + message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		ToolLog.i("+++WebSocket onClose()");
	}

	@Override
	public void onError(Exception ex) {
		ToolLog.i("+++WebSocket onError()");
	}
}
