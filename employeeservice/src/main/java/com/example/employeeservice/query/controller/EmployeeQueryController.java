package com.example.employeeservice.query.controller;

import com.example.employeeservice.query.model.EmployeeResponseModel;
import com.example.employeeservice.query.queries.GetAllEmployeeQuery;
import com.example.employeeservice.query.queries.GetDetailEmployeeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
//config swagger
@Tag(name="Employee Query")
@Slf4j
public class EmployeeQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @Operation(
            summary = "Get list employee ",
            description = "Get endpoint for employee with filter",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized/ invalid token"
                    )
            }
    )
    @GetMapping
    public List<EmployeeResponseModel> getAllEmployee(@RequestParam(required = false,defaultValue = "false") Boolean isDescription){
        log.info("Calling to getAllEmployee");
        return queryGateway
                .query(new GetAllEmployeeQuery(isDescription), ResponseTypes.multipleInstancesOf(EmployeeResponseModel.class)).join();
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponseModel getDetailEmployeeResponseModel(@PathVariable("employeeId") String id){
        return queryGateway
                .query(new GetDetailEmployeeQuery(id), ResponseTypes.instanceOf(EmployeeResponseModel.class)).join();
    }

}
