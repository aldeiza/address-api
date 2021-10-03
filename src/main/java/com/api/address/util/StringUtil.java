package com.api.address.util;

import java.text.Normalizer;

public class StringUtil {

	public static String removerAcentosUpperCase(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();
	}

	public static String removerHifen(String cep) {
		return cep.replace("-", "");
	}
}
