package com.farmerapp.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {
    
	private String reviewerName;
    private int rating;
    private String comment;
    private LocalDateTime reviewDate;
}
