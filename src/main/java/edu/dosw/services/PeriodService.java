package edu.dosw.services;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PeriodService {
    private static  final int SUP_LIMIT_FIRST_PERIOD =  5;
    private static final int SUP_LIMIT_INTER_PERIOD = 7;

    public static String getPeriod() {
        int month = LocalDateTime.now().getMonthValue();
        if (month <= SUP_LIMIT_FIRST_PERIOD){
            return "1";
        }else if ( month <= SUP_LIMIT_INTER_PERIOD){
            return "I";
        }else{
            return "2";
        }
    }

    public static String getYear() {
        return String.valueOf(LocalDateTime.now().getYear());
    }
}
