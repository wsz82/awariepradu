package io.wsz82.awariepradu.database;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

@EqualsAndHashCode
public class NotificationKey implements Serializable {
    public String regionName;
    public String areaName;
}
