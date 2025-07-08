package com.farmerapp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

	public ApiResponse(boolean b, String string) {
		// TODO Auto-generated constructor stub
	}
	private String message;
	private Object response;
	
}
