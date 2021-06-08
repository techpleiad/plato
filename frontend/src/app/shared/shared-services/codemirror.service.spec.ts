import { TestBed } from '@angular/core/testing';

import { CodemirrorService } from './codemirror.service';

describe('CodemirrorService', () => {
  let service: CodemirrorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CodemirrorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
