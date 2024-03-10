package co.edu.santiago.springboot.springboot_test.app.controllers;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.dto.TransferDTO;
import co.edu.santiago.springboot.springboot_test.app.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account findById(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody TransferDTO transferDTO) {
        accountService.transfer(
                transferDTO.sourceAccountId(),
                transferDTO.targetAccountId(),
                transferDTO.amount(),
                transferDTO.bankId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("message", "Transfer performed successfully");
        response.put("transaction", transferDTO);

        return ResponseEntity.ok(response);
    }
}
