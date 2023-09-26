package com.resolver;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.annotation.CurrentUserAnno;
import com.entity.User;
import com.mico.app.common.util.HttpUtil;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
                return Objects.equals(parameter.getParameterType(), User.class)
                                && parameter.hasParameterAnnotation(CurrentUserAnno.class);
        }

        @Override
        public Object resolveArgument(
                        MethodParameter parameter,
                        ModelAndViewContainer mavContainer,
                        NativeWebRequest webRequest,
                        WebDataBinderFactory binderFactory) throws Exception {

                return HttpUtil.getRequest()
                                .map(o -> User.builder()
                                                .name(o.getHeader("x-name"))
                                                .age(Integer.valueOf(o.getHeader("x-age")))
                                                .build())
                                .orElse(User.builder().build());
        }

}
