package com.gempukku.swccgo.async;

import com.gempukku.swccgo.async.handler.UriRequestHandler;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SwccgoHttpRequestHandler extends SimpleChannelUpstreamHandler {
    private static final long SIX_MONTHS = 1000L*60L*60L*24L*30L*6L;
    private Logger _log = Logger.getLogger(SwccgoHttpRequestHandler.class);

    private Map<Type, Object> _objects;
    private UriRequestHandler _uriRequestHandler;

    public SwccgoHttpRequestHandler(Map<Type, Object> objects, UriRequestHandler uriRequestHandler) {
        _objects = objects;
        _uriRequestHandler = uriRequestHandler;
    }

    /**
     * Invoked when a message object was received from a remote peer.
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final HttpRequest request = (HttpRequest) e.getMessage();

        if (is100ContinueExpected(request)) {
            send100Continue(e);
        }

        String uri = request.getUri();

        if (uri.contains("?"))
            uri = uri.substring(0, uri.indexOf("?"));

        if (request.isChunked()) {
            send400Error(request, e);
        } else {
            ResponseWriter responseWriter = new ResponseWriter() {
                @Override
                public void writeError(int status) {
                    writeHttpErrorResponse(request, status, null, e);
                }

                @Override
                public void writeError(int status, Map<String, String> headers) {
                    writeHttpErrorResponse(request, status, headers, e);
                }

                @Override
                public void writeXmlResponse(Document document) {
                    writeHttpXmlResponse(request, document, null, e);
                }

                @Override
                public void writeXmlResponse(Document document, Map<String, String> headers) {
                    writeHttpXmlResponse(request, document, headers, e);
                }

                @Override
                public void writeHtmlResponse(String html) {
                    writeHttpHtmlResponse(request, html, e);
                }

                @Override
                public void writeByteResponse(String contentType, byte[] bytes) {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put(CONTENT_TYPE, contentType);
                    writeHttpByteResponse(request, bytes, headers, e);
                }

                @Override
                public void writeFile(File file, Map<String, String> headers) {
                    writeFileResponse(request, file, headers, e);
                }
            };

            try {
                _uriRequestHandler.handleRequest(uri, request, _objects, responseWriter, e);
            } catch (HttpProcessingException exp) {
                responseWriter.writeError(exp.getStatus());
            } catch (Exception exp) {
                _log.error("Error while processing request", exp);
                responseWriter.writeError(500);
            }
        }
    }

    private Map<String, byte[]> _fileCache = Collections.synchronizedMap(new HashMap<String, byte[]>());

    private void writeFileResponse(HttpRequest request, File file, Map<String, String> headers, MessageEvent e) {
        try {
            String canonicalPath = file.getCanonicalPath();
            byte[] fileBytes = _fileCache.get(canonicalPath);
            if (fileBytes == null) {
                if (!file.exists() || !file.isFile()) {
                    writeHttpErrorResponse(request, 404, null, e);
                    return;
                }

                FileInputStream fis = new FileInputStream(file);
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copyLarge(fis, baos);
                    fileBytes = baos.toByteArray();
                    _fileCache.put(canonicalPath, fileBytes);
                } finally {
                    IOUtils.closeQuietly(fis);
                }
            }

                writeHttpByteResponse(request, fileBytes, getHeadersForFile(headers, file), e);
        } catch (IOException exp) {
            writeHttpErrorResponse(request, 500, null, e);
        }
    }

    private Map<String, String> getHeadersForFile(Map<String, String> headers, File file) {
        Map<String, String> fileHeaders = new HashMap<String, String>(headers);

        boolean disableCaching = false;
        boolean cache = false;

        String fileName = file.getName();
        String contentType;
        if (fileName.endsWith(".html")) {
            contentType = "text/html; charset=UTF-8";
        } else if (fileName.endsWith(".js")) {
            contentType = "application/javascript; charset=UTF-8";
        } else if (fileName.endsWith(".css")) {
            contentType = "text/css; charset=UTF-8";
        } else if (fileName.endsWith(".jpg")) {
            cache = true;
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            cache = true;
            contentType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            cache = true;
            contentType = "image/gif";
        } else if (fileName.endsWith(".wav")) {
            cache = true;
            contentType = "audio/wav";
        } else {
            contentType = "application/octet-stream";
        }

        if (disableCaching) {
            fileHeaders.put(CACHE_CONTROL, "no-cache");
            fileHeaders.put(PRAGMA, "no-cache");
            fileHeaders.put(EXPIRES, String.valueOf(-1));
        } else if (cache) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            long sixMonthsFromNow = System.currentTimeMillis()+SIX_MONTHS;
            fileHeaders.put(EXPIRES, dateFormat.format(new Date(sixMonthsFromNow)));
        }

        fileHeaders.put(CONTENT_TYPE, contentType);
        return fileHeaders;
    }

    private void writeHttpErrorResponse(HttpRequest request, int status, Map<String, String> headers, MessageEvent e) {
        boolean keepAlive = isKeepAlive(request);

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(status));

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet())
                response.setHeader(header.getKey(), header.getValue());
        }

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        }

        ChannelFuture future = e.getChannel().write(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void writeHttpByteResponse(HttpRequest request, byte[] bytes, Map<String, String> headers, MessageEvent e) {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        try {
            // Build the response object.
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet())
                    response.setHeader(header.getKey(), header.getValue());
            }

            response.setContent(ChannelBuffers.copiedBuffer(bytes));

            int length = bytes.length;

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.setHeader(CONTENT_LENGTH, length);
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception exp) {
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private void writeHttpXmlResponse(HttpRequest request, Document document, Map<String, String> headers, MessageEvent e) {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        try {
            // Build the response object.
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet())
                    response.setHeader(header.getKey(), header.getValue());
            }

            int length = 0;
            if (document != null) {
                DOMSource domSource = new DOMSource(document);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.transform(domSource, result);

                String responseString = writer.toString();
                response.setContent(ChannelBuffers.copiedBuffer(responseString, CharsetUtil.UTF_8));
                response.setHeader(CONTENT_TYPE, "application/xml; charset=UTF-8");

                length = responseString.length();
            }

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.setHeader(CONTENT_LENGTH, length);
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception exp) {
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

    private void writeHttpHtmlResponse(HttpRequest request, String html, MessageEvent e) {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        try {
            // Build the response object.
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

            response.setContent(ChannelBuffers.copiedBuffer(html, CharsetUtil.UTF_8));
            response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");

            int length = html.length();

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.setHeader(CONTENT_LENGTH, length);
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception exp) {
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));

            if (keepAlive) {
                // Add 'Content-Length' header only for a keep-alive connection.
                response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
            }

            // Write the response.
            ChannelFuture future = e.getChannel().write(response);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private void send400Error(HttpRequest request, MessageEvent e) {
        boolean keepAlive = isKeepAlive(request);

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, BAD_REQUEST);
        ChannelFuture future = e.getChannel().write(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        if (!(e.getCause() instanceof IOException))
            _log.error("Error while processing request", e.getCause());
        e.getChannel().close();
    }
}
