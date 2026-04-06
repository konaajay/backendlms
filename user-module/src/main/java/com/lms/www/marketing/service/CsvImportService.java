package com.lms.www.marketing.service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.marketing.model.Lead;
import com.lms.www.marketing.repository.LeadRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
public class CsvImportService {

    @Autowired
    private LeadRepository leadRepository;

    public List<Lead> importLeadsFromCsv(MultipartFile file, String source) throws Exception {
        List<Lead> leads = new ArrayList<>();
        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withSkipLines(1)
                .build()) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (nextRecord.length < 2)
                    continue;

                Lead lead = new Lead();
                lead.setName(nextRecord[0].trim());
                lead.setEmail(nextRecord[1].trim());
                if (nextRecord.length > 2)
                    lead.setPhone(nextRecord[2].trim());
                lead.setSource(source != null ? source : "CSV_IMPORT");

                // Set default UTMs if not present
                lead.setUtmSource("csv_upload");
                lead.setUtmMedium("bulk_import");

                leads.add(lead);
            }
        }
        return leadRepository.saveAll(leads);
    }
}
