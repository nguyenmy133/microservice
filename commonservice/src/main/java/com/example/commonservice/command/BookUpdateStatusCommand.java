package com.example.commonservice.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateStatusCommand {

    @TargetAggregateIdentifier
    private String bookId;
    private  Boolean isReady;
    private String employeeId;
    private String borrowingId;

}
