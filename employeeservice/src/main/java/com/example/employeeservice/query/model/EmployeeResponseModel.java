package com.example.employeeservice.query.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseModel {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String kin;
    private Boolean isDescription;

}
