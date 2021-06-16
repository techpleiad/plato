import { TestBed } from '@angular/core/testing';

import { ResolveBranchInconsistencyService } from './resolve-branch-inconsistency.service';

describe('ResolveBranchInconsistencyService', () => {
  let service: ResolveBranchInconsistencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResolveBranchInconsistencyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
