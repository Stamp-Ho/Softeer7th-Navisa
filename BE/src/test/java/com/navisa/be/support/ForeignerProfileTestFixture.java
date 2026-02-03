package com.navisa.be.support;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ForeignerProfileTestFixture {

    private final ForeignerProfileRepository foreignerProfileRepository;

    public ForeignerProfile createForeignerProfile(User loginUser) {
        ForeignerProfile foreignerProfile = new ForeignerProfile(loginUser.getId(), ForeignerSearchStatus.IDLE);
        return foreignerProfileRepository.save(foreignerProfile);
    }
}
