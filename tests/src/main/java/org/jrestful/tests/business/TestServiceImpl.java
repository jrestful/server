package org.jrestful.tests.business;

import org.jrestful.business.support.sequence.GenericSequencedDocumentServiceImpl;
import org.jrestful.tests.data.documents.TestDocument;
import org.jrestful.tests.data.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends GenericSequencedDocumentServiceImpl<TestRepository, TestDocument> implements TestService {

  @Autowired
  public TestServiceImpl(TestRepository testRepository) {
    super(testRepository);
  }

  @Override
  public TestDocument findByLabel(String label) {
    return repository.findByLabel(label);
  }

}
