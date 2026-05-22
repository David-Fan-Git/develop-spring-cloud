package com.develop.mvp.pk.module.system.domain.notify;
// DDD 角色：站内信消息聚合根
import java.time.LocalDateTime; import java.util.Objects;
public final class NotifyMessage {
    private final Long id; private Long userId; private Integer userType, readStatus; private Long templateId; private String templateCode, templateType, templateContent, templateNickname; private Integer templateParams; private LocalDateTime readTime;
    private NotifyMessage(Long id) { this.id = Objects.requireNonNull(id); }
    public static NotifyMessage of(Long id) { return new NotifyMessage(id); }
    public Long id() { return id; } public Long userId() { return userId; } public Integer userType() { return userType; }
    public Integer readStatus() { return readStatus; } public Long templateId() { return templateId; } public String templateCode() { return templateCode; }
    public String templateContent() { return templateContent; } public String templateNickname() { return templateNickname; }
    public NotifyMessage userId(Long v) { userId = v; return this; } public NotifyMessage userType(Integer v) { userType = v; return this; }
    public NotifyMessage readStatus(Integer v) { readStatus = v; return this; } public NotifyMessage templateId(Long v) { templateId = v; return this; }
    public NotifyMessage templateCode(String v) { templateCode = v; return this; } public NotifyMessage templateType(String v) { templateType = v; return this; }
    public NotifyMessage templateContent(String v) { templateContent = v; return this; } public NotifyMessage templateNickname(String v) { templateNickname = v; return this; }
    public NotifyMessage templateParams(Integer v) { templateParams = v; return this; } public NotifyMessage readTime(LocalDateTime v) { readTime = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof NotifyMessage m && id.equals(m.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
