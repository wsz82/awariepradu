package io.wsz82.awariepradu.model.notification;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class NotificationKey implements Serializable {
    public String regionName;
    public String areaName;
}
