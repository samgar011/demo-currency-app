package com.currency.demo_app.util;

import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

public class KeyUtil {

    public static String filter(CurrencyConversionFilterRequestDTO request) {
        if (request.getTransactionId() != null) {
            return "t-" + request.getTransactionId() + ":" + request.getPage() + ":" + request.getSize();
        }
        return "d-" + request.getDate() + ":" + request.getPage() + ":" + request.getSize();
    }

    public static String bulk(MultipartFile file, boolean useExternal) {
        return "f-" + DigestUtils.md5Hex(getBytesSafely(file)) + "-" + useExternal;
    }

    private static byte[] getBytesSafely(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}