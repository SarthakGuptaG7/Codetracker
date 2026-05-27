package com.example.demo.dto;

public class CredentialsUpdateRequest {
    private String leetcodeUsername;
    private String geeksforgeeksUsername;
    private String hackerrankUsername;

    public CredentialsUpdateRequest() {}

    public String getLeetcodeUsername() { return leetcodeUsername; }
    public void setLeetcodeUsername(String leetcodeUsername) { this.leetcodeUsername = leetcodeUsername; }

    public String getGeeksforgeeksUsername() { return geeksforgeeksUsername; }
    public void setGeeksforgeeksUsername(String geeksforgeeksUsername) { this.geeksforgeeksUsername = geeksforgeeksUsername; }

    public String getHackerrankUsername() { return hackerrankUsername; }
    public void setHackerrankUsername(String hackerrankUsername) { this.hackerrankUsername = hackerrankUsername; }
}
