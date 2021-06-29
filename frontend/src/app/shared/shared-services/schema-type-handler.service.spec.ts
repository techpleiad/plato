import { TestBed } from '@angular/core/testing';

import { SchemaTypeHandlerService } from './schema-type-handler.service';

describe('SchemaTypeHandlerService', () => {
  let service: SchemaTypeHandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchemaTypeHandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
