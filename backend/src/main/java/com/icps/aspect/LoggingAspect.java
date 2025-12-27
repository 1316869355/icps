package com.icps.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

/**
 * 日志切面类，通过@Log注解控制方法调用的日志记录
 * 
 * <h3>使用方式：</h3>
 * <p>
 * <ol>
 * <li>在方法或类上添加{@link com.icps.aspect.Log}注解</li>
 * <li>通过注解属性控制日志打印行为：
 *    <ul>
 *    <li>value: 日志描述</li>
 *    <li>printParams: 是否打印请求参数（默认：true）</li>
 *    <li>printResult: 是否打印返回结果（默认：true）</li>
 *    <li>printExecutionTime: 是否打印执行时间（默认：true）</li>
 *    </ul>
 * </li>
 * </ol>
 * </p>
 * <h3>示例：</h3>
 * <pre>
 * {@code
 * @Log(value = "用户登录接口", printParams = true, printResult = true, printExecutionTime = true)
 * public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
 *     // 方法实现
 * }
 * }
 * </pre>
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 定义切入点，拦截带有@Log注解的方法
     */
    @Pointcut("@annotation(com.icps.aspect.Log) || @within(com.icps.aspect.Log)")
    public void logPointcut() {
        // 切入点定义，无需实现
    }

    /**
     * 方法调用前执行，记录请求信息
     */
    @Before("logPointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // 获取方法上的@Log注解
        Log logAnnotation = getLogAnnotation(joinPoint);
        
        logger.info("[开始调用] {}.{}()", className, methodName);
        
        // 根据注解属性决定是否打印请求参数
        if (logAnnotation != null && logAnnotation.printParams() && args != null && args.length > 0) {
            logger.info("[请求参数] {}.{}(): {}", className, methodName, args);
        }
    }

    /**
     * 方法调用后执行，记录返回结果
     */
    @AfterReturning(pointcut = "logPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        
        logger.info("[调用结束] {}.{}()", className, methodName);
        
        // 获取方法上的@Log注解
        Log logAnnotation = getLogAnnotation(joinPoint);
        
        // 根据注解属性决定是否打印返回结果
        if (logAnnotation != null && logAnnotation.printResult()) {
            logger.info("[返回结果] {}.{}(): {}", className, methodName, result);
        }
    }

    /**
     * 方法抛出异常时执行，记录异常信息
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        
        logger.error("[调用异常] {}.{}(): {}", className, methodName, exception.getMessage(), exception);
    }

    /**
     * 环绕通知，记录方法执行时间
     */
    @Around("logPointcut()")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        Object result = joinPoint.proceed();
        
        stopWatch.stop();
        
        // 获取方法上的@Log注解
        Log logAnnotation = getLogAnnotation(joinPoint);
        
        // 根据注解属性决定是否打印执行时间
        if (logAnnotation != null && logAnnotation.printExecutionTime()) {
            logger.info("[执行时间] {}.{}(): {} ms", className, methodName, stopWatch.getTotalTimeMillis());
        }
        
        return result;
    }
    
    /**
     * 获取方法或类上的@Log注解
     */
    private Log getLogAnnotation(JoinPoint joinPoint) {
        // 获取方法上的注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        
        if (method.isAnnotationPresent(Log.class)) {
            return method.getAnnotation(Log.class);
        }
        
        // 如果方法上没有，获取类上的注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(Log.class)) {
            return targetClass.getAnnotation(Log.class);
        }
        
        return null;
    }
}