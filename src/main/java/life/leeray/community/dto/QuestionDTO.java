package life.leeray.community.dto;

import life.leeray.community.model.Question;
import life.leeray.community.model.User;
import lombok.Data;

import java.io.Serializable;

/**
 * @author leeray
 * @version 1.0
 * @date 2019/7/25 0025 10:35
 */
@Data
public class QuestionDTO implements Serializable {
    private Question question;
    private User user;
}
