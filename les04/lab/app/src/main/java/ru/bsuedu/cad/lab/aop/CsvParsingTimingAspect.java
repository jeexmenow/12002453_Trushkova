package ru.bsuedu.cad.lab.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CsvParsingTimingAspect {

    @Around("execution(* ru.bsuedu.cad.lab.impl.CSVParser.parse(..))")
    public Object logParseDuration(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNs = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long ms = (System.nanoTime() - startNs) / 1_000_000L;
            System.out.println("[AOP] Парсинг CSV (CSVParser.parse): " + ms + " мс");
        }
    }
}
