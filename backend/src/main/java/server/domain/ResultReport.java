package server.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ResultReport")
@Data
public class ResultReport {

    @Id
    private Integer proposalId;

    private Integer amount;

    private Date creationDate;

}
