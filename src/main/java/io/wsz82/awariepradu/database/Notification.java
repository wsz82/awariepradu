package io.wsz82.awariepradu.database;

import javax.persistence.*;
import java.util.Objects;

@Entity
@IdClass(NotificationKey.class)
public class Notification {
        @Id
        @Column
        public String regionName;

        @Id
        @Column
        public String areaName;

        @Column(columnDefinition="text")
        public String areaMessage;

        @Column
        public String warningPeriod;

        public Notification() {}

        public Notification(String regionName, String areaName, String areaMessage, String warningPeriod) {
                this.regionName = regionName;
                this.areaName = areaName;
                this.areaMessage = areaMessage;
                this.warningPeriod = warningPeriod;
        }

        public boolean isTheSameArea(Notification that) {
                return Objects.equals(regionName, that.regionName) && Objects.equals(areaName, that.areaName);
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Notification that = (Notification) o;
                return Objects.equals(regionName, that.regionName) && Objects.equals(areaName, that.areaName) && Objects.equals(areaMessage, that.areaMessage) && Objects.equals(warningPeriod, that.warningPeriod);
        }

        @Override
        public int hashCode() {
                return Objects.hash(regionName, areaName, areaMessage, warningPeriod);
        }

        @Override
        public String toString() {
                return "AreaNotification{" +
                        "regionName='" + regionName + '\'' +
                        ", areaName='" + areaName + '\'' +
                        ", areaMessage='" + areaMessage + '\'' +
                        ", warningPeriod='" + warningPeriod + '\'' +
                        '}';
        }
}
