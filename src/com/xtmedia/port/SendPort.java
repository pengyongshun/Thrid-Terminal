package com.xtmedia.port;

import android.util.Log;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xtmedia.xtview.GlRenderNative;
import com.xtmedia.xtview.SvrInfo;

public class SendPort {

	public static SvrInfo[] send_opt;
	static int[] src_chan = new int[] { -2, -1};

	public static void createSendPort() {
		if (src_chan == null || src_chan[0] == -2) {
			int[] trackids = new int[] { 1, 2};
			src_chan = new int[2];
			GlRenderNative.mediaServerCreateSrc(trackids.length, 0, trackids, src_chan);
			send_opt = new SvrInfo[] { new SvrInfo(), new SvrInfo() };
			GlRenderNative.getSvrInfo(src_chan[0], send_opt, ConstantsValues.isReport);

			Log.i("createSendPort", "XtLog:创建发送端口 send_opt[0] : " + send_opt[0].toString() + " src0["
					+ src_chan[0] + "]");
			Log.i("createSendPort", "XtLog:创建发送端口 send_opt[1] : " + send_opt[1].toString() + " src1["
					+ src_chan[1] + "]");
		}
	}

	public static int[] getSrc_chan() {
		return src_chan;
	}

	public static int[] getSendPort() {
		return new int[] { send_opt[0].rtp_send_port, send_opt[1].rtp_send_port };
	}

	public static String[] getSendTrackName() {
		return new String[] { send_opt[0].trackname, send_opt[1].trackname };
	}

	public static int[] getSendTrackId() {
		return new int[] { send_opt[0].trackid, send_opt[1].trackid };
	}

	public static void destroysendPort() {
		GlRenderNative.mediaServerDestroySrc(src_chan[0]);
		src_chan[0] = -2;
	}
}