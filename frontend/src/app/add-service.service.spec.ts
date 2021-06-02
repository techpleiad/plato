import { TestBed } from '@angular/core/testing';

import { AddServiceService } from './add-service.service';

describe('AddServiceService', () => {
  let service: AddServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
