package com.xt.mobile.terminal.util;


import com.xt.mobile.terminal.domain.SipInfo;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<SipInfo> {

	public int compare(SipInfo o1, SipInfo o2) {
		return  o1.getUsername().compareTo(o2.getUsername());
	}

}
