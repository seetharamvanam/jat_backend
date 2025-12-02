package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobApplicationRequestDTO;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;

@Service
public class JobScraperService {

    static{
        WebDriverManager.chromedriver().setup();
    }

    public JobApplicationRequestDTO scrapeDetails(String jobUrl){
       JobApplicationRequestDTO dto = new JobApplicationRequestDTO();
        WebDriver driver = null;

        try{
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-infobars");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");

            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            driver.get(jobUrl);
            System.out.println("DEBUG - Page Title Found: "+ driver.getTitle());
            String renderedHtml = driver.getPageSource();
           Document doc = Jsoup.parse(renderedHtml);

           String pageTitle = doc.title();
           String orgTitle = doc.select("meta[property=og:title]").attr("content");
           String ogSiteName = doc.select("meta[property=og:site_name]").attr("content");

           if(pageTitle.contains("| LinkedIn") || "LinkedIn".equals(ogSiteName)){
               parseLinkedInTitle(pageTitle,dto);
           }else{
               if(!orgTitle.isEmpty()) dto.setJobTitle(orgTitle);
               if(!ogSiteName.isEmpty()) dto.setCompanyName(ogSiteName);

               if(dto.getJobTitle() == null || dto.getCompanyName() == null){
                   parseFallbackTitle(pageTitle, dto);
               }
           }
       } catch (Exception e){
           System.err.println("Scraping failed: "+e.getMessage());
       } finally{
            if(driver!=null){
                driver.quit();
            }
        }
       return dto;
    }
    private void parseLinkedInTitle(String title, JobApplicationRequestDTO dto){
        // 1. Clean the end suffix
        String cleanTitle = title.replace("| LinkedIn","").replace(" | LinkedIn", "")
                .trim();

        //2. Split by hiring
        if(cleanTitle.contains("hiring")){
            String[] parts = cleanTitle.split(" hiring ");
            String company = parts[0].trim();
            dto.setCompanyName(company);

            if(parts.length >1){
                String rest = parts[1].trim();

                if(rest.contains(" in ")){
                    String[] jobParts = rest.split(" in ");
                    dto.setJobTitle(jobParts[0]);
                }else{
                    dto.setJobTitle(rest);
                }
            }
        }else{
            dto.setJobTitle(cleanTitle);
            dto.setCompanyName("LinkedIn (Unknown Company)");
        }
    }
    private void parseFallbackTitle(String pageTitle, JobApplicationRequestDTO dto){
        if(pageTitle == null || pageTitle.isEmpty()){
            return;
        }
        String cleanTitle = pageTitle.replace("Careers","")
                .replace("Job Application","").trim();
        String company = null;
        String jobTitle = null;

        if(pageTitle.contains(" at ")){
            String[] parts = pageTitle.split(" at ");
            jobTitle = parts[0].trim();
            if(parts.length > 1){
                company = parts[1].trim();
            }
        } else if (pageTitle.contains(" - ")){
            String[] parts = pageTitle.split(" - ");
            if (parts.length > 1){
                company = parts[0].trim();
                jobTitle = parts[1].trim();
            }
        } else if (pageTitle.contains(" | ")) {
            String[] parts = pageTitle.split(" \\| ");
            if (parts.length>1){
                jobTitle = parts[0].trim();
                company = parts[1].trim();
            }
        }
        if(dto.getCompanyName() == null && company != null){
            dto.setCompanyName(company);
        }
        if(dto.getJobTitle() == null && jobTitle != null){
            dto.setJobTitle(jobTitle);
        }
        if(dto.getJobTitle() == null){
            dto.setJobTitle(cleanTitle);
        }
    }
}
