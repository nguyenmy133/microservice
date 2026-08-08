package com.example.employeeservice.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEmployeeModel {
    @NotBlank(message = "Firstname is mandatory")
    private String firstName;

    @NotBlank(message = "Lastname is mandatory")
    private String lastName;

    @NotBlank(message = "Kin is mandatory")
    private String kin;

    public CreateEmployeeModel(String string, @NotBlank(message = "Firstname is mandatory") String firstName, @NotBlank(message = "Lastname is mandatory") String lastName, @NotBlank(message = "Kin is mandatory") String kin, boolean b) {
    }
}
