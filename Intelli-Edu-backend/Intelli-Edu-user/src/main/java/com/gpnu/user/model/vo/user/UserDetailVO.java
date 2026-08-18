package com.gpnu.user.model.vo.user;

import com.gpnu.user.model.enums.UserStatus;
import lombok.Data;

@Data
public class UserDetailVO extends UserVO {
    private String email;
    private String mobile;
    private UserStatus status;

    // 学生档案（user_type=1 时填充）
    private StudentProfileVO studentProfile;

    // 教师档案（user_type=2 时填充）
    private TeacherProfileVO teacherProfile;


    /** 手机号脱敏：138****5678 */
    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() != 11) return mobile;
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    /** 邮箱脱敏：t***@gmail.com */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
