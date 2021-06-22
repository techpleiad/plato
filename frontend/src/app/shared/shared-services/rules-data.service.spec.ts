import { TestBed } from '@angular/core/testing';

import { RulesDataService } from './rules-data.service';

describe('RulesDataService', () => {
  let service: RulesDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RulesDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
