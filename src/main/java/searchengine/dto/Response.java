package searchengine.dto;

import lombok.Data;

@Data
public class Response {
    private boolean result;

    private String message;

    private int count;

    private Object data;
}

