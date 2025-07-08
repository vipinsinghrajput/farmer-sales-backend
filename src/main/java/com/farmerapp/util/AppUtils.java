package com.farmerapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AppUtils {


	

		@Autowired
		private HttpServletRequest servletRequest;

		public String getTokenFromHeader() {
			String header = this.servletRequest.getHeader("Authorization");
			System.out.println(header+"++++++++++++++++++++++____");
			if (header != null && header.startsWith("Bearer "))
				header = header.substring(7);

			return header;
		}


}
