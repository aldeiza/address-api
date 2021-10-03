package com.api.address.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class ArquivoUtil {

	public static byte[] baixarPorPath(String path, String nome) {
		try {
			InputStream in = new URL(path).openStream();
			return IOUtils.toByteArray(in);
		} catch (IOException e) {
			return new byte[]{};
		}
	}
}
