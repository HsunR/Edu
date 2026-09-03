-- Local development accounts only. This migration is enabled by the Docker Nacos configuration.
-- All three accounts use the password: Test@123

INSERT INTO public.us_user (
    user_id,
    name,
    password,
    user_type,
    email,
    sex,
    school,
    personal_signature,
    status,
    version,
    is_deleted
)
VALUES
    (
        900000000000001,
        'student',
        '$2a$10$I.UBDrdrCMvpYNLw27gh1uhPVhjJqd55lUodDPoIk0YXFMQMTk.K.',
        1,
        'student@intelli-edu.local',
        0,
        'Intelli-Edu Test School',
        'Local student test account',
        1,
        0,
        0
    ),
    (
        900000000000002,
        'teacher',
        '$2a$10$I.UBDrdrCMvpYNLw27gh1uhPVhjJqd55lUodDPoIk0YXFMQMTk.K.',
        2,
        'teacher@intelli-edu.local',
        0,
        'Intelli-Edu Test School',
        'Local teacher test account',
        1,
        0,
        0
    ),
    (
        900000000000003,
        'admin',
        '$2a$10$I.UBDrdrCMvpYNLw27gh1uhPVhjJqd55lUodDPoIk0YXFMQMTk.K.',
        3,
        'admin@intelli-edu.local',
        0,
        'Intelli-Edu Test School',
        'Local administrator test account',
        1,
        0,
        0
    );

INSERT INTO public.us_student_profile (
    user_id,
    student_no,
    grade,
    major,
    enrollment_year
)
VALUES (
    900000000000001,
    'TEST20260001',
    '2026',
    'Computer Science',
    2026
);

INSERT INTO public.us_teacher_profile (
    user_id,
    teacher_no,
    title,
    department,
    bio
)
VALUES (
    900000000000002,
    'TEACHER2026001',
    'Test Lecturer',
    'Computer Science',
    'Local teacher test profile'
);
