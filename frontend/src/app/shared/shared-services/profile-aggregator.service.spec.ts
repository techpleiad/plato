import { TestBed } from '@angular/core/testing';

import { ProfileAggregatorService } from './profile-aggregator.service';

describe('ProfileAggregatorService', () => {
  let service: ProfileAggregatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfileAggregatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
