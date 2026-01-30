package com.navisa.be.storage.service;

import com.navisa.be.storage.dto.request.IssuePresignedUrlRequest;
import com.navisa.be.storage.dto.response.IssuePresignedUrlResponse;

public interface StorageService {

    IssuePresignedUrlResponse issuePresignedUrl(IssuePresignedUrlRequest request);
}
