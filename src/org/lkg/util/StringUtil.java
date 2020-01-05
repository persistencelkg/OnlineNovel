package org.lkg.util;

import java.util.Objects;

/**
 * 检测字符串是否为空
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月5日 下午1:40:20
 * @CopyRight lkg.nb.com
 */
public class StringUtil {
	public static boolean isEmpty(String input) {
		if(Objects.isNull(input)||input.length()==0) return true;
		return false;
	}
}
