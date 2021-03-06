package life.leeray.community;

import life.leeray.community.dto.QuestionQueryDTO;
import life.leeray.community.mapper.QuestionExtMapper;
import life.leeray.community.mapper.QuestionMapper;
import life.leeray.community.mapper.UserMapper;
import life.leeray.community.model.Question;
import life.leeray.community.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommunityApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    QuestionMapper questionMapper;


    @Value("${spring.datasource.username}")
    String name;
    @Value("${spring.datasource.password}")
    String password;

    @Test
    public void Test() {
        System.out.println(name + " " + password);
    }
//

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void TestCache() {
        long userId = 21;
        User user;
        if (redisTemplate.hasKey("u" + userId)) {
            user = (User) redisTemplate.opsForValue().get("u" + userId);
            System.out.println("redis:" + user.getName());
        } else {
            user = userMapper.selectByPrimaryKey(userId);
            redisTemplate.opsForValue().set("u" + userId, user);
            System.out.println("数据库：" + user.getName());
        }
    }

    @Cacheable(cacheNames = "user", key = "#id")
    public User getUser(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Test
    public void Test11() {
        System.out.println(this.getUser(24L));
    }

    @Autowired
    JavaMailSenderImpl javaMailSender;

    @Test
    public void TT() {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setSubject("通知-这是一个测试程序");
        simpleMessage.setText("测测测！");
        simpleMessage.setTo("1034776984@qq.com");
        simpleMessage.setFrom("1964773741@qq.com");
        javaMailSender.send(simpleMessage);
    }


    @Autowired
    QuestionExtMapper questionExtMapper;

    @Test
    public void TT1() {
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setPage(1);
        questionQueryDTO.setSize(5);
        questionQueryDTO.setTag("JAVA");
        List<Question> questions = questionExtMapper.selectByTag(questionQueryDTO);
        for (Question question : questions) {
            System.out.println(question.getTag());
        }
    }

}
