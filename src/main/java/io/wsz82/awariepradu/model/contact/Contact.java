package io.wsz82.awariepradu.model.contact;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Entity

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contact {
    @Id
    @Email(message = "{Contact.Email}")
    @NotBlank(message = "{Contact.Email.Add}")
    private String email;

    @Column(name = "phone_number")
    @Pattern(regexp = "(^$|48\\d{9})", message = "{Contact.PhoneNumber.Pattern}")
    private String phoneNumber;

    @NotBlank(message = "{Contact.Region.NotBlank}")
    private String region;

    @NotBlank(message = "{Contact.Location.NotBlank}")
    private String location;

    @NonNull
    private Boolean isCity;

    @NonNull
    private Integer subscriptionType;

    public SubscriptionType parsedSubscriptionType() {
        return SubscriptionType.values()[subscriptionType];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(phoneNumber, contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

}
