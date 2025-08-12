package server.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "inspection")
@Data
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_date", nullable = false)
    private Date createDate;


    // ✅ 연관관계 추가
    @OneToMany(mappedBy = "inspection",fetch = FetchType.LAZY)
    private List<Issue> issues;

    @OneToOne(mappedBy = "inspection", cascade = CascadeType.ALL)
    private Report report;

}
