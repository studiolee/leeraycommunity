package life.leeray.community.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leeray
 * @version 1.0
 * @date 2019/8/3 0003 10:34
 */
@Data
public class FileDTO implements Serializable {
    private int success;
    private String message;
    private String url;
}
