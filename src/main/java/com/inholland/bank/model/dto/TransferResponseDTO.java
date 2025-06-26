package com.inholland.bank.model.dto;

public class TransferResponseDTO {
    private String status;
    private String message;

    public TransferResponseDTO() {}

    public TransferResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
