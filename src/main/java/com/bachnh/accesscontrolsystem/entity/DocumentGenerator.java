package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "document_generator")
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class DocumentGenerator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "custodycd")
    private String employeeCode;

    @Column(name = "request_log", length = 2000)
    private String requestLog;

    @Column(name = "path_file", length = 100)
    private String pathFile;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "storage", length = 100)
    private String storage;

    @Column(name = "template", length = 100)
    private String template;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "object_url")
    private String objectUrl;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;
    @Column(name = "version_key")
    private String versionKey;


    public enum DocumentSyncStatus {
        ACTIVE, INACTIVE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DocumentGenerator that = (DocumentGenerator) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
