package vote.Util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import vote.Entity.Organization;
import vote.Entity.VoterRegistry;
import vote.Exception.BusinessRuleException;

@Component
public class CSVProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CSVProcessor.class);

    private static final String[] HEADERS = {"matric_number", "email", "phone", "full_name"};

    public List<VoterRegistry> processVoterRegistryFile(MultipartFile file, Organization organization) {
        logger.info("Processing CSV file: {}", file.getOriginalFilename());

        List<VoterRegistry> voters = new ArrayList<>();
        Set<String> uniqueIdentifiers = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVParser csvParser = new CSVParser(reader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            int lineNumber = 1; // Header is line 1

            for (CSVRecord record : csvParser) {
                lineNumber++;

                try {
                    String matricNumber = getValue(record, "matric_number");
                    String email = getValue(record, "email");
                    String phone = getValue(record, "phone");
                    String fullName = getValue(record, "full_name");

                    // Validate at least one identifier is present
                    if (matricNumber == null && email == null && phone == null) {
                        throw new BusinessRuleException(
                            String.format("Line %d: At least one identifier (matric_number, email, or phone) is required",
                            lineNumber));
                    }

                    // Check for duplicates in file
                    String uniqueKey = generateUniqueKey(matricNumber, email, phone);
                    if (uniqueIdentifiers.contains(uniqueKey)) {
                        throw new BusinessRuleException(
                            String.format("Line %d: Duplicate voter entry", lineNumber));
                    }
                    uniqueIdentifiers.add(uniqueKey);

                    // Validate email format if present
                    if (email != null && !isValidEmail(email)) {
                        throw new BusinessRuleException(
                            String.format("Line %d: Invalid email format: %s", lineNumber, email));
                    }

                    // Validate phone format if present
                    if (phone != null && !isValidPhone(phone)) {
                        throw new BusinessRuleException(
                            String.format("Line %d: Invalid phone format: %s", lineNumber, phone));
                    }

                    // Create voter registry entry
                    VoterRegistry voter = new VoterRegistry();
                    voter.setOrganization(organization);
                    voter.setMatricNumber(matricNumber);
                    voter.setEmail(email);
                    voter.setPhone(phone);
                    voter.setFullName(fullName);
                    voter.setUsed(false);

                    voters.add(voter);

                } catch (BusinessRuleException e) {
                    logger.warn("Validation error at line {}: {}", lineNumber, e.getMessage());
                    throw e; // Re-throw to stop processing
                } catch (Exception e) {
                    logger.error("Error processing line {}: {}", lineNumber, e.getMessage());
                    throw new BusinessRuleException(
                        String.format("Error processing line %d: %s", lineNumber, e.getMessage()));
                }
            }

            logger.info("Successfully processed {} voter records from CSV file", voters.size());

        } catch (BusinessRuleException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            logger.error("Error processing CSV file: {}", e.getMessage(), e);
            throw new BusinessRuleException("Error processing CSV file: " + e.getMessage());
        }

        return voters;
    }

    private String getValue(CSVRecord record, String header) {
        try {
            String value = record.get(header);
            return (value == null || value.trim().isEmpty()) ? null : value.trim();
        } catch (IllegalArgumentException e) {
            return null; // Header not found
        }
    }

    private String generateUniqueKey(String matricNumber, String email, String phone) {
        StringBuilder key = new StringBuilder();
        if (matricNumber != null) {
			key.append("M:").append(matricNumber);
		}
        if (email != null) {
			key.append("E:").append(email);
		}
        if (phone != null) {
			key.append("P:").append(phone);
		}
        return key.toString();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\+?[1-9]\\d{1,14}$");
    }
}