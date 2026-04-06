package com.lms.www.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lms.www.management.enums.TicketCategory;
import com.lms.www.management.model.FAQ;
import com.lms.www.management.repository.FAQRepository;

@Configuration
public class HelpdeskDataSeeder {

/*
    @Bean
    public CommandLineRunner seedFAQs(FAQRepository faqRepository) {
        return args -> {
            if (faqRepository.count() == 0) {
                faqRepository.save(FAQ.builder()
                        .question("How do I reset my portal password?")
                        .answer("You can reset your password from the login screen by clicking 'Forgot Password' or through your Profile settings.")
                        .category(TicketCategory.TECHNICAL)
                        .build());

                faqRepository.save(FAQ.builder()
                        .question("Where can I find my course completion certificates?")
                        .answer("Go to the 'Certificates' section in the sidebar. Once you complete a course, it will be automatically generated.")
                        .category(TicketCategory.ACADEMICS)
                        .build());

                faqRepository.save(FAQ.builder()
                        .question("Who should I contact for attendance corrections?")
                        .answer("Please reach out to your respective Batch Coordinator or file a ticket under the 'Attendance' category.")
                        .category(TicketCategory.OTHER)
                        .build());
            }
        };
    }
*/
}
