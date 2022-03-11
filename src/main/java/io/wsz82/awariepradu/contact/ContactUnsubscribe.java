package io.wsz82.awariepradu.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUnsubscribe {

    @Pattern(regexp = "48\\d{9}", message = "{Contact.PhoneNumber.Pattern}")
    private String phoneNumber;

}
