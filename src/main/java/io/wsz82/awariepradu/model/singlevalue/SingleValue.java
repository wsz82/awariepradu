package io.wsz82.awariepradu.model.singlevalue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
