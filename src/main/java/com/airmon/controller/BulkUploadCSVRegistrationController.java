package com.airmon.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.airmon.dto.Constants;
import com.airmon.dto.ResponseDto;
import com.airmon.dto.UserRegistrationDto;
import com.airmon.entity.Role;
import com.airmon.repository.ErrorRepository;
import com.airmon.repository.RoleRepository;
import com.airmon.repository.UserRepository;
import com.airmon.service.IUserService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Controller
public class BulkUploadCSVRegistrationController {

	@Autowired
	IUserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ErrorRepository errorRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	 List<ResponseDto> responseDtoList = new ArrayList<ResponseDto>();
	
    @PostMapping("/register")
	public ResponseEntity<?> uploadCSVFile(@RequestParam("file") MultipartFile file) {
    	List<UserRegistrationDto> userRegistrationDtoList = null;
        
        if (file.isEmpty()) {
        	responseDtoList.add(new ResponseDto(Constants.SUCCESS_CODE , "Please select a CSV file to upload" , false ,null));
        } else {

            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                CsvToBean<UserRegistrationDto> csvToBean = new CsvToBeanBuilder<UserRegistrationDto>(reader)
                        .withType(UserRegistrationDto.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        //.withSeparator('|')
                        .build();

                 userRegistrationDtoList = csvToBean.parse();


            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }

         return userService.bulkRegister(userRegistrationDtoList);
    }
    
    
    @GetMapping("/download/{file}")
    public void exportCSV(HttpServletResponse response) throws Exception {

    	userService.exportCSV(response);
                
    }
    
    @PostConstruct
	public void insertingRoles() {
		
		roleRepository.save(new Role("ROLE_USER"));
		roleRepository.save(new Role("ROLE_ADMIN"));
		roleRepository.save(new Role("ROLE_SUPERADMIN"));
		
	}
    
}