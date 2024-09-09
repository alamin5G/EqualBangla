package com.goonok.equalbangla.service;

import com.goonok.equalbangla.model.ContactPerson;
import com.goonok.equalbangla.repository.ContactPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactPersonService {

    @Autowired
    private ContactPersonRepository contactPersonRepository;

    // Save contact person details
    public ContactPerson saveContactPerson(ContactPerson contactPerson) {
        return contactPersonRepository.save(contactPerson);
    }
}
