package com.hejun.addresslist.util;

import com.hejun.addresslist.bean.UserBean;
import java.util.Comparator;

public class PinyinComparator implements Comparator<UserBean> {

	public int compare(UserBean o1, UserBean o2) {
		if (o1.getFirstName().equals("@") || o2.getFirstName().equals("#")) {
			return -1;
		} else if (o1.getFirstName().equals("#")
				|| o2.getFirstName().equals("@")) {
			return 1;
		} else {
			return o1.getFirstName().compareTo(o2.getFirstName());
		}
	}

}
