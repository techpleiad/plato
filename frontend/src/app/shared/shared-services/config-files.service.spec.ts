import { TestBed } from '@angular/core/testing';

import { ConfigFilesService } from './config-files.service';

describe('ConfigFilesService', () => {
  let service: ConfigFilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfigFilesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
