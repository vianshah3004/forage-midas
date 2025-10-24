package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.IncentiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncentiveService incentiveService;

    @KafkaListener(topics = "transactions", groupId = "midas")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);

        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            System.out.println("❌ Invalid sender or recipient");
            return;
        }

        // Validate transaction: sender must have enough balance
        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("❌ Insufficient funds: " + transaction);
            return;
        }

        // ✅ Fetch incentive from Incentive API
        double incentive = incentiveService.getIncentiveAmount(transaction);

        // ✅ Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive);

        // ✅ Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        System.out.println("✅ Transaction recorded: " + transaction);
        System.out.println("💰 Incentive added: " + incentive);
    }
}
