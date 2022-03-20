package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.model.contact.Contact;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter

@Entity
public class VerificationToken {
    private static final long EXPIRATION = 24*60*60*1000;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Contact.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "email")
    private Contact contact;

    private long expiryTimeMillis;

    public VerificationToken() {
        this.expiryTimeMillis = calculateExpiryMillis(EXPIRATION);
    }

    public VerificationToken(String token, Contact contact) {
        this.token = token;
        this.contact = contact;
        this.expiryTimeMillis = calculateExpiryMillis(EXPIRATION);
    }

    private long calculateExpiryMillis(long expiryTimeLengthMillis) {
        return System.currentTimeMillis() + expiryTimeLengthMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expiryDate=" + expiryTimeMillis +
                '}';
    }
}


