package com.farmerapp.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.farmerapp.exception.EmptyOrNullFoundlException;
import com.farmerapp.response.NominatimResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class GeocodingService {

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json";

    public double[] getCoordinates(String address) {
       System.err.println(address);
    	try {
        	
            String url = String.format(NOMINATIM_API_URL, address.replace(" ","+"));
            System.err.println("url..."+url);
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "FarmerSalesPlatform/1.0 (rajputvipin9302@example.com)"); // Required by Nominatim
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            if (jsonNode.isArray() && jsonNode.size() > 0) {
                JsonNode location = jsonNode.get(0);
                double latitude = Double.parseDouble(location.get("lat").asText());
                double longitude = Double.parseDouble(location.get("lon").asText());
                return new double[] { latitude, longitude };
            } else {
                System.err.println("❌ No results found for address: " + address);
                return null;
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching coordinates for address: " + address);
            e.printStackTrace();
            return null;
        }
    }

    public double[] fetchCoordinatesWithFallback(String fullAddress, String city, String state, String country, String postalCode) {
        double[] latLon = getCoordinates(fullAddress);
        System.out.println("📍 Full Address Coordinates: " + Arrays.toString(latLon));

        if (latLon == null || latLon.length < 2) {
            System.err.println("⚠️ No results found for full address. Retrying with simplified address...");
            String simplifiedAddress = String.format("%s, %s, %s, %s",
                    city.trim(), state.trim(), postalCode.trim(), country.trim());
            System.err.println("🔁 Simplified Address: " + simplifiedAddress);
            latLon = getCoordinates(simplifiedAddress);
            System.out.println("📍 Simplified Address Coordinates: " + Arrays.toString(latLon));
        }

        if (latLon == null || latLon.length < 2) {
            throw new EmptyOrNullFoundlException("❌ Could not fetch coordinates. Please check the address.");
        }

        return latLon;
    }
}
