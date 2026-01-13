package com.example.studybuddy.data.model;

import java.util.ArrayList;

public class Connections {
    private String connectionID;
    private String senderEmail;
    private String receiverEmail;
    private String status;

    public Connections(String connectionID, String senderEmail, String receiverEmail, String status) {
        this.connectionID = connectionID;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.status = status;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;

    }

        public String getReceiverEmail () {
            return receiverEmail;
        }

        public void setReceiverEmail (String receiverEmail){
            this.receiverEmail = receiverEmail;

        }
    }
