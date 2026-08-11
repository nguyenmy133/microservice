package com.example.borrowservice.command.event;

import com.example.borrowservice.command.data.Borrowing;
import com.example.borrowservice.command.data.BorrowingRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BorrowingEventHandler {
    @Autowired
    private BorrowingRepository borrowingRepository;

    @EventHandler
    public void on(BorrowingCreatedEvent event){
        Borrowing model=new Borrowing();
        model.setId(event.getId());
        model.setBorrowingDate(event.getBorrowingDate());
        model.setBookId(event.getBookId());
        model.setEmployeeId(event.getEmployeeId());
        borrowingRepository.save(model);
    }

    @EventHandler
    public void on(BorrowingDeleteEvent event){
        Optional<Borrowing> oldEntity=borrowingRepository.findById(event.getId());
        oldEntity.ifPresent(borrowing -> borrowingRepository.delete(borrowing));
    }
}
