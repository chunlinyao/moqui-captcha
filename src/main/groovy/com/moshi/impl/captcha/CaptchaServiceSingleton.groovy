package com.moshi.impl.captcha

import com.octo.captcha.engine.image.fisheye.SimpleFishEyeEngine
import com.octo.captcha.engine.image.gimpy.BaffleListGimpyEngine
import com.octo.captcha.engine.image.gimpy.DeformedBaffleListGimpyEngine
import com.octo.captcha.engine.image.gimpy.HotmailEngine
import com.octo.captcha.engine.image.gimpy.HotmailEngine2008
import com.octo.captcha.engine.image.gimpy.NonLinearTextGimpyEngine
import com.octo.captcha.service.CaptchaServiceException
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService
import com.octo.captcha.service.image.ImageCaptchaService
import org.moqui.context.ExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CaptchaServiceSingleton {
    protected final static Logger logger = LoggerFactory.getLogger(CaptchaServiceSingleton.class)

    private static ImageCaptchaService instance;
    static {
        instance = new DefaultManageableImageCaptchaService();
        instance.setCaptchaEngine(new MyEngine());
    }

    static ImageCaptchaService getInstance() {
        return instance
    }

    static boolean validateResponseForID(ExecutionContext ec, String response) {
        boolean isResponseCorrect = false
        String captchaId = ec.web.session.getId()
        try {
            isResponseCorrect = getInstance().validateResponseForID(captchaId, response)
        } catch (CaptchaServiceException e) {
            //should not happen, may be thrown if the id is not valid
            logger.warn("CaptchaServiceException: ${e}")
        }
        return isResponseCorrect
    }

}
