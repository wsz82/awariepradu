package io.wsz82.awariepradu.database;

import java.io.Serializable;
import java.util.Objects;

public class NotificationKey implements Serializable {
    public String regionName;
    public String areaName;

    public NotificationKey() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationKey that = (NotificationKey) o;
        return Objects.equals(regionName, that.regionName) && Objects.equals(areaName, that.areaName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionName, areaName);
    }
}
