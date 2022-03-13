package io.wsz82.awariepradu.model.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUnsubscribe {

    @Email(message = "{Contact.Email}")
    @NotBlank(message = "{Contact.Email.Add}")
    private String email;

}
