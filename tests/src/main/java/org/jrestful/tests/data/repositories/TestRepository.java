package org.jrestful.tests.data.repositories;

import org.jrestful.data.repositories.support.sequence.GenericSequencedDocumentRepository;
import org.jrestful.tests.data.documents.TestDocument;

public interface TestRepository extends GenericSequencedDocumentRepository<TestDocument> {

  TestDocument findByLabel(String label);
  
}
