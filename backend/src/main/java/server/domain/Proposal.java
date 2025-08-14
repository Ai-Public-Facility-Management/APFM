package server.domain;

import jakarta.persistence.*;
import lombok.Data;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Proposal_table")
@Data
public class Proposal{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileUrl;

    @ManyToMany
    @JoinTable(
            name = "proposal_issue",
            joinColumns = @JoinColumn(name = "proposal_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id")
    )
    private Set<Issue> issues = new HashSet<>();
}
