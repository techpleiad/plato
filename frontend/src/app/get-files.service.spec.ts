import { TestBed } from '@angular/core/testing';

import { GetFilesService } from './get-files.service';

describe('GetFilesService', () => {
  let service: GetFilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GetFilesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
