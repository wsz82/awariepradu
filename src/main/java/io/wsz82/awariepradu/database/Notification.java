package io.wsz82.awariepradu.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@IdClass(NotificationKey.class)
public class Notification {
        @Id
        @Column
        @NotBlank(message = "Region cannot be blank")
        private String regionName;

        @Id
        @Column
        @NotBlank(message = "Area cannot be blank")
        private String areaName;

        @Column
        @NotBlank(message = "Area message cannot be blank")
        private String areaMessage;

        @Column
        @NotBlank(message = "Warning period cannot be blank")
        private String warningPeriod;

        public boolean isTheSameArea(Notification that) {
                return Objects.equals(regionName, that.regionName) && Objects.equals(areaName, that.areaName);
        }
}
