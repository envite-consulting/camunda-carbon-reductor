package de.envite.greenbpm.carbonreductorconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PPMService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DateTime getRemainingDurationPrediction() throws URISyntaxException, JsonProcessingException {
        Duration remainingTime = makeCall();
        return DateTime.now().plus(remainingTime.toMillis());
    }

    private Duration makeCall() throws URISyntaxException, JsonProcessingException {
        // curl -X POST http://127.0.0.1:5000/predictRemainingTime -d '["ER Triage"]'  -H "Content-type: application/json"
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<String>> request = new HttpEntity<>(List.of("IV Antibiotics"), headers);

        ResponseEntity<String> responseEntityStr = restTemplate.postForEntity("http://127.0.0.1:5000/predictRemainingTime", request, String.class);
        JsonNode root = objectMapper.readTree(responseEntityStr.getBody());
        return Duration.parse(root.get("remaining_time").asText());
    }
}
