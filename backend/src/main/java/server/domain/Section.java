package server.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@NoArgsConstructor
@Data
public class Section {
    private double xCenter;
    private double yCenter;
    private double width;
    private double height;

    public Section(List<Integer> box){
        this.width = box.get(2) - box.get(0);
        this.height = box.get(3) - box.get(1);
        this.xCenter = box.get(0) + width / 2.0;
        this.yCenter = box.get(1) + height / 2.0;
    }

}
