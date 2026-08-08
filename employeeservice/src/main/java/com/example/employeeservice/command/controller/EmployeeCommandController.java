package com.example.employeeservice.command.controller;

import com.example.employeeservice.command.command.CreateEmployeeCommand;
import com.example.employeeservice.command.command.DeleteEmployeeCommand;
import com.example.employeeservice.command.command.UpdateEmployeeCommand;
import com.example.employeeservice.command.model.CreateEmployeeModel;
import com.example.employeeservice.command.model.DeleteEmployeeModel;
import com.example.employeeservice.command.model.UpdateEmployeeModel;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/employees")
public class EmployeeCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addEmployee(@Valid @RequestBody CreateEmployeeModel model){
        CreateEmployeeCommand command= new CreateEmployeeCommand(UUID.randomUUID().toString(), model.getFirstName(), model.getLastName(), model.getKin(),false);
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{employeeId}")
    public String updateEmployee(@Valid @RequestBody UpdateEmployeeModel model, @PathVariable String employeeId) {
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(employeeId, model.getFirstName(), model.getLastName(), model.getKin(), model.getIsDescription());
        return commandGateway.sendAndWait(command);

    }
    @DeleteMapping("/{employeeId}")
    public String deleteEmployee(@PathVariable String employeeId){
        DeleteEmployeeCommand command=new DeleteEmployeeCommand(employeeId);
        return commandGateway.sendAndWait(command);

    }
}
