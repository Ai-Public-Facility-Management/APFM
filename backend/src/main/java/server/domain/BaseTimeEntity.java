// [기능 요약] 모든 엔티티에서 생성/수정/삭제(소프트) 시각을 공통으로 관리
package server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Getter
public abstract class BaseTimeEntity {

    @CreationTimestamp
    @Column(updatable = false)
    protected Instant createdAt;

    @UpdateTimestamp
    protected Instant updatedAt;

    // 소프트 삭제(널이면 활성 상태)
    protected Instant deletedAt;

    public boolean isDeleted() { return deletedAt != null; }

    public void softDelete() { this.deletedAt = Instant.now(); }

    public void restore() { this.deletedAt = null; }
}
