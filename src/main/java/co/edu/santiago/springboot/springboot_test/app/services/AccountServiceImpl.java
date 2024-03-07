package co.edu.santiago.springboot.springboot_test.app.services;

import co.edu.santiago.springboot.springboot_test.app.models.Account;
import co.edu.santiago.springboot.springboot_test.app.models.Bank;
import co.edu.santiago.springboot.springboot_test.app.repository.AccountRepository;
import co.edu.santiago.springboot.springboot_test.app.repository.BankRepository;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    public int getTotalTransfers(Long bankId) {
        Bank bank = bankRepository.findById(bankId).orElseThrow();
        return bank.getTotalTransfers();
    }

    @Override
    public BigDecimal getBalance(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        return account.getBalance();
    }

    @Override
    public void transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount, Long bankId) {
        Account sourceAccount = accountRepository.findById(sourceAccountId).orElseThrow();
        sourceAccount.debit(amount);
        accountRepository.save(sourceAccount);

        Bank bank = bankRepository.findById(bankId).orElseThrow();
        int totalTransfers = bank.getTotalTransfers();
        bank.setTotalTransfers(++totalTransfers);
        bankRepository.save(bank);

        Account targetAccount = accountRepository.findById(targetAccountId).orElseThrow();
        targetAccount.credit(amount);
        accountRepository.save(targetAccount);
    }
}
