import { TestBed } from '@angular/core/testing';

import { SprimeraFilesService } from './sprimera-files.service';

describe('SprimeraFilesService', () => {
  let service: SprimeraFilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SprimeraFilesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
