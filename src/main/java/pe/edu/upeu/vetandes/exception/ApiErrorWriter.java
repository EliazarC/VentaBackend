package pe.edu.upeu.vetandes.exception;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public final class ApiErrorWriter {
    private ApiErrorWriter() { }

    public static Map<String, Object> body(HttpStatus status, String message, String path, Map<String, String> fields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", message);
        body.put("ruta", path);
        if (fields != null && !fields.isEmpty()) body.put("campos", fields);
        return body;
    }

    public static void write(HttpServletResponse response, HttpStatus status,
                             String message, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        String timestamp = LocalDateTime.now().toString();
        String json = "{\"timestamp\":\"" + escape(timestamp) + "\","
                + "\"status\":" + status.value() + ","
                + "\"error\":\"" + escape(status.getReasonPhrase()) + "\","
                + "\"mensaje\":\"" + escape(message) + "\","
                + "\"ruta\":\"" + escape(path) + "\"}";
        response.getWriter().write(json);
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
