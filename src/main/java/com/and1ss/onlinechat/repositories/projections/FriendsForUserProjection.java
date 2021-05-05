package com.and1ss.onlinechat.repositories.projections;

public interface FriendsForUserProjection {
    String getRequestIssuerId();
    String getRequestIssuerName();
    String getRequestIssuerSurname();
    String getRequestIssuerLogin();
    String getRequesteeId();
    String getRequesteeName();
    String getRequesteeSurname();
    String getRequesteeLogin();
    String getStatus();
}
