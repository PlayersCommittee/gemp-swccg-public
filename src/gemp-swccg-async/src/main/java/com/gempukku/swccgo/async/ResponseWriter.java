package com.gempukku.swccgo.async;

import org.w3c.dom.Document;

import java.io.File;
import java.util.Map;

public interface ResponseWriter {
    void writeError(int status);
    void writeError(int status, Map<String, String> headers);

    void writeFile(File file, Map<String, String> headers);

    void writeHtmlResponse(String html);

    void writeJsonResponse(String json);

    void writeByteResponse(byte[] bytes, Map<? extends CharSequence, String> headers);

    void writeXmlResponse(Document document);

    void writeXmlResponse(Document document, Map<? extends CharSequence, String> addHeaders);
}
