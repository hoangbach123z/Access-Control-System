package com.bachnh.accesscontrolsystem.repository;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class CommonAdapter {
    private final static Logger log = LoggerFactory.getLogger(CommonAdapter.class.getName());
    @Autowired
    private EntityManager entityManager;
}
