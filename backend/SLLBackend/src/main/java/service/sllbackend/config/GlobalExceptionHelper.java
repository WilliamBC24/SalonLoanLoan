package service.sllbackend.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHelper {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("exception", ex);
        return mav;
    }
}
