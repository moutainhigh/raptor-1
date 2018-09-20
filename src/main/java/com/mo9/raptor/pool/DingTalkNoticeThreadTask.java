package com.mo9.raptor.pool;

import com.mo9.raptor.service.DingTalkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ycheng
 */
@Component
public class DingTalkNoticeThreadTask implements ThreadPoolTask {

    private static final Logger logger = LoggerFactory.getLogger(DingTalkNoticeThreadTask.class);

    private String title;

    private String message;

    @Resource
    private DingTalkService dingTalkService;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            dingTalkService.sendNotice(title,message);
        } catch (Exception e) {
            logger.error("dingtalk notice error", e);
        }
    }
}
