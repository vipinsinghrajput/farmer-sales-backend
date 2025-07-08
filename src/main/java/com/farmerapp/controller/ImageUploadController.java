package com.farmerapp.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.farmerapp.service.ImageUploadService;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

	
	  @Autowired
	    private Cloudinary cloudinary; // Configure Cloudinary Bean

	    public String uploadImage(MultipartFile file) {
	        try {
	            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
	            return uploadResult.get("secure_url").toString();
	        } catch (IOException e) {
	            throw new RuntimeException("Image upload failed", e);
	        }
	    }
}
