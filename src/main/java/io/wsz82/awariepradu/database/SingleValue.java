package io.wsz82.awariepradu.database;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class SingleValue {
        @Id
        public int id;

        @Column
        public String value;

        public SingleValue() {}

        public SingleValue(int id, String value) {
                this.id = id;
                this.value = value;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                SingleValue that = (SingleValue) o;
                return id == that.id && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, value);
        }

        @Override
        public String toString() {
                return "SingleValue{" +
                        "id=" + id +
                        ", value='" + value + '\'' +
                        '}';
        }
}
