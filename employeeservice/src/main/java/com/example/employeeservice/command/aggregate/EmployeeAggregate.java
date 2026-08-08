package com.example.employeeservice.command.aggregate;

import com.example.employeeservice.command.command.CreateEmployeeCommand;
import com.example.employeeservice.command.command.DeleteEmployeeCommand;
import com.example.employeeservice.command.command.UpdateEmployeeCommand;
import com.example.employeeservice.command.event.EmployeeCreatedEvent;
import com.example.employeeservice.command.event.EmployeeDeleteEvent;
import com.example.employeeservice.command.event.EmployeeUpdateEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@Aggregate
public class EmployeeAggregate {

    @AggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String kin;
    private Boolean isDescription;

    @CommandHandler
    public EmployeeAggregate(CreateEmployeeCommand command){
        EmployeeCreatedEvent event=new EmployeeCreatedEvent();
        BeanUtils.copyProperties(command,event);
        AggregateLifecycle.apply(event);

    }
    @EventSourcingHandler
    public void on(EmployeeCreatedEvent event){
        this.id=event.getId();
        this.lastName=event.getLastName();
        this.firstName=event.getFirstName();
        this.kin=event.getKin();
        this.isDescription=event.getIsDescription();
    }

    @CommandHandler
    public void handle(UpdateEmployeeCommand command){
        EmployeeUpdateEvent event=new EmployeeUpdateEvent();
        BeanUtils.copyProperties(command,event);
        AggregateLifecycle.apply(event);
    }
    @EventSourcingHandler
    public void on(EmployeeUpdateEvent event){
        this.id=event.getId();
        this.lastName= event.getLastName();
        this.firstName= event.getFirstName();
        this.kin= event.getKin();
        this.isDescription=event.getIsDescription();
    }

    @CommandHandler
    public void handle(DeleteEmployeeCommand command){
        EmployeeDeleteEvent event=new EmployeeDeleteEvent();
        BeanUtils.copyProperties(command,event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on (EmployeeDeleteEvent event){
        this.id=event.getId();
        AggregateLifecycle.markDeleted();
    }
}
