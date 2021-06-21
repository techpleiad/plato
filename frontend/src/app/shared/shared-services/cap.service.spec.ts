import { TestBed } from '@angular/core/testing';

import { CapService } from './cap.service';

describe('CapService', () => {
  let service: CapService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CapService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
