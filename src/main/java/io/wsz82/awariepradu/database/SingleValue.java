package io.wsz82.awariepradu.database;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class SingleValue {
        @Id
        public int id;

        @Column
        public String value;
}
