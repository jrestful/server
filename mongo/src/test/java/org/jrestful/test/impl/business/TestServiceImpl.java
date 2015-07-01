package org.jrestful.test.impl.business;

import org.jrestful.business.support.sequence.GenericSequencedDocumentServiceImpl;
import org.jrestful.test.impl.data.documents.TestDocument;
import org.jrestful.test.impl.data.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends GenericSequencedDocumentServiceImpl<TestRepository, TestDocument> implements TestService {

  @Autowired
  public TestServiceImpl(TestRepository testRepository) {
    super(testRepository);
  }

}
