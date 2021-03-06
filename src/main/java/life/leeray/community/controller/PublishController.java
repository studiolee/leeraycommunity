package life.leeray.community.controller;

import life.leeray.community.cache.TagCache;
import life.leeray.community.dto.QuestionDTO;
import life.leeray.community.mapper.QuestionMapper;
import life.leeray.community.model.Question;
import life.leeray.community.model.User;
import life.leeray.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author leeray
 * @version 1.0
 * @date 2019/7/24 0024 15:17
 */
@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    /**
     * post方式发布问题
     *
     * @param request
     * @param model
     * @param id
     * @return
     */
    @PostMapping("/publish")
    public String doPublish(HttpServletRequest request,
                            Model model,
                            @RequestParam(value = "id", required = false) Long id) {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String tag = request.getParameter("tag");

        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);

        if (title == null || title.equals("")) {
            model.addAttribute("error", "标题不能为空！");
            return "publish";
        }
        if (description == null || description.equals("")) {
            model.addAttribute("error", "补充内容不能为空！");
            return "publish";
        }
        if (tag == null || tag.equals("")) {
            model.addAttribute("error", "标签不能为空！");
            return "publish";
        }
        String invalidTag = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(invalidTag)) {
            model.addAttribute("error", "包含不支持的标签：" + invalidTag);
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登录，无权发表主题");
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

    /**
     * 用于跳转到修改问题页面
     *
     * @param id
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        if (id != null) {
            Question dbQuestion = questionMapper.selectByPrimaryKey(id);
            if (user.getId() == dbQuestion.getCreator()) {
                QuestionDTO question = questionService.getById(id);
                model.addAttribute("title", question.getQuestion().getTitle());
                model.addAttribute("description", question.getQuestion().getDescription());
                model.addAttribute("tag", question.getQuestion().getTag());
                model.addAttribute("id", question.getQuestion().getId());
                model.addAttribute("tags", TagCache.get());
            } else {
                return "redirect:/";
            }
        }
        return "publish";
    }
}
