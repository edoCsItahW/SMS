package com.sms.enums;


import lombok.Getter;

@Getter
public enum Weekday {
    MONDAY("星期一", "Monday"),
    TUESDAY("星期二", "Tuesday"),
    WEDNESDAY("星期三", "Wednesday"),
    THURSDAY("星期四", "Thursday"),
    FRIDAY("星期五", "Friday"),
    SATURDAY("星期六", "Saturday"),
    SUNDAY("星期日", "Sunday");

    private final String displayName;
    private final String peopertyName;

    Weekday(String displayName, String peopertyName) {
        this.displayName = displayName;
        this.peopertyName = peopertyName;
    }

}
