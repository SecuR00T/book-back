-- [CTF Lab] 공지사항 파일 첨부 지원: 확장자 검증 없이 모든 파일 타입 허용 (웹쉘 업로드 취약점 실습)
ALTER TABLE notices
    ADD COLUMN attachment_name VARCHAR(255) NULL,
    ADD COLUMN attachment_url  VARCHAR(500) NULL;
