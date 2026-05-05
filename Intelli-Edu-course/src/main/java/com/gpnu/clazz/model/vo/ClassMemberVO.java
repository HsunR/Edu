package com.gpnu.clazz.model.vo;

import com.gpnu.clazz.model.enums.MemberStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class ClassMemberVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long studentId;

    private String studentName;

    private String avatarUrl;

    private MemberStatus status;

    private OffsetDateTime joinedAt;

}
