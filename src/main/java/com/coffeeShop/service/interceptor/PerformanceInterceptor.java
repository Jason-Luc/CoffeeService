package com.coffeeShop.service.interceptor;

import com.coffeeShop.service.support.RedisMockRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coffeeShop.service.support.ConstValue.EACH_REQUEST_TIME_COST_REDIS_KEY;
import static com.coffeeShop.service.support.ConstValue.TOTAL_TIME_COST_REDIS_KEY;

@Slf4j
public class PerformanceInterceptor implements HandlerInterceptor {
    @Autowired
    RedisMockRepository redisMockRepository;
    private StopWatch eachRequestStopWatch = new StopWatch();
    private StopWatch totalStopWatch = new StopWatch();
    Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (redisMockRepository == null) {
            logger.error("PerformanceInterceptor -  redisMockRepository is null");
            throw new RuntimeException("can not autowire redisMockRepository");
        }

        StopWatch sw = eachRequestStopWatch;
        sw.start();
        totalStopWatch.start();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        StopWatch sw = eachRequestStopWatch;
        sw.stop();
        totalStopWatch.stop();
        String method = handler.getClass().getSimpleName();
        if (handler instanceof HandlerMethod) {
            String beanType = ((HandlerMethod) handler).getBeanType().getName();
            String methodName = ((HandlerMethod) handler).getMethod().getName();
            method = beanType + "." + methodName;
        }
        log.info("{};{};{};{};{}ms;{}ms", request.getRequestURI(), method,
                response.getStatus(), ex == null ? "-" : ex.getClass().getSimpleName(),
                sw.getLastTaskTimeMillis());

        updateEachRequestTimeCost(request.getRequestURI(), sw);
        updateTotalTimeCost();
    }

    private void updateEachRequestTimeCost(String requestURI, StopWatch sw) {
        List<Map<String, Long>> existingEachRequestTimeCostList = (List) (redisMockRepository.mockRepository.get(EACH_REQUEST_TIME_COST_REDIS_KEY));
        if (existingEachRequestTimeCostList == null) {
            existingEachRequestTimeCostList = new ArrayList<>();
        }
        Map eachRequestTimeCostMap = new HashMap();
        eachRequestTimeCostMap.put(requestURI, sw.getTotalTimeMillis());
        existingEachRequestTimeCostList.add(eachRequestTimeCostMap);
        redisMockRepository.mockRepository.put(EACH_REQUEST_TIME_COST_REDIS_KEY, existingEachRequestTimeCostList);
    }

    private void updateTotalTimeCost() {
        redisMockRepository.mockRepository.put(TOTAL_TIME_COST_REDIS_KEY, Long.valueOf(totalStopWatch.getTotalTimeMillis()));
    }

}

