package com.vladimirpandurov.invoiceManager01B.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;

public class SmsUtils {
    public static final String FROM_NUMBER = "+13612824006";
    public static final String SID_KEY = "AC6772183c93d2d62dfacc96c1aa8cb517";
    public static final String TOKEN_KEY = "179292ceaf40e6dd5c7b62299c0efbe3";

    public static void sendSMS(String to, String messageBody) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message message = creator(new PhoneNumber("+381" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }
}
