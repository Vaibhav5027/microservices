package com.ms.jobms.serviceImpl;

import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ms.jobms.dto.JobWithCompanyDTO;
import com.ms.jobms.externalmodel.Company;
import com.ms.jobms.model.Job;
import com.ms.jobms.repo.JobRepository;
import com.ms.jobms.service.JobService;



@Service
public class JobServiceImpl implements JobService {

	@Autowired
	private JobRepository jobRepo;

//	List<Job> jobs = new ArrayList<Job>();

	@Override
	public ResponseEntity<List<JobWithCompanyDTO>> findAllJobs() {
		List<Job> jobs = jobRepo.findAll();
		
		  List<JobWithCompanyDTO> joblist = jobs.stream().map(this::convertToDto).collect(Collectors.toList());
		return new ResponseEntity<>(joblist, HttpStatus.OK);
	}

	 private JobWithCompanyDTO convertToDto(Job job) {
	
				JobWithCompanyDTO jobWithCompany=new JobWithCompanyDTO();
				RestTemplate restTemplate = new RestTemplate();
				Long id=job.getCompanyId()!=null?job.getCompanyId():1;
				Company company = restTemplate.getForObject("http://localhost:8082/api/company/companyById/"+id, Company.class);
			    jobWithCompany.setCompany(company);
			    jobWithCompany.setJob(job);
			 
			
			return jobWithCompany;
	 }
	
	@Override
	public ResponseEntity<String> cretaJob(Job job) {
		jobRepo.save(job);
		return new ResponseEntity<String>("job creatd succesfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Job> findById(int id) {
		Optional<Job> job = jobRepo.findById(id);
//		List<Job> jobs = jobRepo.findAll();
//		Job job = jobs.stream().filter(j -> j.getJobId() == id).findFirst().orElse(null);

		if (job == null) {
			return new ResponseEntity<Job>(new Job(), HttpStatus.BAD_REQUEST);
		} else
			return new ResponseEntity<Job>(job.get(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteById(int id) {
		Optional<Job> findById = jobRepo.findById(id);
		if(findById.isPresent()) {
			jobRepo.deleteById(id);
			return new ResponseEntity<String>("deleted succesffuly", HttpStatus.OK);
		}
		else
		return new ResponseEntity<String>("Not Found", HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<String> updateJob(int id, Job job) {
		Optional<Job> newJob1 = jobRepo.findById(id);

		if (newJob1.isPresent()) {
			Job newJob = newJob1.get();
			newJob.setJobId(id);
			newJob.setDescription(job.getDescription());
			newJob.setLocation(job.getLocation());
			newJob.setMaxSalary(job.getMaxSalary());
			newJob.setMinSalary(job.getMinSalary());
			newJob.setTitle(job.getTitle());
			jobRepo.save(newJob);
			return new ResponseEntity<String>("job details updated", HttpStatus.OK);
		}
		return new ResponseEntity<String>("job not found", HttpStatus.BAD_REQUEST);
	}

}
