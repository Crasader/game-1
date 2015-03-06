package com.common.util;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class MathUtil {

	private static Random random = new Random();

	/**
	 * 
	 * @param min
	 * @param max
	 * @return min~max的随机数
	 */
	public static int getRandom(int min, int max) {

		if (min == max)
			return min;
		if (min > max) {
			int temp = min;
			min = max;
			max = temp;
		}

		return Math.abs(random.nextInt()) % (max - min + 1) + min;

	}

	/**
	 * 输入一个几率列表，返回一个随机值
	 */
	public static int getRandIndexByList(List<Double> list) {
		int all = 0;
		for (Double i : list) {
			all += i;
		}
		int rand = getRandom(0, all);

		int add = 0;
		for (int i = 0; i < list.size(); i++) {
			add += list.get(i);
			if (rand <= add)
				return i;
		}
		return 0;
	}

	public static int getNextUid() {

		Pattern p = Pattern.compile("([\\d])\\1{1,}");

		while (true) {
			Integer uid = getRandom(100000, 999999);

			if (p.matcher(uid.toString()).find())
				continue;
			return uid;
		}

	}

}
