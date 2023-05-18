package com.micro.employeeservice.controller;

import com.micro.employeeservice.entity.Employee;
import com.micro.employeeservice.model.Laptop;
import com.micro.employeeservice.model.Smartphone;
import com.micro.employeeservice.service.EmployeeService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;


    @GetMapping
    public ResponseEntity<List<Employee>> getAll(){
        List<Employee> listEmployee = employeeService.getAll();

        if (listEmployee.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(listEmployee);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable("id") int id){

        Employee employeeNew = employeeService.GetById(id);

        if (employeeNew == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(employeeNew);

    }

    @PostMapping
    public ResponseEntity<Employee> save(@RequestBody Employee employee){

        Employee employeeNew = employeeService.save(employee);

        return ResponseEntity.ok(employeeNew);
    }
    @CircuitBreaker(name = "laptopA", fallbackMethod = "fallBackSaveLaptop")
    @PostMapping("/savelaptop/{userid}")
    public ResponseEntity<Laptop> saveLaptop(@PathVariable("userid") int userid, @RequestBody Laptop laptop){

        Employee employeeNew = employeeService.GetById(userid);

        if (employeeNew == null) return ResponseEntity.notFound().build();

        Laptop laptopNew = employeeService.saveLaptop(userid,laptop);

        return ResponseEntity.ok(laptopNew);
    }
    @CircuitBreaker(name = "smartphoneB", fallbackMethod = "fallBackSaveSmartphone")
    @PostMapping("/savesmartphone/{userid}")
    public ResponseEntity<Smartphone> saveSmartphone(@PathVariable("userid") int userid, @RequestBody Smartphone smartphone){

        Employee employeeNew = employeeService.GetById(userid);

        if (employeeNew == null) return ResponseEntity.notFound().build();

        Smartphone smartphoneNew = employeeService.saveSmartphone(userid,smartphone);

        return ResponseEntity.ok(smartphoneNew);
    }
    @CircuitBreaker(name = "laptopA", fallbackMethod = "fallBackGetAllByIdEmployee")
    @GetMapping("/laptop/{employeeId}")
    public ResponseEntity<List<Laptop>> getAllByIdEmployee(@PathVariable("employeeId") int employeeId){
        Employee employeeNew = employeeService.GetById(employeeId);

        if (employeeNew == null) return ResponseEntity.notFound().build();

        List<Laptop> listLaptop = employeeService.byUserId(employeeId);

        if (listLaptop.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(listLaptop);

    }
    @CircuitBreaker(name = "smartphoneB", fallbackMethod = "fallBackGetAllByIdEmployeeSmartphone")
    @GetMapping("/smartphone/{employeeId}")
    public ResponseEntity<List<Smartphone>> getAllByIdEmployeeSmartphone(@PathVariable("employeeId") int employeeId){
        Employee employeeNew = employeeService.GetById(employeeId);

        if (employeeNew == null) return ResponseEntity.notFound().build();

        List<Smartphone> listSmartphone = employeeService.byUserIdSmartphone(employeeId);

        if (listSmartphone.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(listSmartphone);

    }
    @CircuitBreaker(name = "allC", fallbackMethod = "fallBackGetAllDevice")
    @GetMapping("/getAll/{employeeId}")
    public ResponseEntity<Map<String,Object>> getAllDevice(@PathVariable("employeeId") int employeeId){
        Map<String,Object> result = employeeService.getEmployeeAndDevice(employeeId);
        return ResponseEntity.ok(result);

    }


    public ResponseEntity<Laptop> fallBackSaveLaptop(@PathVariable("userid") int userid, @RequestBody Laptop laptop, RuntimeException e){

        return new ResponseEntity("No se puede asignar laptop al usuario " + userid, HttpStatus.OK);
    }



    public ResponseEntity<Smartphone> fallBackSaveSmartphone(@PathVariable("userid") int userid, @RequestBody Smartphone smartphone){

        return new ResponseEntity("No se puede asignar smartphone al usuario " + userid, HttpStatus.OK);
    }


    public ResponseEntity<List<Laptop>> fallBackGetAllByIdEmployee(@PathVariable("employeeId") int employeeId){

        return new ResponseEntity("No se puede acceder a los laptop del usuario " + employeeId, HttpStatus.OK);

    }


    public ResponseEntity<List<Smartphone>> fallBackGetAllByIdEmployeeSmartphone(@PathVariable("employeeId") int employeeId){

        return new ResponseEntity("No se puede acceder a los Smartphone del usuario " + employeeId, HttpStatus.OK);

    }

    public ResponseEntity<Map<String,Object>> fallBackGetAllDevice(@PathVariable("employeeId") int employeeId){

        return new ResponseEntity("No se puede acceder a los dispositivos del usuario " + employeeId, HttpStatus.OK);

    }

}
