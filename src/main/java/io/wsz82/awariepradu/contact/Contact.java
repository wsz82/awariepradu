package io.wsz82.awariepradu.contact;

import lombok.*;
import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Pattern(regexp = "48\\d{9}", message = "{Contact.PhoneNumber.Pattern}")
    private String phoneNumber;

    @NotBlank(message = "{Contact.Region.NotBlank}")
    private String region;

    @NotBlank(message = "{Contact.Location.NotBlank}")
    private String location;

    @NonNull
    private Boolean isCity;

}
