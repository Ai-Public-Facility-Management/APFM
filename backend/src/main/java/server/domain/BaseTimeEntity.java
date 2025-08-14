// [기능 요약] 모든 엔티티에서 생성/수정/삭제(소프트) 시각을 공통으로 관리
package server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseTimeEntity {

    @CreationTimestamp
    @Column(updatable = false)
    protected Instant createdAt;

    @UpdateTimestamp
    protected Instant updatedAt;

    // 소프트 삭제(널이면 활성 상태)
    protected Instant deletedAt;

    // ===== 작성자/수정자 공통 필드 =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_email")
    protected Users createdBy; // 생성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_email")
    protected Users updatedBy; // 마지막 수정자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by_email")
    protected Users deletedBy;

    public boolean isDeleted() { return deletedAt != null; }

    public void softDelete(Users user) {
        this.deletedAt = Instant.now();
        this.deletedBy = user;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
