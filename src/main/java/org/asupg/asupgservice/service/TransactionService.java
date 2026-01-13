package org.asupg.asupgservice.service;

import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionDTO getTransactionById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new AppException(404, "Invalid transaction id", "Transaction with id: " + id + " not found")
        );
    }
}
